package ee.ut.cs.mobile;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class PictureListAdapter extends ArrayAdapter<Uri> {

	private LayoutInflater mInflater;
	
	public PictureListAdapter(Context context, int resource,
			int textViewResourceId, List<Uri> objects) {
		super(context, resource, textViewResourceId, objects);

		mInflater = (LayoutInflater) context.getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Uri pictureFile = getItem(position);
		
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.picturelistitem, null);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.listpicture);
		
		image.setImageBitmap(getPreview(pictureFile));
		//image.setImageURI(pictureFile);
		
		return convertView;
	}

	Bitmap getPreview(Uri uri) {
	    File image = new File(uri.getPath());
	    final int THUMBNAIL_SIZE = 192;

	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
	            : bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
	    return BitmapFactory.decodeFile(image.getPath(), opts);
	}
}
