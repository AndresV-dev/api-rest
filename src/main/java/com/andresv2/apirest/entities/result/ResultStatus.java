package com.andresv2.apirest.entities.result;

public enum ResultStatus {
    FAILED("FAILED"),
    OK("OK"),
    EMPTY("EMPTY"),
    UNDEFINED("UNDEFINED");

    private String type;

    ResultStatus(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
