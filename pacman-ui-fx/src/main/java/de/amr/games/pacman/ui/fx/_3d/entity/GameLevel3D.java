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
package de.amr.games.pacman.ui.fx._3d.entity;

import static de.amr.games.pacman.model.common.actors.GhostState.ENTERING_HOUSE;
import static de.amr.games.pacman.model.common.actors.GhostState.LEAVING_HOUSE;
import static de.amr.games.pacman.model.common.actors.GhostState.RETURNING_TO_HOUSE;
import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;

import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.lib.math.Vector2f;
import de.amr.games.pacman.model.common.GameLevel;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.model.common.world.ArcadeGhostHouse;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.fx.Env3D;
import de.amr.games.pacman.ui.fx._2d.rendering.GameRenderer;
import de.amr.games.pacman.ui.fx._2d.rendering.Rendering2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;

/**
 * @author Armin Reichert
 */
public class GameLevel3D extends Group {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	public final ObjectProperty<DrawMode> drawModePy = new SimpleObjectProperty<>(this, "drawMode", DrawMode.FILL);

	private final GameLevel level;
	private final World3D world3D;
	private final Food3D food3D;
	private final Pac3D pac3D;
	private final Ghost3D[] ghosts3D;
	private final Bonus3D bonus3D;
	private final LevelCounter3D levelCounter3D;
	private final LivesCounter3D livesCounter3D;
	private final Scores3D scores3D;
	private final PointLight houseLighting;

	public GameLevel3D(GameLevel level, Rendering2D r2D) {
		this.level = level;

		var width = level.world().numCols() * World.TS;
		var height = level.world().numRows() * World.TS;

		int mazeNumber = level.game().mazeNumber(level.number());
		var mazeColors = new Maze3DColors(//
				r2D.mazeSideColor(mazeNumber), //
				r2D.mazeTopColor(mazeNumber), //
				r2D.ghostHouseDoorColor());

		world3D = new World3D(level.world(), mazeColors);
		world3D.drawModePy.bind(drawModePy);

		pac3D = new Pac3D(level.pac());
		pac3D.init(level.world());
		pac3D.lightOnPy.bind(Env3D.pacLightedPy);
		LOGGER.info("3D %s created", level.pac().name());

		ghosts3D = level.ghosts().map(this::createGhost3D).toArray(Ghost3D[]::new);
		LOGGER.info("3D ghosts created");

		bonus3D = new Bonus3D();

		houseLighting = new PointLight();
		houseLighting.setColor(Color.GHOSTWHITE);
		houseLighting.setMaxRange(10 * TS);
		houseLighting.setTranslateX(0.5 * width);
		houseLighting.setTranslateY(0.5 * (height - 2 * TS));
		houseLighting.setTranslateZ(-TS);

		var foodColor = r2D.mazeFoodColor(mazeNumber);
		food3D = new Food3D(level.world(), foodColor);

		var levelCounterPos = new Vector2f((level.world().numCols() - 1) * TS, TS);
		levelCounter3D = new LevelCounter3D(level.game().levelCounter(), levelCounterPos, r2D::bonusSymbolSpriteImage);

		livesCounter3D = new LivesCounter3D();
		livesCounter3D.setTranslateX(TS);
		livesCounter3D.setTranslateY(TS);
		livesCounter3D.setTranslateZ(-HTS);
		livesCounter3D().setVisible(level.game().hasCredit());

		var scoreFont = r2D.arcadeFont(TS);
		scores3D = new Scores3D(scoreFont);

		getChildren().add(world3D);
		getChildren().add(food3D);
		getChildren().add(pac3D);
		getChildren().addAll(ghosts3D);
		getChildren().add(bonus3D);
		getChildren().add(scores3D);
		getChildren().add(houseLighting);
		getChildren().add(levelCounter3D);
		getChildren().add(livesCounter3D);

	}

	private Ghost3D createGhost3D(Ghost ghost) {
		var ghost3D = new Ghost3D(ghost, GameRenderer.GHOST_COLORS[ghost.id()]);
		ghost3D.init(level);
		ghost3D.drawModePy.bind(drawModePy);
		return ghost3D;
	}

	public void update() {
		pac3D.update(level.world());
		Stream.of(ghosts3D).forEach(ghost3D -> ghost3D.update(level));
		bonus3D.update(level.bonus());
		updateHouseLightingState();
		updateDoorState();
		livesCounter3D.update(level.game().isOneLessLifeDisplayed() ? level.game().lives() - 1 : level.game().lives());
		scores3D.update(level);
		if (level.game().hasCredit()) {
			scores3D.setShowPoints(true);
		} else {
			scores3D.setShowText(Color.RED, "GAME OVER!");
		}
	}

	public World3D world3D() {
		return world3D;
	}

	public Food3D food3D() {
		return food3D;
	}

	public Pac3D pac3D() {
		return pac3D;
	}

	public Ghost3D[] ghosts3D() {
		return ghosts3D;
	}

	public Bonus3D bonus3D() {
		return bonus3D;
	}

	public Scores3D scores3D() {
		return scores3D;
	}

	public LivesCounter3D livesCounter3D() {
		return livesCounter3D;
	}

	public LevelCounter3D levelCounter3D() {
		return levelCounter3D;
	}

	private void updateHouseLightingState() {
		boolean anyGhostInHouse = level.ghosts(GhostState.LOCKED, GhostState.ENTERING_HOUSE, GhostState.LEAVING_HOUSE)
				.count() > 0;
		houseLighting.setLightOn(anyGhostInHouse);
	}

	// should be generalized to work with any ghost house
	private void updateDoorState() {
		if (level.world().ghostHouse() instanceof ArcadeGhostHouse) {
			var accessGranted = isAccessGranted(level.ghosts(), ArcadeGhostHouse.DOOR_CENTER_POSITION);
			world3D.doors().forEach(door3D -> door3D.setOpen(accessGranted));
		}
	}

	private boolean isAccessGranted(Stream<Ghost> ghosts, Vector2f doorPosition) {
		return ghosts.anyMatch(ghost -> isAccessGranted(ghost, doorPosition));
	}

	private boolean isAccessGranted(Ghost ghost, Vector2f doorPosition) {
		return ghost.isVisible() && ghost.is(RETURNING_TO_HOUSE, ENTERING_HOUSE, LEAVING_HOUSE)
				&& inDoorDistance(ghost, doorPosition);
	}

	private boolean inDoorDistance(Ghost ghost, Vector2f doorPosition) {
		return ghost.position().euclideanDistance(doorPosition) <= (ghost.is(LEAVING_HOUSE) ? TS : 3 * TS);
	}
}