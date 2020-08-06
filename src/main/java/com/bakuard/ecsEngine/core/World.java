package com.bakuard.ecsEngine.core;

/**
 * Данный класс представляет собой игровой мир. Каждый игровой мир представляет собой отдельную и независимую
 * партию игры. Отдельный игровой мир хранит все данные связанные с одной конкретной игровой партией. Каждый
 * игровой мир хранит свой менеджер сущностей и компонентов, менеджер систем, а также объект предоставляющий управление
 * игровым циклом.
 */
public final class World {

    private final EntityComponentManager ENTITY_COMPONENT_MANAGER;
    private final SystemManager SYSTEM_MANAGER;
    private final GameLoop GAME_LOOP;

    private World(Builder builder) {
        ENTITY_COMPONENT_MANAGER = new EntityComponentManager();
        SYSTEM_MANAGER = new SystemManager(builder.numberPorts);
        GAME_LOOP = new GameLoop(builder.numberUpdatePerSecond, builder.maxFrameSkip, SYSTEM_MANAGER);
    }

    /**
     * Возвращает менеджер компонентов и сущностей представляющих все игровые объекты данного игрового мира.
     * @return менеджер компонентов и сущностей.
     */
    public EntityComponentManager getEntityCompManager() {
        return ENTITY_COMPONENT_MANAGER;
    }

    /**
     * Возвращает менджер систем представляющего все системы данного игрового мира.
     * @return менджер систем.
     */
    public SystemManager getSystemManager() {
        return SYSTEM_MANAGER;
    }

    /**
     * Возвращает персональный игровой цикл данного игрового мира.
     * @return персональный игровой цикл данного игрового мира.
     */
    public GameLoop getGameLoop() {
        return GAME_LOOP;
    }


    /**
     * Объекты данного класса используются для предварительной настройки и создания объектов {@link World}. Все
     * настраиваемые параметры и их значения по умолчанию представлены ниже: <br/>
     * 1. частота обновлений игровой логики в секунду - 30 <br/>
     * 2. максимальное кол-во дополнительных обновлений игровой логики - 10 <br/>
     * 3. Кол-во портов для событий систем - 256. <br/>
     * Подробная информация для каждого параметра представлена в API к методу класса Builder задающего значение этого
     * параметра.
     */
    public static final class Builder {

        int numberUpdatePerSecond;
        int maxFrameSkip;
        int numberPorts;

        /**
         * Создает объект типа Builder с настройками по умолчанию.
         */
        public Builder() {
            numberUpdatePerSecond = 30;
            maxFrameSkip = 10;
            numberPorts = 256;
        }

        /**
         * Настройки для игрового цикла (см. {@link GameLoop}). Устанавливает предпочтительное кол-во обновлений всей
         * игровой логики в секунду. Фактическое кол-во обновлений игровой логики гарантированно не будет больше
         * заданного значения, но может быть меньше его.
         *
         * @param numberUpdatePerSecond кол-во обновлений всей игровой логики в секунду.
         * @return ссылку на этот же объект Builder.
         */
        public Builder setNumberUpdatePerSecond(int numberUpdatePerSecond) {
            this.numberUpdatePerSecond = numberUpdatePerSecond;
            return this;
        }

        /**
         * Настройки для игрового цикла (см. {@link GameLoop}). В случае, если игровой цикл не успевает обработать
         * игровую логику с задананной частотой, он будет пытаться на каждом своем шаге выполнить несколько
         * дополнительных обновлений игровой логики, чтобы нагнать отставание от заданной частоты. Однако если игровой
         * цикл будет стабильно отставать от заданной частоты при каждом обновлении игровой логики, то кол-во
         * дополнительных обновлений игровой логики, которые ему предстоит сделать на следующем шаге будет постоянно
         * расти. Данный метод задает максимальное кол-во дополнительных обновлений игровой логики на каждом
         * шаге игрового цикла, которые тот может выполнить в случае отставания от заданной частоты обновлений.
         * @param maxFrameSkip максимальное кол-во дополнительных обновлений игровой логики на каждом шаге
         *                     игрового цикла.
         * @return ссылку на этот же объект Builder.
         */
        public Builder setMaxFrameSkip(int maxFrameSkip) {
            this.maxFrameSkip = maxFrameSkip;
            return this;
        }

        /**
         * Устаналивает кол-во портов для записи событий систем (подробнее см.
         * {@link SystemManager#putEvent(int, Event)}).
         * @param numberPorts кол-во портов для записи событий систем.
         * @return ссылку на этот же объект Builder.
         */
        public Builder setNumberPorts(int numberPorts) {
            this.numberPorts = numberPorts;
            return this;
        }

        /**
         * Создает и возвращает объект World.
         * @return объект World.
         */
        public World build() {
            return new World(this);
        }

    }

}
