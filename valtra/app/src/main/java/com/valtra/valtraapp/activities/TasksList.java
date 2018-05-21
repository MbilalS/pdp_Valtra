package com.valtra.valtraapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valtra.valtraapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksList extends Activity {

    String current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        final ListView lv = (ListView) findViewById(R.id.lv);
        //final Button btn = (Button) findViewById(R.id.btn);

        // Initializing a new String Array
        String[] tasks = new String[]{
        };

        final String currentUser = MainActivity.passName;
        // Create a List from String Array elements
        final List<String> tasks_list = new ArrayList<String>(Arrays.asList(tasks));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, tasks_list);

        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference name = database.getReference("Tasks").child("Name");
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference task = database.getReference("Tasks").child("Task");
        task.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String task = dataSnapshot.getValue(String.class);
                if(current.equals(currentUser)) {
                    tasks_list.add(task);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference task1 = database.getReference("Tasks").child("Task1");
        task1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String task = dataSnapshot.getValue(String.class);
                //if(current.equals(currentUser)) {
                    tasks_list.add(task);
                    arrayAdapter.notifyDataSetChanged();
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference task2 = database.getReference("Tasks").child("Task2");
        task2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String task = dataSnapshot.getValue(String.class);
                //if(current.equals(currentUser)) {
                    tasks_list.add(task);
                    arrayAdapter.notifyDataSetChanged();
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference task3 = database.getReference("Tasks").child("Task3");
        task3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String task = dataSnapshot.getValue(String.class);
                //if(current.equals(currentUser)) {
                    tasks_list.add(task);
                    arrayAdapter.notifyDataSetChanged();
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Add new Items to List
//                fruits_list.add("Loquat");
//                fruits_list.add("Pear");
//                    /*
//                        notifyDataSetChanged ()
//                            Notifies the attached observers that the underlying
//                            data has been changed and any View reflecting the
//                            data set should refresh itself.
//                     */
//                arrayAdapter.notifyDataSetChanged();
//            }
//        });
    }
}
