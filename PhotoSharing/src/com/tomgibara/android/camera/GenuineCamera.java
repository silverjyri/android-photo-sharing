package com.tomgibara.android.camera;

import java.io.IOException;

import android.graphics.Bitmap;
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
	private final SurfaceHolder surface;
	
	private Camera device = null;
	
	public GenuineCamera(int width, int height, SurfaceHolder surface) {
		this.width = width;
		this.height = height;
		this.surface = surface;
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
	
	public Bitmap capture() {
		if (device == null) return null;
		try {
			device.setPreviewDisplay(surface);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		device.startPreview();
		return null;
	}

}
