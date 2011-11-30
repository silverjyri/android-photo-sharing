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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoSharingActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	ArrayList<Uri> pictures = new ArrayList<Uri>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
	            // create Intent to take a picture and return control to the calling application
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	            Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

	            // start the image capture Intent
	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});

        ListView pictureList = (ListView) findViewById(R.id.pictureList);
        PictureListAdapter adapter = new PictureListAdapter(this, R.layout.picturelistitem, R.id.textid, pictures);
        pictureList.setAdapter(adapter);


        // Store a test image into internal storage
        Bitmap testImage = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        OutputStream os;
        String path = getApplicationContext().getFilesDir() + "/images/";
		try {
			File file = new File(path);
			file.mkdirs();
			os = new BufferedOutputStream(new FileOutputStream(path + "testImage.jpg", true));
	        testImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
	        os.flush();
	        os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		// Read images from the storage
		File file = new File(path);
		File[] files = file.listFiles();
		for (File f : files) {
			Uri uri = Uri.fromFile(f);
	    	pictures.add(uri);
		}
    	adapter.notifyDataSetChanged();
        
        setNumPictures(pictures.size());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                //Toast.makeText(this, "Image saved to:\n" +
                //         data.getData(), Toast.LENGTH_LONG).show();
				Toast.makeText(this,
						(data == null) ? "Result: null" : "Result: " + data.getData(),
						Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /** Create a file Uri for saving an image */
    private static Uri getOutputMediaFileUri(){
          return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "PhotoSharing");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

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