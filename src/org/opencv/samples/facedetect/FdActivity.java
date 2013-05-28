package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
	MediaPlayer mp;

	private java.util.AbstractList<CascadeClassifier> cascades = new java.util.ArrayList<CascadeClassifier>();

	private final String PROJECT_DIR = "LeadMyWay/";
	private final String CASCADE_DIR = "cascade/";
	private final String IMAGE_DIR   = "image/";
	private final String AUDIO_DIR   = "audio/";
	ImageView imgView;
	private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
		String image ;
		 public DownloadFilesTask (String image){
			 this.image = image;
		 }

		             @Override
		             protected Void doInBackground(Void... params) {
		            	
		              return null;
		             }
		             protected void onPostExecute(Void result) {
		            	 File imgFile = new  File(Environment.getExternalStorageDirectory()
			         				.getAbsolutePath()+File.separator+PROJECT_DIR+IMAGE_DIR+image);
			         		if(imgFile.exists()){
			         			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			         			imgView.setImageBitmap(myBitmap);
			         		}
		            }          
		        } 
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("detection_based_tracker");

				try {

					File cascadeFiles = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath(), PROJECT_DIR+CASCADE_DIR);


					for(File cascadeFile : cascadeFiles.listFiles()){
						CascadeClassifier cc = new CascadeClassifier(cascadeFile.getAbsolutePath());
						cascades.add(cc);
					}


					// load cascade file from application resources

					/*
					InputStream is2 = getResources().openRawResource(R.raw.obhod);
					File cascadeDir2 = getDir("cascade", Context.MODE_PRIVATE);
					File mCascadeFile2 = new File(cascadeDir2, "obhod.xml");
					FileOutputStream os2 = new FileOutputStream(mCascadeFile2);

					byte[] buffer2 = new byte[4096];
					int bytesRead2;
					while ((bytesRead2 = is2.read(buffer2)) != -1) {
						os2.write(buffer2, 0, bytesRead2);
					}
					is2.close();
					os2.close();

					cc2 = new CascadeClassifier(mCascadeFile2.getAbsolutePath());
					cascadeDir2.delete();
					 */
					//if (mJavaDetector.empty()) {
					//    Log.e(TAG, "Failed to load cascade classifier");
					//    mJavaDetector = null;
					//} else
					//    Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
					//DetectionBasedTracker  mNativeDetector;
					//CascadeClassifier stop2 = new CascadeClassifier(mCascadeFile.getAbsolutePath());
					//stop2.load(mCascadeFile.getAbsolutePath());
					//stop = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);



				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}

				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	public FdActivity() {
		//mDetectorName = new String[2];
		//mDetectorName[JAVA_DETECTOR] = "Java";
		//mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";



		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		//ActiveAndroid.initialize(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.face_detect_surface_view);
		imgView = (ImageView) findViewById(R.id.imageView1);
		 CountDownTimer cntr_aCounter = new CountDownTimer(3000, 1000) {
		        public void onTick(long millisUntilFinished) {
		        	audioPlayer("a.mp3");
		        	}

		        public void onFinish() {
		            //code fire after finish
		        	mp.stop();
		        }
		        };
		        cntr_aCounter.start();
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
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

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		MatOfRect faces = new MatOfRect();
		new DownloadFilesTask("Koala.jpg").execute();
		for(CascadeClassifier cc : cascades)
			detectAndDisplay(faces,cc);
		return mRgba;
	}
	
	public void audioPlayer(String fileName){
	    //set up MediaPlayer    
	    mp = new MediaPlayer();
	    try {
	    	FileInputStream fileInputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+PROJECT_DIR+AUDIO_DIR+fileName);
	        mp.setDataSource(fileInputStream .getFD());
	        mp.prepare();
	        mp.setLooping(true);
	        mp.start();
	    } catch (Exception e) {
	    	Log.e(TAG+"MediaPlayer", "MediaPlayer "+e);
	        e.printStackTrace();
	    }
	}

	void detectAndDisplay( MatOfRect faces, CascadeClassifier cc ){
		cc.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
			new Size(0, 0), new Size());
		cc.detectMultiScale(mGray, faces);
		Rect[] facesArray = faces.toArray();

		for (int i = 0; i < facesArray.length; i++){
			Point centerPoint = new Point ( facesArray[i].x + facesArray[i].width*0.5, facesArray[i].y + facesArray[i].height*0.5 );
			//Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
			//Core.circle(mRgba, center, facesArray[i].width/2, new Scalar( 255, 0, 255));
			Point textPoint = new Point ( facesArray[i].x + facesArray[i].width*0.2, facesArray[i].y + facesArray[i].height*0.5 );

			Core.ellipse( mRgba, centerPoint, new Size( facesArray[i].width*0.5, facesArray[i].height*0.5), 0, 0, 360, new Scalar( 0, 255, 0 ), 4, 8, 0 );
			Core.putText(mRgba, "Sign", textPoint, 2, 1, new Scalar(255, 255, 0), 3);
			
			
			new DownloadFilesTask("stop.png").execute();


			

			Log.e(TAG, "Detection method tapty!");
		}
	}
}
