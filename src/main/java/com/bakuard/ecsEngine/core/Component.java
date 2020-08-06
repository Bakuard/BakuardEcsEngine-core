package com.bakuard.ecsEngine.core;

/**
 * Представляет собой базовый класс для всех компонентов. Компонент - это просто "мешок с данными". Компоненты
 * не должны содержать логику (исключением должен являться только наследуемый метод {@link #destruct()}. Это правило
 * желательно соблюдать, однако его несоблюдение не повлияет на работу игрового движка).
 * <br/><br/>
 * Все данные каждого условного игрового объекта представляют собой исключительно набор компонентов. Сами же игровые
 * объекты - это множество компонентов логически связанных между собой через одну и туже сущность (см. {@link Entity}).
 * <br/><br/>
 * Каждый компонент обязательно должен быть связан с одной и только одной сущностью. Сущность может быть связана с
 * нулем и более компонентами. Сущность, с которой связан компонент, называется сущностью-владельцем данного
 * компонента. Сущность-владелец может быть назначена компоненту только один раз при его создании. Сущность может
 * быть связана с несколькими компонентами одного типа.
 */
public abstract class Component {

    //Добавляет компонент added в начало списка и возвращает его в качестве первого элемента.
    static Component add(Component head, Component added) {
        added.next = head;
        return added;
    }

    /*
    * Удаляет компонент removed и возвращает первый элемент списка. Учитывая проверки в классе
    * EntityComponentManager, данный метод полагается на то, что removed присутсвует в списке.
     */
    static Component remove(Component head, Component removed) {
        String label = removed.label;
        Component previous = null;
        Component current = head;

        while(current != null && !label.equals(current.label)) {
            previous = current;
            current = current.next;
        }

        if(current == head) head = current.next;
        else previous.next = current.next;

        current.next = null;

        return head;
    }

    static Component findByLabel(Component head, String label) {
        Component current = head;
        while(current != null && !label.equals(current.label)) {
            current = current.next;
        }
        return current;
    }

    static void detachAll(Component head) {
        while(head != null) {
            Component current = head;
            head = head.next;
            current.unbind();
        }
    }

    static int getCountComponent(Component head) {
        int countComponent = 0;
        while(head != null) {
            ++countComponent;
            head = head.next;
        }
        return countComponent;
    }



    private final int TYPE_ID;
    private final Entity OWNER;
    private boolean isBind;
    private String label;
    private Component next; //Все однотипные компоненты одной сущности хранятся в виде односвязного списка.

    /**
     * Используется для назначения компоненту сущности-владельца и идентификатора типа компонента. Важно отметить,
     * что после назначения сущности-владельца компоненту, компонент не будет сразу же связан со своим владельцем.
     * Для фактического установления связи между компонентом и сущностью-владельцем необходимо вызвать метод:
     * {@link EntityComponentManager#bind(Component)} или {@link EntityComponentManager#bind(Component...)}
     * @param owner сущность-владелец компонента.
     * @param typeID идентификатор типа компонента.
     * @throws IllegalArgumentException если идентифткатор типа компонента меньше нуля.
     * @throws NullPointerException если в качестве сущности-владельца указано значение null.
     */
    public Component(Entity owner, int typeID) {
        if(typeID < 0) {
            throw new IllegalArgumentException("ID типа компонента не должно быть меньше нуля. Получено " + typeID);
        } else if(owner == null) {
            throw new NullPointerException("Владелец компонента не может равняться null.");
        } else {
            OWNER = owner;
            TYPE_ID = typeID;
            label = "";
        }
    }

    /**
     * Вызывается в момент удаления сущности-владельца данного компонента. Данный метод может быть
     * использован, например, чтобы удалить компонент из дополнительной структуры данных используемой
     * для его хранения. Данный метод будет вызван у всех компонентов удаляемой сущности, до того, как связь
     * между сущностью и её компонентами будет фактически разорвана.
     */
    public abstract void destruct();

    /**
     * Возвращает идентификатор типа компонента.
     * @return идентификатор типа компонента.
     */
    public final int getTypeID() {
        return TYPE_ID;
    }

    /**
     * Возвращает сущность-владельца данного компонента.
     * @return сущность-владелец данного компонента.
     */
    public final Entity getOwner() {
        return OWNER;
    }

    /**
     * Возвращает true, если компонент связан с его сущностью-владельцем на момент вызова этого метода, иначе - false.
     * @return true, если компонент связан с его сущностью-владельцем, иначе - false.
     */
    public final boolean isBind() {
        return isBind;
    }

    /**
     * Возвращает метку данного компонента. Метки используются чтобы различать между собой несколько однотипных
     * компонентов связанных с одной сущностью.
     * @return метка данного компонента.
     */
    public final String getLabel() {
        return label;
    }

    /**
     * Задает метку для данного компонента. Метки используются чтобы различать между собой несколько однотипных
     * компонентов связанных с одной сущностью
     * @param label метка данного компонента.
     */
    public final void setLabel(String label) {
        this.label = label == null ? "" : label;
    }

    final Component getNext() {
        return next;
    }

    final void bind() {
        isBind = true;
    }

    final void unbind() {
        isBind = false;
        next = null;
    }

}
