package dschirripa.onediversified.com.visca.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import dschirripa.onediversified.com.visca.communications.SerialCommunicationsManager;
import dschirripa.onediversified.com.visca.communications.VISCA;

public class CameraControlPanel extends JPanel {

	private JButton[] ptzButtons;

	private byte[][] viscaCommands;

	/**
	 * Create the panel.
	 */
	public CameraControlPanel(SerialCommunicationsManager man, ViscaControllerFrame vf) {

		viscaCommands = new byte[7][];

		viscaCommands[0] = VISCA.ptCommand(VISCA.PTZ_LEFT, (byte) 0x06);
		viscaCommands[1] = VISCA.ptCommand(VISCA.PTZ_UP, (byte) 0x06);
		viscaCommands[2] = VISCA.ptCommand(VISCA.PTZ_RIGHT, (byte) 0x06);
		viscaCommands[3] = VISCA.ptCommand(VISCA.PTZ_DOWN, (byte) 0x06);
		viscaCommands[4] = VISCA.HOME;

		viscaCommands[5] = VISCA.zoomCommand(VISCA.PTZ_IN, (byte) -1);
		viscaCommands[6] = VISCA.zoomCommand(VISCA.PTZ_OUT, (byte) -1);
		ptzButtons = new JButton[7];
		for (int i = 0; i < ptzButtons.length; i++) {
			JButton ptzButton = new JButton();
			int command = i;
			ptzButton.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {
					man.sendCommand(viscaCommands[command]);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					man.sendCommand(VISCA.PT_STOP);
					man.sendCommand(VISCA.Z_STOP);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

			});
			ptzButton.setPreferredSize(new Dimension(50, 50));
			ptzButtons[i] = ptzButton;
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;

		c.gridx = 0;
		c.gridy = 1;

		add(ptzButtons[0], c);

		c.gridx = 1;
		c.gridy = 0;

		add(ptzButtons[1], c);

		c.gridx = 2;
		c.gridy = 1;

		add(ptzButtons[2], c);

		c.gridx = 1;
		c.gridy = 2;

		add(ptzButtons[3], c);

		c.gridx = 4;

		add(Box.createHorizontalStrut(10), c);

		c.gridx = 6;

		add(Box.createHorizontalStrut(10), c);

		c.gridx = 7;
		c.gridy = 2;

		add(ptzButtons[4], c);

		c.gridx = 5;
		c.gridy = 0;

		add(ptzButtons[5], c);

		c.gridy = 2;

		add(ptzButtons[6], c);

		JButton autoTrackButton = new JButton();
		autoTrackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (autoTrackButton.getBackground() == Color.red) {
					autoTrackButton.setBackground(Color.GREEN);
					vf.doAutoTrack(true);
					return;
				}
				autoTrackButton.setBackground(Color.red);
				vf.doAutoTrack(false);
			}
		});

		c.gridx = 6;

		add(autoTrackButton, c);

		setBackground(new Color(0, 0, 0, 0));

	}

}
