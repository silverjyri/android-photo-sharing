package ee.ut.cs.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class MediaManager {
	
	private static File currentStoragePath;
	
	public static File getCurrentStoragePath() {
		return currentStoragePath;
	}

	public static void setCurrentStoragePath(File currentStoragePath) {
		if (!currentStoragePath.exists()) {
			return;
		}
		MediaManager.currentStoragePath = currentStoragePath;
	}

	private static File getExternalStoragePath() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

	private static File getExternalStorageDownloadsPath() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}

    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	}
    
	private static File getExternalStorageDCIMPath() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}

    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	}
	
	private static File getExternalStorageBluetoothDownloadsPath() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}

    	return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/bluetooth");
	}
	
	private static File getInternalStoragePath(Context context) {
		return new File(context.getApplicationContext().getFilesDir() + "/images");
	}
	
    public static File getImageStoragePath(Context context) {
    	if (currentStoragePath != null && currentStoragePath.exists())
    		return currentStoragePath;
    	
    	currentStoragePath = getExternalStoragePath();
    	if (currentStoragePath != null && currentStoragePath.exists()) {
    		return currentStoragePath;
    	}

		return getInternalStoragePath(context);
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
    	File[] files = getImageStoragePath(context).listFiles();
    	ArrayList<File> pictures = new ArrayList<File>();
    	for (File p : files) {
    		String fileName = p.getAbsolutePath();
    		int mid= fileName.lastIndexOf(".");
    		String ext = fileName.substring(mid+1,fileName.length());
    		if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif")) {
    			pictures.add(p);
    		}
    	}
    	File[] picturesArray = new File[pictures.size()];
    	pictures.toArray(picturesArray);
    	return picturesArray;
    }
    
    public static void saveBitmapImage(Bitmap bitmap, Uri uri, Context context) {
        OutputStream os;
		try {
			os = new BufferedOutputStream(new FileOutputStream(uri.getPath(), true));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
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
    
    public static String[] getAlbumList(Context context) {
    	ArrayList<String> albums = new ArrayList<String>();
    	
    	File path = getInternalStoragePath(context);
    	if (path != null && path.exists()) {
    		albums.add(path.getAbsolutePath());
    	}
    	
    	path = getExternalStoragePath();
    	if (path != null && path.exists()) {
    		albums.add(path.getAbsolutePath());
    	}
    	
    	path = getExternalStorageDownloadsPath();
    	if (path != null && path.exists()) {
    		albums.add(path.getAbsolutePath());
    	}
    	
    	path = getExternalStorageBluetoothDownloadsPath();
    	if (path != null && path.exists()) {
    		albums.add(path.getAbsolutePath());
    	}
    	
    	path = getExternalStorageDCIMPath();
    	if (path != null && path.exists()) {
    		albums.add(path.getAbsolutePath());
    	}
    	
    	String aa[] = new String[albums.size()];
    	albums.toArray(aa);
    	return aa;
    }
}
