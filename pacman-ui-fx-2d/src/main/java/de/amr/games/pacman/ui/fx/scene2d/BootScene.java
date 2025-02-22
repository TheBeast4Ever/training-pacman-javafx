/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.scene2d;

import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.ui.fx.PacManGames2dApp;
import de.amr.games.pacman.ui.fx.rendering2d.ArcadePalette;
import javafx.geometry.Rectangle2D;

import static de.amr.games.pacman.lib.Globals.RND;

/**
 * @author Armin Reichert
 */
public class BootScene extends GameScene2D {

	private double start; // seconds

	@Override
	public void init() {
		start = 1.0;
		setScoreVisible(false);
	}

	@Override
	public void update() {
		if (state().timer().atSecond(start + 3)) {
			gameController().terminateCurrentState();
		}
	}

	@Override
	public void draw() {
		var timer = state().timer();
		if (timer.tick() == 1) {
			clearCanvas();
		}
		else if (timer.betweenSeconds(start, start + 1) && timer.tick() % 4 == 0) {
			paintRandomHexCodes();
		}
		else if (timer.betweenSeconds(start + 1, start + 2.5) && timer.tick() % 4 == 0) {
			paintRandomSprites();
		}
		else if (timer.atSecond(start + 2.5)) {
			paintGrid(PacManGames2dApp.CANVAS_WIDTH_UNSCALED, PacManGames2dApp.CANVAS_HEIGHT_UNSCALED, 16);
		}
	}

	@Override
	protected void drawSceneContent() {
		// not used here
	}

	private void paintRandomHexCodes() {
		clearCanvas();
		g.setFill(ArcadePalette.PALE);
		g.setFont(sceneFont(8));
		for (int row = 0; row < GameModel.TILES_Y; ++row) {
			for (int col = 0; col < GameModel.TILES_X; ++col) {
				var hexCode = Integer.toHexString(RND.nextInt(16));
				g.fillText(hexCode, s(t(col)), s(t(row + 1)));
			}
		}
	}

	private void paintRandomSprites() {
		clearCanvas();
		for (int row = 0; row < GameModel.TILES_Y / 2; ++row) {
			if (RND.nextInt(100) > 10) {
				var region1 = randomSpritesheetTile();
				var region2 = randomSpritesheetTile();
				var splitX = GameModel.TILES_X / 8 + RND.nextInt(GameModel.TILES_X / 4);
				for (int col = 0; col < GameModel.TILES_X / 2; ++col) {
					var region = col < splitX ? region1 : region2;
					drawSprite(region, region.getWidth() * col, region.getHeight() * row);
				}
			}
		}
	}

	private Rectangle2D randomSpritesheetTile() {
		var source = context.spritesheet().source();
		var raster = context.spritesheet().raster();
		double x = RND.nextDouble() * (source.getWidth() - raster);
		double y = RND.nextDouble() * (source.getHeight() - raster);
		return new Rectangle2D(x, y, raster, raster);
	}

	private void paintGrid(double width, double height, int raster) {
		clearCanvas();
		var numRows = GameModel.TILES_Y / 2;
		var numCols = GameModel.TILES_X / 2;
		g.setStroke(ArcadePalette.PALE);
		g.setLineWidth(s(2.0));
		for (int row = 0; row <= numRows; ++row) {
			g.setLineWidth(row == 0 || row == numRows ? s(4.0) : s(2.0));
			g.strokeLine(0, s(row * raster), s(width), s(row * raster));
		}
		for (int col = 0; col <= numCols; ++col) {
			g.setLineWidth(col == 0 || col == numCols ? s(4.0) : s(2.0));
			g.strokeLine(s(col * raster), 0, s(col * raster), s(height));
		}
	}
}