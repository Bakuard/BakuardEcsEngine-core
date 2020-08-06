package com.bakuard.ecsEngine.exceptions;

/**
 * Интерфейс определяющий метод последовательной обработки определенного типа ошибок, которые могут возникнуть
 * при выполнении пакетоной операции.
 * @param <T> тип обрабатываемого исключения.
 */
@FunctionalInterface
public interface ExceptionHandler<T extends Exception> {

    /**
     * Данный метод вызывается каждый раз при возникновении исключения входе выполнения пакетной операции.
     * Возникшее исключение передается этому методу в качестве аргумента. Данный метод может выбросить исключение
     * {@link BatchException}, если необходиом прервать выполнение пакетной операции.
     * @param exception обрабатываемое исключение.
     * @throws BatchException исключение сигнализирующее о прерывании пакетной операции.
     */
    public void handle(T exception) throws BatchException;

}
