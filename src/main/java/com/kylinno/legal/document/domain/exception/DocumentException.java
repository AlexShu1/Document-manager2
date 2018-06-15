package com.kylinno.legal.document.domain.exception;

public class DocumentException extends Exception {


    /**
     * 错误消息
     */
    private String message;

    public DocumentException() {
        super();
    }

    public DocumentException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
