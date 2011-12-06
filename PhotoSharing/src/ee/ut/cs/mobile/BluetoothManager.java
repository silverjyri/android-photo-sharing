package ee.ut.cs.mobile;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothManager {
	private static BluetoothAdapter mBluetoothAdapter;
	
	public static boolean isBluetoothSupported() {
		if (mBluetoothAdapter != null) {
			return true;
		}
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return mBluetoothAdapter != null;
	}
	
	public static boolean isEnabled() {
		if (mBluetoothAdapter == null) {
			return false;
		}
		return mBluetoothAdapter.isEnabled();
	}
	
	public static void enable(Activity activity, int requestCode) {
		if (!isBluetoothSupported()) {
			return;
		}

		if (isEnabled()) {
			return;
		}
		
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, requestCode);
	}
	
	public static Set<BluetoothDevice> getBondedDevices() {
		if (!isBluetoothSupported()) {
			return new HashSet<BluetoothDevice>();
		}
		
		return mBluetoothAdapter.getBondedDevices();
	}
	
	public static void ensureDiscoverable(Activity activity) {
		if (!isEnabled()) {
			return;
		}
		
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			activity.startActivity(discoverableIntent);
		}
	}

	public static void startDeviceSearch(Activity activity, BroadcastReceiver receiver) {
		if (!isEnabled()) {
			return;
		}
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		activity.registerReceiver(receiver, filter); // Don't forget to call stopDeviceSearch
		
		mBluetoothAdapter.startDiscovery();
	}
	
	public static void stopDeviceSearch(Activity activity, BroadcastReceiver receiver) {
		if (!isEnabled()) {
			return;
		}
		
		activity.unregisterReceiver(receiver);
	}
}
