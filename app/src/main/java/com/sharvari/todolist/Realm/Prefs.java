package com.sharvari.todolist.Realm;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sharvari on 22-Jan-18.
 */

public class Prefs{

    private static final String PRE_LOAD = "preLoad";
    private static final String PREFS_NAME = "sharvari";
    private static Prefs instance;
    private final SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context context){
        if(instance == null){
            instance = new Prefs(context);
        }
        return instance;
    }

    public void setPreLoad(boolean totalTime){
        sharedPreferences.edit().putBoolean(PRE_LOAD, totalTime).apply();
    }

    public boolean getPreLoad(){
        return sharedPreferences.getBoolean(PRE_LOAD, false);
    }
}
