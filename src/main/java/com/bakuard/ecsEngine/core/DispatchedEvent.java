package com.bakuard.ecsEngine.core;

/**
 * Представляет базовый класс для рассылаемых событий. Данный класс необходимо использовать в качестве базового
 * класса для пользовательских событий если вы хотите использовать механизм рассылки событий предоставляемый методом
 * {@link SystemManager#sendEvent(DispatchedEvent)}.
 */
public abstract class DispatchedEvent extends Event {

    private final int PRIORITY;

    /**
     * Создает событие с уникальным идентификатором типа этого события и приоритетом определяющем порядок
     * рассылки событий. Событий с большим приоритетом будут раньше отправлены своим получателям.
     * @param typeID уникальный идентификатор типа события.
     * @param priority приоритет рассылки для данного объекта события.
     */
    public DispatchedEvent(int typeID, int priority) {
        super(typeID);
        PRIORITY = priority;
    }

    /**
     * Возвращает приоритет рассылки для данного события.
     * @return приоритет рассылки данного события.
     */
    public final int getPriority() {
        return PRIORITY;
    }

}
