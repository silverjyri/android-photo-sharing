package com.tomgibara.android.camera;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * A CameraSource implementation that obtains its bitmaps via an HTTP request
 * to a URL.
 * 
 * @author Tom Gibara
 *
 */

public class HttpCamera implements CameraSource {

	
	private static final int CONNECT_TIMEOUT = 1000;
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final String url;
	private final Rect bounds;
	private final Paint paint = new Paint();

	public HttpCamera(String url, int width, int height) {
		this.url = url;
		bounds = new Rect(0, 0, width, height);
		
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
	}
	
	public int getWidth() {
		return bounds.right;
	}
	
	public int getHeight() {
		return bounds.bottom;
	}
	
	public boolean open() {
		/* nothing to do */
		return true;
	}

	public void startPreview(SurfaceHolder holder) {
	}
	
	public void stopPreview() {
	}
	
	public void capture(CameraCallback callback) {
		Bitmap bitmap = null;
		try {
			InputStream in = null;
			int response = -1;
			try {
				//we use URLConnection because it's anticipated that it is lighter-weight than HttpClient
				//NOTE: At this time, neither properly support connect timeouts
				//as a consequence, this implementation will hang on a failure to connect
				URL url = new URL(this.url);
				URLConnection conn = url.openConnection();
				if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP connection.");
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setConnectTimeout(CONNECT_TIMEOUT);
				httpConn.setReadTimeout(SOCKET_TIMEOUT);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				response = httpConn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();
					bitmap = BitmapFactory.decodeStream(in);
				} else {
					Log.d("HttpCamera", "HTTP result: " + response);
				}
				callback.onPictureTaken(bitmap);
			} finally {
				if (in != null) try {
					in.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
			
			if (bitmap == null) throw new IOException("Response Code: " + response);

		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
		}
	}

	public void close() {
		/* nothing to do */
	}

}
