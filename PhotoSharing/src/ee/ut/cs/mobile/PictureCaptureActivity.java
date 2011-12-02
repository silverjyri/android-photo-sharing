package ee.ut.cs.mobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tomgibara.android.camera.CameraSource;
import com.tomgibara.android.camera.CameraSource.CameraCallback;
import com.tomgibara.android.camera.GenuineCamera;

public class PictureCaptureActivity extends Activity implements SurfaceHolder.Callback, CameraCallback {
	CameraSource cameraSource;
	
	SurfaceView captureView;
	
	@Override
	public void onBackPressed() {
		cameraSource.close();
		super.onBackPressed();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (cameraSource == null || event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}
		
		cameraSource.capture(this);
		return true;
	}

	public void onPictureTaken(Bitmap bitmap) {
		cameraSource.close();
    	cameraSource = null;
		if (bitmap != null) {
        	Uri data = getIntent().getData();
        	MediaManager.saveBitmapImage(bitmap, data, this);
        	setResult(RESULT_OK, getIntent());
        	finish();
        }
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        //cameraSource = new SocketCamera("192.168.1.75", 8082, surfaceView.getWidth(), surfaceView.getHeight(), true);
        cameraSource = new GenuineCamera();
        //cameraSource = new HttpCamera("http://192.168.1.75:8888", surfaceView.getWidth(), surfaceView.getHeight(), true);
        //cameraSource = new HttpCamera("http://anthrax11.homeip.net:8888/out.jpg", captureView.getWidth(), captureView.getHeight());
        
        if (cameraSource.open()) {
            captureView = (SurfaceView) findViewById(R.id.captureSurfaceView);
        	SurfaceHolder holder = captureView.getHolder();
        	holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
       	cameraSource.startPreview(holder);
    	holder.removeCallback(this);
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
	}
}
