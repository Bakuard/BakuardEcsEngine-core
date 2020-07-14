package com.bakuard.ecsEngine.core;

/**
 * Представляет базовый класс события используемых для общения систем между собой.
 */
public abstract class Event {

    private final int TYPE_ID;

    /**
     * Создает событие с уникальным идентификатором типа этого события.
     * @param typeID уникальный идентификатор типа события.
     */
    public Event(int typeID) {
        TYPE_ID = typeID;
    }

    /**
     * Возвращает уникальный идентификатор типа этого события.
     * @return уникальный идентификатор типа этого события.
     */
    public final int getTypeID() {
        return TYPE_ID;
    }

}
