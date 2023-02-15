package dschirripa.onediversified.com.visca.communications;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

import dschirripa.onediversified.com.visca.Logger;

public class SerialCommunicationsManager {
	private Logger log = new Logger("SerialPort", 0);
	private SerialPort tty;
	private InputStream in;
	private OutputStream out;

	public SerialCommunicationsManager() {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			log.log(port.getDescriptivePortName());
		}
	}

	public SerialCommunicationsManager(SerialPort tty) {
		this();
		this.tty = tty;
	}

	public void openCommPort() {
		if (tty == null)
			tty = SerialPort.getCommPorts()[0];
		log.log("Opening serial port " + tty.getDescriptivePortName());
		tty.setComPortParameters(9600, 8, 1, 0);
		tty.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		tty.openPort();
		tty.addDataListener(new VISCAMessageListener());
		sendCommand(VISCA.ADDRESS_SET);
	}

	public void sendCommand(byte[] cmd) {
		log.log("Sending command...");
		int ret = tty.writeBytes(cmd, cmd.length);
		log.log("Wrote " + ret + " out of " + cmd.length);
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
