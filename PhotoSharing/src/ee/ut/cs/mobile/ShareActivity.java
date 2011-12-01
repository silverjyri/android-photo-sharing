package ee.ut.cs.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShareActivity extends Activity {
	
	ArrayList<String> items = new ArrayList<String>();
	ArrayAdapter<String> itemAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        ListView shareList = (ListView) findViewById(R.id.shareList);
        
		itemAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		shareList.setAdapter(itemAdapter);
		registerForContextMenu(shareList);
		shareList.setClickable(true);
		
		items.add("Test Device 1");
		items.add("Andres@BT");
		items.add("Android phone");
		itemAdapter.notifyDataSetChanged();
    }
}
