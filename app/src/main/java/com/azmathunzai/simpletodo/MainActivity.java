package com.azmathunzai.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azmathunzai.simpletodo.adapters.ToDoAdapter;
import com.azmathunzai.simpletodo.classes.ToDo;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.azmathunzai.simpletodo.storage.LocalStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "simple to do app" ;
    private RecyclerView rvTask;
    private List<ToDo> toDoList;
    private ToDoAdapter tasksAdapter;

    private FloatingActionButton floatingActionButton;

    private LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localStorage = new LocalStorage(this);
        localStorage.openDatabase();

        floatingActionButton = findViewById(R.id.newToDoItem);



        rvTask = findViewById(R.id.task_rv);
        rvTask.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(localStorage,MainActivity.this);
        rvTask.setAdapter(tasksAdapter);

        // swipe to delete and edit
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(rvTask);


//        // drag recycler view
//        ItemTouchHelper.Callback callback = new RecyclerRowCallBack(tasksAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(rvTask);
//
        toDoList = localStorage.getToDoList();
        tasksAdapter.setTasks(toDoList);



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(MainActivity.this, AddNewItemActivity.class);
                newTaskIntent.putExtra(AddNewItemActivity.ACTION, AddNewItemActivity.ACTION_NEW);
                startActivity(newTaskIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_us_item:
                Log.d(MainActivity.TAG, "item clicked");
                Intent aboutUsIntent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(aboutUsIntent);
                break;
            case R.id.share_item:
                Log.d(MainActivity.TAG, "item clicked");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        finish();
       super.onBackPressed();
    }
}
