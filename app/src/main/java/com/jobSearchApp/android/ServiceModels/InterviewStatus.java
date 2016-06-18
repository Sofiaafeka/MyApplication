package com.jobSearchApp.android.ServiceModels;

public enum InterviewStatus {
    NONE(0), PENDING(1), REJECTED(2), ACCEPTED(3);

    private final int id;

    InterviewStatus(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public static InterviewStatus fromInteger(int x) {
        switch(x) {
            case 0:
                return NONE;
            case 1:
                return PENDING;
            case 2:
                return  REJECTED;
            case 3:
                return ACCEPTED;
        }
        return null;
    }
}
