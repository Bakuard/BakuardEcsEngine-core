package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.core.exceptions.BatchException;
import com.bakuard.ecsEngine.core.exceptions.ExceptionHandler;
import com.bakuard.ecsEngine.core.utils.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Данный класс представляет собой менеджер компонентов и сущностей. Менеджер компонентов и сущностей хранит и
 * предоставляет методы для маниуплирования всеми компонентами и сущностями игрового мира.
 * Каждый игровой мир имеет свой менеджер компонентов и сущностей.
 * Попытка работать с одними и теми же сущностями и компонентами через разные менеджеры сущностей может привести
 * к непредсказуемому поведению.
 */
public final class EntityComponentManager {

    private final IntArray ENTITY_GENERATIONS;
    private final Bits AVAILABLE_ENTITIES_ID; //Для переиспользования ID сущностей. Используется менджером и буферами.
    private final Bits LIVE_ENTITIES;
    private final Array<Entity> ENTITIES_BY_ID;
    private final IntArray INDEXES_BY_ENTITIES_ID; //индексы сущностей в архетипах.
    private final Array<Archetype> ARCHETYPES_BY_ENTITIES;
    private final Array<Archetype> ARCHETYPES;

    private int actualModCount; //Используется для реализации Fail-Fast итераторов.
    private final int EMPTY_ENTITIES_INDEX; //Индекс архетипа хранящего все сущности без компонентов.

    EntityComponentManager() {
        EMPTY_ENTITIES_INDEX = 0;

        AVAILABLE_ENTITIES_ID = new Bits(640);
        LIVE_ENTITIES = new Bits(640);
        ENTITY_GENERATIONS = new IntArray(640);
        ENTITIES_BY_ID = new Array<>(Entity.class, 0);
        INDEXES_BY_ENTITIES_ID = new IntArray(0);
        ARCHETYPES_BY_ENTITIES = new Array<>(Archetype.class, 0);
        ARCHETYPES = new Array<>(Archetype.class, 0);
        ARCHETYPES.add(new Archetype(new Bits()));
    }

    /**
     * Создает и возвращает новую уникальную сущность. Созданная сущность будет иметь уникальный идентификатор
     * среди всех сущностей в EntityComponentManager и других сущностей хранящихся в буферах на момент вызова этого
     * метода.
     * @param entityTypeID идентификатор типа сущности.
     * @return новую сущность.
     * @throws IllegalArgumentException если идентификатор типа сущности меньше нуля.
     */
    public Entity createEntity(int entityTypeID) {
        actualModCount++;

        if(entityTypeID >= 0) {
            int entityID = 0;
            int generation = 0;
            synchronized(this) {
                entityID = getNewEntityID();
                generation = getGeneration(entityID);
            }

            Entity entity = new Entity(entityTypeID, entityID, generation);
            ENTITIES_BY_ID.setAndExpand(entityID, entity);
            LIVE_ENTITIES.expandTo(entityID);
            LIVE_ENTITIES.set(entityID);
            ARCHETYPES.get(EMPTY_ENTITIES_INDEX).addEntity(INDEXES_BY_ENTITIES_ID, entity);
            ARCHETYPES_BY_ENTITIES.setAndExpand(entityID, ARCHETYPES.get(EMPTY_ENTITIES_INDEX));

            return entity;
        }
        throw new IllegalArgumentException("ID типа сущности не должно быть меньше нуля. Получено " + entityTypeID);
    }

    /**
     * Удаляет указаную сущность из менеджера компонентов и сущностей.
     * @param entity удаляемая сущность.
     * @throws IllegalArgumentException если сущность уже была удалена.
     */
    public void removeEntity(Entity entity) {
        actualModCount++;

        if(isAlive(entity)) {
            int entityID = entity.getPersonalID();
            freeEntityID(entityID);

            LIVE_ENTITIES.clear(entityID);
            ENTITIES_BY_ID.set(entityID, null);
            Archetype archetype = ARCHETYPES_BY_ENTITIES.set(entityID, null);
            archetype.destructAllComponent(INDEXES_BY_ENTITIES_ID.get(entityID));
            archetype.unbindAllComponents(INDEXES_BY_ENTITIES_ID.get(entityID));
            archetype.removeEntity(INDEXES_BY_ENTITIES_ID, entity);
        } else {
            throw new IllegalArgumentException("Сущность " + entity + " уже была удалена.");
        }
    }

