package danschirripa.javashell.com.visca.communications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Scanner;

import danschirripa.javashell.com.visca.Camera;
import danschirripa.javashell.com.visca.Logger;

public class CameraTypeManager {
	private static Hashtable<String, Camera> cameraTypes = new Hashtable<String, Camera>();
	private static Logger log = new Logger("CamTypes", 13);

	private static File configurationFile = new File(System.getProperty("user.home") + "/.autotrack_camera_types");

	public static void loadCameraTypes() {
		try {
			if (!configurationFile.exists()) {
				configurationFile.createNewFile();

				PrintStream out = new PrintStream(new FileOutputStream(configurationFile));
				out.println("Sony EVI D-70:800:600:48");
				out.flush();
				out.close();
			}
			Scanner sc = new Scanner(new FileInputStream(configurationFile));
			while (sc.hasNextLine()) {
				String nextLine = sc.nextLine();
				if (nextLine.startsWith("#"))
					continue;
				String[] values = nextLine.split(":");

				String cameraType = values[0];
				int imgWidth = Integer.parseInt(values[1]);
				int imgHeight = Integer.parseInt(values[2]);
				int fov = Integer.parseInt(values[3]);

				Camera cam = new Camera(imgWidth, imgHeight, fov);

				log.log("Added camera \"" + cameraType + "\" with resolution of " + imgWidth + "x" + imgHeight
						+ " and FOV of " + fov);

				cameraTypes.put(cameraType, cam);
			}
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to load/create main configuration file...");
			System.exit(-1);
		}
	}

	public static String[] getCameraTypes() {
		return cameraTypes.keySet().toArray(new String[cameraTypes.size()]);
	}

	public static Camera getCamera(String type) {
		return cameraTypes.get(type);
	}

}
