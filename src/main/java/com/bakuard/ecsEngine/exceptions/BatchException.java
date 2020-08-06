package com.bakuard.ecsEngine.exceptions;

import com.bakuard.ecsEngine.core.EntityCommandBuffer;

/**
 * Непроверяемое исключение, которое может быть выброшено при выполнении метода
 * {@link com.bakuard.ecsEngine.core.EntityComponentManager#flushBuffer(EntityCommandBuffer, ExceptionHandler)}.
 * Данное исключение генерируется методом {@link ExceptionHandler#handle(Exception)}, если возникла необходимость
 * прервать пакетную операцию.
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
