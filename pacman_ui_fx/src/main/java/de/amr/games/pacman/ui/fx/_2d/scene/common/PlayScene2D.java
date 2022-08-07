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
package de.amr.games.pacman.ui.fx._2d.scene.common;

import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.lib.animation.EntityAnimationSet;
import de.amr.games.pacman.model.common.GameSound;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.model.pacman.PacManGame;
import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.shell.Actions;
import de.amr.games.pacman.ui.fx.shell.Keyboard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * 2D scene displaying the maze and the game play.
 * 
 * @author Armin Reichert
 */
public class PlayScene2D extends GameScene2D {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private final GuysInfo guysInfo = new GuysInfo(this);

	@Override
	public void init() {
		guysInfo.init(ctx.game());
		hud.setCreditVisible(!ctx.hasCredit()); // show credit only if it is zero (attract mode)
		ctx.game().bonus().setInactive();
		var game = ctx.game();
		var arcadeWorld = (ArcadeWorld) game.world();
		arcadeWorld.setFlashingAnimation(ctx.r2D.createMazeFlashingAnimation(game.level.mazeNumber));
		game.pac.setAnimationSet(ctx.r2D.createPacAnimationSet(game.pac));
		game.ghosts().forEach(ghost -> ghost.setAnimationSet(ctx.r2D.createGhostAnimationSet(ghost)));
		LOGGER.info("Recreated animations for maze, Pac-Man and the ghosts.");
	}

	@Override
	public void onKeyPressed() {
		if (Keyboard.pressed(KeyCode.DIGIT5) && !ctx.hasCredit()) {
			Actions.addCredit();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.E)) {
			Actions.cheatEatAllPellets();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.L)) {
			Actions.addLives(3);
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.N)) {
			Actions.cheatEnterNextLevel();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.X)) {
			Actions.cheatKillAllEatableGhosts();
		}
	}

	@Override
	public void update() {
		if (Env.showDebugInfoPy.get()) {
			guysInfo.update();
		}
		hud.setCreditVisible(!ctx.hasCredit() || ctx.state() == GameState.GAME_OVER);
	}

	@Override
	public void drawSceneContent(GraphicsContext g) {
		if (ctx.world() instanceof ArcadeWorld arcadeWorld) {
			var flashing = arcadeWorld.flashingAnimation();
			if (flashing.isPresent() && flashing.get().isRunning()) {
				g.drawImage((Image) flashing.get().frame(), t(0), t(3));
			} else {
				drawMaze(g);
			}
		} else {
			drawMaze(g);
		}
		ctx.r2D.drawGameStateMessage(g, ctx.hasCredit() ? ctx.state() : GameState.GAME_OVER);
		ctx.r2D.drawBonus(g, ctx.game().bonus());
		ctx.r2D.drawPac(g, ctx.game().pac);
		ctx.r2D.drawGhosts(g, ctx.game().theGhosts);
		if (!hud.isCreditVisible()) {
			int livesDisplayed = ctx.game().livesOneLessShown ? ctx.game().lives - 1 : ctx.game().lives;
			ctx.r2D.drawLivesCounter(g, livesDisplayed);
		}
		ctx.r2D.drawLevelCounter(g, ctx.game().levelCounter);
	}

	private void drawMaze(GraphicsContext g) {
		ctx.r2D.drawMaze(g, t(0), t(3), ctx.world(), ctx.level().mazeNumber, !ctx.game().energizerPulse.frame());
	}

	@Override
	protected void renderOverlayCanvas() {
		super.renderOverlayCanvas();
		var g = overlayCanvas.getGraphicsContext2D();
		if (ctx.gameVariant() == GameVariant.PACMAN) {
			g.setFill(Color.RED);
			PacManGame.RED_ZONE.forEach(tile -> {
				g.fillRect(tile.x() * TS, tile.y() * TS, TS, TS);
			});
		}
	}

	public void onSwitchFrom3D() {
		ctx.game().pac.animationSet().ifPresent(EntityAnimationSet::ensureRunning);
		ctx.game().ghosts().map(Ghost::animationSet).forEach(anim -> anim.ifPresent(EntityAnimationSet::ensureRunning));
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		ctx.gameController.sounds().play(GameSound.BONUS_EATEN);
	}

	@Override
	public void onPlayerGetsExtraLife(GameEvent e) {
		ctx.gameController.sounds().play(GameSound.EXTRA_LIFE);
	}
}