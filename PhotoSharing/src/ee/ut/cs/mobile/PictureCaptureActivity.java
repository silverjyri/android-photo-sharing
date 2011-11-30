package ee.ut.cs.mobile;

import com.tomgibara.android.camera.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

public class PictureCaptureActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //CameraSource cameraSource = new SocketCamera("192.168.1.75", 8082, surfaceView.getWidth(), surfaceView.getHeight(), true);
        //CameraSource cameraSource = new GenuineCamera(surfaceView.getWidth(), surfaceView.getHeight());
        CameraSource cameraSource = new HttpCamera("192.168.1.75:8888", surfaceView.getWidth(), surfaceView.getHeight(), true);
        cameraSource.capture(surfaceView.getHolder());
    }

}
