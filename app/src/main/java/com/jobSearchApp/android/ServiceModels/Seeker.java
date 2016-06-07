package com.jobSearchApp.android.ServiceModels;

import java.util.Date;
import java.util.List;

public class Seeker{

    public Seeker() {
        YearOfBirth = 1990;
    }

    public  String FirstName;
    public   String LastName;
    public    int YearOfBirth;
    public  boolean Matriculation;
    public   String Degree;
    public  String FieldOfStudy;
    public String DesiredPosition;
    public Long AvailableFrom;
    public   boolean  FullTime;
    public   boolean IsMobile;
    public String URL;
    public String SalaryRanges;
    public List<SkillExperience> SkillExperiences;
    public LocationModel HomeLocation;
    public LocationModel SeekArea;
    public InterviewInfo InterviewInfo;
}
