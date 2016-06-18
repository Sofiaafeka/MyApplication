package com.jobSearchApp.android.ServiceModels;

public class SeekerInfo {

    public  String Id;
    public   String FullName;
    public InterviewInfo InterviewInfo;

    @Override
    public String toString() {
        return FullName;
    }
}
