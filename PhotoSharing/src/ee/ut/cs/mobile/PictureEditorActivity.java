package ee.ut.cs.mobile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
	
	private Uri uri;
	private ImageView imageView;
	private Bitmap bitmap = null;
	private ArrayList<Bitmap> undoList = new ArrayList<Bitmap>();
	private ShakeListener mShaker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);

		uri = getIntent().getData();
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		imageView = (ImageView) findViewById(R.id.editorImageView);
		imageView.setImageBitmap(bitmap);
		registerForContextMenu(imageView);
		imageView.setClickable(true);
		
		
		final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		mShaker = ShakeListener.Create(this);
		if (mShaker == null) {
			return;
		}
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				vibe.vibrate(100);
				undo();
			}
		});
	}
	
	private void undo() {
		if (undoList.size() == 0) {
			return;
		}
		bitmap.recycle();
		bitmap = undoList.get(0);
		undoList.remove(0);
		imageView.setImageBitmap(bitmap);
	}
	
	 @Override
	  public void onResume()
	  {
		 if (mShaker != null) {
			 mShaker.resume();
		 }
	    super.onResume();
	  }
	  @Override
	  public void onPause()
	  {
		  if (mShaker != null) {
			  mShaker.pause();
		  }
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
	
	public static Bitmap toRotate(Bitmap bmpOriginal)
	{
        Matrix mat = new Matrix();
        mat.postRotate(90);
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), mat, true);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Activities");
		menu.add(0, 0, 0, "To Grayscale");
		menu.add(0, 1, 1, "Rotate 90CW");
		menu.add(0, 2, 2, "Save");
		menu.add(0, 3, 3, "Undo");
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {

		switch (menuItem.getItemId()) {
		case 0:
			undoList.add(0, bitmap);
			bitmap = toGrayscale(bitmap);
			imageView.setImageBitmap(bitmap);
			break;
		case 1:
			undoList.add(0, bitmap);
	        bitmap = toRotate(bitmap);
	        imageView.setImageBitmap(bitmap);
	        break;
		case 2:
			MediaManager.saveBitmapImage(bitmap, "testImage" + Calendar.getInstance().getTimeInMillis() + ".png", this);
			break;
		case 3:
			undo();
			break;
		}

		return true;
	}
	
}
