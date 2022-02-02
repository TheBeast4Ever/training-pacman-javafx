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
package de.amr.games.pacman.ui.fx._2d.scene.pacman;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.pacman.Intermission3Controller;
import de.amr.games.pacman.controller.pacman.Intermission3Controller.IntermissionState;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.ui.GameSounds;
import de.amr.games.pacman.ui.fx._2d.entity.common.Player2D;
import de.amr.games.pacman.ui.fx._2d.entity.pacman.BlinkyNaked2D;
import de.amr.games.pacman.ui.fx._2d.entity.pacman.BlinkyPatched2D;
import de.amr.games.pacman.ui.fx._2d.scene.common.AbstractGameScene2D;
import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.shell.GameUI;
import javafx.scene.canvas.Canvas;

/**
 * Third intermission scene: Blinky in shred dress chases Pac-Man, comes back half-naked drawing dress over the floor.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene3 extends AbstractGameScene2D {

	private final Intermission3Controller sc = new Intermission3Controller();
	private Player2D pacMan2D;
	private BlinkyPatched2D blinkyPatched2D;
	private BlinkyNaked2D blinkyNaked2D;

	public PacMan_IntermissionScene3(GameController gameController, Canvas canvas) {
		super(gameController, canvas, GameUI.RENDERING_PACMAN);
	}

	@Override
	public void init() {
		super.init();

		sc.playIntermissionSound = () -> Env.sounds.loop(GameSounds.INTERMISSION_3, 2);
		sc.init(gameController);

		pacMan2D = new Player2D(sc.pac, r2D);
		blinkyPatched2D = new BlinkyPatched2D(sc.blinky, GameUI.RENDERING_PACMAN);
		blinkyNaked2D = new BlinkyNaked2D(sc.blinky, GameUI.RENDERING_PACMAN);
		pacMan2D.munchingAnimations.values().forEach(TimedSequence::restart);
		blinkyPatched2D.animation.restart();
		blinkyNaked2D.animation.restart();
	}

	@Override
	public void doUpdate() {
		sc.updateState();
	}

	@Override
	public void doRender() {
		drawLevelCounter();
		pacMan2D.render(gc);
		if (sc.currentStateID == IntermissionState.CHASING_PACMAN) {
			blinkyPatched2D.render(gc);
		} else {
			blinkyNaked2D.render(gc);
		}
	}
}