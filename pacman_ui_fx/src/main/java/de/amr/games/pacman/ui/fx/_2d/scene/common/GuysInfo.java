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

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.EntityAnimation;
import de.amr.games.pacman.lib.animation.EntityAnimationByDirection;
import de.amr.games.pacman.lib.animation.SingleEntityAnimation;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.model.common.actors.BonusState;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.model.mspacman.MsPacManGame;
import de.amr.games.pacman.model.pacman.PacManGame;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Armin Reichert
 */
public class GuysInfo {

	private static String bonusName(GameVariant gameVariant, int symbol) {
		return switch (gameVariant) {
		case MS_PACMAN -> MsPacManGame.bonusName(symbol);
		case PACMAN -> PacManGame.bonusName(symbol);
		};
	}

	private final PlayScene2D playScene;
	private GameModel game;
	private final Text[] texts = new Text[6];

	public GuysInfo(PlayScene2D playScene) {
		this.playScene = playScene;
		for (int i = 0; i < texts.length; ++i) {
			texts[i] = new Text();
			texts[i].setTextAlignment(TextAlignment.CENTER);
			texts[i].setFill(Color.WHITE);
		}
		playScene.infoLayer.getChildren().addAll(texts);
	}

	public void init(GameModel game) {
		this.game = game;
	}

	private String fmtAnimationState(EntityAnimation animation, Direction dir) {
		if (animation instanceof EntityAnimationByDirection dam) {
			return dam.get(dir).isRunning() ? "" : "(Stopped) ";
		} else if (animation instanceof SingleEntityAnimation<?> ssa) {
			return ssa.isRunning() ? "" : "(Stopped) ";
		} else {
			return "";
		}
	}

	private String fmtGhostInfo(Ghost ghost) {
		String name = ghost.id == Ghost.RED_GHOST && ghost.elroy > 0 ? "Elroy " + ghost.elroy : ghost.name;
		var stateText = ghost.getState().name();
		if (ghost.is(GhostState.HUNTING_PAC)) {
			stateText += game.huntingTimer.inChasingPhase() ? " (Chasing)" : " (Scattering)";
		}
		if (ghost.killedIndex != -1) {
			stateText += " killed: %d".formatted(ghost.killedIndex);
		}
		var animSet = ghost.animationSet();
		if (animSet.isPresent()) {
			var animState = fmtAnimationState(animSet.get().selectedAnimation(), ghost.wishDir());
			return "%s%n%s%n%s%n %s%s".formatted(ghost.tile(), name, stateText, animState, animSet.get().selected());
		} else {
			return "%s%n%s%n%s%n".formatted(ghost.tile(), name, stateText);
		}
	}

	private String fmtPacInfo(Pac pac) {
		var pacAnims = pac.animationSet();
		var stuck = pac.stuck ? " stuck" : "";
		var locationInfo = "%s%s%s".formatted(pac.tile(), pac.offset(), stuck);
		if (pacAnims.isPresent()) {
			var animState = fmtAnimationState(pacAnims.get().selectedAnimation(), pac.moveDir());
			return "%s%n%s%n%s%s".formatted(locationInfo, pac.name, animState, pacAnims.get().selected());
		}
		return "%s%n%s".formatted(locationInfo, pac.name);
	}

	private String fmtBonusInfo(Bonus bonus) {
		var symbolName = bonus.state() == BonusState.INACTIVE ? "INACTIVE" : bonusName(game.variant, bonus.symbol());
		return "%s%n%s".formatted(symbolName, game.bonus().state());
	}

	private void updateTextView(Text textView, String text, Entity entity) {
		textView.setText(text);
		var textSize = textView.getBoundsInLocal();
		var scaling = playScene.getScaling();
		textView.setX((entity.getPosition().x() + World.HTS) * scaling - textSize.getWidth() / 2);
		textView.setY(entity.getPosition().y() * scaling - textSize.getHeight());
		textView.setVisible(entity.isVisible());
	}

	private void updateTextView(Text textView, String text, Bonus bonus) {
		textView.setText(text);
		var textSize = textView.getBoundsInLocal();
		var scaling = playScene.getScaling();
		textView.setX((bonus.entity().getPosition().x() + World.HTS) * scaling - textSize.getWidth() / 2);
		textView.setY(bonus.entity().getPosition().y() * scaling - textSize.getHeight());
		textView.setVisible(bonus.state() != BonusState.INACTIVE);
	}

	public void update() {
		for (int i = 0; i < texts.length; ++i) {
			if (i < texts.length - 2) {
				var ghost = game.theGhosts[i];
				updateTextView(texts[i], fmtGhostInfo(ghost), ghost);
			} else if (i == texts.length - 2) {
				updateTextView(texts[i], fmtPacInfo(game.pac), game.pac);
			} else {
				updateTextView(texts[i], fmtBonusInfo(game.bonus()), game.bonus());
			}
		}
	}
}