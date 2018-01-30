package com.sharvari.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sharvari on 22-Jan-18.
 */

public class ToDoListAdapter extends RecyclerView.Adapter {

    private ArrayList<ToDoList> toDoLists = new ArrayList<>();
    private RecyclerViewItemClickListener listener;

    public ToDoListAdapter(ArrayList<ToDoList> list, RecyclerViewItemClickListener listener) {
        this.toDoLists = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_to_do_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final ToDoList list = toDoLists.get(position);
        holder.title.setText(list.getReminderTitle());
        holder.description.setText(list.getReminderDescription());
        String d = new SimpleDateFormat("dd-MM-yyyy") .format(list.getReminderDate());
        holder.date.setText(d);
    }

    @Override
    public int getItemCount() {
        return toDoLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, description, date;

        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.description);
            date = (TextView) v.findViewById(R.id.date);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.RecyclerViewItemClicked(view,this.getLayoutPosition());
        }
    }
}
