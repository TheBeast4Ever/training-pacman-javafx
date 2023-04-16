/*
MIT License

Copyright (c) 2021-2023 Armin Reichert

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

package de.amr.games.pacman.ui.fx._3d.animation;

import de.amr.games.pacman.lib.math.Vector2f;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.fx._3d.entity.Pellet3D;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.Group;
import javafx.util.Duration;

/**
 * @author Armin Reichert
 */
public class FoodOscillation extends Transition {

	private final Vector2f worldCenter = ArcadeWorld.SIZE_TILES.toFloatVec().scaled(World.HTS);
	private final Group foodGroup;

	public FoodOscillation(Group foodGroup) {
		this.foodGroup = foodGroup;
		setCycleDuration(Duration.seconds(0.6));
		setCycleCount(INDEFINITE);
		setAutoReverse(true);
		setInterpolator(Interpolator.LINEAR);
	}

	@Override
	protected void interpolate(double t) {
		for (var node : foodGroup.getChildren()) {
			if (node.getUserData() instanceof Pellet3D pellet3D) {
				var centerDistance = pellet3D.position().toVector2f().euclideanDistance(worldCenter);
				double dz = 2 * Math.sin(2 * centerDistance) * t;
				node.setTranslateZ(-4 + dz);
			}
		}
	}
}