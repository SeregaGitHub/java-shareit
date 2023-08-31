package ru.practicum.shareit.util;

public class RequestParamError {
    private String error;

    public RequestParamError(String errorMessage) {
        this.error = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
