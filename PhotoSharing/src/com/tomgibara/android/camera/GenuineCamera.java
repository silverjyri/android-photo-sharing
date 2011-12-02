package com.tomgibara.android.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

	private Camera device = null;
	
	public GenuineCamera() {
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
			Matrix mat = new Matrix();
	        mat.postRotate(90);
	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
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
			if (previewSize == null) {
				Log.d(LOG_TAG, "No best preview size found");
				return;
			}
			params.setPreviewSize(previewSize.width, previewSize.height);
			params.setPictureSize(pictureSize.width, pictureSize.height);
			device.setParameters(params);
			device.setDisplayOrientation(90);
			
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
