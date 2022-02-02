package com.azmathunzai.simpletodo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azmathunzai.simpletodo.adapters.ChecklistAdapter;
import com.azmathunzai.simpletodo.classes.ToDo;
import com.azmathunzai.simpletodo.storage.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewItemActivity extends AppCompatActivity {

    public static final String TAG = "MyNotePad";
    public static final String ACTION_NEW = "new_todo";
    public static final String ACTION_VIEW = "view_todo";
    public static final String ACTION = "action";
    final int REQUEST_CODE_GALLERY = 999;

    private ChecklistAdapter checklistAdapter;
    private EditText etTitle;
    private EditText etText;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ImageButton checkListBtn, imageBtn, backgroundColor, handWriteBtn,ibAddChecklist;
    List<ToDo> list = null;
    private LocalStorage localStorage;
    private static final int REQUEST_PERMISSION = 901;
    private String imagePath;
    private String colorCode;
    private LinearLayout linearLayout;


    private PaintView paintView;
    private ToDo currentTodo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);


        String action = getIntent().getStringExtra(AddNewItemActivity.ACTION);


        localStorage = new LocalStorage(this);
        localStorage.openDatabase();

        etTitle = findViewById(R.id.activity_new_item_title_et);
        etText = findViewById(R.id.activity_new_item_text_et);
        imageView = findViewById(R.id.add_new_image);
        recyclerView = findViewById(R.id.check_list_rv);
        linearLayout = findViewById(R.id.activity_add_new_item_linear_layout);

        checkListBtn = findViewById(R.id.new_check_list_btn);
        imageBtn = findViewById(R.id.new_image_btn);
        backgroundColor = findViewById(R.id.background_color_change);
//        handWriteBtn = findViewById(R.id.hand_write_btn);
        ibAddChecklist = findViewById(R.id.add_checklist_ib);

        paintView = findViewById(R.id.activity_add_new_item_pv);


        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");
                paintView.setVisibility(View.GONE);
                if (ActivityCompat.checkSelfPermission(AddNewItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            AddNewItemActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION
                    );
                } else {
                    Intent pickIntent = new Intent();
                    pickIntent.setAction(Intent.ACTION_PICK);
                    pickIntent.setType("image/*");
                    ActivityCompat.startActivityForResult(AddNewItemActivity.this, pickIntent, REQUEST_CODE_GALLERY, null);
                }
            }
        });

