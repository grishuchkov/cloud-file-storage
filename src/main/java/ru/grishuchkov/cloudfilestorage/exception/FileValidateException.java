package ru.grishuchkov.cloudfilestorage.exception;

public class FileValidateException extends AppException{
    public FileValidateException() {
        super();
    }

    public FileValidateException(String message) {
        super(message);
    }

    public FileValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileValidateException(Throwable cause) {
        super(cause);
    }
}
