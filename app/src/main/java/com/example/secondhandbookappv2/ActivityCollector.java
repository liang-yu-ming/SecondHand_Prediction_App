package com.example.secondhandbookappv2;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishOneActivity(String activityName){
        //在activities集合中找到類名與指定類名相同的Activity就關閉
        for (Activity activity : activities){
            String name= activity.getClass().getName();//activity的包名+類名
            if(name.equals(activityName)){
                if(activity.isFinishing()){
                    //當前activity如果已經Finish，則只從activities清除就好了
                    activities.remove(activity);
                } else {
                    //沒有Finish則Finish
                    activity.finish();
                }
            }
        }
    }

}
