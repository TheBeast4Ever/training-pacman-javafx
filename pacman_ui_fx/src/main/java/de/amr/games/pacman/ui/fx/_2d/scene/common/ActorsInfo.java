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

import java.util.ArrayList;
import java.util.List;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.EntityAnimation;
import de.amr.games.pacman.lib.animation.EntityAnimationByDirection;
import de.amr.games.pacman.lib.animation.SingleEntityAnimation;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.model.common.actors.BonusState;
import de.amr.games.pacman.model.common.actors.Creature;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Armin Reichert
 */
public class ActorsInfo {

	private static final String[] PACMAN_BONUS_NAMES = { "CHERRIES", "STRAWBERRY", "PEACH", "APPLE", "GRAPES", "GALAXIAN",
			"BELL", "KEY" };

	private static final String[] MS_PACMAN_BONUS_NAMES = { "CHERRIES", "STRAWBERRY", "PEACH", "PRETZEL", "APPLE", "PEAR",
			"BANANA" };

	// 0..3: ghost info, 4: Pac info, 5: bonus info
	private static final int NUM_INFOS = 6;
	private static final int PAC_INDEX = 4;
	private static final int BONUS_INDEX = 5;

	private final GameScene2D hostScene;
	private final List<Pane> panes = new ArrayList<>();
	private final List<Text> texts = new ArrayList<>();

	public final BooleanProperty enabledPy = new SimpleBooleanProperty();

	public ActorsInfo(GameScene2D hostScene) {
		this.hostScene = hostScene;
		for (int i = 0; i < NUM_INFOS; ++i) {
			var text = new Text();
			text.setTextAlignment(TextAlignment.CENTER);
			text.setFill(Color.WHITE);
			texts.add(text);
			var textBox = new VBox(text);
			textBox.setBackground(Ufx.colorBackground(Color.rgb(200, 200, 255, 0.5)));
			panes.add(textBox);
		}
		panes.forEach(hostScene::addToOverlayPane);
	}

	public void update() {
		if (!enabledPy.get()) {
			return;
		}
		var game = hostScene.ctx.game();
		for (int i = 0; i < 4; ++i) {
			var ghost = game.theGhosts[i];
			updateInfo(panes.get(i), texts.get(i), ghostInfo(ghost), ghost);
			panes.get(i).setVisible(ghost.isVisible());
		}

		updateInfo(panes.get(PAC_INDEX), texts.get(PAC_INDEX), pacInfo(game.pac), game.pac);
		panes.get(PAC_INDEX).setVisible(game.pac.isVisible());

		var bonus = game.bonus();
		updateInfo(panes.get(BONUS_INDEX), texts.get(BONUS_INDEX), bonusInfo(bonus), bonus.entity());
		panes.get(BONUS_INDEX).setVisible(bonus.state() != BonusState.INACTIVE);
	}

	private void updateInfo(Pane pane, Text text, String info, Entity entity) {
		text.setText(info);
		var textSize = text.getBoundsInLocal();
		var scaling = hostScene.scaling();
		pane.setTranslateX((entity.getPosition().x() + World.HTS) * scaling - textSize.getWidth() / 2);
		pane.setTranslateY(entity.getPosition().y() * scaling - textSize.getHeight());
	}

	private String locationInfo(Creature guy) {
		return "Tile: %s%s%s".formatted(guy.tile(), guy.offset(), guy.isStuck() ? " stuck" : "");
	}

	private String movementInfo(Creature guy) {
		return "Velocity: %s%ndir:%s wish:%s".formatted(guy.getVelocity(), guy.moveDir(), guy.wishDir());
	}

	private String animationStateInfo(EntityAnimation animation, Direction dir) {
		if (animation instanceof EntityAnimationByDirection dam) {
			return dam.get(dir).isRunning() ? "" : "(Stopped) ";
		} else if (animation instanceof SingleEntityAnimation<?> ssa) {
			return ssa.isRunning() ? "" : "(Stopped) ";
		} else {
			return "";
		}
	}

	private String ghostInfo(Ghost ghost) {
		var game = hostScene.ctx.game();
		String name = ghost.id == Ghost.RED_GHOST && game.cruiseElroyState > 0 ? "Elroy " + game.cruiseElroyState
				: ghost.name;
		var stateText = ghost.getState().name();
		if (ghost.is(GhostState.HUNTING_PAC)) {
			stateText += game.huntingTimer.inChasingPhase() ? " (Chasing)" : " (Scattering)";
		}
		if (game.killedIndex[ghost.id] != -1) {
			stateText += " killed: %d".formatted(game.killedIndex[ghost.id]);
		}
		var selectedAnim = ghost.animation();
		if (selectedAnim.isPresent()) {
			var key = ghost.animationSet().get().selectedKey();
			var animState = animationStateInfo(selectedAnim.get(), ghost.wishDir());
			return "%s%n%s%n%s%n%s %s%s".formatted(name, locationInfo(ghost), movementInfo(ghost), stateText, animState, key);
		}
		return "%s%n%s%n%s%n%s".formatted(name, locationInfo(ghost), movementInfo(ghost), stateText);
	}

	private String pacInfo(Pac pac) {
		var selectedAnim = pac.animation();
		if (selectedAnim.isPresent()) {
			var key = pac.animationSet().get().selectedKey();
			var animState = animationStateInfo(selectedAnim.get(), pac.moveDir());
			return "%s%n%s%n%s%n%s%s".formatted(pac.name, locationInfo(pac), movementInfo(pac), animState, key);
		} else {
			return "%s%n%s%n%s".formatted(pac.name, locationInfo(pac), movementInfo(pac));
		}
	}

	private String bonusInfo(Bonus bonus) {
		var game = hostScene.ctx.game();
		var bonusName = switch (game.variant) {
		case MS_PACMAN -> MS_PACMAN_BONUS_NAMES[bonus.index()];
		case PACMAN -> PACMAN_BONUS_NAMES[bonus.index()];
		};
		var symbolText = bonus.state() == BonusState.INACTIVE ? "INACTIVE" : bonusName;
		return "%s%n%s".formatted(symbolText, game.bonus().state());
	}
}