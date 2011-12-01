package ee.ut.cs.mobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.tomgibara.android.camera.CameraSource;
import com.tomgibara.android.camera.GenuineCamera;
import com.tomgibara.android.camera.HttpCamera;

public class PictureCaptureActivity extends Activity {
	CameraSource cameraSource;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        ImageView captureView = (ImageView) findViewById(R.id.captureImageView);

        //cameraSource = new SocketCamera("192.168.1.75", 8082, surfaceView.getWidth(), surfaceView.getHeight(), true);
        cameraSource = new GenuineCamera(captureView.getWidth(), captureView.getHeight());
        //cameraSource = new HttpCamera("http://192.168.1.75:8888", surfaceView.getWidth(), surfaceView.getHeight(), true);
        //cameraSource = new HttpCamera("http://anthrax11.homeip.net:8888/out.jpg", captureView.getWidth(), captureView.getHeight());

        Bitmap bitmap = cameraSource.capture();
		if (bitmap != null) {
			captureView.setImageBitmap(bitmap);
        	Uri data = getIntent().getData();
        	MediaManager.saveBitmapImage(bitmap, data, this);
        	setResult(RESULT_OK, getIntent());
        	finish();
        }
    }
}
