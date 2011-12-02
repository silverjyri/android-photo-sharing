package ee.ut.cs.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class MediaManager {
	
	private static File getExternalStoragePath() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

    public static File getImageStoragePath(Context context) {
    	return getExternalStoragePath();

    	// Internal storage location
    	//return new File(context.getApplicationContext().getFilesDir() + "/images");
    }
	
    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(Context context) {
        File mediaStorageDir = getImageStoragePath(context); 

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("PhotoSharing", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
    
    /** Create a file Uri for saving an image */
    public static Uri getOutputMediaFileUri(Context context) {
          return Uri.fromFile(getOutputMediaFile(context));
    }
    
    public static File[] getPictureFiles(Context context) {
		return getImageStoragePath(context).listFiles();
    }
    
    public static void saveBitmapImage(Bitmap bitmap, Uri uri, Context context) {
        OutputStream os;
		try {
			os = new BufferedOutputStream(new FileOutputStream(uri.getPath(), true));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
	        os.flush();
	        os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void saveBitmapImage(Bitmap bitmap, String name, Context context) {
        File path = getImageStoragePath(context);
        path.mkdirs();
        saveBitmapImage(bitmap, Uri.parse(path.getPath() + File.separator + name), context);
    }
}
