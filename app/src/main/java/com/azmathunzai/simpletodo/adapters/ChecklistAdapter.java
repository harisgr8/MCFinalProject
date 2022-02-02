package com.azmathunzai.simpletodo.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azmathunzai.simpletodo.AddNewItemActivity;
import com.azmathunzai.simpletodo.R;
import com.azmathunzai.simpletodo.RecyclerRowCallBack;
import com.azmathunzai.simpletodo.classes.CheckListModel;
import com.azmathunzai.simpletodo.classes.ToDo;
import com.azmathunzai.simpletodo.storage.LocalStorage;

import java.util.Collections;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyviewModel> implements RecyclerRowCallBack.RecyclerViewRowTouchHelperContract {
    private List<ToDo> dataList;
    private LocalStorage localStorage;

    public ChecklistAdapter(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    public void setDataList(List<ToDo> dataList) {
        this.dataList = dataList;
    }


    public List<ToDo> getDataList() {
        return this.dataList;
    }

    @NonNull
    @Override
    public ChecklistAdapter.MyviewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_item, parent, false);
        return new MyviewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewModel holder, int position) {
        if (holder != null) {
            holder.setData(this.dataList, position);
        }
    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {

        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(dataList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(dataList, i, i - 1);
            }
        }
       notifyItemMoved(from, to);
        Log.d("XT_LOG","from:"+from+", to:"+to);
    }

    @Override
    public void onRowSelected(ToDoAdapter.ViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(ToDoAdapter.ViewHolder myViewHolder) {

    }

    public static class MyviewModel extends RecyclerView.ViewHolder {

        private EditText etCheckListText;
        private CheckBox checkBox;
        private ImageButton btnDelCheckList;

        private List<ToDo> todoList = null;
        private int position;

        public void setPosition(int position){
            this.position = position;
        }
        public MyviewModel(@NonNull View itemView) {
            super(itemView);
            etCheckListText = itemView.findViewById(R.id.check_list_text);
            checkBox = itemView.findViewById(R.id.check_list_cb);
            btnDelCheckList = itemView.findViewById(R.id.delete_check_list);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    todoList.get(position).setStatus(isChecked ? 1 : 0);
                    Log.d("XT_LOG","position:"+position);


                }
            });

            etCheckListText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etCheckListText != null) {
                        todoList.get(position).setTextNote(etCheckListText.getText().toString());
                        Log.d("XT_LOG","positon:"+position);
                    }
                }

            });

            btnDelCheckList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(AddNewItemActivity.TAG, "delete clecked");
                }
            });
        }



        public void setData(List<ToDo> todoList, int position) {

            this.todoList = todoList;
            this.position = position;
            ToDo currentTodo = this.todoList.get(position);

            etCheckListText.setText(currentTodo.getTextNote());
            checkBox.setChecked(currentTodo.getStatus()!=0);
        }
    }

}
