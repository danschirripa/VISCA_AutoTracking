package danschirripa.javashell.com.visca.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
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

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import com.fazecast.jSerialComm.SerialPort;

import danschirripa.javashell.com.ndi.NDISender;
import danschirripa.javashell.com.visca.Camera;
import danschirripa.javashell.com.visca.communications.CameraTypeManager;
import danschirripa.javashell.com.visca.communications.SerialCommunicationsManager;
import danschirripa.javashell.com.visca.communications.VISCA;

/**
 * Create the preview and control frame for the AutoTracking interface Will
 * first prompt for camera values, ec specific camera type, video interface,
 * serial interface
 * 
 * @author dan
 *
 */
public class ViscaControllerFrame extends JFrame {
	private SerialCommunicationsManager man;
	private JFrame thisFrame;
	private CascadeClassifier classifier = new CascadeClassifier();
	private boolean doAutoTrack = false;
	private Point lastCenterPoint = null;
	private double distanceRange = 100;
	private Camera cam;
	private NDISender ndiSender;
	private boolean doPreview = true;

	/**
	 * Create the main preview frame and prompt for variables
	 * 
	 * @param doPreview Enable or disable the preview
	 * @param videoDev  Optionally provide video device to avoid prompting
	 * @param ttyDev    Optionally provide serial device to avoid prompting
	 */
	public ViscaControllerFrame(boolean doPreview, String videoDev, String ttyDev) {
		thisFrame = this;
		this.doPreview = doPreview;
		this.setTitle("VISCA Controller");

		JFrame selectionFrame = new JFrame("Interface Selection");
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] portNames = new String[ports.length];

		InputStream cascadeInput = getClass().getResourceAsStream("/haarcascade_frontalface_default.xml");
		// Read and load the "haarcascasde" classifier
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

		// List all video input files in /dev, filters by the keyword "video"
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

