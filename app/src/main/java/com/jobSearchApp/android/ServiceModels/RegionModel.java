package com.jobSearchApp.android.ServiceModels;

import java.util.List;

/**
 * Created by סופי on 23/05/2016.
 */
public class RegionModel {
    public String Name;
    public List<RegionModel> SubRegions;

    public RegionModel(){
    }

    public RegionModel(String name) {
        Name = name;
    }

    @Override
    public String toString(){
        return Name;
    }
}
