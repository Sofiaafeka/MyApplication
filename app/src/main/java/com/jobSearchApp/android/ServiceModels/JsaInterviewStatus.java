package com.jobSearchApp.android.ServiceModels;

public enum JsaInterviewStatus {
    NONE(0), PENDING(1), REJECTED(2), ACCEPTED(3);

    private final int id;

    JsaInterviewStatus(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
