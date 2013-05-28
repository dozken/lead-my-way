package org.opencv.samples.facedetect;

import java.io.File;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class FdActivity extends Activity implements CvCameraViewListener2 {

	private static final String    TAG                 = "OCVSample::Activity";

	private Mat                    mRgba;
	private Mat                    mGray;

	private CascadeClassifier      cc1;
	private CascadeClassifier      cc2;

	private CameraBridgeViewBase   mOpenCvCameraView;

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
					// load cascade file from application resources
					InputStream is1 = getResources().openRawResource(R.raw.stop);
					File cascadeDir1 = getDir("cascade", Context.MODE_PRIVATE);
					File mCascadeFile1 = new File(cascadeDir1, "stop.xml");
					FileOutputStream os1 = new FileOutputStream(mCascadeFile1);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is1.read(buffer)) != -1) {
						os1.write(buffer, 0, bytesRead);
					}
					is1.close();
					os1.close();

					cc1 = new CascadeClassifier(mCascadeFile1.getAbsolutePath());
					cascadeDir1.delete();

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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.face_detect_surface_view);

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

		detectAndDisplay(faces,cc1);
		detectAndDisplay(faces,cc2);

		return mRgba;
	}
	
	static boolean flag = true;
	
	void detectAndDisplay( MatOfRect faces, CascadeClassifier cc ){
		//cc.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
			//	new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
		cc.detectMultiScale(mGray, faces);
		Rect[] facesArray = faces.toArray();
		
		for (int i = 0; i < facesArray.length; i++){
			Point centerPoint = new Point ( facesArray[i].x + facesArray[i].width*0.5, facesArray[i].y + facesArray[i].height*0.5 );
			//Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
			//Core.circle(mRgba, center, facesArray[i].width/2, new Scalar( 255, 0, 255));
			Point textPoint = new Point ( facesArray[i].x + facesArray[i].width*0.2, facesArray[i].y + facesArray[i].height*0.5 );
			
			Core.ellipse( mRgba, centerPoint, new Size( facesArray[i].width*0.5, facesArray[i].height*0.5), 0, 0, 360, new Scalar( 0, 255, 0 ), 4, 8, 0 );
			Core.putText(mRgba, "Sign", textPoint, 2, 1, new Scalar(255, 255, 0), 3);

			if(!flag){
				FdActivity.flag = false;
				Intent inf=new Intent(FdActivity.this,Activityfullscreen.class);

				startActivity(inf);
			}

			//MatOfInt p = new MatOfInt(100);
			//Highgui.imwrite("/sdcard/Download/image_" + Integer.toString(i) + ".jpg", mRgba.submat(facesArray[i]), p);
			//Toast.makeText(this, "yeaho", 1000);
			Log.e("tapty", "Detection method tapty!");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		/*if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else
        if (item == mItemType) {
            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorNames[mDetectorType]);
            setDetectorType(mDetectorType);
        }
		 */
		return true;
	}

}
