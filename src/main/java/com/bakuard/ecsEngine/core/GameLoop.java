package com.bakuard.ecsEngine.core;

/**
 * Данный класс отвечает напосредственно за обновление всех систем (см. {@link System}) в игровом мире.
 * Каждый игровой мир имеет свой игровой цикл. Игровой цикл работает в собственном потоке (этот поток
 * является главным потоком). Игровой цикл может находится в одном из трех состояний: остановлен, обнавляется
 * или стоит на паузе.
 *
 * Каждый шаг игрового цикла состоит из одной и той же строго определенной последовательности подшагов:
 * 1. Изменяется список обновляемых систем (все системы добавленные в очередь добавления или удаления на
 *    предыдущем шаге игрового цикла будут фактически добавлены или удалены из списка обрабатываемых систем).
 * 2. Рассылка всех событий добавленных на предыдущем шаге игрового цикла. События рассылаются в порядке убывания
 *    их приоритета, а получатели каждого события получат его в порядек определенном для данного события
 *    (подробнее см. {@link SystemManager#sendEvent}).
 * 3. Вызывается метод update() у всех систем ввода в порядке убывания их приоритетов.
 * 4. Вызывается метод update() у всех систем логики в порядке убывания их приоритетов.
 * 5. Вызывается метод update() у всех систем рендера в порядке убывания их приоритетов.
 */
public class GameLoop {

    private final int NUMBER_UPDATE_PER_SECOND;
    private final int MAX_FRAME_SKIP;
    private volatile boolean run;
    private volatile boolean pause;

    private Thread thread;
    private final Object LOCK = new Object();

    private final SystemManager SYSTEM_MANAGER;

    GameLoop(int numberUpdatePerSecond, int maxFrameSkip, SystemManager systemManager) {
        if(numberUpdatePerSecond <= 0 || numberUpdatePerSecond > 1000) {
            throw new IllegalArgumentException("Для numberUpdatePerSecond должно выполняться условие: " +
                    "numberUpdatePerSecond > 0 || numberUpdatePerSecond <= 1000. " +
                    "Получено " + numberUpdatePerSecond);
        } else if(maxFrameSkip <= 0) {
            throw new IllegalArgumentException("maxFrameSkip должен быть больше нуля. Получено " + maxFrameSkip);
        }

        NUMBER_UPDATE_PER_SECOND = numberUpdatePerSecond;
        MAX_FRAME_SKIP = maxFrameSkip;
        SYSTEM_MANAGER = systemManager;
    }

    /**
     * Запускает новый поток и игровой цикл в этом потоке. После вызова данного метода и перед первым шагом
     * цикла в этом потоке, для всех систем в игровом мире будет вызван метод start() в следующем порядке:
     * 1. Для всех систем ввода в порядке убывания их приоритетов.
     * 2. Для всех систем логики в порядке убывания их приоритетов.
     * 3. Для всех систем рендера в порядке убывания их приоритетов.
     * Метод start() всех систем вызывается в потоке игрового цикла.
     *
     * @throws IllegalStateException при попытке вызвать этот метод у объекта игрового цикла, пока тот исполняется
     *                               или стоит на паузе.
     */
    public void start() {
        if(thread == null || !thread.isAlive()) {
            run = true;
            pause = false;
            thread = new Thread(() -> {
                try {
                    gameLoop();
                } catch(InterruptedException e) {
                    java.lang.System.err.println("Игровой цикл остановлен: " + e);
                }
            });
            thread.start();
        } else {
            throw new IllegalStateException("Нельзя вызвать метод start пока игровой цикл не завершен.");
        }
    }

    /**
     * После вызова данного метода, игровой цикл закончит выполнять текущий шаг и остановится на паузу перед
     * следующим шагом. Если вызвать данны метод у остановленного игрового цикла или когда он стоит на паузе
     * - ничего не произойдет.
     */
    public void pause() {
        pause = true;
    }

    /**
     * После вызова данного метода, если игровой цикл стоял на паузе - он продолжит свое исполнения. Если
     * вызвать данный метод у игрвого цикла, пока тот обновляется или остановлен - ничего не произойдет.
     */
    public void resume() {
        pause = false;
        synchronized(LOCK) {
            LOCK.notify();
        }
    }

    /**
     * Если данный метод был вызван в момент, когда игровой цикл находился в состоянии обновления, то игровой
     * цикл закончит текущий шаг, далее будут вызван метод stop() у всех систем и затем игровой цикл будет
     * остановлен.
     * Если данный метод был вызван в момент, когда игровой цикл находился в состоянии паузы, то после вызова
     * этого метода у всех систем будет вызван метод stop(), а затем игровой цикл будет остановлен.
     * Метод stop() вызывается у всех систем в следующем порядке:
     * 1. Для всех систем ввода в порядке убывания их приоритетов.
     * 2. Для всех систем логики в порядке убывания их приоритетов.
     * 3. Для всех систем рендера в порядке убывания их приоритетов.
     * Метод stop() всех систем вызывается в потоке игрового цикла.
     */
    public void stop() {
        run = false;
        if(thread != null) thread.interrupt();
    }

    private void gameLoop() throws InterruptedException {
        SYSTEM_MANAGER.changeSystemsList();

        SYSTEM_MANAGER.startSystems();

        final long UPDATE_INTERVAL = 1000L / NUMBER_UPDATE_PER_SECOND;

        long lastTime = java.lang.System.currentTimeMillis();
        long delta = 0; //кол-во миллисекунд прошедшее с прошлого обновления

        while(run) {
            if(pause) {
                synchronized(LOCK) {
                    long startPause = java.lang.System.currentTimeMillis();
                    while(pause) LOCK.wait(); //while(pause) - защита от спонтанного пробуждения
                    long pauseTime = java.lang.System.currentTimeMillis() - startPause;
                    lastTime += pauseTime;
                }
            }

            long now = java.lang.System.currentTimeMillis();
            long elapsedTime = now - lastTime;
            lastTime = now;
            delta += elapsedTime;
            long fullElapsedInterval = delta;

            SYSTEM_MANAGER.changeSystemsList();

            SYSTEM_MANAGER.sendEvents();

            SYSTEM_MANAGER.updateInputSystems(UPDATE_INTERVAL, fullElapsedInterval);

            for(int i = 0; delta >= UPDATE_INTERVAL && i < MAX_FRAME_SKIP; ++i) {
                SYSTEM_MANAGER.updateLogicSystems(UPDATE_INTERVAL, fullElapsedInterval);
                delta -= UPDATE_INTERVAL;
            }

            SYSTEM_MANAGER.updateRenderSystems(UPDATE_INTERVAL, fullElapsedInterval);
        }

        SYSTEM_MANAGER.stopSystems();
    }

}
