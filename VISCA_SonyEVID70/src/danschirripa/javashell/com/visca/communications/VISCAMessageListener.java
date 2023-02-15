package danschirripa.javashell.com.visca.communications;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import danschirripa.javashell.com.visca.Logger;

public class VISCAMessageListener implements SerialPortMessageListener {
	private Logger logger = new Logger("VISCA", 1);

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}

	@Override
	public void serialEvent(SerialPortEvent e) {
		logger.log("Data Received");
		byte[] data = e.getReceivedData();
		String result = "";
		for (byte b : data) {
			result += String.format("%02X", b) + " ";
		}
		logger.log(result);
	}

	@Override
	public boolean delimiterIndicatesEndOfMessage() {
		return true;
	}

	@Override
	public byte[] getMessageDelimiter() {
		return new byte[] { (byte) 0xFF };
	}

}
