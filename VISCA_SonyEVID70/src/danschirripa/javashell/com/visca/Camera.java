package danschirripa.javashell.com.visca;

import java.awt.Point;

import danschirripa.javashell.com.visca.communications.VISCA;

/**
 * Stores and compute necessary PTZ adjustments based on provided camera
 * specifications
 * 
 * @author dan
 *
 */
public class Camera {
	private int maximumX, maximumY, imgCenterX, imgCenterY, fovDegrees;
	private int horizontalAngularChangeMaximum, verticalAngularChangeMaximum;

	/**
	 * Create a "Camera" object to represent a physical camera
	 * 
	 * @param imgWidth   Camera's image resolution width
	 * @param imgHeight  Camera's image resolution height
	 * @param fovDegrees The FOV in degrees possible
	 */
	public Camera(int imgWidth, int imgHeight, int fovDegrees) {
		this.maximumX = imgWidth;
		this.maximumY = imgHeight;
		this.fovDegrees = fovDegrees;
		this.imgCenterX = this.maximumX / 2;
		this.imgCenterY = this.maximumY / 2;

		this.horizontalAngularChangeMaximum = fovDegrees / 2;

		this.verticalAngularChangeMaximum = (imgCenterY * this.horizontalAngularChangeMaximum) / imgCenterX;

		System.out.println("FOV " + fovDegrees);
		System.out.println("ImgCenterX " + imgCenterX);
		System.out.println("ImgCenterY " + imgCenterY);
		System.out.println("HorizontalAngleMax " + horizontalAngularChangeMaximum);
		System.out.println("VerticalAngleMax " + verticalAngularChangeMaximum);
	}

	/**
	 * Determine PTZ adjustment to move towards the specified point. If the point is
	 * already close to the images center point, halt camera motion to prevent
	 * camera jiggling when movement is not necessary
	 * 
	 * @param centerPoint New point to move towards as center
	 * @return VISCA command translation of point->point translation
	 */
	public byte[] determinePTZAdjustment(Point centerPoint) {
		int x = centerPoint.x;
		int y = centerPoint.y;
		int deltaX = x - imgCenterX;
		int deltaY = (-1) * (y - imgCenterY);

		if ((deltaX < 20 && deltaX > -20) && (deltaY < 20 && deltaY > -20)) {
			return VISCA.PT_STOP;
		}

		int changeX, changeY;
		changeX = (deltaX * horizontalAngularChangeMaximum) / imgCenterX;
		changeY = (deltaY * verticalAngularChangeMaximum) / imgCenterY;

		System.out.println(deltaX + " : " + deltaY);
		System.out.println();
		System.out.println(changeX + " : " + changeY);

		return VISCA.relativePtCommand(changeX, changeY, (byte) 0x17);
	}

	public int getWidth() {
		return maximumX;
	}

	public int getHeight() {
		return maximumY;
	}

}
