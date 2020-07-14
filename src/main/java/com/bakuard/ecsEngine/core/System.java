package com.bakuard.ecsEngine.core;

/**
 * Общий интерфейс всех систем. Вся логика игры реализуется в классах расширяющих данный интерфейс.
 * Все методы данного интерфейса вызываются из потока игрового цикла (подробнее см {@link GameLoop}).
 */
public interface System {

    /**
     * Вызывается сразу после запуска игрового цикла (подробнее см. {@link GameLoop#start()}).
     */
    public void start();

    /**
     * Вызывается на каждом шаге игрового цикла. Чтобы узнать порядок обновления систем см. {@link GameLoop}.
     * @param updateInterval фиксированный временной интервал в миллесекундах обнавления логики игрового мира.
     * @param elapsedInterval фактическое время в миллесекундах прошедшее с предыдущего шага игрового цикла.
     */
    public void update(long updateInterval, long elapsedInterval);

    /**
     * Вызывается после остановки игрового цикла (подробнее см. {@link GameLoop#stop()}).
     */
    public void stop();

    /**
     * Данный метод вызывается, когда система получает адресованное ей событие.
     * Подробнее см. {@link SystemManager#sendEvent}
     * @param event получаемое событие отправленное данной системе.
     */
    public void listenEvent(Event event);

}
