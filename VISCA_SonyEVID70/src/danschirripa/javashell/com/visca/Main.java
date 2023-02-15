package danschirripa.javashell.com.visca;

import java.io.File;
import java.util.stream.Stream;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Version;
import org.opencv.core.Core;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;

import danschirripa.javashell.com.visca.communications.CameraTypeManager;
import danschirripa.javashell.com.visca.gui.ViscaControllerFrame;

public class Main {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		configurePaths();
		Gst.init(Version.BASELINE, "VISCA Interface", args);

		CameraTypeManager.loadCameraTypes();
		new ViscaControllerFrame();

		Gst.main();

	}

	static void configurePaths() {
		if (Platform.isWindows()) {
			String gstPath = System.getProperty("gstreamer.path", findWindowsLocation());
			if (!gstPath.isEmpty()) {
				String systemPath = System.getenv("PATH");
				if (systemPath == null || systemPath.trim().isEmpty()) {
					Kernel32.INSTANCE.SetEnvironmentVariable("PATH", gstPath);
				} else {
					Kernel32.INSTANCE.SetEnvironmentVariable("PATH", gstPath + File.pathSeparator + systemPath);
				}
			}
		} else if (Platform.isMac()) {
			String gstPath = System.getProperty("gstreamer.path", "/Library/Frameworks/GStreamer.framework/Libraries/");
			if (!gstPath.isEmpty()) {
				String jnaPath = System.getProperty("jna.library.path", "").trim();
				if (jnaPath.isEmpty()) {
					System.setProperty("jna.library.path", gstPath);
				} else {
					System.setProperty("jna.library.path", jnaPath + File.pathSeparator + gstPath);
				}
			}

		}
	}

	static String findWindowsLocation() {
		if (Platform.is64Bit()) {
			return Stream
					.of("GSTREAMER_1_0_ROOT_MSVC_X86_64", "GSTREAMER_1_0_ROOT_MINGW_X86_64",
							"GSTREAMER_1_0_ROOT_X86_64")
					.map(System::getenv).filter(p -> p != null).map(p -> p.endsWith("\\") ? p + "bin\\" : p + "\\bin\\")
					.findFirst().orElse("");
		} else {
			return "";
		}
	}
}
