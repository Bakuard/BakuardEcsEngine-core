package com.bakuard.ecsEngine.exceptions;

import com.bakuard.ecsEngine.core.EntityCommandBuffer;

/**
 * Непроверяемое исключение, которое может быть выброшено при выполнении метода
 * {@link com.bakuard.ecsEngine.core.EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}.
 * Данное исключение возникает в случае, если метод {@link ExceptionHandler#handle(Exception)} сгенерировал
 * исключение, чтобы прервать выполнение пакетной операции (метод flushBuffer). При этом, сгенерированное исключение
 * методом {@link ExceptionHandler#handle(Exception)} будет указано как причина для исключения типа BatchException.
 * Фактически, BatchException является оберткой над исключением генериуремым {@link ExceptionHandler#handle(Exception)}.
 * Такое решение было принято, поскольку выше указанный метод генерирует проверяемое исключение, и чтобы избавить
 * клиентский код от необходимости каждый раз оборачивать метод
 * {@link com.bakuard.ecsEngine.core.EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}
 * в блок try-catch, было принято обернуть проверяемое исключение в непроверяемое.
 */
public final class BatchException extends RuntimeException {

    /**
     * Создает объект исключение BatchException.
     */
    public BatchException() {}

    /**
     * Создает объект исключение BatchException с указанным сообщением.
     * @param message сообщение исключения.
     */
    public BatchException(String message) {
        super(message);
    }

    /**
     * Создает исключение типа BatchException с исключением указанным в качестве причины возникновения BatchException.
     * @param cause причина исключения.
     */
    public BatchException(Throwable cause) {
        super(cause);
    }

    /**
     * Создает исключение типа BatchException с исключением указанным в качестве причины возникновения BatchException
     * и с указанным сообщением.
     * @param message сообщение исключения.
     * @param cause причина исключения.
     */
    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }

}
