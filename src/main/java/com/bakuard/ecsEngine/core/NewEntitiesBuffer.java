package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.core.utils.Array;
import com.bakuard.ecsEngine.core.utils.Bits;
import com.bakuard.ecsEngine.core.utils.IntArray;

/**
 * Используется в случае, когда необходимо асинхронно создать большое кол-во новых сущностей, асинхронно изменить
 * состав компонентов созданных сущностей, а потом разом добавить все внесенные изменения в
 * {@link EntityComponentManager}. <br/>
 * Иначе говоря, используйте объекты этого класса, если выполняются хотябы одно из ниже перечисленных условий: <br/>
 * 1. Асинхронно создать большое кол-во сущностей и асинхронно связать их с компонентами. <br/>
 * 2. Указанные в первом пункте действия должны быть выполнены в отдельном потоке. <br/>
 * 3. До тех пор, пока все запланированные сущности и их компоненты не будут созданы и связаны между собой, остальной
 *    код не должен иметь к ним доступ.
 * <br/><br/>
 * Буфер является "изолированной средой" - он не имеет никакого представления о том, какие
 * изменения были внесены в другие буферы и {@link EntityComponentManager}, также как и другие буферы и
 * {@link EntityComponentManager} ничего не знают о том, какие изменения были внесены в данный буфер. Единственная
 * общая информация, которая видна всем буферам - какие ID сущностей сейчас используются и какие свободны.
 * Чтобы применить изменения внесенные в буфер, необходимо передать его методу
 * {@link EntityComponentManager#flushBuffer(NewEntitiesBuffer)}.
 * <br/><br/>
 * Объекты данного класса нельзя переиспользовать после их слияния с {@link EntityComponentManager}. Слияние
 * осуществляется в основном потоке. <br/>
 * Объект данного класса может работать только с сущностями созданными через него. При попытке работать с сущностями
 * не созданными через объект данного класса будет генерироваться исключение. <br/>
 * За создание объектов NewEntitiesBuffer отвечает метод {@link EntityComponentManager#createNewEntitiesBuffer()}.
 */
public final class NewEntitiesBuffer {

    private final EntityComponentManager MANAGER;

    final Bits USED_ENTITIES_ID;
    final Array<Entity> ENTITIES_BY_ID;
    final Array<Archetype> ARCHETYPES;
    private final IntArray INDEXES_BY_ENTITIES_ID;
    private final Array<Archetype> ARCHETYPES_BY_ENTITIES;

    private final int EMPTY_ENTITIES_INDEX; //Индекс корзины хранящей все сущности без компонентов.
    private boolean isValid; //После flush'а буфер нельзя переиспользовать

    NewEntitiesBuffer(EntityComponentManager manager) {
        EMPTY_ENTITIES_INDEX = 0;

        MANAGER = manager;

        USED_ENTITIES_ID = new Bits();
        ENTITIES_BY_ID = new Array<>(Entity.class, 0);
        INDEXES_BY_ENTITIES_ID = new IntArray(0);
        ARCHETYPES_BY_ENTITIES = new Array<>(Archetype.class, 0);
        ARCHETYPES = new Array<>(Archetype.class, 0);
        ARCHETYPES.add(new Archetype(new Bits()));

        isValid = true;
    }

