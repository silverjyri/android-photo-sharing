package ee.ut.cs.mobile;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoSharingActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int EDIT_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	
	private ArrayList<Uri> pictures = new ArrayList<Uri>();
	private PictureListAdapter adapter;
	private ListView pictureList;
	
	private Button selectAlbumButton;
	private View contextMenuSource;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Context context = this;

        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
	            Uri fileUri = MediaManager.getOutputMediaFileUri(context); // create a file to save the image
	            
	            // Use CameraSource to capture images
				Intent capture = new Intent(context, PictureCaptureActivity.class);
				capture.setData(fileUri);
		        startActivityForResult(capture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

		        /*
	            // Use the default camera intent to capture images

	            // create Intent to take a picture and return control to the calling application
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

	            // start the image capture Intent
	            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	            */
			}
		});
        
        selectAlbumButton = (Button) findViewById(R.id.selectAlbum);
        selectAlbumButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
		        registerForContextMenu(selectAlbumButton);
				openContextMenu(selectAlbumButton);
			}
        });

        pictureList = (ListView) findViewById(R.id.pictureList);
        adapter = new PictureListAdapter(this, R.layout.picturelistitem, R.id.textid, pictures);
        pictureList.setAdapter(adapter);
        registerForContextMenu(pictureList);
        pictureList.setClickable(true);


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
		contextMenuSource = v;
		if (v == pictureList) {
			menu.setHeaderTitle(pictures.get(info.position).toString());
			menu.add(Menu.NONE, 0, 0, R.string.edittext);
			menu.add(Menu.NONE, 1, 1, R.string.sendtext);
			menu.add(Menu.NONE, 2, 2, R.string.deletetext);
		} else if (v == selectAlbumButton) {
			menu.setHeaderTitle(R.string.selectAlbum);
			String[] albums = MediaManager.getAlbumList(this);
			for (String s : albums) {
				menu.add(s);
			}
		}
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		if (contextMenuSource == pictureList) {
			Uri item = pictures.get(info.position);
			
			switch (menuItem.getItemId()) {
			case 0:
				Intent editorActivity = new Intent(this, PictureEditorActivity.class);
				editorActivity.setData(item);
		        startActivityForResult(editorActivity, EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case 1:
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				share.putExtra(Intent.EXTRA_STREAM, item);
				startActivity(Intent.createChooser(share, "Send" + item.toString()));
				break;
			case 2:
				pictures.remove(info.position);
				adapter.notifyDataSetChanged();
				setNumPictures(pictures.size());
				File file = new File(item.getPath());
				file.delete();
				break;
			}
		} else if (contextMenuSource == selectAlbumButton){
			MediaManager.setCurrentStoragePath(new File(menuItem.toString()));
			readPicturesList();
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