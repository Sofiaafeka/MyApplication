package com.jobSearchApp.android.ServiceModels;


public class Skill {
    public int Id;
    public String Name;

    public Skill(){
    }

    public Skill(int id, String name) {
        Id = id;
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }

    @Override
    public boolean equals(Object o) {
        return Id ==((Skill)o).Id;
    }
}
