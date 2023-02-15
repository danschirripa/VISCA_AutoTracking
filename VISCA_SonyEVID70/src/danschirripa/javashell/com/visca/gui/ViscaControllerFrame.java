package danschirripa.javashell.com.visca.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.freedesktop.gstreamer.swing.GstVideoComponent;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import com.fazecast.jSerialComm.SerialPort;

import danschirripa.javashell.com.visca.Camera;
import danschirripa.javashell.com.visca.communications.CameraTypeManager;
import danschirripa.javashell.com.visca.communications.SerialCommunicationsManager;
import danschirripa.javashell.com.visca.communications.VISCA;

public class ViscaControllerFrame extends JFrame {
	private SerialCommunicationsManager man;
	private JFrame thisFrame;
	private CascadeClassifier classifier = new CascadeClassifier();
	private boolean doAutoTrack = false;
	private Point lastCenterPoint = null;
	private double distanceRange = 20;
	private Camera cam;

	public ViscaControllerFrame() {
		thisFrame = this;
		this.setTitle("VISCA Controller");
		JFrame selectionFrame = new JFrame("Interface Selection");
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] portNames = new String[ports.length];

		InputStream cascadeInput = getClass().getResourceAsStream("/haarcascade_frontalface_default.xml");

		try {
			File tmpFile = File.createTempFile("cascade", ".xml");
			FileOutputStream tmpOut = new FileOutputStream(tmpFile);
			tmpOut.write(cascadeInput.readAllBytes());
			tmpOut.flush();
			tmpOut.close();
			classifier.load(tmpFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load cascade classifier");
			System.exit(-1);
		}

		int i = 0;
		for (SerialPort port : ports) {
			portNames[i] = port.getDescriptivePortName();
			i++;
		}

		File deviceDir = new File("/dev");
		String[] devs = deviceDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("video"))
					return true;
				return false;
			}

		});

		String[] camTypes = CameraTypeManager.getCameraTypes();

		JComboBox<String> serialSelections = new JComboBox<String>(portNames);
		JComboBox<String> cameraSelection = new JComboBox<String>(devs);
		JComboBox<String> cameraTypeSelection = new JComboBox<String>(camTypes);

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cam = CameraTypeManager.getCamera(camTypes[cameraTypeSelection.getSelectedIndex()]);
				startInterface(ports[serialSelections.getSelectedIndex()], devs[cameraSelection.getSelectedIndex()]);
				selectionFrame.dispose();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.PAGE_AXIS));

		JPanel cameraTypeSelectionPanel = new JPanel(new BorderLayout());
		cameraTypeSelectionPanel.add(new JLabel("Select Camera Type: "), BorderLayout.WEST);
		cameraTypeSelectionPanel.add(cameraTypeSelection, BorderLayout.CENTER);

		JPanel serialSelectionPanel = new JPanel(new BorderLayout());
		serialSelectionPanel.add(new JLabel("Select Communications Interface: "), BorderLayout.WEST);
		serialSelectionPanel.add(serialSelections, BorderLayout.CENTER);

		JPanel deviceSelectionPanel = new JPanel(new BorderLayout());
		deviceSelectionPanel.add(new JLabel("Select Video Source Device: "), BorderLayout.WEST);
		deviceSelectionPanel.add(cameraSelection, BorderLayout.CENTER);

		JPanel confirmationPanel = new JPanel();
		confirmationPanel.add(cancelButton);
		confirmationPanel.add(okButton);

		selectionPanel.add(cameraTypeSelectionPanel);
		selectionPanel.add(serialSelectionPanel);
		selectionPanel.add(deviceSelectionPanel);
		selectionPanel.add(confirmationPanel);

		selectionFrame.setContentPane(selectionPanel);

		selectionFrame.setVisible(true);
		selectionFrame.setSize(400, 150);
	}

	private static Image camPreviewImg = null;

	public void startInterface(SerialPort tty, String dev) {
		System.out.println("Initializing using interfaces " + tty.getDescriptivePortName() + " and " + dev);
		man = new SerialCommunicationsManager(tty);
		man.openCommPort();
		man.sendCommand(VISCA.HOME);

		EventQueue.invokeLater(() -> {
			GstVideoComponent vc = new GstVideoComponent();
			String gstreamerString = "v4l2src device=\"/dev/" + dev + "\" ! " + "videoscale ! videoconvert ! "
					+ "capsfilter caps=video/x-raw,width=800,height=600 ! appsink";

			VideoCapture cap = new VideoCapture(gstreamerString);

			TimerTask frameGrab = new TimerTask() {
				public void run() {
					Mat frame = new Mat();
					cap.read(frame);
					camPreviewImg = matToBufferedImage(frame);
					repaint();
					if (doAutoTrack)
						autoTrack(frame);
				}
			};

			Timer t = new Timer();
			t.scheduleAtFixedRate(frameGrab, 0, 10);

			JPanel openCvPreview = new JPanel() {
				public void paint(Graphics g) {
					if (camPreviewImg != null)
						g.drawImage(camPreviewImg, 0, 0, thisFrame.getWidth(), thisFrame.getHeight(), null);
					g.setColor(Color.RED);
					if (lastCenterPoint != null && doAutoTrack)
						g.fillOval(lastCenterPoint.x - 10, lastCenterPoint.y - 10, 10, 10);
				}
			};

			setSize(800, 600);

			JLayeredPane layeredPane = new JLayeredPane();
			layeredPane.setBounds(0, 0, getWidth(), getHeight());

			openCvPreview.setOpaque(true);
			openCvPreview.setBounds(0, 0, getWidth(), getHeight());

			CameraControlPanel ccp = new CameraControlPanel(man, this);
			ccp.setOpaque(true);
			ccp.setBounds(0, getHeight() - 300, 300, 300);

			layeredPane.add(openCvPreview, new Integer(0), 0);
			layeredPane.add(ccp, new Integer(1), 0);

			add(layeredPane);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			this.setVisible(true);

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int W = 4;
					int H = 3;
					Rectangle b = e.getComponent().getBounds();
					int height = b.width * H / W;
					layeredPane.setBounds(b.x, b.y, b.width, height);
					openCvPreview.setBounds(b.x, b.y, b.width, height);
					ccp.setBounds(b.x, height - 300, 300, 300);
					thisFrame.setBounds(b.x, b.y, b.width, height);
				}
			});
		});
	}

	private double faceSize = 0;

	private void autoTrack(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat gray = new Mat();

		Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(gray, gray);

		if (faceSize == 0) {
			int height = gray.rows();
			if (Math.round(height * 0.2f) > 0) {
				faceSize = Math.round(height * 0.2f);
			}
		}

		classifier.detectMultiScale(gray, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(faceSize, faceSize), new Size());

		Rect[] facesArray = faces.toArray();
		if (facesArray.length == 0)
			return;
		Rect face = facesArray[0];
		Point centerPoint = new Point(face.x + (face.width / 2), face.y + (face.height / 2));
		for (Rect f : facesArray) {
			Point nCenterPoint = new Point(f.x + (f.width / 2), f.y + (f.height / 2));
			if (isInRange(nCenterPoint)) {
				face = f;
				centerPoint = nCenterPoint;
			}
		}
		lastCenterPoint = centerPoint;
		System.out.println("Face @ " + centerPoint.toString());
		man.sendCommand(cam.determinePTZAdjustment(centerPoint));
	}

	private boolean isInRange(Point p) {
		if (lastCenterPoint == null)
			return true;
		double dist = Point.distance(p.getX(), p.getY(), lastCenterPoint.getX(), lastCenterPoint.getY());
		System.out.println(dist);
		if (dist <= distanceRange)
			return true;
		return false;
	}

	private static BufferedImage matToBufferedImage(Mat original) {
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	public void doAutoTrack(boolean autoTrack) {
		doAutoTrack = autoTrack;
	}
}
