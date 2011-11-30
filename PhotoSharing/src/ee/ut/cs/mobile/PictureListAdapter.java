package ee.ut.cs.mobile;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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
		Uri pictureFileName = getItem(position);
		
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.picturelistitem, null);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.listpicture);
		image.setImageURI(pictureFileName);
		
		return convertView;
	}

}
