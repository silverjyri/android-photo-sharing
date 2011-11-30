package ee.ut.cs.mobile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoSharingActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	ArrayList<Uri> pictures = new ArrayList<Uri>();
	PictureListAdapter adapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        final Context context = this;
        takePictureButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
	            Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
	            //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
	            
				Intent capture = new Intent(context, PictureCaptureActivity.class);
				//capture.setData(fileUri);
		        startActivityForResult(capture, 0);
				/*
	            // create Intent to take a picture and return control to the calling application
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	            // start the image capture Intent
	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	            */
			}
		});

        ListView pictureList = (ListView) findViewById(R.id.pictureList);
        adapter = new PictureListAdapter(this, R.layout.picturelistitem, R.id.textid, pictures);
        pictureList.setAdapter(adapter);
        registerForContextMenu(pictureList);
        pictureList.setClickable(true);


        // Store a test image into internal storage
        Bitmap testImage = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        OutputStream os;
        File path = getImageStoragePath();
		try {
			path.mkdirs();
			os = new BufferedOutputStream(new FileOutputStream(path.getPath() + File.separator + "testImage.jpg", true));
	        testImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
	        os.flush();
	        os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		// Read images from the storage
		File[] files = path.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				Log.d("PhotoSharing", f.toString());
				Uri uri = Uri.fromFile(f);
				pictures.add(uri);
			}
		}
    	adapter.notifyDataSetChanged();
        
        setNumPictures(pictures.size());
    }
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(pictures.get(info.position).toString());
		menu.add(0, 0, 0, R.string.edittext);
		menu.add(0, 1, 1, R.string.sendtext);
		menu.add(0, 2, 2, R.string.deletetext);
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		
		Uri item = pictures.get(info.position);
		
		switch (menuItem.getItemId()) {
		case 0:
			Intent editorActivity = new Intent(this, PictureEditorActivity.class);
			editorActivity.setData(item);
	        startActivityForResult(editorActivity, 0);
			break;
		case 1:
			break;
		case 2:
			pictures.remove(info.position);
			adapter.notifyDataSetChanged();
			setNumPictures(pictures.size());
			File file = new File(item.getPath());
			file.delete();
			break;
		}

		return true;
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                         data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /** Create a file Uri for saving an image */
    private Uri getOutputMediaFileUri() {
          return Uri.fromFile(getOutputMediaFile());
    }

    private File getImageStoragePath() {
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    	
    	//return new File(getApplicationContext().getFilesDir() + "/images");
    }
    
    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.d("PhotoSharing", "SD card not mounted! State: " + Environment.getExternalStorageState());
    		return null;
    	}
    	
        File mediaStorageDir = getImageStoragePath(); 

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
    
    private void setNumPictures(int n) {
        TextView imagesText = (TextView) findViewById(R.id.imagesText);
        imagesText.setText("Pictures (" + n + ")");
    }
}