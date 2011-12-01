package com.tomgibara.android.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
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
	
		Camera.Parameters params = device.getParameters();
		//params.setPictureSize(width, height);
		//params.setPreviewSize(width, height);
		device.setParameters(params);
		
		return true;
	}
	
	public void close() {
		if (device == null) return;
		stopPreview();
		device.release();
		device = null;
	}
	
	public class PictureCallback2 implements PictureCallback {
		
		private CameraCallback callback;
		
		public PictureCallback2(CameraCallback callback) {
			this.callback = callback;
		}
		
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			bitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
			if (bitmap == null) {
				Log.e(LOG_TAG, "Image not decodable!");
			}
			callback.onPictureTaken(bitmap);
		}
		
		public Bitmap bitmap;
	}
	
	public void startPreview(SurfaceHolder holder) {
		try {
			device.setPreviewDisplay(holder);
			device.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopPreview() {
		device.stopPreview();
	}
	
	public void capture(CameraCallback callback) {
		if (device == null)
			return;
		PictureCallback2 pcb = new PictureCallback2(callback);
		device.takePicture(null, null, pcb);
	}



}
