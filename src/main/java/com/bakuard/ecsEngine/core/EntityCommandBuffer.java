package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.core.exceptions.ExceptionHandler;
import com.bakuard.ecsEngine.core.utils.Bits;

import java.util.ArrayDeque;

/**
 * Используется, когда необходимо отложено выполнить несколько последоваельных операций. В момент слияния с
 * {@link EntityComponentManager} все операции, добавленные в этот буфер, будут последовательно и синхронно
 * применены к {@link EntityComponentManager} в порядке их добавления в буфер. <br/>
 * Используйте объекты этого класса, если выполняется хотя бы одно из перечисленных ниже условий: <br/>
 * 1. Необходимо создавать, удалять или изменять состав компонентов сущностей во время итерации по ним. <br/>
 * 2. Необходимо отложенно выполнить ряд операций, для которых может быть важен порядок их выполнения. <br/>
 * 3. Необходимо в отдельном потоке заплонировать ряд последовательных операций, а потом отложенно их выполнить.
 * <br/><br/>
 * Буфер является "изолированной средой" - он не имеет никакого представления о том, какие
 * изменения были внесены в другие буферы и {@link EntityComponentManager}, также как и другие буферы и
 * {@link EntityComponentManager} ничего не знают о том, какие операции были внесены в данный буфер. Единственная
 * общая информация, которая видна всем буферам - какие ID сущностей сейчас используются и какие свободны.
 * Чтобы применить изменения внесенные в буфер, необходимо передать его методу
 * {@link EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}.
 * <br/><br/>
 * Объекты данного класса нельзя переиспользовать после их слияния с {@link EntityComponentManager}. Слияние
 * осуществляется в основном потоке. <br/>
 * Объекты данного класса не делают никаких проверок на коректность выполнения операций (например, при попытке
 * удалить сущность они не будут проверять, а была ли сущность уже удалена, или при попытке связать компонент с
 * сущностью они не будут проверять - а был ли компонент уже свзязан с ущностью и жива ли сущность). Все проверки
 * будут выполнены в момент слияния буфера через метод
 * {@link EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}. <br/>
 * За создание объектов EntityCommandBuffer отвечает метод {@link EntityComponentManager#createEntityCommandBuffer()}.
 */
public final class EntityCommandBuffer {

    enum OperationType {
        CREATE_ENTITY,
        REMOVE_ENTITY,
        BIND_COMPONENT,
        UNBIND_COMPONENT,
        BIND_COMPONENTS,
        UNBIND_COMPONENTS
    }

    private final EntityComponentManager MANAGER;
    private final Bits USED_ENTITIES_ID;
    private final ArrayDeque<Operation> OPERATIONS;
    private boolean isValid; //После flush'а, буфер нельзя переиспользовать

    EntityCommandBuffer(EntityComponentManager manager) {
        MANAGER = manager;
        USED_ENTITIES_ID = new Bits(0);
        OPERATIONS = new ArrayDeque<>();

        isValid = true;
    }

    /**
     * Создает и возвращает новую уникальную сущность. Созданная сущность будет иметь уникальный идентификатор
     * среди всех сущностей в {@link EntityComponentManager} и других сущностей хранящихся в других буферах на момент
     * вызова этого метода. Добавляет в очередь отложенных операций регистрацию данной сущности в
     * {@link EntityComponentManager}.
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
            USED_ENTITIES_ID.expandTo(entityID);
            USED_ENTITIES_ID.set(entityID);
            OPERATIONS.addLast(new Operation(OperationType.CREATE_ENTITY, entity));

            return entity;
        }
        throw new IllegalArgumentException("ID типа сущности должно быть больше нуля. Получено " + entityTypeID);
    }

    /**
     * Добавляет операцию по удалению указанной сущности в очередь отложенных операций.
     * @param entity удаляемая сущность.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}.
     */
    public void removeEntity(Entity entity) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        OPERATIONS.addLast(new Operation(OperationType.REMOVE_ENTITY, entity));
    }

    /**
     * Добавляет операцию по связыванию компонента с его сущностью-владельцем
     * (см. {@link Component#Component(Entity, int)}) в очередь отложенных операций.
     * @param component связываемый компонент.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}
     */
    public void bind(Component component) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        OPERATIONS.addLast(new Operation(OperationType.BIND_COMPONENT, null, component));
    }

    /**
     * Добавляет операцию по отвязыванию компонента от его сущности-владельца
     * (см. {@link Component#Component(Entity, int)}) в очередь отложенных операций.
     * @param component отвязываемый компонент.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}
     */
    public void unbind(Component component) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        OPERATIONS.addLast(new Operation(OperationType.UNBIND_COMPONENT, null, component));
    }

    /**
     * Добавляет операцию по связыванию компонентов с их сущностью-владельцем
     * (см. {@link Component#Component(Entity, int)}) в очередь отложенных операций.
     *
     * @param components связываемые компоненты.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}
     */
    public void bind(Component... components) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        OPERATIONS.addLast(new Operation(OperationType.BIND_COMPONENTS, null, components));
    }

    /**
     * Добавляет операцию по отвязыванию компонентов от их сущности-владельца
     * (см. {@link Component#Component(Entity, int)}) в очередь отложенных операций.
     * @param components отвязываемые компоненты.
     * @throws IllegalStateException при попытке использовать данный буфер после его слияния с
     *                              {@link EntityComponentManager}
     */
    public void unbind(Component... components) {
        if(!isValid) throw new IllegalStateException("Данный буфер не может быть переиспользован.");

        OPERATIONS.addLast(new Operation(OperationType.UNBIND_COMPONENTS, null, components));
    }

    /**
     * Проверяет - является ли состояние буфера валидным. Если это так, возвращает true, иначе - false. Буфер
     * считается валидным с момента его создания и до его слияния с {@link EntityComponentManager} через метод
     * {@link EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}.
     * @return true, если буфер валиден, иначе - false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Проверяет - является ли очередь отложенных операций данного буфера пустой. Если это так, возвращает true,
     * иначе - false.
     * @return true, если очередь отложенных операций пуста, иначе - false.
     */
    public boolean isEmpty() {
        return OPERATIONS.isEmpty();
    }

    void invalid() {
        isValid = false;
    }

    Operation getAndRemoveHead() {
        return OPERATIONS.pollFirst();
    }

    Bits getUsedEntitiesID() {
        return USED_ENTITIES_ID;
    }



    static class Operation {

        final OperationType OPERATION_TYPE;
        final Entity ENTITY;
        final Component[] COMPONENTS;

        private Operation(OperationType operationType, Entity entity, Component... components) {
            OPERATION_TYPE = operationType;
            ENTITY = entity;
            COMPONENTS = components;
        }

    }

}
