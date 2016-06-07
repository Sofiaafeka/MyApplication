package com.jobSearchApp.android.ServiceModels;

public enum JsaUserType {
    SEEKER(1), EMPLOYER(2);

    private final int id;

    JsaUserType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
