package danschirripa.javashell.com.ndi;

public class NDISender {
	static {
		try {
			System.load("libndi.so");
			System.load("libndisender.so");
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
