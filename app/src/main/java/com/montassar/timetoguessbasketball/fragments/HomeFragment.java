package com.montassar.timetoguessbasketball.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.montassar.timetoguessbasketball.R;
import com.montassar.timetoguessbasketball.utils.SoundsManager;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SoundsManager sou;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences =getContext().getSharedPreferences("MyPref",0);
        editor = sharedPreferences.edit();
        sou = new SoundsManager(getContext());


        ImageView imgPlayBtn = (ImageView) view.findViewById(R.id.img_play_btn);
        ImageView imgStoreBtn = (ImageView) view.findViewById(R.id.img_store_btn);
        ImageView imgSettingsBtn = (ImageView) view.findViewById(R.id.img_settings_btn);

        imgPlayBtn.setOnClickListener(this);
        imgStoreBtn.setOnClickListener(this);
        imgSettingsBtn.setOnClickListener(this);




        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.img_play_btn:
                sou.playSound(R.raw.play);
                moveToGame();
                break;
            case R.id.img_store_btn:
                sou.playSound(R.raw.play);
                moveToStore();
                break;
            case R.id.img_settings_btn:
                sou.playSound(R.raw.play);
                moveToSettings();
                break;
        }
    }

    private void moveToGame() {
        GameFragment fragment = new GameFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void moveToSettings()
    {
        SettingsFragment fragment = new SettingsFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void moveToStore()
    {
        StoreFragment fragment = new StoreFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