		// Begin setup for camera interface prompt
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

		Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		centerPoint.translate(-200, -75);
		selectionFrame.setLocation(centerPoint);
		selectionFrame.setVisible(true);
		selectionFrame.setSize(400, 150);
	}

	private static Image camPreviewImg = null;

	/**
	 * Start ingesting the camera's feed, and begin processing for NDI as well as
	 * autotracking
	 * 
	 * @param tty Serial device to use for VISCA control
	 * @param dev Video device file path
	 */
	public void startInterface(SerialPort tty, String dev) {
		System.out.println("Initializing using interfaces " + tty.getDescriptivePortName() + " and " + dev);
		// Open the provided serial port
		man = new SerialCommunicationsManager(tty);
		man.openCommPort();
		// Set the camera to the HOME position by default
		man.sendCommand(VISCA.HOME);

		// Create the NDI sender with the camera's specified resolution
		this.ndiSender = new NDISender(cam.getWidth(), cam.getHeight());

		EventQueue.invokeLater(() -> {
			// Open the provided video device path using GStreamer, with scaling and video
			// conversion enabled
			String gstreamerString = "v4l2src device=\"/dev/" + dev + "\" ! " + "videoscale ! videoconvert ! "
					+ "capsfilter caps=video/x-raw,width=" + cam.getWidth() + ",height=" + cam.getHeight()
					+ " ! appsink";

			VideoCapture cap = new VideoCapture(gstreamerString);

			TimerTask frameGrab = new TimerTask() {
				// Read each frame and process it for both NDI and autotracking
				public void run() {
					Mat frame = new Mat();
					cap.read(frame);
					camPreviewImg = matToBufferedImage(frame);
					final BufferedImage forNdi = (BufferedImage) camPreviewImg;
					final int[] argb = forNdi.getRGB(0, 0, forNdi.getWidth(), forNdi.getHeight(), null, 0,
							forNdi.getWidth());
					final byte[] rgba = new byte[argb.length * 4];

					// Convert colorspace for NDI
					for (int i = 0; i < argb.length; i++) {
						rgba[4 * i] = (byte) ((argb[i] >> 16) & 0xff); // R
						rgba[4 * i + 1] = (byte) ((argb[i] >> 8) & 0xff); // G
						rgba[4 * i + 2] = (byte) ((argb[i]) & 0xff); // B
						rgba[4 * i + 3] = (byte) ((argb[i] >> 24) & 0xff); // A
					}
					// Send NDI adjusted image over NDI
					ndiSender.sendNdiFrame(rgba);

					if (doPreview)
						repaint();
					if (doAutoTrack)
						autoTrack(frame);
				}
			};

			Timer t = new Timer();
			t.scheduleAtFixedRate(frameGrab, 0, 10);

			// Draw the preview image, and overlay a RED dot where the center of the last
			// detected face was
			JPanel openCvPreview = new JPanel() {
				public void paint(Graphics g) {
					if (camPreviewImg != null)
						g.drawImage(camPreviewImg, 0, 0, thisFrame.getWidth(), thisFrame.getHeight(), null);
					g.setColor(Color.RED);
					if (lastCenterPoint != null && doAutoTrack)
						g.fillOval(lastCenterPoint.x - 10, lastCenterPoint.y - 10, 10, 10);
				}
			};

			// Center point for the frames location
			Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
			centerPoint.translate(-(cam.getWidth() / 2), -(cam.getHeight() / 2));

			if (doPreview)
				setSize(cam.getWidth(), cam.getHeight());
			else
				setSize(300, 300);

			JLayeredPane layeredPane = new JLayeredPane();
			layeredPane.setBounds(0, 0, getWidth(), getHeight());

			openCvPreview.setOpaque(true);
			openCvPreview.setBounds(0, 0, getWidth(), getHeight());

			// Initialize camera controls
			CameraControlPanel ccp = new CameraControlPanel(man, this);
			ccp.setOpaque(true);
			ccp.setBounds(0, getHeight() - 300, 300, 300);

			if (doPreview)
				layeredPane.add(openCvPreview, new Integer(0), 0);
			layeredPane.add(ccp, new Integer(1), 0);

			add(layeredPane);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			setJMenuBar(new VISCAMenuBar(man));

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

	/**
	 * If autotracking is enabled, use OpenCV to detect faces within the cameras
	 * image, and calculate the proper PTZ adjustments to focus the image on the
	 * centerpoint
	 * 
	 * @param frame Image to analyze
	 */
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
		Point centerPoint = null;
		// Identify which rectangles centerpoint most likely correlates with the last
		// detected face, prevents points from jumping around erroneously
		for (Rect f : facesArray) {
			Point nCenterPoint = new Point(f.x + (f.width / 2), f.y + (f.height / 2));
			if (isInRange(nCenterPoint)) {
				face = f;
				centerPoint = nCenterPoint;
				break;
			}
		}
		if (!(centerPoint == null)) {
			lastCenterPoint = centerPoint;
			System.out.println("Face @ " + centerPoint.toString());
			// Calculate PTZ adjustment based on Camera specs, and send translated VISCA
			// command over the serial port
			man.sendCommand(cam.determinePTZAdjustment(centerPoint));
		}
	}

	/**
	 * Determine if a point is within an acceptable distance range from the
	 * previously recorded centerpoint
	 * 
	 * @param p Point to verify
	 * @return True if point is within specified distance requirements
	 */
	private boolean isInRange(Point p) {
		if (lastCenterPoint == null)
			return true;
		double dist = Math.abs(Point.distance(p.getX(), p.getY(), lastCenterPoint.getX(), lastCenterPoint.getY()));
		System.out.println("DISTANCE " + dist);
		if (dist < distanceRange)
			return true;
		return false;
	}

	/**
	 * Convert a MAT object to a BufferedImage for processing
	 * 
	 * @param original Mat to convert
	 * @return Converted BufferedImage
	 */
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
