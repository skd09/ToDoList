package com.sharvari.todolist.Realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import com.sharvari.todolist.ToDoList;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sharvari on 22-Jan-18.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        this.realm = Realm.getDefaultInstance();
    }

    public static RealmController with(android.support.v4.app.Fragment fragment){
        if(instance == null){
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }
    public static RealmController with(Activity activity){
        if(instance == null){
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }
    public static RealmController with(Application application){
        if(instance == null){
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance(){
        return instance;
    }
    public Realm getRealm(){
        return realm;
    }

    public void refresh(){
        realm.refresh();
    }

    public void clearAll(){
        realm.beginTransaction();
        realm.delete(ToDoList.class);
        realm.commitTransaction();
    }

    public RealmResults<ToDoList> getLists(){
        return realm.where(ToDoList.class).findAll();
    }

    public boolean removeListItem(int position){

        RealmResults<ToDoList> results = realm.where(ToDoList.class).findAll();
        results.get(position).deleteFromRealm();
        return true;
    }

    public RealmResults<ToDoList> filterByDate(Date fromDate, Date toDate){
        RealmResults<ToDoList> results = realm.where(ToDoList.class)
                .greaterThanOrEqualTo("reminderDate",fromDate)
                .lessThan("reminderDate",toDate)
                .findAll();

        return results;
    }
}