    /**
     * Создает и возвращает новую уникальную сущность. Созданная сущность будет иметь уникальный идентификатор
     * среди всех сущностей в {@link EntityComponentManager} и других сущностей хранящихся в других буферах на момент
     * вызова этого метода.
     * @param entityTypeID идентификатор типа сущности.
     * @return новую сущность.
     * @throws IllegalArgumentException если идентификатор типа сущности меньше нуля.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public Entity createEntity(int entityTypeID) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        if(entityTypeID >= 0) {
            int entityID = 0;
            int generation = 0;
            synchronized(MANAGER) {
                entityID = MANAGER.getNewEntityID();
                generation = MANAGER.getGeneration(entityID);
            }

            Entity entity = new Entity(entityTypeID, entityID, generation);
            ENTITIES_BY_ID.setAndExpand(entityID, entity);
            USED_ENTITIES_ID.expandTo(entityID);
            USED_ENTITIES_ID.set(entityID);
            ARCHETYPES.get(EMPTY_ENTITIES_INDEX).addEntity(INDEXES_BY_ENTITIES_ID, entity);
            ARCHETYPES_BY_ENTITIES.setAndExpand(entityID, ARCHETYPES.get(EMPTY_ENTITIES_INDEX));

            return entity;
        }
        throw new IllegalArgumentException("ID типа сущности не должно быть меньше нуля. Получено " + entityTypeID);
    }

    /**
     * Удаляет указаную сущность из данного буфера.
     * @param entity удаляемая сущность.
     * @throws IllegalArgumentException если указанная сущность уже была удалена или создана не через данный буфер.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void removeEntity(Entity entity) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        if(contains(entity)) {
            int entityID = entity.getPersonalID();
            MANAGER.freeEntityID(entityID);

            ENTITIES_BY_ID.set(entityID, null);
            USED_ENTITIES_ID.clear(entityID);
            Archetype archetype = ARCHETYPES_BY_ENTITIES.set(entityID, null);
            archetype.destructAllComponent(INDEXES_BY_ENTITIES_ID.get(entityID));
            archetype.unbindAllComponents(INDEXES_BY_ENTITIES_ID.get(entityID));
            archetype.removeEntity(INDEXES_BY_ENTITIES_ID, entity);
        } else {
            throw new IllegalArgumentException(
                    "Сущность " + entity + " была создана не через данный буфер, или уже была удалена.");
        }
    }

    /**
     * Связывает указанный компонент с его сущностю-владельцем (см. {@link Component#Component(Entity, int)}).
     * Информация о связи между сущностю-владельцем и компонентом будет сохранена в данном буфере.
     * @param component связываемый компонент.
     * @throws IllegalArgumentException если указанная сущность была удалена из данного буфера или была созданна
     *                                  не через него, а также, если указанный компонент уже связан с
     *                                  сущностью-владельцем.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void bind(Component component) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        checkBind(component);

        Entity owner = component.getOwner();

        component.bind();

        Archetype archetype = ARCHETYPES_BY_ENTITIES.get(owner.getPersonalID());
        if(archetype.containsComponentType(component.getTypeID())) {
            archetype.addComponent(INDEXES_BY_ENTITIES_ID.get(owner.getPersonalID()), component);
        } else {
            final Bits MASK = archetype.getCopyComponentTypes();
            MASK.expandTo(component.getTypeID() + 1);
            MASK.set(component.getTypeID());
            int indexBasket = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));

            Archetype newArchetype = null;
            if (indexBasket != -1) {
                newArchetype = ARCHETYPES.get(indexBasket);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
            newArchetype.addComponent(INDEXES_BY_ENTITIES_ID.get(owner.getPersonalID()), component);
            ARCHETYPES_BY_ENTITIES.set(owner.getPersonalID(), newArchetype);
        }
    }

    /**
     * Отвзяывает компонент от его сущности-владельца (см. {@link Component#Component(Entity, int)}).
     * @param component отвязываемый компонент.
     * @throws IllegalArgumentException если указанная сущность была удалена из данного буфера или была созданна
     *                                  не через него, а также, если указанный компонент уже отсвязан от
     *                                  сущности-владельца.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void unbind(Component component) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

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
            int indexBasket = ARCHETYPES.binarySearch((Archetype b) -> b.compareComponentTypes(MASK));

            Archetype newArchetype = null;
            if(indexBasket != -1) {
                newArchetype = ARCHETYPES.get(indexBasket);
            } else {
                newArchetype = new Archetype(MASK);
                ARCHETYPES.binaryInsert(newArchetype, Archetype::compareTo);
            }
            archetype.moveEntityTo(INDEXES_BY_ENTITIES_ID, owner, newArchetype);
            ARCHETYPES_BY_ENTITIES.set(entityPersonalID, newArchetype);
        }

        component.unbind();
    }

    /**
     * Связывает все передаваемые компоненты с одной сущностью-владельцем
     * (см. {@link Component#Component(Entity, int)}). Данный метод требует, чтобы все компоненты связывались с
     * одной и той же сущностью. Информация о связи между сущностю-владельцем и компонентами будет сохранена в данном
     * буфере. В случае, если выполнение метода завершается исключением - ни один компонент не будет связан с
     * сущностью.
     * @param components связываемые компоненты.
     * @throws IllegalArgumentException генериурется по одной из следущих причин: <br/>
     *                                  1. Передаваемый массив компонентов - пуст. <br/>
     *                                  2. Передаваемые компоненты связываются с разными сущностями. <br/>
     *                                  3. Сущность-владелец была создана не через данный буфер или сущность-владелец
     *                                     была удалена. <br/>
     *                                  4. Один из передаваемых компонентов уже связан с сущностью.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void bind(Component... components) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

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
     * Отвязывает все передаваемые компоненты от одной сущности-владельца
     * (см. {@link Component#Component(Entity, int)}). Данный метод требует, чтобы все компоненты отвязывались
     * от одной и той же сущности.
     * В случае, если выполнение метода завершается исключением - ни один компонент не будет отвязан от сущности.
     * @param components отвязываемые компоненты.
     * @throws IllegalArgumentException генериурется по одной из следущих причин: <br/>
     *                                  1. Передаваемый массив компонентов - пуст. <br/>
     *                                  2. Передаваемые компоненты связываются с разными сущностями. <br/>
     *                                  3. Сущность-владелец была создана не через данный буфер или сущность-владелец
     *                                     была удалена. <br/>
     *                                  4. Один из передаваемых компонентов уже отвязан от сущности.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void unbind(Component... components) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

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
     * Возвращает сущность созданую и хранящуюся в данном буфере, и имеющую указанный personalID
     * или null - если сущности с таким ID не хранится в данном буфере на момент вызова этого метода.
     * @param personalID персональный ID сущности.
     * @return сущность созданую через данный буфер и хранящуюся в нем или null.
     */
    public Entity getEntity(int personalID) {
        Entity entity = null;
        if(personalID >= 0 && personalID < ENTITIES_BY_ID.getLength()) {
            entity = ENTITIES_BY_ID.get(personalID);
        }
        return entity;
    }

