package ee.ut.cs.mobile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class PictureEditorActivity extends Activity {
	
	Uri uri;
	ImageView imageView;
	Bitmap bitmap;
	Bitmap oldBitmap;
	private ShakeListener mShaker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		
		uri = getIntent().getData();
		imageView = (ImageView) findViewById(R.id.editorImageView);
		imageView.setImageURI(uri);
		registerForContextMenu(imageView);
		imageView.setClickable(true);
		
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			oldBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
		} catch (FileNotFoundException e) {
			bitmap = null;
			oldBitmap = null;
			e.printStackTrace();
		} catch (IOException e) {
			bitmap = null;
			oldBitmap = null;
			e.printStackTrace();
		}
		
		final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		mShaker = new ShakeListener(this);
	    mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
	      public void onShake()
	      {
	        vibe.vibrate(100);
	        Bitmap temp = bitmap;
			bitmap = oldBitmap;
			oldBitmap = temp;
			temp = null;
			imageView.setImageBitmap(bitmap);
	      }
	    });
	    
	}
	
	 @Override
	  public void onResume()
	  {
	    mShaker.resume();
	    super.onResume();
	  }
	  @Override
	  public void onPause()
	  {
	    mShaker.pause();
	    super.onPause();
	  }
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
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
		menu.add(0, 1, 1, "Rotate 90CW");
		menu.add(0, 2, 2, "Save");
//		menu.add(0, 2, 2, "Undo");
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {

		switch (menuItem.getItemId()) {
		case 0:
			oldBitmap = bitmap;
			bitmap = toGrayscale(bitmap);
			imageView.setImageBitmap(bitmap);
			break;
		case 1:
			oldBitmap = bitmap;
			Matrix mat = new Matrix();
	        mat.postRotate(90);
	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
	        imageView.setImageBitmap(bitmap);
		case 2:
			MediaManager.saveBitmapImage(bitmap, "testImage" + Calendar.getInstance().getTimeInMillis() + ".jpg", this);
			break;
//		case 2:
//			Bitmap temp = bitmap;
//			bitmap = oldBitmap;
//			oldBitmap = temp;
//			temp = null;
//			imageView.setImageBitmap(bitmap);
//			break;
		}

		return true;
	}
	
}
