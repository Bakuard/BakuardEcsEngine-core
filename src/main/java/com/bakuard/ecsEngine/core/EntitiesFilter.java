package com.bakuard.ecsEngine.core;

import com.bakuard.ecsEngine.utils.Bits;

/**
 * Данный интерфейс используется для фильтрации сущностей при итерации по ним.
 */
public interface EntitiesFilter {

    /**
     * Данный метод используется для фильтрации сущностей по их набору компонентов. Возвращает true, если
     * идентификаторы типов компонентов указанные в componentTypes (индекс каждого бита установленного в единицу -
     * соответсвует идентификатору типа сущности) удовлетворяют некоторому условию определяемомму реализацией
     * данного интерфейса.
     * @param componentTypes идентификаторы типов компонентов для
     * @return true, если идентификаторы тпов компонентов соответсвуют некоторому условию, иначе - false.
     */
    public boolean isValidComponentTypes(Bits componentTypes);

    /**
     * Данный метод используется для фильтрации сущностей по их типам. Возвращает true, если идентификатор типа
     * сущности соответствует заданному условию, иначе - false.
     * @param entityTypeID идентификатор типа сущности.
     * @return true, если идентификатор типа сущности соответствует заданному условию, иначе - false.
     */
    public boolean isValidEntityType(int entityTypeID);

}
