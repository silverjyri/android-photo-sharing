package ee.ut.cs.mobile;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.tomgibara.android.camera.CameraSource;
import com.tomgibara.android.camera.HttpCamera;

public class PictureCaptureActivity extends Activity {
	CameraSource cameraSource;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        ImageView captureView = (ImageView) findViewById(R.id.captureImageView);

        //cameraSource = new SocketCamera("192.168.1.75", 8082, surfaceView.getWidth(), surfaceView.getHeight(), true);
        //cameraSource = new GenuineCamera(surfaceView.getWidth(), surfaceView.getHeight());
        //cameraSource = new HttpCamera("http://192.168.1.75:8888", surfaceView.getWidth(), surfaceView.getHeight(), true);
        cameraSource = new HttpCamera("http://anthrax11.homeip.net:8888/out.jpg", captureView.getWidth(), captureView.getHeight());

        Bitmap bitmap = cameraSource.capture();
		if (bitmap != null) {
			captureView.setImageBitmap(bitmap);
			
        	Uri data = getIntent().getData();
        	Log.d("PictureCapture", data.getPath());
        	
            OutputStream os;
    		try {
    			os = new BufferedOutputStream(new FileOutputStream(data.getPath(), true));
    			if (bitmap != null) {
    				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        	        os.flush();
        	        os.close();
    				setResult(RESULT_OK, getIntent());
    		        finish();
    			}
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }

    }

}
