package ee.ut.cs.mobile;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceItem {
	private BluetoothDevice device;
	
	public BluetoothDevice getDevice() {
		return device;
	}

	public BluetoothDeviceItem(BluetoothDevice device) {
		this.device = device;
	}
	
	@Override
	public String toString() {
		return device.getName();
	}
}
