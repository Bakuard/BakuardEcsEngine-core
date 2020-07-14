package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.utils.Array;
import com.bakuard.ecsEngine.utils.IntArray;

/**
 * Управляет всеми системами в игровом мире, а также позволяет системам общаться между собой используя
 * систему событий. Каждый игровой мир имеет свой менеджер систем. При попыке работать содними и теми же
 * системами через разные менеджеры систем может привести к непредсказуемому поведению.
 */
public final class SystemManager {

    private enum SystemType {
        INPUT,
        LOGIC,
        RENDER
    }

    private final Array<SystemWrapper> ADDED_SYSTEMS;
    private final IntArray DELETED_SYSTEMS;
    private final Array<SystemWrapper> INPUT_SYSTEMS;
    private final Array<SystemWrapper> LOGIC_SYSTEMS;
    private final Array<SystemWrapper> RENDER_SYSTEMS;

    private final Event[] PORTS;
    private Array<DispatchedEvent> eventsList;
    private Array<DispatchedEvent> eventsBuffer;

    SystemManager(int numberPorts) {
        if(numberPorts <= 0) {
            throw new IllegalArgumentException("numberPorts должен быть больше нуля.");
        }

        ADDED_SYSTEMS = new Array<>(SystemWrapper.class, 0);
        DELETED_SYSTEMS = new IntArray(0);
        INPUT_SYSTEMS = new Array<>(SystemWrapper.class, 0);
        LOGIC_SYSTEMS = new Array<>(SystemWrapper.class, 0);
        RENDER_SYSTEMS = new Array<>(SystemWrapper.class, 0);

        PORTS = new Event[numberPorts];
        eventsList = new Array<>(DispatchedEvent.class, 0);
        eventsBuffer = new Array<>(DispatchedEvent.class, 0);
    }

