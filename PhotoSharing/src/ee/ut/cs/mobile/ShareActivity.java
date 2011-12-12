package ee.ut.cs.mobile;

import java.util.ArrayList;
import java.util.Set;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ShareActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 100;

	private ListView shareList;
	private ArrayList<BluetoothDeviceItem> items = new ArrayList<BluetoothDeviceItem>();
	private ArrayAdapter<BluetoothDeviceItem> itemAdapter;

	private BroadcastReceiver mReceiver;
	
	private ProgressBar scanProgress;
	private TimerTask scanProgressTimerTask;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);

		if (!BluetoothManager.isBluetoothSupported()) {
			Toast.makeText(this, "Bluetooth not supported!", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		shareList = (ListView) findViewById(R.id.shareList);
		itemAdapter = new ArrayAdapter<BluetoothDeviceItem>(this,
				android.R.layout.simple_list_item_1, items);
		shareList.setAdapter(itemAdapter);
		registerForContextMenu(shareList);
		shareList.setClickable(true);
		shareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				stopDeviceSearch();
				BluetoothDevice device = items.get(position).getDevice();
			}
		});

		BluetoothManager.enable(this, REQUEST_ENABLE_BT);
		if (BluetoothManager.isEnabled()) {
			searchBluetoothDevices();
		}

		final Activity activity = this;
		Button discoverable = (Button) findViewById(R.id.discoverable);
		discoverable.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				BluetoothManager.ensureDiscoverable(activity);
			}
		});
	}

	private void searchBluetoothDevices() {
		Set<BluetoothDevice> pairedDevices = BluetoothManager.getBondedDevices();

		// Loop through paired devices
		for (BluetoothDevice device : pairedDevices) {
			// Add the device to an array adapter to show in a ListView
			itemAdapter.add(new BluetoothDeviceItem(device));
		}

		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

					// Add the device to an array adapter to show in a ListView
					boolean exists = false;
					for (BluetoothDeviceItem d : items) {
						if (d.getDevice().getAddress().equals(device.getAddress())) {
							exists = true;
							break;
						}
					}
					if (!exists) {
						itemAdapter.add(new BluetoothDeviceItem(device));
					}
				}
			}
		};
		BluetoothManager.startDeviceSearch(this, mReceiver);
		
		scanProgress = (ProgressBar)findViewById(R.id.progressBar1);
		scanProgressTimerTask = new TimerTask() {
			@Override
			public void run() {
				int progress = scanProgress.getProgress();
				if (progress == scanProgress.getMax()){
					stopDeviceSearch();
				} else {
					mHandler.postDelayed(scanProgressTimerTask, 100);
					scanProgress.setProgress(progress + 1);
				}
			}
		};
		
		mHandler.removeCallbacks(scanProgressTimerTask);
        mHandler.postDelayed(scanProgressTimerTask, 100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			searchBluetoothDevices();
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void stopDeviceSearch() {
		findViewById(R.id.scanningText).setVisibility(View.GONE);
		scanProgress.setVisibility(View.GONE);
		scanProgress.setProgress(0);
		
		mHandler.removeCallbacks(scanProgressTimerTask);
		if (mReceiver != null) {
			BluetoothManager.stopDeviceSearch(this, mReceiver);
		}
		mReceiver = null;
	}
	
	@Override
	protected void onDestroy() {
		stopDeviceSearch();
		super.onDestroy();
	}
}
