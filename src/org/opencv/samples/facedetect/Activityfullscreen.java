package org.opencv.samples.facedetect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class Activityfullscreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img);
       ImageView img=(ImageView)findViewById(R.id.widget45);
       img.setBackgroundResource(R.drawable.icon);
    }
    
    protected void onStop(){
    	FdActivity.flag = true;
    }
}