    /**
     * Возвращает живую сущность (подробнее см. {@link #isAlive(Entity)}) имеющую заданный ID или null, если такой
     * сущности нет на момент вызова данного метода.
     * @param personalID персональный ID сущности.
     * @return живую сущность имеющую заданный персональный ID или null.
     */
    public Entity getEntity(int personalID) {
        Entity entity = null;
        if(personalID >= 0 && personalID < ENTITIES_BY_ID.getLength()) entity = ENTITIES_BY_ID.get(personalID);
        return entity;
    }

    /**
     * Проверяет, является ли указанная сущность живой. Если это так, возвращает true, иначе - false. Сущность
     * считается живой, если она находится в EntityComponentManager.
     * @param entity сущность, для которой выполняется проверка.
     * @return true, если указанная сущность является живой, иначе - false.
     */
    public boolean isAlive(Entity entity) {
        return entity.equals(ENTITIES_BY_ID.get(entity.getPersonalID()));
    }

    /**
     * Возвращает итератор перебирающий все сущности удовлетворяющие критерию задаваемому объектом типа
     * {@link EntitiesFilter}. Порядок перебора сущностей может отличаться для каждого итератора полученного
     * через данный метод. Если вы хотите использовать декларативный стиль перебора сущностей используйте
     * метод {@link #forEach(EntitiesFilter, Consumer)}.
     * <br/><br/>
     * Как и все итераторы Java, данный итератор придерживается политики fail-fast. Для создания, удаления и
     * модификации состава компонентов сущностей во время перебора используйте класс {@link EntityCommandBuffer}.
     * @param FILTER объект задающий критерий отбора сущностей.
     * @return итератор перебирающий все сущности удовлетворяющие заданому критерию.
     * @throws ConcurrentModificationException если EntityComponentManager был модифицирован во время перебора
     *                                         сущностей с помощью итератора.
     */
    public Iterator<Entity> getEntities(final EntitiesFilter FILTER) {
        return new Iterator<>() {

            private int currentBasketIndex = -1;
            private Iterator<Entity> iteratorEntities;
            private Entity currentEntity;
            private final int EXPECTED_MOD_COUNT;

            {
                EXPECTED_MOD_COUNT = actualModCount;

                while(currentEntity == null && ++currentBasketIndex < ARCHETYPES.getLength()) {
                    Archetype archetype = ARCHETYPES.get(currentBasketIndex);

                    if(archetype.isValid(FILTER)) {
                        iteratorEntities = archetype.getEntitiesIterator();

                        while(currentEntity == null && iteratorEntities.hasNext()) {
                            Entity temp = iteratorEntities.next();
                            if(FILTER.isValidEntityType(temp.getTypeID())) currentEntity = temp;
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return currentEntity != null;
            }

            @Override
            public Entity next() {
                if(EXPECTED_MOD_COUNT != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя изменять состояние EntityComponentManager во время перебора сущностей по фильтру.");
                } else if(currentEntity == null) {
                    throw new NoSuchElementException();
                } else {
                    Entity result = currentEntity;
                    currentEntity = null;

                    while(currentEntity == null && iteratorEntities.hasNext()) {
                        Entity temp = iteratorEntities.next();
                        if(FILTER.isValidEntityType(temp.getTypeID())) currentEntity = temp;
                    }

                    while(currentEntity == null && ++currentBasketIndex < ARCHETYPES.getLength()) {
                        Archetype archetype = ARCHETYPES.get(currentBasketIndex);

                        if(archetype.isValid(FILTER)) {
                            iteratorEntities = archetype.getEntitiesIterator();

                            while(currentEntity == null && iteratorEntities.hasNext()) {
                                Entity temp = iteratorEntities.next();
                                if(FILTER.isValidEntityType(temp.getTypeID())) currentEntity = temp;
                            }
                        }
                    }

                    return result;
                }
            }
        };
    }

    /**
     * Перебирает все сущности удовлетворяющие критерию задаваемому объектом типа {@link EntitiesFilter}. Порядок
     * перебора сущностей может отличаться для каждого нового вызова этого метода. Если вы хотите использовать
     * императивный стиль перебора сущностей, используйте метод {@link #getEntities(EntitiesFilter)}.
     * <br/><br/>
     * Данный метод придерживается политики fail-fast. Для создания, удаления и модификации состава компонентво
     * сущностей во время перебора используйте класс {@link EntityCommandBuffer}.
     * @param FILTER объект задающий критерий отбора сущностей.
     * @param ACTION действие, которое будет выполнено для каждой сущности удовлетворяющей заданному критерию.
     * @throws ConcurrentModificationException если EntityComponentManager был модифицирован во время перебора
     *                                         сущностей с помощью данного метода.
     */
    public void forEach(final EntitiesFilter FILTER, final Consumer<Entity> ACTION) {
        final int EXPECTED_MOD_COUNT = actualModCount;

        for(int i = 0; i < ARCHETYPES.getLength(); ++i) {
            Archetype archetype = ARCHETYPES.get(i);

            if(archetype.isValid(FILTER)) {
                Array<Entity> archetypeEntities = archetype.getEntities();

                for(int j = 0; j < archetypeEntities.getLength(); ++j) {
                    Entity entity = archetypeEntities.get(i);

                    if(FILTER.isValidEntityType(entity.getTypeID())) ACTION.accept(entity);

                    if(EXPECTED_MOD_COUNT != actualModCount) {
                        throw new ConcurrentModificationException(
                                "Нельзя изменять состояние EntityComponentManager во время перебора сущностей."
                        );
                    }
                }
            }
        }
    }


    /**
     * Возвращает true, если указанная сущность является живой и связана хотя бы с одним компонентом идентификатор
     * типа которого равен componentTypeID, иначе возвращает false.
     * @param owner сущность для которой выполняется проверка.
     * @param componentTypeID идентификатор типа компонента.
     * @return true, если указанная сущность является живой и связана хотя бы с одним компонентом идентификатор
     *         типа которого равен componentTypeID, иначе возвращает false.
     */
    public boolean hasComponent(Entity owner, int componentTypeID) {
        return isAlive(owner) && ARCHETYPES_BY_ENTITIES.get(owner.getPersonalID()).containsComponentType(componentTypeID);
    }

    /**
     * Возвращает кол-во компонентов связанных с указанной сущностью и идентификатор типа которых равен
     * componentTypeID. Возвращает ноль, если нет ни одного компонента имеющего указанный идентфикатор и связанного
     * с указанной суностью, или сущность не явлеятся живой.
     * @param owner сущность для которой выполняется проверка.
     * @param componentTypeID идентификатор типа компонентов.
     * @return кол-во компонентов связанных с указанной сущностью и идентификатор типа которых равен componentTypeID.
     */
    public int getCountComponent(Entity owner, int componentTypeID) {
        return Component.getCountComponent(getComponent(owner, componentTypeID));
    }

    /**
     * Возвращает компонент связанный с указанной сущностью. Если сущность связана сразу с несколькими
     * компонентами указанного типа - вернется один из них, при этом метод не дает гарантий какой именно компонент
     * будет возвращен. Если сущность не связана ни с одним компонентом указанного типа или не является живой -
     * возвращает null.
     * @param owner сущность для которой выполняется проверка.
     * @param componentTypeID идентификатор типа компонентов.
     * @return компонент ассоциированый с указанной сущностью или null.
     */
    public Component getComponent(Entity owner, int componentTypeID) {
        Component component = null;
        int entityPersonalID = owner.getPersonalID();
        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        if(isAlive(owner) && archetype.containsComponentType(componentTypeID)) {
            component = archetype.getComponent(INDEXES_BY_ENTITIES_ID.get(entityPersonalID), componentTypeID);
        }
        return component;
    }

    /**
     * Возвращает компонент связанный с указанной сущностью и имеющий указаную метку. Если сущность связана
     * сразу с несколькими компонетами указанного типа и имеющими указаную метку - возвращает один из них, при
     * этом метод не дает гарантий касательно того, кокой именно компонент будет возвращен. Если сущность не связана
     * ни с одним компонентом указанного типа и имеющим указаную метку, или не является живой - возвращает null.
     * @param owner сущность для которой выполняется проверка.
     * @param componentTypeID идентификатор типа компонентов.
     * @param label строковая метка по которой ищется компонент асоциированый с указываемой сущностью.
     * @return компонент ассоциированый с указанной сущностью и имеющий указаную метку или null.
     */
    public Component getComponent(Entity owner, int componentTypeID, String label) {
        Component component = null;
        int entityPersonalID = owner.getPersonalID();
        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        if(isAlive(owner) && archetype.containsComponentType(componentTypeID)) {
            component = archetype.getComponent(INDEXES_BY_ENTITIES_ID.get(entityPersonalID), componentTypeID, label);
        }
        return component;
    }

    /**
     * Возвращает массив компонентов связанных с указанной сущностью и имеющих идентифкатор типа равный
     * componentTypeID. Если нет ни одного компонента имеющего указанный идентификатор типа и связанного с
     * указанной сущностью, или сущность не является живой - метод возвращает null.
     * @param owner сущность для которой выполняется проверка.
     * @param componentTypeID идентификатор типа компонентов.
     * @return массив компонентов связанных с указанной сущностью или null.
     */
    public Array<Component> getComponents(Entity owner, int componentTypeID) {
        Array<Component> components = null;
        int entityPersonalID = owner.getPersonalID();
        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        if(isAlive(owner) && archetype.containsComponentType(componentTypeID)) {
            components = archetype.getComponents(INDEXES_BY_ENTITIES_ID.get(entityPersonalID), componentTypeID);
        }
        return components;
    }

    /**
     * Связывает указанный компонент с его сущностю-владельцем (см. {@link Component#Component(Entity, int)}).
     * Информация о связи между сущностю-владельцем и компонентом будет сохранена в данном менеджере сущностей и
     * компонентов.
     * @param component связываемый компонент.
     * @throws IllegalArgumentException если сущность-владелец не является живой или компонент уже связан с сущностью.
     */
    public void bind(Component component) {
        actualModCount++;

        checkBind(component);

        Entity owner = component.getOwner();

        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(owner.getPersonalID());
        if(archetype.containsComponentType(component.getTypeID())) {
            archetype.addComponent(INDEXES_BY_ENTITIES_ID.get(owner.getPersonalID()), component);
        } else {
            final Bits MASK = archetype.getCopyComponentTypes();
            MASK.expandTo(component.getTypeID() + 1);
            MASK.set(component.getTypeID());
            int indexArchetype = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));

            Archetype newArchetype = null;
            if (indexArchetype != -1) {
                newArchetype = ARCHETYPES.get(indexArchetype);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }
            ARCHETYPES_BY_ENTITIES.set(owner.getPersonalID(), newArchetype);
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
            newArchetype.addComponent(INDEXES_BY_ENTITIES_ID.get(owner.getPersonalID()), component);
        }

        component.bind();
    }

    /**
     * Отвязывает указанный компонент от его сущности-владельца (см. {@link Component#Component(Entity, int)}).
     * @param component отвязываемый компонент.
     * @throws IllegalArgumentException если сущность-владелец не является живой или компонент уже отвязан от сущности.
     */
    public void unbind(Component component) {
        actualModCount++;

        checkUnbind(component);

        Entity owner = component.getOwner();
        int entityPersonalID = owner.getPersonalID();
        int entityIndex = INDEXES_BY_ENTITIES_ID.get(entityPersonalID);

        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        if(archetype.hasMoreThanOneComponent(entityIndex, component.getTypeID())) {
            archetype.removeComponent(entityIndex, component);
        } else {
            final Bits MASK = archetype.getCopyComponentTypes();
            MASK.clear(component.getTypeID());
            int indexArchetype = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));

            Archetype newArchetype = null;
            if(indexArchetype != -1) {
                newArchetype = ARCHETYPES.get(indexArchetype);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }
            ARCHETYPES_BY_ENTITIES.set(entityPersonalID, newArchetype);
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
        }

