package com.tomgibara.android.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
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

	private int width;
	private int height;
	
	private Camera device = null;
	
	public GenuineCamera() {
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
			Rect rect = holder.getSurfaceFrame();
			
			Camera.Parameters params = device.getParameters();
			Camera.Size previewSize = getBestPreviewSize(rect.width(), rect.height(), params);
			Camera.Size pictureSize = getBestPictureSize(rect.width(), rect.height(), params);
			if (previewSize != null) {
				width = previewSize.width;
				height = previewSize.height;
				Log.d(LOG_TAG, Integer.toString(previewSize.width) + "xx" + Integer.toString(previewSize.height));
			} else {
				Log.d(LOG_TAG, "No best preview size found");
				int newWidth = (width > 1024) ? 1024 : width;
				height = (int)((float)height * ((float)newWidth / (float)width));
				width = newWidth;
			}
			//params.setPictureSize(width, height);
			params.setPreviewSize(width, height);
			params.setPictureSize(pictureSize.width, pictureSize.height);
			device.setParameters(params);
			
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

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		Log.d(LOG_TAG, Integer.toString(width) + "x" + Integer.toString(height));
		
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return result;
	}
	
	private Camera.Size getBestPictureSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		Log.d(LOG_TAG, Integer.toString(width) + "x" + Integer.toString(height));
		
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return result;
	}
}
