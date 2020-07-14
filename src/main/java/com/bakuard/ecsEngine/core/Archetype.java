package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.utils.Array;
import com.bakuard.ecsEngine.utils.Bits;
import com.bakuard.ecsEngine.utils.IntArray;
import com.bakuard.ecsEngine.utils.IntMap;

import java.util.Iterator;
import java.util.function.Consumer;

/*
 * Хранит сущности имеющий один и тотже набор компонентов. Коллекция из таких объектов используется для оптимизации
 * поиска сущностей с определенным набором компонентов и типом сущностей, а также для компактного хранения компонентов.
 */
final class Archetype {

    private final Array<Entity> ENTITIES;
    private final IntMap<Array<Component>> COMPONENTS;
    private final Bits COMPONENT_TYPES;
    private final IntArray ENTITIES_TYPE; //Используется для оптимизации фильтрации сущностей по типам.

    Archetype(Bits componentTypes) {
        ENTITIES = new Array<>(Entity.class, 0);
        COMPONENTS = new IntMap<>();
        this.COMPONENT_TYPES = componentTypes;
        ENTITIES_TYPE = new IntArray(0);

        int componentTypeID = componentTypes.nextSetBit(0);
        while(componentTypeID != -1) {
            COMPONENTS.put(componentTypeID, new Array<>(Component.class, 0));
            componentTypeID = componentTypes.nextSetBit(componentTypeID + 1);
        }
    }

    void merge(Archetype other, IntArray indexesByEntitiesID, Array<Archetype> archetypesByEntities) {
        int numberEntitiesBeforeMerge = ENTITIES.getLength();

        for(int i = 0; i < other.ENTITIES.getLength(); i++) {
            Entity immigrant = other.ENTITIES.get(i);
            int immigrantID = immigrant.getPersonalID();
            int immigrantTypeID = immigrant.getTypeID();

            indexesByEntitiesID.setAndExpand(immigrantID, numberEntitiesBeforeMerge + i);
            archetypesByEntities.setAndExpand(immigrantID, this);

            ENTITIES_TYPE.expandTo(immigrantTypeID + 1);
            ENTITIES_TYPE.increment(immigrantTypeID);

            ENTITIES.add(immigrant);
        }

        COMPONENTS.forEach((IntMap.Node<Array<Component>> node) -> {
            Array<Component> array = node.getValue();
            Array<Component> otherArray = other.COMPONENTS.get(node.getKey());
            array.addAll(otherArray);
        });
    }

    Bits getCopyComponentTypes() {
        return new Bits(COMPONENT_TYPES);
    }

    void destructAllComponent(int entityIndex) {
        COMPONENTS.forEach((IntMap.Node<Array<Component>> node) -> {
            Component component = node.getValue().get(entityIndex);
            while(component != null) {
                component.destruct();
                component = component.getNext();
            }
        });
    }

    boolean containsComponentType(int componentTypeID) {
        return COMPONENT_TYPES.get(componentTypeID);
    }

    boolean containsComponentTypes(Bits componentTypes) {
        return COMPONENT_TYPES.equalsIgnoreSize(componentTypes);
    }

    boolean hasMoreThanOneComponent(int entityIndex, int componentTypeID) {
        Component head = COMPONENTS.get(componentTypeID).get(entityIndex);
        return head != null && head.getNext() == null;
    }

    void addComponent(int entityIndex, Component component) {
        Array<Component> componentsArray = COMPONENTS.get(component.getTypeID());
        Component head = componentsArray.get(entityIndex);
        componentsArray.setAndExpand(entityIndex, Component.add(head, component));
    }

    void removeComponent(int entityIndex, Component component) {
        Array<Component> componentsArray = COMPONENTS.get(component.getTypeID());
        Component head = componentsArray.get(entityIndex);
        componentsArray.set(entityIndex, Component.remove(head, component));
    }

