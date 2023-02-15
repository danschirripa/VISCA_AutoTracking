package danschirripa.javashell.com.visca;

import java.awt.Point;

import danschirripa.javashell.com.visca.communications.VISCA;

public class Camera {
	private int maximumX, maximumY, imgCenterX, imgCenterY, fovDegrees;
	private int horizontalAngularChangeMaximum, verticalAngularChangeMaximum;

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

	public byte[] determinePTZAdjustment(Point centerPoint) {
		int x = centerPoint.x;
		int y = centerPoint.y;
		int deltaX = x - imgCenterX;
		int deltaY = (-1) * (y - imgCenterY);

		if ((deltaX < 20 && deltaX > -20) && (deltaY < 20 && deltaY > -20)) {
			return VISCA.PT_STOP;
		}

		int changeX, changeY;
		/*
		 * if (deltaX > 0) changeX = 3; else changeX = -3;
		 * 
		 * if (deltaY > 0) changeY = -3; else changeY = 3;
		 */
		changeX = (deltaX * horizontalAngularChangeMaximum) / imgCenterX;
		changeY = (deltaY * verticalAngularChangeMaximum) / imgCenterY;

		System.out.println(deltaX + " : " + deltaY);
		System.out.println();
		System.out.println(changeX + " : " + changeY);

		return VISCA.relativePtCommand(changeX, changeY, (byte) 0x17);
	}

}
