package ee.ut.cs.mobile;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;

public class PictureEditorActivity extends Activity {
	
	Uri uri;
	ImageView image;
	Bitmap bitmap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		
		uri = getIntent().getData();
		image = (ImageView) findViewById(R.id.editorImageView);
		image.setImageURI(uri);
		registerForContextMenu(image);
		image.setClickable(true);		
	}
	
	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Activities");
		menu.add(0, 0, 0, "To Grayscale");
		menu.add(0, 1, 1, "Save");
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		
		switch (menuItem.getItemId()) {
		case 0:
			try {
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image.setImageBitmap(toGrayscale(bitmap));
			break;
		case 1:
			break;
		}

		return true;
	}
	
}