    /**
     * Возвращает true, если указаная сущность на момент вызова этого метода находится в данном буфере, иначе - false.
     * В буфере данного типа могут находится только сущности созданные через него.
     * @param entity сущность для которой выполняется проверка на её принадлежность буферу.
     * @return true, если указаная сущность на момент вызова этого метода находится в данном буфере, иначе - false.
     */
    public boolean contains(Entity entity) {
        return entity.equals(ENTITIES_BY_ID.get(entity.getPersonalID()));
    }

    /**
     * Проверяет - является ли состояние буфера валидным. Если это так, возвращает true, иначе - false. Буфер
     * считается валидным с момента его создания и до его слияния с {@link EntityComponentManager} через метод
     * {@link EntityComponentManager#flushBuffer(NewEntitiesBuffer)}.
     * @return true, если буфер валиден, иначе - false.
     */
    public boolean isValid() {
        return isValid;
    }

    void invalid() {
        isValid = false;
    }

    private void checkBind(Component component) {
        Entity owner = component.getOwner();
        if(!contains(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + " была создана не через данный буфер или была удалена.");

        } else if(component.isBind()) {
            throw new IllegalArgumentException(
                    "Компонент " + component + " уже связан с сущностью " + owner
            );
        }
    }

    private void checkUnbind(Component component) {
        Entity owner = component.getOwner();
        if(!contains(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + " была создана не через данный буфер или была удалена.");
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

        if(!contains(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + " была создана не через данный буфер или была удалена."
            );
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

        if(!contains(owner)) {
            throw new IllegalArgumentException(
                    "Сущность " + owner + " была создана не через данный буфер или была удалена."
            );
        }

        for(Component comp : components) {
            if(!comp.isBind()) {
                throw new IllegalArgumentException("Компонент " + comp + " уже отвязан от сущности " + owner);
            }
        }
    }

}