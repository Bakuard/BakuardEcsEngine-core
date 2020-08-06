package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.utils.Array;
import com.bakuard.ecsEngine.utils.IntArray;
import com.bakuard.ecsEngine.utils.IntMap;

/**
 * Управляет всеми системами в игровом мире, а также позволяет системам общаться между собой используя
 * систему событий. Каждый игровой мир имеет свой менеджер систем. Попытка работать с одними и теми же
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
    private final IntMap<ListenersGroup> LISTENERS;

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
        LISTENERS = new IntMap<>();
    }

    /**
     * Данный метод добавляет указанную систему ввода в список обрабатывамых систем ввода. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий
     * (подробнее см. {@link GameLoop}).
     * <br/><br/>
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем ввода. Чем больше значение priority - тем раньше будет обрабатываться система на каждом шаге игрового
     * цикла.
     * <br/><br/>
     * В момент фактического добавления системы может быть сгенерировано исключение IllegalArgumentException по
     * одной из следующих причин: <br/>
     * 1. Если указанный systemID не является уникальным среди всех систем ввода, логики и рендера. <br/>
     * 2. Указанный priority не является уникальным среди приоритетов других систем ввода.
     * @param system добавляемая система ввода.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     */
    public void addInputSystem(System system, int priority, int systemID) {
        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.INPUT));
    }

    /**
     * Данный метод добавляет указанную систему логики в список обрабатывамых систем логики. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий
     * (подробнее см. {@link GameLoop}).
     * <br/><br/>
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем логики. Чем больше priority - тем раньше будет обрабатываться система на каждом шаге игрового цикла.
     * <br/><br/>
     * В момент фактического добавления системы может быть сгенерировано исключение IllegalArgumentException по
     * одной из следующих причин: <br/>
     * 1. Если указанный systemID не является уникальным среди всех систем ввода, логики и рендера. <br/>
     * 2. Указанный priority не является уникальным среди приоритетов других систем логики.
     * @param system добавляемая система игровой логики.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     */
    public void addLogicSystem(System system, int priority, int systemID) {
        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.LOGIC));
    }

    /**
     * Данный метод добавляет указанную систему рендера в список обрабатывамых систем рендера. Фактическое добавление
     * указанной системы произойдет только в самом начале следующего шага игрового цикла перед рассылкой событий.
     * (подробнее см. {@link GameLoop}).
     * <br/><br/>
     * Задаваемый приориет системы влияет на порядок в котором будет обработана данная система относительно других
     * систем рендера. Чем больше priority - тем раньше будет обрабатываться система на каждом шаге игрового цикла.
     * <br/><br/>
     * В момент фактического добавления системы может быть сгенерировано исключение IllegalArgumentException по
     * одной из следующих причин: <br/>
     * 1. Если указанный systemID не является уникальным среди всех систем ввода, логики и рендера. <br/>
     * 2. Указанный priority не является уникальным среди приоритетов других систем рендера.
     * @param system добавляемая система рендера.
     * @param priority приоритет добавляемой системы.
     * @param systemID уникальный идентефикатор системы.
     */
    public void addRenderSystem(System system, int priority, int systemID) {
        ADDED_SYSTEMS.add(new SystemWrapper(system, priority, systemID, SystemType.RENDER));
    }


    /**
     * Удаляет систему с указанным идентификатором из очереди обрабатываемых систем и из всех подписок на получение
     * событий. Фактическое удаление системы произойдет только в начале следующего шага игрового цикла перед
     * рассылкой событий (подробнее см. {@link GameLoop}). Если
     * <br/><br/>
     * В момент фактического удаления системы может быть сгенерировано исключегие IllegalArgumentException, если
     * система с указанным идентификатором отсутсвует в списке обрабатываемых систем ввода, логики или рендера.
     * @param systemID идентификатор удаляемой системы.
     */
    public void removeSystem(int systemID) {
        DELETED_SYSTEMS.add(systemID);
    }


    /**
     * Возвращает систему имеющую указанный идентификатор или null, если среди обрабатываемых в данный момент
     * систем нет системы с таким идентификатором.
     * @param systemID идентификатор системы.
     * @return систему имеющую указанный идентификатор или null.
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
     * <br/><br/>
     * Данный метод можно вызвать только для системы находящихся на момент вызова этого метода в списке
     * обрабатываемых систем ввода, логики или рендера.
     * <br/><br/>
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
     * с помощью вызова метода {@link #getAndRemoveEvent(int)}.
     * <br/><br/>
     * Данный метод следует использовать в случае, когда одной системе необходимо оповестить другую систему
     * обрабатывающуюся после неё на текущем шаге игрового цикла. Событие размещенное на указанном порту
     * с помощью данного метода сразу же будет достпуно с помощью методов {@link #getEvent(int)} и
     * {@link #getAndRemoveEvent(int)}.
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
     * Возвращает событие размещенное на указанном порту и освобождает порт. Если указанный порт свободен,
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
     * Добавляет указанное событие в очередь событий. События из данной очереди будут разосланы получателям в
     * порядке убывания приоритета событий (подробнее см. {@link DispatchedEvent}), а получатели получат их в
     * порядке заданном пользователем подробнее см. {@link #addEventListener(int, int, int)}). Рассылка событий будет
     * осуществлена в начале следующего шага цикла, а очередь опустошена.
     * <br/><br/>
     * Данный метод позволяет добавить один и тот же объект события только один раз на каждом шаге цикла и требует,
     * чтобы указываемое значение приоритета было уникально среди всех других событий находящихся в очереди событий
     * на момент вызова этого метода.
     * @param event отправляемое событие.
     * @throws NullPointerException если event имеет значение null.
     * @throws IllegalArgumentException Данное исключение выбрасывается в одном из 2 случаев: <br/>
     *                                  1. Указываемый объект события уже добавлялся в очередь событий на данном
     *                                     шаге цикла. <br/>
     *                                  2. В очереди событий уже есть событие с указанным приоритетом.
     */
    public void sendEvent(DispatchedEvent event) {
        if(event == null) {
            throw new NullPointerException("Значение event не может быть null.");
        }

        for(int i = 0; i < eventsBuffer.getLength(); ++i) {
            DispatchedEvent eventInQueue = eventsBuffer.get(i);
            if(eventInQueue == event) {
                throw new IllegalArgumentException(
                        "Указываемый объект события уже добавлялся в очередь событий на данном шаге цикла."
                );
            } else if(eventInQueue.getPriority() == event.getPriority()) {
                throw new IllegalArgumentException(
                        "В очереди событий уже есть событие с указанным приоритетом = " + eventInQueue.getPriority()
                );
            }
        }

        eventsBuffer.binaryInsert(
                event,
                (DispatchedEvent a, DispatchedEvent b) -> Integer.compare(b.getPriority(), a.getPriority())
        );
    }

    /**
     * Подписывает указанную систему на прослушивание указанных типов событий.
     * <br/><br/>
     * listenerPriority - значение указывающее место в очереди на получение указанного типа событий. Чем выше
     * значение listenerPriority, тем раньше система будет получать события указанного типа относительно других
     * систем прослушивающих собфтия этого же типа.
     * <br/><br/>
     * Данный метод не позволяет подписать одну и туже систему на один и то же тип событий более одного раза и
     * требует, чтобы значение listenerPriority было уникально среди всех систем подписанных на указанный тип
     * события, а система с указанным идентификатором находилась в очереди обрабатываемых систем на момент
     * вызова этого метода.
     * @param systemID идентификатор системы подписываемой на рассылку соыбтия указанного типа.
     * @param listenerPriority приоритет определяющий место в очереди на получение указанного типа событий.
     * @param eventTypeID идентификатор типа событий.
     * @throws IllegalArgumentException генериурет по одной из следующих причин: <br/>
     *                                  1. Система с указанным идентификатором уже подписана на события указанного
     *                                     типа. <br/>
     *                                  2. Среди систем подписанных на события указанного типа уже есть система
     *                                     с указанным приоритетом получения событий этого типа. <br/>
     *                                  3. Система с указанным идентификатором не находится в очереди обрабатываемых
     *                                     систем на момент вызова этого метода.
     */
    public void addEventListener(int systemID, int listenerPriority, int eventTypeID) {
        ListenersGroup listeners = LISTENERS.get(eventTypeID);
        if(listeners == null) {
            listeners = new ListenersGroup();
            LISTENERS.put(eventTypeID, listeners);
        }

        SystemWrapper wrapper = getSystemWrapper(systemID);
        if(wrapper == null) {
            throw new IllegalArgumentException(
                    "Система с идентификатором " + systemID + " не находится в очереди обрабатываемых систем " +
                            "на момент вызова этого метода."
            );
        }

        listeners.addListener(wrapper, listenerPriority, eventTypeID);
    }

    /**
     * Отписывает указанную систему от получения событий указанного типа. Если на момент вызова этого метода
     * указанная система не подписана на получение событий указанного типа - метод ничего не делает. Данный метод
     * требует, чтобы система с указанным идентификатором на момент его вызова находилась в списке обрабатываемых
     * систем.
     * @param systemID идентификатор системы отписываемой от получения событий указанного типа.
     * @param eventTypeID идентификатор типа событий.
     * @throws IllegalArgumentException если указаная систем ане находистя в списке обрабатываемых систем.
     */
    public void removeEventListener(int systemID, int eventTypeID) {
        if(getSystemWrapper(systemID) == null) {
            throw new IllegalArgumentException(
                    "Система с идентификатором " + systemID + " отсутсвует в списке обрабатываемых систем."
            );
        }

        ListenersGroup listeners = LISTENERS.get(eventTypeID);
        if(listeners != null) listeners.removeListener(systemID);
    }


    void changeSystemsList() {
        for(int i = 0; i < ADDED_SYSTEMS.getLength(); ++i) {
            SystemWrapper wrapper = ADDED_SYSTEMS.get(i);
            if(getSystemWrapper(wrapper.SYSTEM_ID) != null) {
                throw new IllegalArgumentException(
                        "Система с идентификатором " + wrapper.SYSTEM_ID +
                                " уже находится в списке обрабатываемых систем."
                );
            }

            if(wrapper.TYPE == SystemType.INPUT) {
                if(INPUT_SYSTEMS.binarySearch(wrapper, SystemWrapper::compareTo) != -1) {
                    throw new IllegalArgumentException(
                            "Среди систем ввода уже есть система с приоритетом " + wrapper.PRIORITY
                    );
                }
                INPUT_SYSTEMS.binaryInsert(wrapper, SystemWrapper::compareTo);
            } else if(wrapper.TYPE == SystemType.LOGIC) {
                if(LOGIC_SYSTEMS.binarySearch(wrapper, SystemWrapper::compareTo) != -1) {
                    throw new IllegalArgumentException(
                            "Среди систем логики уже есть система с приоритетом " + wrapper.PRIORITY
                    );
                }
                LOGIC_SYSTEMS.binaryInsert(wrapper, SystemWrapper::compareTo);
            } else if(wrapper.TYPE == SystemType.RENDER) {
                if(RENDER_SYSTEMS.binarySearch(wrapper, SystemWrapper::compareTo) != -1) {
                    throw new IllegalArgumentException(
                            "Среди систем рендера уже есть система с приоритетом " + wrapper.PRIORITY
                    );
                }
                RENDER_SYSTEMS.binaryInsert(wrapper, SystemWrapper::compareTo);
            }
        }

        for(int i = DELETED_SYSTEMS.getLength(); i >= 0; --i) {
            boolean notFindDeletedSystem = true;

            int systemID = DELETED_SYSTEMS.get(i);

            for(int j = 0; j < INPUT_SYSTEMS.getLength() && notFindDeletedSystem; ++j) {
                if(INPUT_SYSTEMS.get(j).SYSTEM_ID == systemID) {
                    INPUT_SYSTEMS.orderedRemove(i);
                    notFindDeletedSystem = false;
                }
            }
            for(int j = 0; j < LOGIC_SYSTEMS.getLength() && notFindDeletedSystem; ++j) {
                if(LOGIC_SYSTEMS.get(j).SYSTEM_ID == systemID) {
                    LOGIC_SYSTEMS.orderedRemove(i);
                    notFindDeletedSystem = false;
                }
            }
            for(int j = 0; j < RENDER_SYSTEMS.getLength() && notFindDeletedSystem; ++j) {
                if(RENDER_SYSTEMS.get(j).SYSTEM_ID == systemID) {
                    RENDER_SYSTEMS.orderedRemove(i);
                    notFindDeletedSystem = false;
                }
            }

            if(notFindDeletedSystem) {
                throw new IllegalArgumentException(
                        "Система с идентификатором " + systemID + " не может быть удалена, т.к. отсутсвует " +
                                "в списке обрабатываемых систем."
                );
            } else {
                LISTENERS.forEach((IntMap.Node<ListenersGroup> listeners) ->
                        listeners.getValue().removeListener(systemID));
            }
        }
    }

    void sendEvents() {
        Array<DispatchedEvent> temp = eventsBuffer;
        eventsBuffer = eventsList;
        eventsList = temp;

        for(int i = 0; i < eventsList.getLength(); ++i) {
            DispatchedEvent event = eventsList.get(i);
            ListenersGroup listeners = LISTENERS.get(event.getTypeID());
            if(listeners != null) listeners.notifyListeners(event);
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


    private final static class SystemWrapper implements Comparable<SystemWrapper> {

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

        @Override
        public int compareTo(SystemWrapper other) {
            if(other == null) throw new NullPointerException();
            return Integer.compare(other.PRIORITY, PRIORITY);
        }

    }

    private static final class ListenersGroup {

        private final IntArray PRIORITIES;
        private final Array<SystemWrapper> LISTENERS;

        ListenersGroup() {
            PRIORITIES = new IntArray(0);
            LISTENERS = new Array<>(SystemWrapper.class, 0);
        }

        void addListener(SystemWrapper listener, int listenerPriority, int eventTypeID) {
            for(int i = 0; i < LISTENERS.getLength(); ++i) {
                if(LISTENERS.get(i) == listener) {
                    throw new IllegalArgumentException(
                            "Система с идентификатором " + listener.SYSTEM_ID +
                                    " уже подписана на события типа " + eventTypeID
                    );
                } else if(PRIORITIES.get(i) == listenerPriority) {
                    throw new IllegalArgumentException(
                            "Среди систем подписанных на события указанного типа уже есть система " +
                                    "с указанным приоритетом получения событий. Получено " + listenerPriority
                    );
                }
            }

            LISTENERS.insert(
                    PRIORITIES.binaryInsert(listenerPriority, (int a, int b) -> Integer.compare(b, a)),
                    listener
            );
        }

        void removeListener(int systemID) {
            for(int i = 0; i < LISTENERS.getLength(); ++i) {
                if(LISTENERS.get(i).SYSTEM_ID == systemID) {
                    LISTENERS.orderedRemove(i);
                    PRIORITIES.orderedRemove(i);
                    break;
                }
            }
        }

        void notifyListeners(DispatchedEvent event) {
            for(int i = 0; i < LISTENERS.getLength(); ++i) {
                SystemWrapper wrapper = LISTENERS.get(i);
                if(wrapper.isAwake) wrapper.SYSTEM.listenEvent(event);
            }
        }

    }

}
