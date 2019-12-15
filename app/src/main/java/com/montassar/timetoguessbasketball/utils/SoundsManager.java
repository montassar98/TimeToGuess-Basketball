package com.montassar.timetoguessbasketball.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class SoundsManager {

	public Context context;
	MediaPlayer sound;
	SharedPreferences mSharedPreferences;

	public SoundsManager(Context context)
	{
		this.context = context;
		sound = new MediaPlayer();
		mSharedPreferences = context.getSharedPreferences("MyPref",0);
	}

	public void playSound(final int soundName)
	{
		if (mSharedPreferences.getInt("sound",1) == 1)
		{
			sound =new MediaPlayer();
			AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(soundName);
			try {
				sound.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
				sound.prepare();
				sound.start();
				sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
					}
				});

			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
