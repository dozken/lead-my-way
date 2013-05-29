package org.opencv.samples.facedetect;


import java.io.File;

import com.activeandroid.ActiveAndroid;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class LeadMyWay extends com.activeandroid.app.Application {
	//public Sign data = new Sign();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("onCreate","onCreate!!!!");

		//ActiveAndroid.initialize(this);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		Log.i("TERMINATE","TERMINATE!!!!");
		super.onTerminate();
	}


	void initDb(){

		final String PREFS_NAME = "MyPrefsFile";

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		if (settings.getBoolean("my_first_time", true)) {
			//the app is being launched for first time, do something

			String category[] = new String[]{"Ескерту белгілері",
					"Басымдылық белгілері",
					"Тыйым салу белгілері",
					"Міндеттеу белгілері",
					"Ақпараттық-көрсеткіш белгілері",
					"Қызмет көрсету белгілері",
			"Қосымша тақтайшалар"};

/*
			for(String temp: category){
				Category c = new Category(temp);
				c.save();
				//list.add(s);
			}
			*/
			
			//Sign s = new Sign("Аялдамасыз қозғалысқа тиым салынған",);
			// record the fact that the app has been started at least once
			settings.edit().putBoolean("my_first_time", false).commit(); 
		}
		else{			
			//list = Salah.getAll();

			//TODO
		}
	}
}
