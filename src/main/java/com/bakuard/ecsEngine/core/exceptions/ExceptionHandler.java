package com.bakuard.ecsEngine.core.exceptions;

/**
 * Интерфейс определяющий метод последовательной обработки определенного типа ошибок, которые могут возникнуть
 * при выполнении пакетоной операции.
 * @param <T> тип обрабатываемого исключения.
 */
@FunctionalInterface
public interface ExceptionHandler<T extends Exception> {

    /**
     * Данный метод вызывается каждый раз при возникновении исключения входе выполнения пакетной операции.
     * Возникшее исключение передается этому методу в качестве аргумента. Чтобы прервать выполнение
     * пакетной операции, достаточно пробросить полученное исключение или выбросить новое.
     * @param exception обрабатываемое исключение.
     * @throws Exception исключение сигнализирующее о прерывании пакетной операции.
     */
    public void handle(T exception) throws Exception;

}
