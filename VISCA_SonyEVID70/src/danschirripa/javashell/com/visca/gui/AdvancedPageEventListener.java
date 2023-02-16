package danschirripa.javashell.com.visca.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import danschirripa.javashell.com.visca.communications.SerialCommunicationsManager;
import danschirripa.javashell.com.visca.communications.VISCA;

public class AdvancedPageEventListener implements ActionListener {
	private SerialCommunicationsManager man;
	private boolean backlight = false;

	public AdvancedPageEventListener(SerialCommunicationsManager man) {
		this.man = man;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		switch (actionCommand) {
		case "focusIn":
			man.sendCommand(VISCA.focusCommand(VISCA.FOCUS_FAR, (byte) 0x07));
			break;
		case "focusOut":
			man.sendCommand(VISCA.focusCommand(VISCA.FOCUS_NEAR, (byte) 0x07));
			break;
		case "brightUp":
			man.sendCommand(VISCA.BRIGHTNESS_UP);
			break;
		case "brightDown":
			man.sendCommand(VISCA.BRIGHTNESS_DOWN);
			break;
		case "brightReset":
			man.sendCommand(VISCA.BRIGHTNESS_RESET);
			break;
		case "af":
			man.sendCommand(VISCA.AF_ONE_PRESS);
			break;
		case "backlightCompToggle":
			if (backlight) {
				man.sendCommand(VISCA.BACKLIGHT_COMP_OFF);
				backlight = false;
				break;
			}
			man.sendCommand(VISCA.BACKLIGHT_COMP_ON);
			backlight = true;
			break;
		case "awb":
			break;
		}
	}

}
