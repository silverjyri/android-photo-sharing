package ee.ut.cs.mobile;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
	private static final int EDIT_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
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
	            Uri fileUri = MediaManager.getOutputMediaFileUri(context); // create a file to save the image
	            
				Intent capture = new Intent(context, PictureCaptureActivity.class);
				capture.setData(fileUri);
		        startActivityForResult(capture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				/*
	            // create Intent to take a picture and return control to the calling application
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

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


        // Store a test image
        Bitmap testImage = BitmapFactory.decodeResource(getResources(),R.drawable.test);
        MediaManager.saveBitmapImage(testImage, "testImage.jpg", this);

		readPicturesList();
    }
    
    void readPicturesList() {
    	pictures.clear();
    	
		File[] files = MediaManager.getPictureFiles(this);
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
			Intent shareActivity = new Intent(this, ShareActivity.class);
			shareActivity.setData(item);
	        startActivity(shareActivity);
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
            	if (data == null) {
            		Toast.makeText(this, "Failed to capture image!", Toast.LENGTH_LONG).show();
            		return;
            	}
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                         data.getData(), Toast.LENGTH_LONG).show();
                pictures.add(0, data.getData());
                adapter.notifyDataSetChanged();
                setNumPictures(pictures.size());
            } else if (resultCode == RESULT_CANCELED) {
            	Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EDIT_IMAGE_ACTIVITY_REQUEST_CODE) {
        	readPicturesList();
        }
    }

    private void setNumPictures(int n) {
        TextView imagesText = (TextView) findViewById(R.id.imagesText);
        imagesText.setText("Pictures (" + n + ")");
    }
}