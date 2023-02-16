package danschirripa.javashell.com.ndi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NDISender {
	static {
		try {
			InputStream libNdiStream = NDISender.class.getResourceAsStream("/libndi.so");
			File libNdiFile = new File("libndi.so.5.5.3");
			FileOutputStream libNdiOut = new FileOutputStream(libNdiFile);
			libNdiOut.write(libNdiStream.readAllBytes());
			libNdiOut.flush();
			libNdiOut.close();
			System.load(libNdiFile.getAbsolutePath());

			InputStream libNdiSenderStream = NDISender.class.getResourceAsStream("/libndisender.so");
			File libNdiSenderFile = new File("libndisender.so");
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

	public NDISender() {
		initializeNDI();
	}

	public void sendNdiFrame(byte[] frame) {
		sendFrame(frame);
	}

	private native void initializeNDI();

	private native void sendFrame(byte[] frame);

}
