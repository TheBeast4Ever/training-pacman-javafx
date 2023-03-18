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
package de.amr.games.pacman.ui.fx._2d.rendering.common;

import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.util.List;
import java.util.Optional;

import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.model.mspacman.MovingBonus;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Common rendering functionality for renderers using a spritesheet.
 * 
 * @author Armin Reichert
 */
public abstract class SpritesheetRenderer implements Rendering2D {

	protected final Spritesheet spritesheet;

	protected SpritesheetRenderer(Spritesheet spritesheet) {
		this.spritesheet = spritesheet;
	}

	public Spritesheet spritesheet() {
		return spritesheet;
	}

	public Image image(Rectangle2D region) {
		return spritesheet.subImage(region);
	}

	@Override
	public Font screenFont(double size) {
		return size == ArcadeTheme.SCREEN_FONT.getSize() ? ArcadeTheme.SCREEN_FONT
				: Font.font(ArcadeTheme.SCREEN_FONT.getFamily(), size);
	}

	public abstract Rectangle2D ghostValueRegion(int index);

	public abstract Rectangle2D lifeSymbolRegion();

	public abstract Rectangle2D bonusSymbolRegion(int symbol);

	public abstract Rectangle2D bonusValueRegion(int symbol);

	public void drawSprite(GraphicsContext g, Rectangle2D r, double x, double y) {
		if (r != null) {
			g.drawImage(spritesheet.source(), r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight(), x, y, r.getWidth(),
					r.getHeight());
		}
	}

	public void drawSpriteCenteredOverBox(GraphicsContext g, Rectangle2D r, double x, double y) {
		if (r != null) {
			double dx = HTS - r.getWidth() / 2;
			double dy = HTS - r.getHeight() / 2;
			drawSprite(g, r, x + dx, y + dy);
		}
	}

	public void drawEntitySprite(GraphicsContext g, Entity entity, Rectangle2D r) {
		if (entity.isVisible()) {
			drawSpriteCenteredOverBox(g, r, entity.position().x(), entity.position().y());
		}
	}

	@Override
	public void drawPac(GraphicsContext g, Pac pac) {
		pac.animation().ifPresent(animation -> drawEntitySprite(g, pac, (Rectangle2D) animation.frame()));
	}

	@Override
	public void drawGhost(GraphicsContext g, Ghost ghost) {
		ghost.animation().ifPresent(animation -> drawEntitySprite(g, ghost, (Rectangle2D) animation.frame()));
	}

	@Override
	public void drawBonus(GraphicsContext g, Bonus bonus) {
		var sprite = switch (bonus.state()) {
		case Bonus.STATE_INACTIVE -> null;
		case Bonus.STATE_EDIBLE -> bonusSymbolRegion(bonus.symbol());
		case Bonus.STATE_EATEN -> bonusValueRegion(bonus.symbol());
		default -> throw new IllegalArgumentException();
		};
		if (bonus.entity() instanceof MovingBonus movingBonus) {
			g.save();
			g.translate(0, movingBonus.dy());
			drawEntitySprite(g, movingBonus, sprite);
			g.restore();
		} else {
			drawEntitySprite(g, bonus.entity(), sprite);
		}
	}

	@Override
	public void drawLevelCounter(GraphicsContext g, Optional<Integer> levelNumber, List<Byte> levelCounter) {
		double x = t(24);
		for (var symbol : levelCounter) {
			drawSprite(g, bonusSymbolRegion(symbol), x, t(34));
			x -= t(2);
		}
	}

	@Override
	public void drawLivesCounter(GraphicsContext g, int numLivesDisplayed) {
		if (numLivesDisplayed <= 0) {
			return;
		}
		int x = t(2);
		int y = t(ArcadeWorld.SIZE_TILES.y() - 2);
		int maxLives = 5;
		for (int i = 0; i < Math.min(numLivesDisplayed, maxLives); ++i) {
			drawSprite(g, lifeSymbolRegion(), x + t(2 * i), y);
		}
		// text indicating that more lives are available than displayed
		int excessLives = numLivesDisplayed - maxLives;
		if (excessLives > 0) {
			Rendering2D.drawText(g, "+" + excessLives, ArcadeTheme.YELLOW, Font.font("Serif", FontWeight.BOLD, 8), x + t(10),
					y + t(1));
		}
	}
}