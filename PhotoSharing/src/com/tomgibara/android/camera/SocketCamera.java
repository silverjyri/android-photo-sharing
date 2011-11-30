package com.tomgibara.android.camera;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

/**
 * A CameraSource implementation that obtains its bitmaps via a TCP connection
 * to a remote host on a specified address/port.
 * 
 * @author Tom Gibara
 *
 */

public class SocketCamera implements CameraSource {

	
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final String address;
	private final int port;
	private final Rect bounds;
	private final Paint paint = new Paint();

	public SocketCamera(String address, int port, int width, int height) {
		this.address = address;
		this.port = port;
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


	public Bitmap capture() {
		Bitmap bitmap = null;
		Socket socket = null;
		try {
			socket = new Socket();
			socket.bind(null);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			socket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);

			//obtain the bitmap
			InputStream in = socket.getInputStream();
			bitmap = BitmapFactory.decodeStream(in);

		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				/* ignore */
			}
		}
		return bitmap;
	}

	public void close() {
		/* nothing to do */
	}

}
