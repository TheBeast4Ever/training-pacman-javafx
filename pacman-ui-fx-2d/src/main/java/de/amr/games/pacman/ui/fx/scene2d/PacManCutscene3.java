/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.scene2d;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.event.GameEventType;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.actors.Ghost;
import de.amr.games.pacman.model.actors.GhostAnimations;
import de.amr.games.pacman.model.actors.Pac;
import de.amr.games.pacman.model.actors.PacAnimations;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.GhostAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.PacAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.SpritesheetPacManGame;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static de.amr.games.pacman.lib.Globals.v2i;

/**
 * @author Armin Reichert
 */
public class PacManCutscene3 extends GameScene2D {

	private int initialDelay;
	private int frame;
	private Pac pac;
	private Ghost blinky;

	@Override
	public void init() {
		var ss = (SpritesheetPacManGame) context.spritesheet();

		setCreditVisible(!GameController.it().hasCredit());
		setScoreVisible(true);

		frame = -1;
		initialDelay = 120;

		pac = new Pac("Pac-Man");
		pac.setAnimations(new PacAnimationsPacManGame(pac, ss));
		pac.selectAnimation(PacAnimations.MUNCHING);
		pac.startAnimation();
		pac.placeAtTile(v2i(29, 20), 0, 0);
		pac.setMoveDir(Direction.LEFT);
		pac.setPixelSpeed(1.25f);
		pac.show();

		blinky = new Ghost(GameModel.RED_GHOST, "Blinky");
		blinky.setAnimations(new GhostAnimationsPacManGame(blinky, ss));
		blinky.selectAnimation(GhostAnimations.BLINKY_PATCHED);
		blinky.startAnimation();
		blinky.placeAtTile(v2i(35, 20), 0, 0);
		blinky.setMoveAndWishDir(Direction.LEFT);
		blinky.setPixelSpeed(1.25f);
		blinky.show();
	}

	@Override
	public void update() {
		if (initialDelay > 0) {
			--initialDelay;
			if (initialDelay == 0) {
				GameController.it().publishGameEvent(GameEventType.INTERMISSION_STARTED);
			}
			return;
		}
		if (state().timer().hasExpired()) {
			return;
		}

		switch (++frame) {
		case 400: {
			blinky.placeAtTile(v2i(-1, 20), 0, 0);
			blinky.setMoveAndWishDir(Direction.RIGHT);
			blinky.selectAnimation(GhostAnimations.BLINKY_NAKED);
			blinky.startAnimation();
			break;
		}

		case 700: {
			state().timer().expire();
			break;
		}

		default: {
			pac.move();
			blinky.move();
			break;
		}

		}
	}

	@Override
	public void drawSceneContent() {
		drawPac(pac);
		drawGhost(blinky);
		drawLevelCounter();
	}

	@Override
	protected void drawSceneInfo() {
		var text = initialDelay > 0 ? String.format("Wait %d", initialDelay) : String.format("Frame %d", frame);
		drawText(text, Color.YELLOW, Font.font("Sans", 16), t(1), t(5));
	}
}