package ee.ut.cs.mobile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class PictureEditorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		
		Uri uri = getIntent().getData();
		ImageView image = (ImageView) findViewById(R.id.editorImageView);
		image.setImageURI(uri);
	}
	
}
