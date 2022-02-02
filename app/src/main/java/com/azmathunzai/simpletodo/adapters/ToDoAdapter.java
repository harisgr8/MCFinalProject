package com.azmathunzai.simpletodo.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.azmathunzai.simpletodo.AddNewItemActivity;
import com.azmathunzai.simpletodo.MainActivity;
import com.azmathunzai.simpletodo.R;
import com.azmathunzai.simpletodo.RecyclerItemTouchHelper;
import com.azmathunzai.simpletodo.RecyclerRowCallBack;
import com.azmathunzai.simpletodo.ViewSwipeHelper;
import com.azmathunzai.simpletodo.classes.ToDo;
import com.azmathunzai.simpletodo.storage.LocalStorage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by virgo on 9/25/16.
 */
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> implements RecyclerRowCallBack.RecyclerViewRowTouchHelperContract {

    private List<ToDo> todoList;
    private LocalStorage localStorage;
    private MainActivity activity;


    public ToDoAdapter(LocalStorage localStorage, MainActivity activity) {
        this.localStorage = localStorage;
        this.activity = activity;
    }

    public void setTodoList(List<ToDo> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_todolist_item, parent, false);
        return new ViewHolder(itemView, getContext().getContentResolver());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        localStorage.openDatabase();
        holder.setData(todoList.get(position));
        holder.showDate(true);
        if (position - 1 > 0) {
            ToDo lastTodo = todoList.get(position - 1);
            if (lastTodo.getDate().equals(holder.getObj().getDate()))
                holder.showDate(false);
        }
    }

//    private boolean toBoolean(int n) {
//        return n != 0;
//    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDo> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDo item = todoList.get(position);
        localStorage.deleteToDO(item.getID());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onRowMoved(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(todoList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(todoList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);

    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {

    }

    public void editItem(int position) {

        ToDo currentToDo = todoList.get(position);
        Intent newTaskIntent = new Intent(getContext(), AddNewItemActivity.class);
        newTaskIntent.putExtra(AddNewItemActivity.ACTION, AddNewItemActivity.ACTION_VIEW);
        newTaskIntent.putExtra(ToDo.KEY_ID, currentToDo.getID());
        getContext().startActivity(newTaskIntent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView title;
        TextView text;
        TextView currentDate;
        ImageView imageView;
        CardView cardView;

        private ContentResolver contentResolver;

        ViewHolder(View view, ContentResolver contentResolver) {
            super(view);
            this.contentResolver = contentResolver;
            relativeLayout = view.findViewById(R.id.layout_todo_list_item_rl);
            title = view.findViewById(R.id.title_text);
            text = view.findViewById(R.id.listItemTodoText);
            currentDate = view.findViewById(R.id.date_tv);
            imageView = view.findViewById(R.id.show_new_image);
            cardView = view.findViewById(R.id.item_todo_list_card);

        }

        public View getLayout() {
            return this.cardView;
        }


        private ToDo item;

        public void setData(ToDo todo) {
            this.item = todo;
            currentDate.setText(item.getDate());
            title.setText(item.getTitle());

            if (item.getIsCheckList() == ToDo.TYPE_CHECKLIST) {
//            text.setText("this is checkList");
                List<ToDo> toDoList = AddNewItemActivity.decodeCheckListData(item.getTextNote());
                String checklist = "<ul>";
                for (int i = 0; i < Math.min(3, toDoList.size()); i++) {
                    if (toDoList.get(i).getStatus() != ToDo.STATUS_DEFAULT)
                        checklist = checklist + "<li><strike>" + toDoList.get(i).getTextNote() + "</strike></li>";
                    else
                        checklist = checklist + "<li>" + toDoList.get(i).getTextNote() + "</li>";
                }
                checklist = checklist + "</ul>";
                text.setText(Html.fromHtml(checklist));
            } else {
                text.setText(item.getTextNote());
            }

            if (item.getBgColor() != null) {
                relativeLayout.setBackgroundColor(Color.parseColor(item.getBgColor()));
            }


            // image get
            String path = item.getImage();
            if (path != null && path.length() > 10) {
                Uri imagePath = Uri.parse(path);
                try {
                    InputStream inputStream = this.contentResolver.openInputStream(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public ToDo getObj() {
            return item;
        }

        public void showDate(boolean visible) {
            currentDate.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

}
