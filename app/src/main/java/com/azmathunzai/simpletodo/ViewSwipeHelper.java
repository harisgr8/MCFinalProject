package com.azmathunzai.simpletodo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.azmathunzai.simpletodo.adapters.ToDoAdapter;

public class ViewSwipeHelper implements View.OnTouchListener,View.OnDragListener {

    private Context context;
    public ViewSwipeHelper(Context context){
            this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            Log.d("XTLOG","on touch event");

        return true;
    }


    private DragEvent event1, event2;
    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d("XTLOG","on drag event");
       if( event.getAction() == DragEvent.ACTION_DRAG_STARTED){
           event1 = event;
       }else  if( event.getAction() == DragEvent.ACTION_DRAG_STARTED){
           event2= event;

           doAction(v,(int) (event2.getX()-event1.getX()), (int)(event2.getY()-event1.getY()));
       }

        return false;
    }
    
    private void doAction(View target , int dX, int xY){
        Drawable icon;
        ColorDrawable background;

        View itemView = target;
        int backgroundCornerOffset = 20;
        Canvas c = new Canvas();

        if (dX > 0) {
            icon = ContextCompat.getDrawable(context, R.drawable.edit);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.design_default_color_primary));
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.delete);
            background = new ColorDrawable(Color.RED);
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);

        target.draw(c);
    }

}