    /**
     * Данный метод добавляет указанную систему ввода в список обрабатывамых систем ввода. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий.
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем ввода. Чем больше priority - тем раньше будет обрабатываться система на каждом шаге игрового цикла.
     *
     * Важно отметить, что systemID считается занятым сразу же после успешного добавления системы и до тех пор,
     * пока система с этим идентификатором не будет фактически удалена.
     *
     * Также и priority, считается занятым сразу же после успешного вызова данного метода и до тех
     * пор, пока система с этим идентификатором не будет фактически удалена из списка на обработку.
     * @param system добавляемая система ввода.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     * @throws IllegalArgumentException если указанный systemID не является уникальным среди всех других
     *                                  идентефикаторов систем (не только систем ввода) или указанный
     *                                  priority не является уникальным среди приоритетов других систем ввода.
     */
    public void addInputSystem(System system, int priority, int systemID) {
        if(hasSystem(systemID)) {
            throw new IllegalArgumentException(
                    "Указанный systemID = " + systemID + " уже используется другой обрабатываемой системой, " +
                            "или система с этим systemID находится в ожидании добавления."
            );
        }

        for(int i = 0; i < INPUT_SYSTEMS.getLength(); ++i) {
            if(INPUT_SYSTEMS.get(i).PRIORITY == priority) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы ввода.");
            }
        }
        for(int i = 0; i < ADDED_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = ADDED_SYSTEMS.get(i);
            if(wrapper.PRIORITY == priority && wrapper.TYPE == SystemType.INPUT) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы ввода.");
            }
        }

        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.INPUT));
    }

    /**
     * Данный метод добавляет указанную систему логики в список обрабатывамых систем логики. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий.
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем логики. Чем больше priority - тем раньше будет обрабатываться система на каждом шаге игрового цикла.
     *
     * Важно отметить, что systemID считается занятым сразу же после успешного добавления системы и до тех пор,
     * пока система с этим идентификатором не будет фактически удалена.
     *
     * Также и priority, считается занятым сразу же после успешного вызова данного метода и до тех
     * пор, пока система с этим идентификатором не будет фактически удалена из списка на обработку.
     * @param system добавляемая система игровой логики.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     * @throws IllegalArgumentException если указанный systemID не является уникальным среди всех других
     *                                  идентефикаторов систем (не только систем логики) или указанный
     *                                  priority не является уникальным среди приоритетов других систем логики.
     */
    public void addLogicSystem(System system, int priority, int systemID) {
        if(hasSystem(systemID)) {
            throw new IllegalArgumentException(
                    "Указанный systemID = " + systemID + " уже используется другой обрабатываемой системой, " +
                            "или система с этим systemID находится в ожидании добавления."
            );
        }

        for(int i = 0; i < LOGIC_SYSTEMS.getLength(); ++i) {
            if(LOGIC_SYSTEMS.get(i).PRIORITY == priority) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы логики.");
            }
        }
        for(int i = 0; i < ADDED_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = ADDED_SYSTEMS.get(i);
            if(wrapper.PRIORITY == priority && wrapper.TYPE == SystemType.LOGIC) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы логики.");
            }
        }

        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.LOGIC));
    }

    /**
     * Данный метод добавляет указанную систему рендера в список обрабатывамых систем рендера. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий.
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем рендера. Чем больше priority - тем раньше будет обрабатываться система на каждом шаге игрового цикла.
     *
     * Важно отметить, что systemID считается занятым сразу же после успешного добавления системы и до тех пор,
     * пока система с этим идентификатором не будет фактически удалена.
     *
     * Также и priority, считается занятым сразу же после успешного вызова данного метода и до тех
     * пор, пока система с этим идентификатором не будет фактически удалена из списка на обработку.
     * @param system добавляемая система рендера.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     * @throws IllegalArgumentException если указанный systemID не является уникальным среди всех других
     *                                  идентефикаторов систем (не только систем рендера) или указанный
     *                                  priority не является уникальным среди приоритетов других систем рендера.
     */
    public void addRenderSystem(System system, int priority, int systemID) {
        if(hasSystem(systemID)) {
            throw new IllegalArgumentException(
                    "Указанный systemID = " + systemID + " уже используется другой обрабатываемой системой, " +
                            "или система с этим systemID находится в ожидании добавления."
            );
        }

        for(int i = 0; i < RENDER_SYSTEMS.getLength(); ++i) {
            if(RENDER_SYSTEMS.get(i).PRIORITY == priority) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы рендера.");
            }
        }
        for(int i = 0; i < ADDED_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = ADDED_SYSTEMS.get(i);
            if(wrapper.PRIORITY == priority && wrapper.TYPE == SystemType.RENDER) {
                throw new IllegalArgumentException(
                        "Указанный priority = " + priority + " уже используется для другой системы рендера.");
            }
        }

        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.RENDER));
    }


    /**
     * Удаляет систему с указанным идентификатором. Фактическое удаление системы произойдет только в начале
     * следующего шага игрового цикла перед рассылкой событий.
     * Метод требует, чтобы удаляемая система в момент его вызова находилась в списке обрабатываемых систем или
     * ожидала добавления в этот список на следующем шаге игрового цикла.
     * Метод не может быть вызван для одной и той же системы на одном и том же шаге цикла несколько раз.
     * @param systemID идентификатор удаляемой системы.
     * @throws IllegalArgumentException если нет обрабатываемой системы использующий указанный systemID или система
     *                                  с указанным systemID уже добавлена в очередь на удаление с помощью этого
     *                                  метода на текущем шаге игрового цикла.
     */
    public void removeSystem(int systemID) {
        if(hasSystem(systemID) && DELETED_SYSTEMS.linearSearch(systemID) == -1) {
            DELETED_SYSTEMS.add(systemID);
        } else {
            throw new IllegalArgumentException("Система с указанным systemID = " + systemID + " не существует " +
                    "или уже была добавлена в очередь на удаление на текущем шаге игрового цикла.");
        }
    }


    /**
     * Возвращает систему имеющую указанный идентификатор или null, если среди обрабатываемых в данный момент
     * систем нет системы с таким идентификатором.
     * @param systemID идентификатор системы.
     * @return систему ввода имеющую указанный идентификатор или null.
     */
    public System getSystem(int systemID) {
        SystemWrapper wrapper = getSystemWrapper(systemID);
        if(wrapper != null)  return wrapper.SYSTEM;
        return null;
    }


    /**
     * Переводит систему с указанным идентификатором в режим сна. В этом режиме система остается в списке
     * обрабатываемых систем, однако для неё не будут вызываться методы update() и listenEvent(), но по прежнему
     * будут вызываться методы start() и stop().
     * Данный метод можно вызвать только для системы находящихся на момент вызова этого метода в списке
     * обрабатываемых систем ввода, логики или рендера.
     * Если данный метод вызывается для системы, которая уже находится в режиме сна - вызов данного метода не
     * оказывает никакого эффекта.
     * @param systemID идентификатор усыпляемой системы.
     * @throws IllegalArgumentException если нет обрабатываемой системы использующий указанный systemID.
     */
    public void sleep(int systemID) {
        SystemWrapper wrapper = getSystemWrapper(systemID);
        if(wrapper != null) {
            wrapper.isAwake = false;
        } else {
            throw new IllegalArgumentException("Среди обрабатываемых в данный момент систем " +
                    "нет системы с systemID = " + systemID);
        }
    }

    /**
     * Переводит систему с указанным идентификатором в режим пробуждения. Это обычный рабочий режим для системы,
     * в котором она находится в списке обрабатываемых систем и для которой на каждом шаге цикла вызывается
     * метод update(), а также может быть вызван метод listenEvent().
     * Данный метод можно вызвать только для системы находящихся на момент вызова этого метода в списке
     * обрабатываемых систем ввода, логики или рендера.
     * Если данный метод вызывается для системы, которая уже находится в режиме бодрствования - вызов данного
     * метода не оказывает никакого эффекта.
     * @param systemID идентификатор пробуждаемой системы.
     * @throws IllegalArgumentException если нет обрабатываемой системы использующий указанный systemID.
     */
    public void awake(int systemID) {
        SystemWrapper wrapper = getSystemWrapper(systemID);
        if(wrapper != null) {
            wrapper.isAwake = true;
        } else {
            throw new IllegalArgumentException("Среди обрабатываемых в данный момент систем " +
                    "нет системы с systemID = " + systemID);
        }
    }


    /**
     * Размещает указанное событие на порту с указанным номером. Событие будет оставаться на указанном порту
     * до тех пор, пока не будет перезаписано с помощью вызова этого метода для этого же порта или удалено
     * с помощью вызова метода {@link #getAndRemoveEvent}.
     * @param port номер порта.
     * @param event указываемое событие.
     * @throws IllegalArgumentException - если для port не выполняется условие port >= 0 && port < numberPorts,
     *                                    где numberPorts - число портов задаваемое при создании мира.
     * @throws NullPointerException - если event имеет значение null.
     */
    public void putEvent(int port, Event event) {
        if(port < 0 || port >= PORTS.length) {
            throw new IllegalArgumentException(
                    "Для port должно выполняться условие: port >= 0 && port < " + PORTS.length +
                            ". Получено " + port);
        } else if(event == null) {
            throw new NullPointerException("Переданное событие имеет значение null.");
        } else {
            PORTS[port] = event;
        }
    }

    /**
     * Возвращает событие размещенное на указанном порту или null, если указанный порт свободен.
     * @param port номер порта.
     * @return событие размещенное на указанном порту или null, если указанный порт свободен.
     * @throws IllegalArgumentException - если для port не выполняется условие port >= 0 && port < numberPorts,
     *                                    где numberPorts - число портов задаваемое при создании мира.
     */
    public Event getEvent(int port) {
        if(port < 0 || port >= PORTS.length) {
            throw new IllegalArgumentException(
                    "Для port должно выполняться условие: port >= 0 && port < " + PORTS.length +
                            ". Получено " + port);
        }
        return PORTS[port];
    }

    /**
     * Возвращает событие размещенное на указанном порту и освободает порт. Если указанный порт свободен,
     * метод просто вернет null.
     * @param port номер порта.
     * @return событие размещенное на указанном порту или null, если указанный порт свободен.
     * @throws IllegalArgumentException - если для port не выполняется условие port >= 0 && port < numberPorts,
     *                                    где numberPorts - число портов задаваемое при создании мира.
     */
    public Event getAndRemoveEvent(int port) {
        if(port < 0 || port >= PORTS.length) {
            throw new IllegalArgumentException(
                    "Для port должно выполняться условие: port >= 0 && port < " + PORTS.length +
                            ". Получено " + port);
        }
        Event event = PORTS[port];
        PORTS[port] = null;
        return event;
    }


    /**
     * Добавляет указанное событие в очередь событий. События из данной очереди будут разосланы получателям
     * в порядке приоритета событий, а получатели получат их в порядке заданном пользователем для каждого события.
     * Рассылка событий будет существлена в начале следующего шага цикла, а очередь опустошена.
     *
     * Значение priority задает порядок, в котором события будт разосланы получателям. Чем больше значение priority,
     * тем раньше событие будет разослано получателям. Событие с самым большим приоритетом будет разослано своим
     * получателем первым, а с самым маленьким приоритеттом - последним. Нельзя добавлять несколько событий
     * с одним и тем же приоритетом на одном и том же шаге цикла.
     *
     * systemID - идентификатор системы которая получит указанное событие на следующем шаге цикла.
     *
     * Данный метод можно вызвать только для системы находящихся на момент вызова этого метода в списке
     * обрабатываемых систем ввода, логики или рендера.
     *
     * Нельзя добавлять один и тот же объект события несколько раз на одном и том же шаге игрового цикла.
     * @param event отправляемое событие.
     * @param priority приоритет отправляемого события.
     * @param systemID идентефикатор системы-получателя события.
     * @throws NullPointerException - если event имеет значение null.
     * @throws IllegalArgumentException - Данное исключение выбрасывается в одном из 3 случаев:
     *                                    1. Если на данном шаге цикла уже добавлялось событие с указанным приоритетом.
     *                                    2. На данном шаге цикла уже добавлялся данный объект события.
     *                                    3. Среди обрабатываемых на момент вызова этого метода систем небыло системы
     *                                       с указанным идентификатором.
     */
    public void sendEvent(Event event, int priority, int systemID) {
        if(event == null) {
            throw new NullPointerException("Переданное событие имеет значение null.");
        }

        for(int i = 0; i < eventsBuffer.getLength(); ++i) {
            DispatchedEvent currentEvent = eventsBuffer.get(i);
            if(currentEvent.EVENT == event) {
                throw new IllegalArgumentException(
                        "На данном шаге цикла уже добавлялся данный объект события. Получено " + event
                );
            } else if(currentEvent.PRIORITY == priority) {
                throw new IllegalArgumentException(
                        "На данном шаге цикла уже добавлялось событие с указанным приоритетом. Получено " + priority
                );
            }
        }

        System system = getSystem(systemID);
        if(system != null) {
            eventsBuffer.add(new DispatchedEvent(event, priority, system));
        } else {
            throw new IllegalArgumentException(
                    "На момент вызова этого метода не существует обрабатываемой системы с systemID = " + systemID
            );
        }
    }

    /**
     * Добавляет указанное событие в очередь событий. События из данной очереди будут разосланы получателям
     * в порядке приоритета событий, а получатели получат их в порядке заданном пользователем для каждого события.
     * Рассылка событий будет осуществлена в начале следующего шага цикла, а очередь опустошена.
     *
     * Значение priority задает порядок, в котором события будт разосланы получателям. Чем больше значение priority,
     * тем раньше событие будет разослано получателям. Событие с самым большим приоритетом будет разослано своим
     * получателем первым, а с самым маленьким приоритеттом - последним. Нельзя добавлять несколько событий
     * с одним и тем же приоритетом на одном и том же шаге цикла.
     *
     * systemsID - идентификаторы систем, которые получат данное событие. Порядок, в котором системы получат это
     * событие, соответсвует порядку перечисления систем в массиве systemsID. Параметр systemsID нельзя оставлять
     * пустым.
     *
     * Данный метод можно вызвать только для системы находящихся на момент вызова этого метода в списке
     * обрабатываемых систем ввода, логики или рендера.
     *
     * Нельзя добавлять один и тот же объект события несколько раз на одном и том же шаге игрового цикла.
     * @param event отправляемое событие.
     * @param priority приоритет отправляемого события.
     * @param systemsID идентификаторы систем-получателей события.
     * @throws NullPointerException - если event имеет значение null.
     * @throws IllegalArgumentException - Данное исключение выбрасывается в одном из 3 случаев:
     *                                    1. Если на данном шаге цикла уже добавлялось событие с указанным приоритетом.
     *                                    2. На данном шаге цикла уже добавлялся данный объект события.
     *                                    3. Среди обрабатываемых на момент вызова этого метода систем небыло системы
     *                                       с указанным идентификатором.
     *                                    4. Если массив systemsID пустой.
     *                                    5. Если массив systemsID содержит одинаковые элементы.
     */
    public void sendEvent(Event event, int priority, int... systemsID) {
        if(event == null) {
            throw new NullPointerException("Переданное событие имеет значение null.");
        }

        for(int i = 0; i < eventsBuffer.getLength(); ++i) {
            DispatchedEvent currentEvent = eventsBuffer.get(i);
            if(currentEvent.EVENT == event) {
                throw new IllegalArgumentException(
                        "На данном шаге цикла уже добавлялся данный объект события. Получено " + event
                );
            } else if(currentEvent.PRIORITY == priority) {
                throw new IllegalArgumentException(
                        "На данном шаге цикла уже добавлялось событие с указанным приоритетом. Получено " + priority
                );
            }
        }

        if(systemsID.length == 0) {
            throw new IllegalArgumentException(
                    "Не указан ни один получатель события."
            );
        }

        System[] systems = new System[systemsID.length];
        for(int i = 0; i < systemsID.length; ++i) {
            for(int j = i + 1; j < systemsID.length; ++j) {
                if(systemsID[i] == systemsID[j]) {
                    throw new IllegalArgumentException(
                        "Нельзя указывать в качестве получателя события несколько раз одну и ту же систему."
                    );
                }
            }

            System system = getSystem(systemsID[i]);
            if(system != null) {
                systems[i] = system;
            } else {
                throw new IllegalArgumentException(
                    "На момент вызова этого метода не существует обрабатываемой системы с systemID = " + systemsID[i]
                );
            }
        }

        eventsBuffer.add(new DispatchedEvent(event, priority, systems));
    }


    void changeSystemsList() {
        for(int i = ADDED_SYSTEMS.getLength() - 1; i >= 0; --i) {
            SystemWrapper wrapper = ADDED_SYSTEMS.orderedRemove(i);
            switch(wrapper.TYPE) {
                case INPUT: addSystemWrapper(wrapper, INPUT_SYSTEMS);
                    break;
                case LOGIC: addSystemWrapper(wrapper, LOGIC_SYSTEMS);
                    break;
                case RENDER: addSystemWrapper(wrapper, RENDER_SYSTEMS);
                    break;
            }
        }

        for(int i = DELETED_SYSTEMS.getLength() - 1; i >= 0; --i) {
            removeSystemWrapper(DELETED_SYSTEMS.orderedRemove(i));
        }
    }

    void sendEvents() {
        Array<DispatchedEvent> temp = eventsList;
        eventsList = eventsBuffer;
        eventsBuffer = temp;

        if(eventsList.getLength() > 0) {
            eventsList.sort((DispatchedEvent a, DispatchedEvent b) -> Integer.compare(a.PRIORITY, b.PRIORITY));

            for(int i = eventsList.getLength() - 1; i >= 0; --i) {
                DispatchedEvent dispatchedEvent = eventsList.orderedRemove(i);
                for(int j = 0; j < dispatchedEvent.RECIPIENTS.length; ++j) {
                    dispatchedEvent.RECIPIENTS[j].listenEvent(dispatchedEvent.EVENT);
                }
            }
        }
    }

    void startSystems() {
        for(int i = 0; i < INPUT_SYSTEMS.getLength(); ++i) INPUT_SYSTEMS.get(i).SYSTEM.start();
        for(int i = 0; i < LOGIC_SYSTEMS.getLength(); ++i) LOGIC_SYSTEMS.get(i).SYSTEM.start();
        for(int i = 0; i < RENDER_SYSTEMS.getLength(); ++i) RENDER_SYSTEMS.get(i).SYSTEM.start();
    }

    void updateInputSystems(long updateInterval, long elapsedInterval) {
        for(int i = 0; i < INPUT_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = INPUT_SYSTEMS.get(i);
            if(wrapper.isAwake) wrapper.SYSTEM.update(updateInterval, elapsedInterval);
        }
    }

    void updateLogicSystems(long updateInterval, long elapsedInterval) {
        for(int i = 0; i < LOGIC_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = LOGIC_SYSTEMS.get(i);
            if(wrapper.isAwake) wrapper.SYSTEM.update(updateInterval, elapsedInterval);
        }
    }

    void updateRenderSystems(long updateInterval, long elapsedInterval) {
        for(int i = 0; i < RENDER_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = RENDER_SYSTEMS.get(i);
            if(wrapper.isAwake) wrapper.SYSTEM.update(updateInterval, elapsedInterval);
        };
    }

    void stopSystems() {
        for(int i = 0; i < INPUT_SYSTEMS.getLength(); ++i) INPUT_SYSTEMS.get(i).SYSTEM.stop();
        for(int i = 0; i < LOGIC_SYSTEMS.getLength(); ++i) LOGIC_SYSTEMS.get(i).SYSTEM.stop();
        for(int i = 0; i < RENDER_SYSTEMS.getLength(); ++i) RENDER_SYSTEMS.get(i).SYSTEM.stop();
    }


    private boolean hasSystem(int systemID) {
        boolean find = false;

        for(int i = 0; i < INPUT_SYSTEMS.getLength() && !find; ++i) {
            find = INPUT_SYSTEMS.get(i).SYSTEM_ID == systemID;
        }
        for(int i = 0; i < LOGIC_SYSTEMS.getLength() && !find; ++i) {
            find = LOGIC_SYSTEMS.get(i).SYSTEM_ID == systemID;
        }
        for(int i = 0; i < RENDER_SYSTEMS.getLength() && !find; ++i) {
            find = RENDER_SYSTEMS.get(i).SYSTEM_ID == systemID;
        }
        for(int i = 0; i < ADDED_SYSTEMS.getLength() && !find; ++i) {
            find = ADDED_SYSTEMS.get(i).SYSTEM_ID == systemID;
        }

        return find;
    }

    private SystemWrapper getSystemWrapper(int systemID) {
        SystemWrapper wrapper = null;

        for(int i = 0; i < INPUT_SYSTEMS.getLength() && wrapper == null; ++i) {
            SystemWrapper temp = INPUT_SYSTEMS.get(i);
            if(temp.SYSTEM_ID == systemID) wrapper = temp;
        }
        for(int i = 0; i < LOGIC_SYSTEMS.getLength() && wrapper == null; ++i) {
            SystemWrapper temp = LOGIC_SYSTEMS.get(i);
            if(temp.SYSTEM_ID == systemID) wrapper = temp;
        }
        for(int i = 0; i < RENDER_SYSTEMS.getLength() && wrapper == null; ++i) {
            SystemWrapper temp = RENDER_SYSTEMS.get(i);
            if(temp.SYSTEM_ID == systemID) wrapper = temp;
        }

        return wrapper;
    }

    private void removeSystemWrapper(int systemID) {
        SystemWrapper wrapper = null;

        for(int i = 0; i < INPUT_SYSTEMS.getLength() && wrapper == null; ++i) {
            if(INPUT_SYSTEMS.get(i).SYSTEM_ID == systemID) wrapper = INPUT_SYSTEMS.orderedRemove(i);
        }
        for(int i = 0; i < LOGIC_SYSTEMS.getLength() && wrapper == null; ++i) {
            if(LOGIC_SYSTEMS.get(i).SYSTEM_ID == systemID) wrapper = LOGIC_SYSTEMS.orderedRemove(i);
        }
        for(int i = 0; i < RENDER_SYSTEMS.getLength() && wrapper == null; ++i) {
            if(RENDER_SYSTEMS.get(i).SYSTEM_ID == systemID) wrapper = RENDER_SYSTEMS.orderedRemove(i);
        }
    }

    private void addSystemWrapper(SystemWrapper wrapper, Array<SystemWrapper> systems) {
        systems.binaryInsert(
                wrapper,
                (SystemWrapper a, SystemWrapper b) -> Integer.compare(b.PRIORITY, a.PRIORITY)
        );
    }



    private static class SystemWrapper {

        final System SYSTEM;
        final int PRIORITY;
        final int SYSTEM_ID;
        final SystemType TYPE;
        boolean isAwake;

        SystemWrapper(System system, int priority, int systemID, SystemType type) {
            SYSTEM = system;
            PRIORITY = priority;
            SYSTEM_ID = systemID;
            TYPE = type;
            isAwake = true;
        }

    }

    private static class DispatchedEvent {

        final Event EVENT;
        final int PRIORITY;
        final System[] RECIPIENTS;

        DispatchedEvent(Event event, int priority, System... recipients) {
            EVENT = event;
            PRIORITY = priority;
            RECIPIENTS = recipients;
        }

    }

}
