package ee.ut.cs.mobile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
	
	Bitmap toSephia(Bitmap bmpOriginal) {
		
		int width, height, r,g, b, c, gry; 
		height = bmpOriginal.getHeight(); 
		width = bmpOriginal.getWidth(); 
		int depth = 20;
		
		Bitmap bmpSephia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bmpSephia);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setScale(.3f, .3f, .3f, 1.0f);   
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    canvas.drawBitmap(bmpOriginal, 0, 0, paint);
	    for(int x=0; x < width; x++) {
	        for(int y=0; y < height; y++) {
	            c = bmpOriginal.getPixel(x, y);

	            r = Color.red(c);
	            g = Color.green(c);
	            b = Color.blue(c);

	            gry = (r + g + b) / 3;
	            r = g = b = gry;

	            r = r + (depth * 2);
	            g = g + depth;

	            if(r > 255) {
	              r = 255;
	            }
	            if(g > 255) {
	              g = 255;
	            }
	            bmpSephia.setPixel(x, y, Color.rgb(r, g, b));
	        }
	    }      
	    return bmpSephia;
	}
	
	public static Bitmap doBrightness(Bitmap src, int value) {
		    // image size
		    int width = src.getWidth();
		    int height = src.getHeight();
		    // create output bitmap
		    Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		    // color information
		    int A, R, G, B;
		    int pixel;
		    // scan through all pixels
		    for(int x = 0; x < width; ++x) {
		        for(int y = 0; y < height; ++y) {
		            // get pixel color
		            pixel = src.getPixel(x, y);
		            A = Color.alpha(pixel);
		            R = Color.red(pixel);
		            G = Color.green(pixel);
		            B = Color.blue(pixel);
		            // increase/decrease each channel
		            R += value;
		            if(R > 255) { R = 255; }
		            else if(R < 0) { R = 0; }
		            G += value;
		            if(G > 255) { G = 255; }
		            else if(G < 0) { G = 0; }
		            B += value;
		            if(B > 255) { B = 255; }
		            else if(B < 0) { B = 0; }
		            // apply new pixel color to output bitmap
		            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
		        }
		    }
		    // return final image
		    return bmOut;
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
		menu.add(0, 1, 1, "To Sephia");
		menu.add(0, 2, 2, "Rotate 90CW");
		menu.add(0, 3, 3, "Save");
		menu.add(0, 4, 4, "Undo");
		menu.add(0,5,5, "Brighten up");
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
			bitmap = toSephia(bitmap);
			imageView.setImageBitmap(bitmap);
			break;
		case 2:
			undoList.add(0, bitmap);
	        bitmap = toRotate(bitmap);
	        imageView.setImageBitmap(bitmap);
	        break;
		case 3:
			MediaManager.saveBitmapImage(bitmap, "testImage" + Calendar.getInstance().getTimeInMillis() + ".png", this);
			break;
		case 4:
			undo();
			break;
		case 5:
			undoList.add(0, bitmap);
			bitmap = doBrightness(bitmap, 33);
			imageView.setImageBitmap(bitmap);
			break;
		}

		return true;
	}
	
}