        component.unbind();
    }

    /**
     * Связывает все передаваемые компоненты с одной сущностью-владельцем (см. {@link Component#Component(Entity, int)}).
     * Данный метод требует, чтобы все компоненты связывались с одной и той же сущностью. Информация о связи между
     * сущностю-владельцем и компонентами будет сохранена в данном менеджере сущностей и компонентов. В случае, если
     * выполнение метода завершается исключением - ни один компонент не будет связан с сущностью.
     * @param components связываемые компоненты.
     * @throws IllegalArgumentException генериурется по одной из следущих причин: <br/>
     *                                  1. Передаваемый массив компонентов - пуст. <br/>
     *                                  2. Передаваемые компоненты связываются с разными сущностями. <br/>
     *                                  3. Сущность, с которой собираются связаться компоненты - удалена. <br/>
     *                                  4. Один из передаваемых компонентов уже связан с сущностью.
     */
    public void bind(Component... components) {
        actualModCount++;

        checkMultipleBind(components);

        Entity owner = components[0].getOwner();
        int entityPersonalID = owner.getPersonalID();
        int entityIndex = INDEXES_BY_ENTITIES_ID.get(owner.getPersonalID());

        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        final Bits MASK = archetype.getCopyComponentTypes();
        for(Component comp : components) {
            MASK.expandTo(comp.getTypeID());
            MASK.set(comp.getTypeID());
        }

        if(archetype.containsComponentTypes(MASK)) {
            for(Component comp : components) archetype.addComponent(entityIndex, comp);
        } else {
            int indexArchetype = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));
            Archetype newArchetype = null;
            if (indexArchetype != -1) {
                newArchetype = ARCHETYPES.get(indexArchetype);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }

            ARCHETYPES_BY_ENTITIES.set(owner.getPersonalID(), newArchetype);
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
            for(Component comp : components) newArchetype.addComponent(entityIndex, comp);
        }

        for(Component comp : components) comp.bind();
    }

    /**
     * Отвязывает все передаваемые компоненты от одной сущности-владельца (см. {@link Component#Component(Entity, int)}).
     * Данный метод требует, чтобы все компоненты отвязывались от одной и той же сущности. В случае, если выполнение
     * метода завершается исключением - ни один компонент не будет отвязан от сущности.
     * @param components отвязываемые компоненты.
     * @throws IllegalArgumentException генериурется по одной из следущих причин: <br/>
     *                                  1. Передаваемый массив компонентов - пуст. <br/>
     *                                  2. Передаваемые компоненты связаны с разными сущностями. <br/>
     *                                  3. Сущность, от которой собираются отвязаться компоненты - удалена. <br/>
     *                                  4. Один из передаваемых компонентов уже отвязан от сущности.
     */
    public void unbind(Component... components) {
        actualModCount++;

        checkMultipleUnbind(components);

        Entity owner = components[0].getOwner();
        int entityPersonalID = owner.getPersonalID();
        int entityIndex = INDEXES_BY_ENTITIES_ID.get(entityPersonalID);

        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(entityPersonalID);
        final Bits MASK = archetype.getCopyComponentTypes();
        boolean hasMoreThanOneComponents = true;
        for(Component comp : components) {
            MASK.expandTo(comp.getTypeID());
            MASK.set(comp.getTypeID());
            hasMoreThanOneComponents &= archetype.hasMoreThanOneComponent(entityIndex, comp.getTypeID());
        }

        if(hasMoreThanOneComponents) {
            for(Component comp : components) archetype.removeComponent(entityIndex, comp);
        } else {
            int indexArchetype = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));
            Archetype newArchetype = null;
            if (indexArchetype != -1) {
                newArchetype = ARCHETYPES.get(indexArchetype);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }

            ARCHETYPES_BY_ENTITIES.set(entityPersonalID, newArchetype);
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
        }

        for(Component comp : components) comp.unbind();
    }


    /**
     * Создает и возвращает новый буфер для асинхронного создания и асинхронного редактирования созданных через него
     * сущностей.
     * @return буфер создания и редактирвоания сущностей.
     */
    public NewEntitiesBuffer createNewEntitiesBuffer() {
        return new NewEntitiesBuffer(this);
    }

    /**
     * Создает и возвращает буфер для отложенного выполнения операций по созданию сущностей, их удалению и
     * изменению их состава компонентов.
     * @return буфер для отложенного выполнения операций по работе с сущностями и компонентами.
     */
    public EntityCommandBuffer createEntityCommandBuffer() {
        return new EntityCommandBuffer(this);
    }

    /**
     * Добавляет все сущности, компоненты и связи между ними из переданного буфера в данный менеджер компонентов и
     * сущностей.
     * @param buffer буфер для которого выполняется слияние с данным менеджером компонентов и сущностей.
     */
    public void flushBuffer(NewEntitiesBuffer buffer) {
        actualModCount++;

        buffer.invalid();

        LIVE_ENTITIES.or(buffer.USED_ENTITIES_ID);

        for(int i = 0; i < buffer.ENTITIES_BY_ID.getLength(); i++) {
            Entity entity = buffer.ENTITIES_BY_ID.get(i);
            if(entity != null) ENTITIES_BY_ID.setAndExpand(i, entity);
        }

        for(int i = 0, j = 0; i < buffer.ARCHETYPES.getLength(); i++) {
            Archetype from = buffer.ARCHETYPES.get(i);
            Archetype to = ARCHETYPES.get(j);
            Bits fromComponentTypes = from.getCopyComponentTypes();

            int compareTypes = fromComponentTypes.compareIgnoreSize(to.getCopyComponentTypes());
            while(compareTypes > 0 && ++j < ARCHETYPES.getLength()) {
                to = ARCHETYPES.get(j);
                compareTypes = fromComponentTypes.compareIgnoreSize(to.getCopyComponentTypes());
            }

            if(compareTypes == 0) {
                to.merge(from, INDEXES_BY_ENTITIES_ID, ARCHETYPES_BY_ENTITIES);
            } else {
                Archetype newArchetype = new Archetype(fromComponentTypes);
                newArchetype.merge(from, INDEXES_BY_ENTITIES_ID, ARCHETYPES_BY_ENTITIES);
                ARCHETYPES.insert(j, newArchetype);
            }
        }
    }

    /**
     * Последовательно выполняет все операции сохраненые в буфере для отложенного выполнения операций по созданию
     * сущностей, их удалению и изменению их состава компонентов.
     * @param buffer буфер для отложенного выполнения операций.
     * @param handler обработчик для ошибок, которые могут возникнуть в ходе выполнения операций из буфера.
     * @throws BatchException если пакетная операция была прервана.
     */
    public void flushBuffer(EntityCommandBuffer buffer, ExceptionHandler<IllegalArgumentException> handler) {
        actualModCount++;

        buffer.invalid();

        Bits mask = buffer.getUsedEntitiesID();

        while(!buffer.isEmpty()) {
            try {
                EntityCommandBuffer.Operation operation = buffer.getAndRemoveHead();
                switch (operation.OPERATION_TYPE) {
                    case CREATE_ENTITY:
                        Entity entity = operation.ENTITY;
                        int entityID = entity.getPersonalID();

                        ENTITIES_BY_ID.setAndExpand(entityID, entity);
                        LIVE_ENTITIES.expandTo(entityID);
                        LIVE_ENTITIES.set(entityID);
                        ARCHETYPES.get(EMPTY_ENTITIES_INDEX).addEntity(INDEXES_BY_ENTITIES_ID, entity);
                        ARCHETYPES_BY_ENTITIES.setAndExpand(entityID, ARCHETYPES.get(EMPTY_ENTITIES_INDEX));

                        mask.clear(entityID);
                        break;
                    case REMOVE_ENTITY:
                        removeEntity(operation.ENTITY);
                        break;
                    case BIND_COMPONENT:
                        bind(operation.COMPONENTS[0]);
                        break;
                    case UNBIND_COMPONENT:
                        unbind(operation.COMPONENTS[0]);
                        break;
                    case BIND_COMPONENTS:
                        bind(operation.COMPONENTS);
                        break;
                    case UNBIND_COMPONENTS:
                        unbind(operation.COMPONENTS);
                        break;
                }
            } catch(IllegalArgumentException e) {
                try {
                    handler.handle(e);
                } catch(BatchException batchException) {
                    synchronized(this) {
                        AVAILABLE_ENTITIES_ID.and(mask.expandTo(AVAILABLE_ENTITIES_ID.getSize()).not());
                    }
                    throw batchException;
                }
            }
        }
    }



    /*
     * Данный метод также вызывается из EntityCommandBuffer и NewEntitiesBuffer, и таким оразом может быть вызван
     * из другого потока.
     */
    int getGeneration(int entityPersonalID) {
        ENTITY_GENERATIONS.expandTo(entityPersonalID);
        return ENTITY_GENERATIONS.increment(entityPersonalID);
    }

    /*
    * Данный метод также вызывается из EntityCommandBuffer и NewEntitiesBuffer, и таким оразом может быть вызван
    * из другого потока.
     */
    int getNewEntityID() {
        int entityID = AVAILABLE_ENTITIES_ID.nextClearBit(0);

        if(entityID == -1) {
            entityID = AVAILABLE_ENTITIES_ID.getSize();
            AVAILABLE_ENTITIES_ID.expandTo(entityID * 2);
        }
        AVAILABLE_ENTITIES_ID.set(entityID);

        return entityID;
    }

    /*
     * Данный метод также вызывается из EntityCommandBuffer и NewEntitiesBuffer, и таким оразом может быть вызван
     * из другого потока.
     */
    synchronized void freeEntityID(int entityID) {
        AVAILABLE_ENTITIES_ID.clear(entityID);
    }

    private void checkBind(Component component) {
        Entity owner = component.getOwner();
        if(!isAlive(owner)) {
            throw new IllegalArgumentException(
                    "Компонент " + component + " не может быть связан с удаленой сущностью " + owner
            );
        } else if(component.isBind()) {
            throw new IllegalArgumentException(
                    "Компонент " + component + " уже связан с сущностью " + owner
            );
        }
    }

    private void checkUnbind(Component component) {
        Entity owner = component.getOwner();
        if(!isAlive(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + ", являющееся владельцем компоеннта " + component + " удалена."
            );
        } else if(!component.isBind()) {
            throw new IllegalArgumentException(
                    "Компонент " + component + " уже отвязан от сущности " + owner
            );
        }
    }

    private void checkMultipleBind(Component[] components) {
        if(components.length == 0)
            throw new IllegalArgumentException("Передаваемый массив компонентов не должен быть пустым.");

        Entity owner = components[0].getOwner();
        for(int i = 1; i < components.length; ++i) {
            if(!owner.equals(components[i].getOwner())) {
                throw new IllegalArgumentException(
                        "Все компоненты должны принадлежать одной и той же сущности. " +
                        "Компонент " + components[i] + " пренадлежит сущности " + components[i].getOwner() +
                        ", а компонент " + components[0] + " пренадлежит сущности " + owner
                );
            }
        }

        if(!isAlive(owner)) {
            throw new IllegalArgumentException("Нельзя связать компоненты с удаленой сущностью " + owner);
        }

        for(Component comp : components) {
            if(comp.isBind()) {
                throw new IllegalArgumentException("Компонент " + comp + " уже связан с сущностью " + owner);
            }
        }
    }

    private void checkMultipleUnbind(Component[] components) {
        if(components.length == 0)
            throw new IllegalArgumentException("Передаваемый массив компонентов не должен быть пустым.");

        Entity owner = components[0].getOwner();
        for(int i = 1; i < components.length; ++i) {
            if(!owner.equals(components[i].getOwner())) {
                throw new IllegalArgumentException(
                        "Все компоненты должны принадлежать одной и той же сущности. " +
                        "Компонент " + components[i] + " пренадлежит сущности " + components[i].getOwner() +
                        ", а компонент " + components[0] + " пренадлежит сущности " + owner
                );
            }
        }

        if(!isAlive(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + ", являющееся владельцем указанных компонентво удалена.");
        }

        for(Component comp : components) {
            if(!comp.isBind()) {
                throw new IllegalArgumentException("Компонент " + comp + " уже отвязан от сущности " + owner);
            }
        }
    }

}
