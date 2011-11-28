package ee.ut.cs.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PhotoSharingActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setNumPictures(0);
    }
    
    private void setNumPictures(int n) {
        TextView imagesText = (TextView) findViewById(R.id.imagesText);
        imagesText.setText("Pictures (" + n + ")");
    }
}