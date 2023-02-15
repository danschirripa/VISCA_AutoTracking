package dschirripa.onediversified.com.visca.communications;

public class VISCA {

	public static final byte[] ADDRESS_SET = new byte[] { (byte) 0x88, (byte) 0x30, (byte) 0x01, (byte) 0xFF };
	public static final int PTZ_UP = 1, PTZ_DOWN = 2, PTZ_LEFT = 3, PTZ_RIGHT = 4, PTZ_STOP = 5, PTZ_IN = 6,
			PTZ_OUT = 7, FOCUS_NEAR = 8, FOCUS_FAR = 9;
	public static final byte[] UP = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x06, (byte) 0x01, (byte) 0x02,
			(byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0xFF };

	public static final byte[] HOME = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x06, (byte) 0x04, (byte) 0xFF };
	public static final byte[] PT_STOP = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x06, (byte) 0x01, (byte) 0x00,
			(byte) 0x00, (byte) 0x03, (byte) 0x03, (byte) 0xFF };

	public static final byte[] Z_STOP = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x07, (byte) 0x00,
			(byte) 0xFF };;

	public static final byte[] BACKLIGHT_COMP_ON = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x33,
			(byte) 0x02, (byte) 0xFF };

	public static final byte[] BACKLIGHT_COMP_OFF = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x33,
			(byte) 0x03, (byte) 0xFF };

	public static final byte[] AF_ONE_PRESS = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x18,
			(byte) 0x01, (byte) 0xFF };

	public static final byte[] AF_ON = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x38, (byte) 0x02,
			(byte) 0xFF };

	public static final byte[] AF_OFF = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x38, (byte) 0x03,
			(byte) 0xFF };

	public static final byte[] AF_SENS_LOW = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x58,
			(byte) 0x02, (byte) 0xFF };

	public static final byte[] AF_SENS_HIGH = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x58,
			(byte) 0x03, (byte) 0xFF };

	public static final byte[] AF_NORMAL = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x57, (byte) 0x00,
			(byte) 0xFF };

	public static final byte[] AF_INTERVAL = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x57,
			(byte) 0x01, (byte) 0xFF };

	public static final byte[] AF_ON_ZOOM = new byte[] { (byte) 0x81, (byte) 0x01, (byte) 0x04, (byte) 0x57,
			(byte) 0x02, (byte) 0xFF };

	public static byte[] ptCommand(int direction, byte speed) {
		byte[] command = new byte[9];
		command[0] = (byte) 0x81;
		command[1] = (byte) 0x01;
		command[2] = (byte) 0x06;
		command[3] = (byte) 0x01;
		command[4] = speed;
		command[5] = speed;
		switch (direction) {
		case PTZ_UP:
			command[6] = (byte) 0x03;
			command[7] = (byte) 0x01;
			command[8] = (byte) 0xFF;
			break;
		case PTZ_DOWN:
			command[6] = (byte) 0x03;
			command[7] = (byte) 0x02;
			command[8] = (byte) 0xFF;
			break;
		case PTZ_RIGHT:
			command[6] = (byte) 0x02;
			command[7] = (byte) 0x03;
			command[8] = (byte) 0xFF;
			break;
		case PTZ_LEFT:
			command[6] = (byte) 0x01;
			command[7] = (byte) 0x03;
			command[8] = (byte) 0xFF;
			break;
		case PTZ_STOP:
			command[6] = (byte) 0x03;
			command[7] = (byte) 0x03;
			command[8] = (byte) 0xFF;
			break;
		}
		return command;
	}

	public static byte[] zoomCommand(int direction, byte speed) {
		byte[] command = new byte[6];
		command[0] = (byte) 0x81;
		command[1] = (byte) 0x01;
		command[2] = (byte) 0x04;
		command[3] = (byte) 0x07;
		switch (direction) {
		case PTZ_IN:
			if (speed == -1)
				speed = (byte) 0x02;
			command[4] = speed;
			break;
		case PTZ_OUT:
			if (speed == -1)
				speed = (byte) 0x03;
			command[4] = speed;
			break;
		}
		command[5] = (byte) 0xFF;
		return command;
	}

	public static byte[] focusCommand(int direction, byte speed) {
		byte[] command = new byte[6];
		command[0] = (byte) 0x81;
		command[1] = (byte) 0x01;
		command[2] = (byte) 0x04;
		command[3] = (byte) 0x08;
		switch (direction) {
		case FOCUS_NEAR:
			if (speed == -1)
				speed = (byte) 0x03;
			command[4] = speed;
			break;
		case FOCUS_FAR:
			if (speed == -1)
				speed = (byte) 0x02;
			command[4] = speed;
			break;
		}
		command[5] = (byte) 0xFF;
		return command;
	}

	// -2267 - 0 - 2267 is range for full Pan
	public static byte[] directPtCommand(int pan, int tilt, byte speed) {
		byte[] command = new byte[15];
		command[0] = (byte) 0x81;
		command[1] = (byte) 0x01;
		command[2] = (byte) 0x06;
		command[3] = (byte) 0x03;
		command[4] = speed;
		command[5] = speed;
		String panHexString = Integer.toHexString(pan);
		char[] hexBytePlaces = panHexString.toCharArray();

		byte[] panBytes = new byte[4];

		int lengthDelta = 0;
		if (hexBytePlaces.length < 4) {
			lengthDelta = 4 - hexBytePlaces.length;
			for (int i = 0; i < lengthDelta; i++)
				panBytes[i] = (byte) 0x00;
		}

		if (hexBytePlaces.length > 4) {
			char[] tmp = new char[4];
			for (int i = 0; i < 4; i++) {
				tmp[i] = hexBytePlaces[i + 4];
			}
			hexBytePlaces = tmp;
		}

		for (int i = lengthDelta; i < panBytes.length; i++) {
			panBytes[i] = (byte) Integer.parseInt("0" + hexBytePlaces[i - lengthDelta], 16);
		}

		String tiltHexString = Integer.toHexString(tilt);
		hexBytePlaces = tiltHexString.toCharArray();

		byte[] tiltBytes = new byte[4];

		lengthDelta = 0;
		if (hexBytePlaces.length < 4) {
			lengthDelta = 4 - hexBytePlaces.length;
			for (int i = 0; i < lengthDelta; i++)
				tiltBytes[i] = (byte) 0x00;
		}

		if (hexBytePlaces.length > 4) {
			char[] tmp = new char[4];
			for (int i = 0; i < 4; i++) {
				tmp[i] = hexBytePlaces[i + 4];
			}
			hexBytePlaces = tmp;
		}

		for (int i = lengthDelta; i < tiltBytes.length; i++) {
			tiltBytes[i] = (byte) Integer.parseInt("0" + hexBytePlaces[i - lengthDelta], 16);
		}

		for (int i = 0; i < 4; i++)
			command[i + 6] = panBytes[i];

		for (int i = 0; i < 4; i++)
			command[i + 10] = tiltBytes[i];

		command[14] = (byte) 0xFF;

		String result = "";
		for (byte b : command) {
			result += String.format("%02X", b) + " ";
		}
		System.out.println(result);

		return command;
	}

}
