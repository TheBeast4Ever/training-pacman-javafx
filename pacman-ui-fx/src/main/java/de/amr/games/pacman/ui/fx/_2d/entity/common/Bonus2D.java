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
package de.amr.games.pacman.ui.fx._2d.entity.common;

import static de.amr.games.pacman.model.common.world.World.HTS;

import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.ui.fx._2d.rendering.common.Rendering2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 2D-representation of the bonus symbol.
 * <p>
 * In Ms. Pac-Man, the bonus is jumping up and down while wandering the maze.
 * <p>
 * TODO: Animation is not 100% accurate
 * 
 * @author Armin Reichert
 */
public class Bonus2D {

	private final Bonus bonus;
	private TimedSeq<Integer> jumpAnimation;

	public Bonus2D(Bonus bonus) {
		this.bonus = bonus;
	}

	public void setJumpAnimation(TimedSeq<Integer> jumpAnimation) {
		this.jumpAnimation = jumpAnimation;
	}

	public void startJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.restart();
		}
	}

	public void stopJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.stop();
		}
	}

	public void render(GraphicsContext g, Rendering2D r2D) {
		switch (bonus.state()) {
		case INACTIVE -> {
		}
		case EDIBLE -> {
			if (jumpAnimation != null) {
				g.save();
				g.translate(0, jumpAnimation.animate());
				renderSprite(g, r2D, r2D.getSymbolSprite(bonus.symbol()), bonus.position());
				g.restore();
			} else {
				renderSprite(g, r2D, r2D.getSymbolSprite(bonus.symbol()), bonus.position());
			}
		}
		case EATEN -> {
			renderSprite(g, r2D, r2D.getBonusValueSprite(bonus.value()), bonus.position());
		}
		}
	}

	private void renderSprite(GraphicsContext g, Rendering2D r2D, Rectangle2D sprite, V2d position) {
		double sw = sprite.getWidth(), sh = sprite.getHeight();
		r2D.renderSprite(g, sprite, bonus.position().x + HTS - sw / 2, bonus.position().y + HTS - sh / 2);
	}
}