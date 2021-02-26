package com.bakuard.ecsEngine.core;

/**
 * Данный класс представляет собой идентификатор для логического объединения нескольких компонентов
 * (см. {@link Component}) относящихся к одному и тому же условному игровому объекту. Объекты данного класса
 * не изменяемы и представляют собой объекты-значения.
 */
public final class Entity {

    private final int PERSONAL_ID;
    private final int TYPE_ID;
    private final int GENERATION;

    Entity(int typeID, int personalID, int generation) {
        PERSONAL_ID = personalID;
        TYPE_ID = typeID;
        GENERATION = generation;
    }

    /**
     * Возвращает идентификатор типа данной сущности.
     * @return идентификато типа данной сущности.
     */
    public int getTypeID() {
        return TYPE_ID;
    }

    /**
     * Возвращает персональный ID данной сущности. Персональный ID сущности гарантировано уникален и неизменен
     * на период от создания сущности и до её удаления.
     * @return персональный ID данной сущности.
     */
    public int getPersonalID() {
        return PERSONAL_ID;
    }

    /**
     * Персональные ID сущностей переиспользуются. Данный метод показывает, сколько раз переиспользовался
     * ID, который в данный момент является персональным ID данной сущности.
     * @return кол-во переиспользований ID являющегося в данный момент персональным ID данной сущности.
     */
    public int getGeneration() {
        return GENERATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return PERSONAL_ID == entity.PERSONAL_ID && GENERATION == entity.GENERATION && TYPE_ID == entity.TYPE_ID;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + PERSONAL_ID;
        result = result * 31 + TYPE_ID;
        result = result * 31 + GENERATION;
        return result;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "TYPE_ID=" + getTypeID() +
                ", PERSONAL_ID=" + getPersonalID() +
                ", GENERATION=" + getGeneration() +
                '}';
    }

}