//        handWriteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (isChecked) {
//                    isChecked = false;
//                    paintView.setVisibility(View.GONE);
//                    etTitle.setVisibility(View.VISIBLE);
//                    etText.setVisibility(View.VISIBLE);
//
//                } else {
//                    isChecked = true;
//                    etTitle.setVisibility(View.GONE);
//                    etText.setVisibility(View.GONE);
//                    paintView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        backgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AddNewItemActivity.TAG, "bg btn Clicked");
                BottomSheetColorDialog bottomSheetColorDialog = new BottomSheetColorDialog();
                bottomSheetColorDialog.show(getSupportFragmentManager(), "Color Bottom sheet");
                bottomSheetColorDialog.setOnColorChooseListener(new BottomSheetColorDialog.OnColorChooseListener() {
                    @Override
                    public void onClick(View view, int color) {
                        changeColor(view);
                    }
                });
            }
        });

        checkListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list!=null) {
                    convertToText();
                } else {
                    convertToList(etText.getText().toString());
                }
            }
        });

        ibAddChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checklist", "add Button clicked");
                if(list!=null && checklistAdapter!=null ){
                    list = checklistAdapter.getDataList();
                    list.add(list.size(),new ToDo());

                    checklistAdapter.notifyDataSetChanged();
                   // recyclerView.setAdapter(checklistAdapter);
                }else{
                    Log.d("checklist", "list or adapter null");
                }
            }
        });

        if(action == null || action.equalsIgnoreCase(AddNewItemActivity.ACTION_NEW)){
            //create new
            getSupportActionBar().setTitle("Edit Note");
        }else{
            //load from db
            long todoId = getIntent().getIntExtra(ToDo.KEY_ID,0);
            currentTodo = localStorage.getToDo(todoId);

            etTitle.setText(currentTodo.getTitle());
            if(currentTodo.getIsCheckList() == ToDo.TYPE_CHECKLIST){
                convertToListFromDB(currentTodo.getTextNote());
            }else{
                list= new ArrayList<>();
                convertToText();
            }

            etText.setText(currentTodo.getTextNote());
            Log.d(AddNewItemActivity.TAG, currentTodo.getTextNote());


            if (currentTodo.getBgColor() != null && currentTodo.getBgColor().length()>0) {
                linearLayout.setBackgroundColor(Color.parseColor( currentTodo.getBgColor()));
            }


            String path = currentTodo.getImage();
            if (path != null && path.length() > 10) {
                Uri imagePath = Uri.parse(path);
                try {
                    InputStream inputStream = AddNewItemActivity.this.getContentResolver().openInputStream(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public void changeColor(View view) {
        Log.d(AddNewItemActivity.TAG, "clicked" +view.getTag());
        int tag = Integer.parseInt( (String) view.getTag());
        Log.d(AddNewItemActivity.TAG, "clicked" +view.getTag());

        switch (tag){
            case 0:
                linearLayout.setBackgroundColor(Color.parseColor("#F1E503"));
                colorCode = "#F1E503";
                break;
            case 1:
                linearLayout.setBackgroundColor(Color.parseColor("#D6D6D6"));
                colorCode = "#D6D6D6";
                break;
            case 2:
                linearLayout.setBackgroundColor(Color.parseColor("#EF6C00"));
                colorCode = "#EF6C00";
                break;
            case 3:
                linearLayout.setBackgroundColor(Color.parseColor("#DCAF8E"));
                colorCode = "#DCAF8E";
                break;
            case 4:
                linearLayout.setBackgroundColor(Color.parseColor("#86EAE1"));
                colorCode = "#86EAE1";
                break;
            case 5:
                linearLayout.setBackgroundColor(Color.parseColor("#B0E880"));
                colorCode = "#B0E880";
                break;
            case 6:
                linearLayout.setBackgroundColor(Color.parseColor("#9D86E6"));
                colorCode = "#9D86E6";
                break;
        }
    }

    private void convertToList(String note) {
        list = getListFromText(note);
        if (list.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            etText.setVisibility(View.GONE);
            ibAddChecklist.setVisibility(View.VISIBLE);
            initRecyclerView();
        }
    }

    private void convertToListFromDB(String noteJSON) {

        list = decodeCheckListData(noteJSON);
        if (list.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            etText.setVisibility(View.GONE);
            ibAddChecklist.setVisibility(View.VISIBLE);
            initRecyclerView();
        }
    }

    private void convertToText() {
        recyclerView.setVisibility(View.GONE);
        ibAddChecklist.setVisibility(View.GONE);
        String reverseString = getReverseString(list);
        etText.setText(reverseString);
        etText.setVisibility(View.VISIBLE);
        //clear list
        list = null;
    }

    private List<ToDo> getListFromText(String toString) {

        List<ToDo> newList = new ArrayList<>();
        String[] lines = toString.split("\n");
        Log.d("checklist", "Max Lines:" + lines.length);


        for (int i = 0; i < lines.length; i++) {
            Log.d("checklist", "Line#:" + (i + 1) + " : " + lines[i].trim());
            if (lines[i].trim().length() > 0) {
                newList.add(new ToDo(0, "", lines[i].trim(),"" ,"","", 0));
            }
        }

        return newList;
    }

    private String getReverseString(List<ToDo> list) {
        String str = "";
        for (int i =0; i<list.size() ; i++){
            str += list.get(i).getTextNote();
            if(i<list.size()-1){
                str+="\n";
            }
        }
        return str;
    }

    public void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.check_list_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        checklistAdapter = new ChecklistAdapter(localStorage);
        checklistAdapter.setDataList(list);

        ItemTouchHelper.Callback callback = new RecyclerRowCallBack(checklistAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(checklistAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted Please choose photo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "you don't have permission to excess the gallery", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d("XT", "activity result");
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                Log.d(AddNewItemActivity.TAG, "image path" + uri.getPath());
                Log.d(AddNewItemActivity.TAG, "image path" + uri);
                imagePath = uri.toString();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu_item, menu);

        final MenuItem toggleservice = menu.findItem(R.id.add_new_item);
        toggleservice.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(AddNewItemActivity.TAG, "item Clicked");
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                String message = "";
                if(currentTodo==null){
                    //new item to be added;
                    currentTodo = new ToDo();
                    currentTodo.setTitle(etTitle.getText().toString());
                    currentTodo.setDate(currentDate);
                    currentTodo.setImage(imagePath);
                    currentTodo.setBgColor(colorCode);
                    currentTodo.setIsCheckList((list!=null && list.size()>0)? ToDo.TYPE_CHECKLIST : ToDo.TYPE_TEXT);

                    if(currentTodo.getIsCheckList() == ToDo.TYPE_CHECKLIST){
                        String jsonCheckList = encodeCheckListData(list);
                        currentTodo.setTextNote(jsonCheckList);
                    }else{
                        currentTodo.setTextNote(etText.getText().toString());
                    }
                    currentTodo.setStatus(0);
                    long todoId = localStorage.addNewToDo(currentTodo);
                    currentTodo.setID((int)todoId);
                    message ="Successfully Added";
                }else{
                    currentTodo.setTitle(etTitle.getText().toString());
                    currentTodo.setImage(imagePath);
                    currentTodo.setBgColor(colorCode);
                    currentTodo.setIsCheckList((list!=null && list.size()>0)? ToDo.TYPE_CHECKLIST : ToDo.TYPE_TEXT);
                    if(currentTodo.getIsCheckList() == ToDo.TYPE_CHECKLIST){
                        String jsonCheckList = encodeCheckListData(list);
                        currentTodo.setTextNote(jsonCheckList);
                    }else{
                        currentTodo.setTextNote(etText.getText().toString());
                    }
                    localStorage.updateToDo(currentTodo);
                    message ="Successfully Updated!";
                }

                Log.d("XT_DEBUG",currentTodo.getTextNote());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }

    private String encodeCheckListData(List<ToDo> list) {
        String json =null;
        JSONArray jsonArray = new JSONArray();

        for(int i=0; i<list.size() ; i++){
            ToDo currentToDo  = list.get(i);
            JSONObject temp = new JSONObject();
            try {
                temp.put("item",currentToDo.getTextNote());
                temp.put("status",currentToDo.getStatus());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(temp);
        }

        return  jsonArray.toString();
    }

    public static List<ToDo> decodeCheckListData(String json) {
        List<ToDo> toDoList =null;
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
            toDoList = new ArrayList<>();
            for(int i=0; i<jsonArray.length() ; i++){
                JSONObject currentObject = (JSONObject) jsonArray.get(i);

                ToDo temp  = new ToDo();
                temp.setTextNote(currentObject.optString("item",""));
                temp.setStatus(currentObject.optInt("status",0));

                toDoList.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return  toDoList;
    }

}
