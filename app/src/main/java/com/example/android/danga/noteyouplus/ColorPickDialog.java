package com.example.android.danga.noteyouplus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.example.android.danga.noteyouplus.data.NoteService;

/**
 * Created by An on 6/14/2016.
 */
public class ColorPickDialog extends DialogFragment {

    public static final String TAG = ColorPickDialog.class.getSimpleName();

    public View rootView;
    private FragmentCallBack mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.color_picker, container, false);
        // Color Buttons
        ImageButton redBtn = (ImageButton) rootView.findViewById(R.id.red_color_btn);
        ImageButton orangeBtn = (ImageButton) rootView.findViewById(R.id.orange_color_btn);
        ImageButton yellowBtn = (ImageButton) rootView.findViewById(R.id.yellow_color_btn);
        ImageButton greenBtn = (ImageButton) rootView.findViewById(R.id.green_color_btn);
        ImageButton blueBtn = (ImageButton) rootView.findViewById(R.id.blue_color_btn);
        ImageButton purpleBtn = (ImageButton) rootView.findViewById(R.id.purple_color_btn);
        ImageButton lightYellowBtn = (ImageButton) rootView.findViewById(R.id.light_yellow_color_btn);
        ImageButton lightBlueBtn = (ImageButton) rootView.findViewById(R.id.light_blue_color_btn);
        ImageButton whiteBtn = (ImageButton) rootView.findViewById(R.id.white_color_btn);

        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Red is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.RED_BGR_COLOR);
                }
                dismiss();
            }
        });
        orangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Orange is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.ORANGE_BGR_COLOR);
                }
                dismiss();
            }
        });
        yellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Yellow is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.YELLOW_BGR_COLOR);
                }
                dismiss();
            }
        });
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Green is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.GREEN_BGR_COLOR);
                }
                dismiss();
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Blue is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.BLUE_BGR_COLOR);
                }
                dismiss();
            }
        });
        purpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Purple is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.PURPLE_BGR_COLOR);
                }
                dismiss();
            }
        });
        lightYellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Light Yellow is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.LIGHT_YELLOW_BGR_COLOR);
                }
                dismiss();
            }
        });
        lightBlueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Light Blue is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.LIGHT_BLUE_BGR_COLOR);
                }
                dismiss();
            }
        });
        whiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "White is picked!");
                if(mListener != null){
                    mListener.onColorSelected(NoteService.DEFAULT_BGR_COLOR);
                }
                dismiss();
            }
        });
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            mListener = (FragmentCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
