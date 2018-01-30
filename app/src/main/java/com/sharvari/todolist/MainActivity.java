package com.sharvari.todolist;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.sharvari.todolist.Realm.Prefs;
import com.sharvari.todolist.Realm.RealmController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RecyclerViewItemClickListener,
        RecyclerViewTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recyclerView;
    private ToDoListAdapter adapter;
    private Realm realm;
    private ArrayList<ToDoList> toDoList = new ArrayList<>();
    private LayoutInflater inflater;
    private String date = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.realm = RealmController.with(this).getRealm();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            recyclerViewData();
        }else{
            RealmController.with(this).refresh();
            toDoList.addAll(RealmController.with(this).getLists());
            adapter.notifyDataSetChanged();
        }

        ItemTouchHelper.SimpleCallback touchHelper = new RecyclerViewTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick(view);
            }
        });

        final SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toDoList.removeAll(toDoList);
                RealmController.with(MainActivity.this).refresh();
                toDoList.addAll(RealmController.with(MainActivity.this).getLists());
                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onFilterClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onFilterClick(){
        final Calendar myCalendar = Calendar.getInstance();

        inflater = MainActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_filter, null);
        final EditText fromDate = v.findViewById(R.id.fromdate);
        final EditText toDate = v.findViewById(R.id.todate);

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                fromDate.setText(i2+"-"+(i1+1)+"-"+i);
            }
        };

        fromDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,listener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DATE)).show();
            }
        });
        final DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                toDate.setText(i2+"-"+(i1+1)+"-"+i);
            }
        };

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,listener1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DATE)).show();
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v)
                .setTitle("Edit")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(fromDate.getText().toString().equals("") && toDate.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "Enter both fields.", Toast.LENGTH_SHORT).show();
                        }else{

                            Log.i("sharvari", "onClick: "+fromDate.getText());
                            Log.i("sharvari", "onClick: "+toDate.getText());
                            Date td = null;
                            Date fd = null;
                            try {
                                fd = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate.getText().toString());
                                td = new SimpleDateFormat("dd-MM-yyyy").parse(toDate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            toDoList.removeAll(toDoList);
                            realm.beginTransaction();
                            toDoList.addAll(RealmController.with(MainActivity.this).filterByDate(fd,td));
                            realm.commitTransaction();
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recyclerViewData(){


        ToDoList list = new ToDoList();
        list.setId(1);
        list.setReminderTitle("Reminder Title 1");
        list.setReminderDescription("Reminder Description");
        //list.setReminderDate("20 Jan 2018");
        list.setReminderTime("20.20");
        toDoList.add(list);

        list = new ToDoList();
        list.setId(2);
        list.setReminderTitle("Reminder Title 2");
        list.setReminderDescription("Reminder Description");
        //list.setReminderDate("21 Jan 2018");
        list.setReminderTime("20.20");
        toDoList.add(list);

        list = new ToDoList();
        list.setId(3);
        list.setReminderTitle("Reminder Title 3");
        list.setReminderDescription("Reminder Description");
        //list.setReminderDate("22 Jan 2018");
        list.setReminderTime("20.20");
        toDoList.add(list);

        list = new ToDoList();
        list.setId(4);
        list.setReminderTitle("Reminder Title 4");
        list.setReminderDescription("Reminder Description");
        //list.setReminderDate("23 Jan 2018");
        list.setReminderTime("20.20");
        toDoList.add(list);

        //realm.copyToRealm(toDoList);
        for(ToDoList toDo : toDoList){
            realm.beginTransaction();
            realm.copyToRealm(toDo);
            realm.commitTransaction();
        }
        Prefs.with(this).setPreLoad(true);
    }

    private void setupRecycler() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ToDoListAdapter(toDoList, this);
        recyclerView.setAdapter(adapter);
    }

    private void onFabClick(final View view){
        final Calendar myCalendar = Calendar.getInstance();
        inflater = MainActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_edit_item, null);
        final EditText title = v.findViewById(R.id.title);
        final EditText description = v.findViewById(R.id.description);
        final EditText time = v.findViewById(R.id.time);
        final EditText date = v.findViewById(R.id.date);

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                date.setText(i2+"-"+(i1+1)+"-"+i);
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,listener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DATE)).show();
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v)
                .setTitle("Edit")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(title.getText().toString().equals("") && description.getText().toString().equals("")  &&
                                date.getText().toString().equals("")  && time.getText().toString().equals("") ){
                            Snackbar.make(view,"Entry not saved.\nEnter all the field details.",Snackbar.LENGTH_LONG).show();
                        }else{

                            ToDoList list = new ToDoList();
                            list.setReminderTitle(title.getText().toString());
                            list.setReminderDescription(description.getText().toString());
                            Date d = null;
                            try {
                                d = new SimpleDateFormat("dd-MM-yyyy").parse(date.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            list.setReminderDate(d);
                            list.setReminderTime(time.getText().toString());
                            toDoList.add(list);

                            realm.beginTransaction();
                            realm.copyToRealm(list);
                            realm.commitTransaction();
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void RecyclerViewItemClicked(View view, final int position) {
        final Calendar myCalendar = Calendar.getInstance();
        inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.layout_edit_item, null);
        final EditText title = v.findViewById(R.id.title);
        final EditText description = v.findViewById(R.id.description);
        final EditText time = v.findViewById(R.id.time);
        final EditText date = v.findViewById(R.id.date);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                date.setText(i2+"-"+(i1+1)+"-"+i);
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this,listener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DATE)).show();
            }
        });

        title.setText(toDoList.get(position).getReminderTitle());
        description.setText(toDoList.get(position).getReminderDescription());
        time.setText(toDoList.get(position).getReminderTime());
        String d = new SimpleDateFormat("dd-MM-yyyy").format(toDoList.get(position).getReminderDate());
        date.setText(d);

        builder.setView(v)
                .setTitle("Edit")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        realm.beginTransaction();

                        RealmResults<ToDoList> list = realm.where(ToDoList.class).findAll();
                        list.get(position).setReminderTitle(title.getText().toString());
                        list.get(position).setReminderDescription(description.getText().toString());
                        Date d = null;
                        try {
                            d = new SimpleDateFormat("dd-MM-yyyy").parse(date.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        list.get(position).setReminderDate(d);
                        list.get(position).setReminderTime(time.getText().toString());

                        realm.commitTransaction();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {
        if (holder instanceof ToDoListAdapter.MyViewHolder) {
            String title = toDoList.get(position).getReminderTitle();

            toDoList.remove(position);
            adapter.notifyItemRemoved(position);

            realm.beginTransaction();
            RealmController.with(this).removeListItem(position);
            realm.commitTransaction();
            adapter.notifyDataSetChanged();

            Toast.makeText(this, title+" is deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