    void moveEntityTo(IntArray indexesByEntitiesID, Entity entity, Archetype other) {
        int entityPersonalID = entity.getPersonalID();
        int entityTypeID = entity.getTypeID();
        final int entityIndex = indexesByEntitiesID.get(entityPersonalID);

        ENTITIES.quickRemove(entityIndex);
        if(entityIndex < ENTITIES.getLength())
            indexesByEntitiesID.set(ENTITIES.get(entityIndex).getPersonalID(), entityIndex);
        indexesByEntitiesID.setAndExpand(entityPersonalID, other.ENTITIES.getLength());
        other.ENTITIES.add(entity);

        ENTITIES_TYPE.decrement(entityTypeID);
        other.ENTITIES_TYPE.expandTo(entityTypeID + 1);
        other.ENTITIES_TYPE.increment(entityTypeID);

        COMPONENTS.forEach((IntMap.Node<Array<Component>> node) -> {
            Component component = node.getValue().quickRemove(entityIndex);
            Array<Component> componentsArray = other.COMPONENTS.get(component.getTypeID());
            if(componentsArray != null) componentsArray.add(component);
        });
    }

    void unbindAllComponents(final int entityIndex) {
        COMPONENTS.forEach((IntMap.Node<Array<Component>> node) -> {
            Component.detachAll(node.getValue().get(entityIndex));
        });
    }

    void removeEntity(IntArray indexesByEntitiesID, Entity entity) {
        final int entityIndex = indexesByEntitiesID.get(entity.getPersonalID());

        ENTITIES.quickRemove(entityIndex);
        if(entityIndex < ENTITIES.getLength())
            indexesByEntitiesID.set(ENTITIES.get(entityIndex).getPersonalID(), entityIndex);

        COMPONENTS.forEach((IntMap.Node<Array<Component>> node) -> {
            Array<Component> componentsArray = node.getValue();
            componentsArray.quickRemove(entityIndex);
        });

        ENTITIES_TYPE.decrement(entity.getTypeID());
    }

    void addEntity(IntArray indexesByEntitiesID, Entity entity) {
        int entityPersonalID = entity.getPersonalID();

        indexesByEntitiesID.setAndExpand(entityPersonalID, ENTITIES.getLength());
        ENTITIES.add(entity);

        int entityTypeID = entity.getTypeID();
        ENTITIES_TYPE.expandTo(entityTypeID + 1);
        ENTITIES_TYPE.increment(entityTypeID);
    }

    Component getComponent(int entityIndex, int componentTypeID, String label) {
        Array<Component> componentsArray = COMPONENTS.get(componentTypeID);
        return Component.findByLabel(componentsArray.get(entityIndex), label);
    }

    Component getComponent(int entityIndex, int componentTypeID) {
        Array<Component> componentsArray = COMPONENTS.get(componentTypeID);
        return componentsArray.get(entityIndex);
    }

    Array<Component> getComponents(int entityIndex, int componentTypeID) {
        Array<Component> componentsArray = COMPONENTS.get(componentTypeID);
        Component head = componentsArray.get(entityIndex);
        Array<Component> components = new Array<>(Component.class, 0);
        while(head != null) {
            components.add(head);
            head = head.getNext();
        }
        return components;
    }

    //Используется при вызове binarySearch() и binaryInsert() у объекта Array хранящего объекты Archetype
    int compareComponentTypes(Bits componentTypes) {
        return COMPONENT_TYPES.compareIgnoreSize(componentTypes);
    }

    //Используется при вызове binarySearch() и binaryInsert() у объекта Array хранящего объекты Archetype
    int compareTo(Archetype o) {
        return COMPONENT_TYPES.compareIgnoreSize(o.COMPONENT_TYPES);
    }

    Iterator<Entity> getEntitiesIterator() {
        return ENTITIES.iterator();
    }

    //Возвращаемый массив сущностей используется EntityComponentManager только для чтения.
    Array<Entity> getEntities() {
        return ENTITIES;
    }

    boolean isValid(final EntitiesFilter FILTER) {
        return isValidComponentTypes(FILTER) && isValidEntityType(FILTER);
    }

    private boolean isValidEntityType(final EntitiesFilter FILTER) {
        boolean validEntityType = false;
        for(int i = 0, length = ENTITIES_TYPE.getLength(); i < length && !validEntityType; i++) {
            validEntityType = ENTITIES_TYPE.get(i) > 0 && FILTER.isValidEntityType(i);
        }
        return validEntityType;
    }

    private boolean isValidComponentTypes(final EntitiesFilter FILTER) {
        /*
        * Передаем фильтру копию COMPONENT_TYPES, чтобы защитить COMPONENT_TYPES
        * от изменений со стороны клиентского кода.
         */
        return FILTER.isValidComponentTypes(new Bits(COMPONENT_TYPES));
    }

}
