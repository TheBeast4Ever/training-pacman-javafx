/*
MIT License

Copyright (c) 2021 Armin Reichert

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
package de.amr.games.pacman.ui.fx._3d.scene;

import static de.amr.games.pacman.ui.fx.util.U.lerp;

import javafx.scene.Camera;
import javafx.scene.transform.Rotate;

/**
 * Follows the player closely, board only partially visible.
 * 
 * @author Armin Reichert
 */
public class Cam_NearPlayer implements CameraController<PlayScene3D> {

	private Camera cam;

	public Cam_NearPlayer(Camera cam) {
		this.cam = cam;
	}

	@Override
	public Camera cam() {
		return cam;
	}

	@Override
	public void reset() {
		cam.setNearClip(0.1);
		cam.setFarClip(10000.0);
		cam.setRotationAxis(Rotate.X_AXIS);
		cam.setRotate(80);
		cam.setTranslateZ(-40);
	}

	@Override
	public void update(PlayScene3D scene) {
		// TODO this is just trial and error
		double fraction = 0.02;
		double x = lerp(cam.getTranslateX(), scene.player3D.getTranslateX() - 110, fraction);
		double y = lerp(cam.getTranslateY(), scene.player3D.getTranslateY(), fraction);
		cam.setTranslateX(x);
		cam.setTranslateY(y);
	}

	@Override
	public String toString() {
		return "Near Player";
	}
}