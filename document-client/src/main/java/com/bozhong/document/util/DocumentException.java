package com.bozhong.document.util;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public class DocumentException extends RuntimeException {
    private String errorCode;

    private String errorMessage;

    private String detailErrorMessage;

    public DocumentException(String errorCode, String errorMessage, String detailErrorMessage, Throwable e) {
        super(errorCode, e);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.detailErrorMessage = detailErrorMessage;
    }

    public DocumentException(String errorCode, String errorMessage, String detailErrorMessage) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.detailErrorMessage = detailErrorMessage;
    }

    public DocumentException(String errorCode, String errorMessage, Throwable e) {
        super(errorCode, e);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DocumentException(String errorCode, String errorMessage) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DocumentException(String errorCode, Throwable e) {
        super(errorCode, e);
        this.errorCode = errorCode;
    }

    public DocumentException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }
}
