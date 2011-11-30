package com.tomgibara.android.camera;

import java.io.IOException;

import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * A CameraSource implementation that obtains its bitmaps directly from the
 * device camera.
 * 
 * @author Tom Gibara
 *
 */

public class GenuineCamera implements CameraSource {

	private final int width;
	private final int height;
	
	private Camera device = null;
	
	public GenuineCamera(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean open() {
		if (device != null) return true;
		device = Camera.open();
		if (device == null) return false;
		
		//parameters for the device mostly as specified in sample app
		Camera.Parameters params = device.getParameters();
		params.setPictureSize(width, height);
		device.setParameters(params);
	
		return true;
	}
	
	public void close() {
		if (device == null) return;
		device.release();
		device = null;
	}
	
	public boolean capture(SurfaceHolder surface) {
		if (device == null) return false;
		try {
			device.setPreviewDisplay(surface);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		device.startPreview();
		return true;
	}

}
