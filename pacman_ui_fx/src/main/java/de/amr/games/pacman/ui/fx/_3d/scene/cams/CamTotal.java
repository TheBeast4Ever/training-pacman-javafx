/*
MIT License

Copyright (c) 2021-22 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package de.amr.games.pacman.ui.fx._3d.scene.cams;

import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;

/**
 * @author Armin Reichert
 */
public class CamTotal extends GameSceneCamera {

	public static final Point3D INITIAL_ROTATION_AXIS = Rotate.X_AXIS;
	public static final double INITIAL_ROTATE = 60;
	public static final double INITIAL_TRANSLATE_X = 0;
	public static final double INITIAL_TRANSLATE_Y = 310;
	public static final double INITIAL_TRANSLATE_Z = -160;

	public CamTotal() {
		setNearClip(0.1);
		setFarClip(10000.0);
		setTranslateX(INITIAL_TRANSLATE_X);
		setTranslateY(INITIAL_TRANSLATE_Y);
		setTranslateZ(INITIAL_TRANSLATE_Z);
		setRotationAxis(INITIAL_ROTATION_AXIS);
		setRotate(INITIAL_ROTATE);
	}

	@Override
	public String toString() {
		return "Total";
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		KeyCode key = e.getCode();
		boolean control = e.isControlDown();
		boolean shift = e.isShiftDown();
		if (!control && shift) {
			switch (key) {
			case LEFT -> changeBy(translateXProperty(), -10);
			case RIGHT -> changeBy(translateXProperty(), +10);
			case MINUS -> changeBy(translateYProperty(), -10);
			case PLUS -> changeBy(translateYProperty(), +10);
			case UP -> changeBy(translateZProperty(), -10);
			case DOWN -> changeBy(translateZProperty(), 10);
			default -> { // ignore
			}
			}
		} else if (control && shift) {
			switch (key) {
			case UP -> {
				setRotationAxis(Rotate.X_AXIS);
				setRotate((getRotate() - 1 + 360) % 360);
			}
			case DOWN -> {
				setRotationAxis(Rotate.X_AXIS);
				setRotate((getRotate() + 1 + 360) % 360);
			}
			default -> { // ignore
			}
			}
		}
	}
}