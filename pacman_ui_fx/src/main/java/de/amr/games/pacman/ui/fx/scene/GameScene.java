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
package de.amr.games.pacman.ui.fx.scene;

import de.amr.games.pacman.event.GameEventAdapter;
import de.amr.games.pacman.ui.fx.shell.Keyboard;
import javafx.beans.binding.DoubleExpression;
import javafx.scene.SubScene;

/**
 * Common interface of all game scenes (2D and 3D).
 * 
 * @author Armin Reichert
 */
public interface GameScene extends GameEventAdapter {

	/**
	 * @return the current scene context
	 */
	SceneContext getSceneContext();

	/**
	 * Sets the scene context (game controller/model, 2D rendering, 3D model, sound).
	 * <p>
	 * This method is called before the scene's init method.
	 */
	void setSceneContext(SceneContext context);

	/**
	 * Called when the scene becomes the current one.
	 */
	default void init() {
		// empty default
	}

	/**
	 * Called on every tick.
	 */
	void updateAndRender();

	/**
	 * Called when the scene is replaced by another one.
	 */
	default void end() {
		// empty default
	}

	/**
	 * @return the JavaFX subscene associated with this game scene
	 */
	SubScene getFXSubScene();

	/**
	 * Defines the resizing behavior.
	 * 
	 * @param width  width property of scene container
	 * @param height height property of scene container
	 */
	void setResizeBehavior(DoubleExpression width, DoubleExpression height);

	/**
	 * Resizes the scene to the given height.
	 * 
	 * @param height new height
	 */
	default void resize(double height) {
	}

	/**
	 * @return if this is a scene with 3D content
	 */
	boolean is3D();

	/**
	 * Called when a key has been pressed. The keyboard state can be queried using the {@link Keyboard} class.
	 */
	default void onKeyPressed() {
	}
}