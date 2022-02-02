package com.azmathunzai.simpletodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.zip.Inflater;

public class BottomSheetColorDialog extends BottomSheetDialogFragment {
    private OnColorChooseListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_color_layout, container, false);
        return view;
    }

    public void setColor(View colorView) {
        ImageButton imageButton = (ImageButton) colorView;
        if(listener!=null){
            listener.onClick(colorView, colorView.getSolidColor());
        }
    }

    public void setOnColorChooseListener(OnColorChooseListener listener) {
        this.listener = listener;
    }


    public static interface OnColorChooseListener {
        public void onClick(View view, int color);
    }


}
