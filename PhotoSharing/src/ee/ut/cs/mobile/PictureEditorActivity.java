package ee.ut.cs.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
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
		File path = getImageStoragePath();
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
			
			OutputStream os;
		    File path = getImageStoragePath();
		    try {
				path.mkdirs();
				os = new BufferedOutputStream(new FileOutputStream(path.getPath() + File.separator + "testImage" + Calendar.getInstance().getTimeInMillis() + ".png", true));
			    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			    os.flush();
			    os.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			break;
		}

		return true;
	}
	
	private File getImageStoragePath() {
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    	
    	//return new File(getApplicationContext().getFilesDir() + "/images");
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        PhotoSharingActivity.adapter.notifyDataSetChanged();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	
}
