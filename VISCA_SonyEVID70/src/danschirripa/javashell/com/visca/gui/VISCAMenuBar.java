package danschirripa.javashell.com.visca.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import danschirripa.javashell.com.visca.communications.SerialCommunicationsManager;

public class VISCAMenuBar extends JMenuBar {
	private SerialCommunicationsManager man;
	private AdvancedPageEventListener listener;

	public VISCAMenuBar(SerialCommunicationsManager man) {
		this.man = man;
		listener = new AdvancedPageEventListener(man);
		JMenuItem advancedSettings = new JMenuItem("Advanced");
		advancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdvancedFrame();
			}
		});
		advancedSettings.setBackground(Color.DARK_GRAY);
		advancedSettings.setForeground(Color.WHITE);
		this.setBackground(Color.DARK_GRAY);
		this.add(advancedSettings);
		this.setBorderPainted(false);
	}

	private class AdvancedFrame extends JFrame {
		public AdvancedFrame() {
			this.setBackground(Color.DARK_GRAY);
			ImageIcon upIcon = null;
			ImageIcon downIcon = null;

			try {
				upIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Up_Icon.png"))
						.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
				downIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Down_Icon.png"))
						.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
			} catch (Exception e) {
				e.printStackTrace();
			}

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

			JPanel brightnessPanel = new JPanel();

			JLabel brightnessLabel = new JLabel("Brightness: ");
			brightnessLabel.setForeground(Color.WHITE);
			JButton brightUp = new JButton(upIcon);
			JButton brightDown = new JButton(downIcon);
			JButton brightReset = new JButton("Reset");
			brightReset.setActionCommand("brightReset");
			brightReset.setBackground(Color.DARK_GRAY);
			brightReset.setForeground(Color.WHITE);
			brightUp.setActionCommand("brightUp");
			brightUp.setBackground(new Color(0, 0, 0, 0));
			brightUp.setForeground(new Color(0, 0, 0, 0));
			brightUp.setBorderPainted(false);
			brightDown.setActionCommand("brightDown");
			brightDown.setBackground(new Color(0, 0, 0, 0));
			brightDown.setForeground(new Color(0, 0, 0, 0));
			brightDown.setBorderPainted(false);
			brightnessPanel.add(brightnessLabel);
			brightnessPanel.add(brightUp);
			brightnessPanel.add(brightDown);
			brightnessPanel.add(brightReset);
			brightnessPanel.setBackground(Color.DARK_GRAY);

			brightUp.addActionListener(listener);
			brightDown.addActionListener(listener);
			brightReset.addActionListener(listener);

			JPanel focusPanel = new JPanel();
			JLabel focusLabel = new JLabel("Focus: ");
			focusLabel.setForeground(Color.WHITE);
			JButton afOnePress = new JButton("AF");
			afOnePress.setActionCommand("af");
			afOnePress.setBackground(Color.DARK_GRAY);
			afOnePress.setForeground(Color.WHITE);
			JButton focusIn = new JButton(upIcon);
			JButton focusOut = new JButton(downIcon);
			focusIn.setActionCommand("focusIn");
			focusIn.setBackground(new Color(0, 0, 0, 0));
			focusIn.setForeground(new Color(0, 0, 0, 0));
			focusIn.setBorderPainted(false);
			focusOut.setActionCommand("focusOut");
			focusOut.setBackground(new Color(0, 0, 0, 0));
			focusOut.setForeground(new Color(0, 0, 0, 0));
			focusOut.setBorderPainted(false);
			focusPanel.add(focusLabel);
			focusPanel.add(afOnePress);
			focusPanel.add(focusIn);
			focusPanel.add(focusOut);
			focusPanel.setBackground(Color.DARK_GRAY);

			afOnePress.addActionListener(listener);
			focusIn.addActionListener(listener);
			focusOut.addActionListener(listener);

			JPanel backlightPanel = new JPanel();
			JLabel backlightLabel = new JLabel("Backlight Compensation: ");
			backlightLabel.setForeground(Color.WHITE);
			JButton backlightComp = new JButton();
			backlightComp.setActionCommand("backlightCompToggle");
			backlightPanel.add(backlightLabel);
			backlightPanel.add(backlightComp);
			backlightPanel.setBackground(Color.DARK_GRAY);

			backlightComp.addActionListener(listener);

			JPanel whiteBalancePanel = new JPanel();
			JLabel whiteBalanceLabel = new JLabel("White Balance: ");
			whiteBalanceLabel.setForeground(Color.WHITE);
			String[] whiteBalanceTypes = { "Auto", "Indoor", "Outdoor", "OnePush", "ATW" };
			JComboBox<String> whiteBalance = new JComboBox<String>(whiteBalanceTypes);

			whiteBalance.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});

			whiteBalance.setBackground(Color.DARK_GRAY);
			whiteBalance.setForeground(Color.WHITE);
			JButton awbButton = new JButton("AWB");
			awbButton.setActionCommand("awb");
			awbButton.setBackground(Color.DARK_GRAY);
			awbButton.setForeground(Color.WHITE);
			whiteBalancePanel.add(whiteBalanceLabel);
			whiteBalancePanel.add(whiteBalance);
			whiteBalancePanel.add(awbButton);
			whiteBalancePanel.setBackground(Color.DARK_GRAY);

			awbButton.addActionListener(listener);

			mainPanel.add(brightnessPanel);
			mainPanel.add(focusPanel);
			mainPanel.add(backlightPanel);
			mainPanel.add(whiteBalancePanel);

			setContentPane(mainPanel);
			mainPanel.setBackground(Color.DARK_GRAY);
			setSize(250, 300);
			setVisible(true);
		}
	}

}
