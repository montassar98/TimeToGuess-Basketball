package com.montassar.timetoguessbasketball.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.montassar.timetoguessbasketball.R;
import com.montassar.timetoguessbasketball.database.DBHelper;
import com.montassar.timetoguessbasketball.utils.SoundsManager;

public class SettingsFragment extends Fragment {
    private final String TAG="SettingsFragment";

    private SoundsManager sou;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Dialog mDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings,container,false);
        sou = new SoundsManager(getContext());
        sharedPreferences =getContext().getSharedPreferences("MyPref",0);
        editor = sharedPreferences.edit();

        //=============Navigation for Back Button =============
        ImageView imgBack =(ImageView) view.findViewById(R.id.img_back_settings);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: back Button. ");
                sou.playSound(R.raw.play);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        //=====================================================
        SwitchCompat soundSwitch = view.findViewById(R.id.switch_sound);
        if (sharedPreferences.getInt("sound",1)==1)
        {
            soundSwitch.setChecked(true);
        }else {
            soundSwitch.setChecked(false);
        }
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    editor.putInt("sound",1);
                    editor.commit();
                }else {
                    editor.putInt("sound",0);
                    editor.commit();
                }
            }
        });
        SwitchCompat vibrateSwitch = view.findViewById(R.id.switch_vibrate);
        if (sharedPreferences.getInt("vibrate",1)==1)
        {
            vibrateSwitch.setChecked(true);
        }else {
            vibrateSwitch.setChecked(false);
        }
        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    editor.putInt("vibrate",1);
                    editor.commit();
                }else {
                    editor.putInt("vibrate",0);
                    editor.commit();
                }
            }
        });

        ImageView imgReset = (ImageView) view.findViewById(R.id.img_reset_ic);
        TextView txtReset = (TextView) view.findViewById(R.id.txt_reset_game);
        imgReset.setOnClickListener(resetListener);
        txtReset.setOnClickListener(resetListener);


        return view;
    }
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialog = new Dialog(getContext());
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setContentView(R.layout.popup_helps);
            TextView txtHelps = (TextView) mDialog.findViewById(R.id.txt_helps);
            txtHelps.setText("Are you sure you want to Reset The Game");
            ImageView imgYes = (ImageView) mDialog.findViewById(R.id.img_yes_helps);
            ImageView imgNo = (ImageView) mDialog.findViewById(R.id.img_no_helps);
            imgNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            imgYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHelper db = new DBHelper(getContext());
                    db.resetGame();
                    Toast.makeText(getContext(), "The Game Has Been Reset successfully", Toast.LENGTH_LONG).show();
                    sou.playSound(R.raw.play);
                    mDialog.dismiss();
                }
            });
            mDialog.show();
        }
    };


}
