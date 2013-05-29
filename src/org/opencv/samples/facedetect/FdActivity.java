package org.opencv.samples.facedetect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import com.activeandroid.ActiveAndroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

public class FdActivity extends Activity implements CvCameraViewListener2 {

	private static final String    TAG                 = "OCVSample::Activity";

	private Mat                    mRgba;
	private Mat                    mGray;


	//private CascadeClassifier      cc2;

	private CameraBridgeViewBase   mOpenCvCameraView;
	static MediaPlayer mp;// = new MediaPlayer();

	Map<CascadeClassifier, String> cascades = new HashMap<CascadeClassifier, String>();
	//private java.util.AbstractList<CascadeClassifier,String> cascades = new java.util.ArrayList<CascadeClassifier,String>();

	private final String PROJECT_DIR = "LeadMyWay/";
	private final String CASCADE_DIR = "cascade";
	private final String IMAGE_DIR   = "image/";
	private final String AUDIO_DIR   = "audio/";
	ImageView imgView;
	AssetManager assetManager;


	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:{
				Log.i(TAG, "OpenCV loaded successfully");
				// Load native library after(!) OpenCV initialization
				System.loadLibrary("detection_based_tracker");
				try {
					// load cascade file from application resources

					String [] cascadeFiles = assetManager.list(CASCADE_DIR);
					for(String s : cascadeFiles){
						Log.i("Raw Asset: ", s);
						InputStream is = assetManager.open(CASCADE_DIR+"/"+s);
						File cascadeDir = getDir(CASCADE_DIR, Context.MODE_PRIVATE);
						File mCascadeFile = new File(cascadeDir, s);
						FileOutputStream os = new FileOutputStream(mCascadeFile);						
						byte[] buffer = new byte[4096];
						int bytesRead2;
						while ((bytesRead2 = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead2);
						}
						is.close();
						os.close();
						CascadeClassifier cc = new CascadeClassifier(mCascadeFile.getAbsolutePath());
						cascades.put(cc, s);
						cascadeDir.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}
				mOpenCvCameraView.enableView();
			} break;
			default:{
				super.onManagerConnected(status);
			} break;
			}
		}
	};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		imgView = (ImageView) findViewById(R.id.imageView1);
		assetManager = this.getAssets();

		//play("20km");

		new ChangeImage("koala").execute();
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		MatOfRect faces = new MatOfRect();
		//new ChangeImage("Koala.jpg").execute();

		for (Map.Entry<CascadeClassifier, String> entry : cascades.entrySet()) {
			detectAndDisplay(faces,entry.getKey(),entry.getValue());
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}

		return mRgba;
	}

	void detectAndDisplay( MatOfRect faces, CascadeClassifier cc, String s){
		cc.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
				new Size(0, 0), new Size());
		cc.detectMultiScale(mGray, faces);
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++){
			//Point centerPoint = new Point ( facesArray[i].x + facesArray[i].width*0.5, facesArray[i].y + facesArray[i].height*0.5 );
			Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar( 255, 0, 255), 3);
			//Core.circle(mRgba, center, facesArray[i].width/2, new Scalar( 255, 0, 255));
			//Point textPoint = new Point ( facesArray[i].x + facesArray[i].width*0.2, facesArray[i].y + facesArray[i].height*0.5 );
			//Core.ellipse( mRgba, centerPoint, new Size( facesArray[i].width*0.5, facesArray[i].height*0.5), 0, 0, 360, new Scalar( 0, 255, 0 ), 4, 8, 0 );
			//Core.putText(mRgba, "Sign", textPoint, 2, 1, new Scalar(255, 255, 0), 3);



			try {
				//Thread.sleep(000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if(!s.equals("koala")&&!s.equals(null))
			try{
				play(s);

				new ChangeImage(s).execute();
			}catch(Exception e){

			}


			Log.i(TAG, "Detection method tapty!"+i);
		}
	}

	public void play(String fileName) {

		try {


			try {
				AssetFileDescriptor afd = assetManager.openFd("audio/"+fileName+".mp3");
				mp = new MediaPlayer();
				mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				mp.prepare();
				mp.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.e(TAG, "AssetFileDescriptor! "+e +" - " +e.getMessage());
		}
	}


	private class ChangeImage extends AsyncTask<Void, Void, Void> {
		String image ;
		public ChangeImage (String image){
			this.image = image;
		}

		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

		protected void onPostExecute(Void result) {
			try {
				imgView.setImageDrawable(Drawable.createFromStream(assetManager.open("image/"+image+""), null));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}          
	} 

	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		ActiveAndroid.dispose();
		mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}


}
