package ee.ut.cs.mobile;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShareActivity extends Activity {
	
	private static final int REQUEST_ENABLE_BT = 100;
	
	private ArrayList<String> items = new ArrayList<String>();
	private ArrayAdapter<String> itemAdapter;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    Toast.makeText(this, "Bluetooth not supported!", Toast.LENGTH_LONG).show();
		    finish();
		    return;
		}

        ListView shareList = (ListView) findViewById(R.id.shareList);
		itemAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		shareList.setAdapter(itemAdapter);
		registerForContextMenu(shareList);
		shareList.setClickable(true);

		if (mBluetoothAdapter.isEnabled()) {
			searchBluetoothDevices();
		} else {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
    }
    
    private void searchBluetoothDevices() {
    	if (mBluetoothAdapter == null) {
    		return;
    	}
    	
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	itemAdapter.add(device.getName() + "\n" + device.getAddress());
		    }
		}
		
		// Create a BroadcastReceiver for ACTION_FOUND
		mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            String deviceString = device.getName() + "\n" + device.getAddress();
		            if (!items.contains(deviceString)) {
		            	itemAdapter.add(device.getName() + "\n" + device.getAddress());
		            }
		        }
		    }
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		
		mBluetoothAdapter.startDiscovery();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_ENABLE_BT) {
    		searchBluetoothDevices();
    		return;
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
    	unregisterReceiver(mReceiver);
    	super.onDestroy();
    }
    
	public void ensureDiscoverable() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivity(discoverableIntent);
        }
}
