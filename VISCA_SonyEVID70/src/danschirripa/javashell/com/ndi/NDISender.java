package danschirripa.javashell.com.ndi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * JNI bindings for NDI lib, only supports Sending currently.
 * 
 * @author dan
 *
 */
public class NDISender {
	// Extract and load the NDI libs
	static {
		try {
			InputStream libNdiStream = NDISender.class.getResourceAsStream("/libndi.so");
			File libNdiFile = File.createTempFile("libndi", ".so");
			FileOutputStream libNdiOut = new FileOutputStream(libNdiFile);
			libNdiOut.write(libNdiStream.readAllBytes());
			libNdiOut.flush();
			libNdiOut.close();
			System.load(libNdiFile.getAbsolutePath());

			InputStream libNdiSenderStream = NDISender.class.getResourceAsStream("/libndisender.so");
			File libNdiSenderFile = File.createTempFile("libndisender", ".so");
			FileOutputStream libNdiSenderOut = new FileOutputStream(libNdiSenderFile);
			libNdiSenderOut.write(libNdiSenderStream.readAllBytes());
			libNdiSenderOut.flush();
			libNdiSenderOut.close();
			System.load(libNdiSenderFile.getAbsolutePath());
			System.out.println("LIBRARY LOAD COMPLETED");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Create an NDI instance with the specified width and height. NOTE if the
	 * resolution provided to "sendFrame" does not match the created instance, the
	 * NDI library will SEGSEV, or other unwanted issues will occur
	 * 
	 * @param width  Image width
	 * @param height Image height
	 */
	public NDISender(int width, int height) {
		initializeNDI(width, height);
	}

	/**
	 * Send an RGBA encoded image over NDI, NOTE the width and height of the
	 * provided image MUST match that which the NDI instance was created with
	 * 
	 * @param frame Image to dispatch via NDI
	 */
	public void sendNdiFrame(byte[] frame) {
		sendFrame(frame);
	}

	private native void initializeNDI(int width, int height);

	private native void sendFrame(byte[] frame);

}
