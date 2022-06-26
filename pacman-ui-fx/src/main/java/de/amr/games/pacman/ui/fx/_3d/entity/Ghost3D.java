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
package de.amr.games.pacman.ui.fx._3d.entity;

import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.ui.fx._2d.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.fx._3d.animation.GhostBodyAnimation;
import de.amr.games.pacman.ui.fx._3d.animation.GhostValueAnimation;
import de.amr.games.pacman.ui.fx._3d.model.Model3D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * 3D representation of a ghost.
 * <p>
 * A ghost is displayed in one of the following modes:
 * <ul>
 * <li>complete, colorful ghost with blue eyes,
 * <li>complete, blue ghost with red eyes,
 * <li>complete, blue-white flashing ghost,
 * <li>eyes only (dead ghost),
 * <li>number cube/quad (dead ghost value).
 * </ul>
 * 
 * @author Armin Reichert
 */
public class Ghost3D extends Group {

	public enum AnimationMode {
		COLORED, BLUE, FLASHING, EYES, NUMBER;
	}

	public final Ghost ghost;
	private final Motion motion = new Motion();
	private final GhostValueAnimation valueAnimation;
	private final GhostBodyAnimation bodyAnimation;
	private AnimationMode animationMode;

	public Ghost3D(Ghost ghost, Model3D model3D, Rendering2D r2D) {
		this.ghost = ghost;
		valueAnimation = new GhostValueAnimation(r2D);
		bodyAnimation = new GhostBodyAnimation(ghost, model3D);
		setAnimationMode(AnimationMode.COLORED);
	}

	public void reset(GameModel game) {
		setAnimationMode(AnimationMode.COLORED);
		update(game);
	}

	public void update(GameModel game) {
		if (ghost.killIndex != -1) {
			setAnimationMode(AnimationMode.NUMBER);
		} else if (ghost.is(GhostState.DEAD, GhostState.ENTERING_HOUSE)) {
			setAnimationMode(AnimationMode.EYES);
		} else if (game.powerTimer.isRunning()
				&& ghost.is(GhostState.LOCKED, GhostState.LEAVING_HOUSE, GhostState.FRIGHTENED)) {
			setAnimationMode(game.isPacPowerFading() ? AnimationMode.FLASHING : AnimationMode.BLUE);
		} else {
			setAnimationMode(AnimationMode.COLORED);
			bodyAnimation.update(game.world());
		}
		motion.update(ghost, this);
	}

	public AnimationMode getAnimationMode() {
		return animationMode;
	}

	public void setAnimationMode(AnimationMode animationMode) {
		if (this.animationMode != animationMode) {
			this.animationMode = animationMode;
			switch (animationMode) {
			case COLORED -> {
				bodyAnimation.showDress(true);
				bodyAnimation.setColored();
				getChildren().setAll(bodyAnimation.getRoot());
			}
			case BLUE -> {
				bodyAnimation.showDress(true);
				bodyAnimation.setBlue();
				bodyAnimation.ensureFlashingAnimationStopped();
				getChildren().setAll(bodyAnimation.getRoot());
			}
			case FLASHING -> {
				bodyAnimation.showDress(true);
				bodyAnimation.setBlue();
				bodyAnimation.ensureFlashingAnimationRunning();
				getChildren().setAll(bodyAnimation.getRoot());
			}
			case EYES -> {
				bodyAnimation.showDress(false);
				getChildren().setAll(bodyAnimation.getRoot());
			}
			case NUMBER -> {
				valueAnimation.setNumber(ghost.killIndex);
				// rotate node such that number can be read from left to right
				setRotationAxis(Rotate.X_AXIS);
				setRotate(0);
				getChildren().setAll(valueAnimation.getRoot());
			}
			}
		}
	}

	public void playFlashingAnimation() {
		bodyAnimation.ensureFlashingAnimationRunning();
	}
}