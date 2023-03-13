/*
MIT License

Copyright (c) 2022 Armin Reichert

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

import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.model.common.GameLevel;
import de.amr.games.pacman.ui.fx._2d.rendering.common.ArcadeTheme;
import de.amr.games.pacman.ui.fx._2d.scene.common.GameScene2D;
import de.amr.games.pacman.ui.fx.app.Actions;
import de.amr.games.pacman.ui.fx.app.Keys;
import de.amr.games.pacman.ui.fx.input.Keyboard;

/**
 * @author Armin Reichert
 */
public class PacManCreditScene extends GameScene2D {

	public PacManCreditScene(GameController gameController) {
		super(gameController);
	}

	@Override
	public void init() {
		context.setCreditVisible(true);
	}

	@Override
	public void update() {
		// Nothing to do
	}

	@Override
	public void handleKeyboardInput() {
		if (Keyboard.pressed(Keys.ADD_CREDIT)) {
			Actions.addCredit();
		} else if (Keyboard.pressed(Keys.START_GAME)) {
			Actions.startGame();
		}
	}

	@Override
	public void drawSceneContent() {
		var r = context.r2D();
		var normalFont = r.screenFont(TS);
		var smallFont = r.screenFont(6); // TODO looks ugly
		r.drawText(g, "PUSH START BUTTON", ArcadeTheme.ORANGE, normalFont, t(6), t(17));
		r.drawText(g, "1 PLAYER ONLY", ArcadeTheme.CYAN, normalFont, t(8), t(21));
		r.drawText(g, "BONUS PAC-MAN FOR 10000", ArcadeTheme.ROSE, normalFont, t(1), t(25));
		r.drawText(g, "PTS", ArcadeTheme.ROSE, smallFont, t(25), t(25));
		r.drawText(g, "\u00A9 1980 MIDWAY MFG.CO.", ArcadeTheme.PINK, ArcadeTheme.SCREEN_FONT, t(4), t(29));
		r.drawLevelCounter(g, context.level().map(GameLevel::number), context.game().levelCounter());
	}
}