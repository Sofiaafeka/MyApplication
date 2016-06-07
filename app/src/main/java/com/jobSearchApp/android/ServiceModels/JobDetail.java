package com.jobSearchApp.android.ServiceModels;

import java.util.ArrayList;
import java.util.List;


public class JobDetail {

    public JobDetail() {
        SkillExperiences = new ArrayList<>();
    }

    public  int Id;
    public  String Name, Description, JobPosition;
    public LocationModel Location;
    public List<SkillExperience> SkillExperiences;
    public InterviewInfo InterviewInfo;
}
