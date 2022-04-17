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
package de.amr.games.pacman.ui.fx._3d.scene;

import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.ui.fx.shell.FlashMessageView.showFlashMessage;

import java.util.stream.Stream;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.controller.event.DefaultGameEventHandler;
import de.amr.games.pacman.controller.event.GameEvent;
import de.amr.games.pacman.controller.event.GameStateChangeEvent;
import de.amr.games.pacman.controller.event.ScatterPhaseStartedEvent;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.model.common.GhostState;
import de.amr.games.pacman.ui.GameSound;
import de.amr.games.pacman.ui.fx._2d.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.fx._2d.rendering.mspacman.Rendering2D_MsPacMan;
import de.amr.games.pacman.ui.fx._2d.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.fx._3d.entity.Bonus3D;
import de.amr.games.pacman.ui.fx._3d.entity.Ghost3D;
import de.amr.games.pacman.ui.fx._3d.entity.LevelCounter3D;
import de.amr.games.pacman.ui.fx._3d.entity.LivesCounter3D;
import de.amr.games.pacman.ui.fx._3d.entity.Maze3D;
import de.amr.games.pacman.ui.fx._3d.entity.Pac3D;
import de.amr.games.pacman.ui.fx._3d.entity.Score3D;
import de.amr.games.pacman.ui.fx._3d.model.PacManModel3D;
import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.sound.SoundManager;
import de.amr.games.pacman.ui.fx.util.CoordinateAxes;
import de.amr.games.pacman.ui.fx.util.U;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;

/**
 * 3D play scene with sound and animations.
 * 
 * @author Armin Reichert
 */
public class PlayScene3D extends DefaultGameEventHandler implements GameScene {

	private final GameController gc;
	private final SubScene fxSubScene;
	private final PacManModel3D model3D;
	private final AmbientLight light = new AmbientLight(Color.GHOSTWHITE);
	private final Image floorTexture = U.image("/common/escher-texture.jpg");
	private final Color floorColorWithTexture = Color.DARKBLUE;
	private final Color floorColorNoTexture = Color.rgb(30, 30, 30);
	private final PlaySceneCamera camera = new PlaySceneCamera();

	private GameModel game;
	private Rendering2D r2D;
	private Pac3D player3D;
	private Maze3D maze3D;
	private Ghost3D[] ghosts3D;
	private Bonus3D bonus3D;
	private Score3D score3D;
	private LevelCounter3D levelCounter3D;
	private LivesCounter3D livesCounter3D;

	public PlayScene3D(GameController gc, PacManModel3D model3D) {
		this.gc = gc;
		this.model3D = model3D;
		var axes = new CoordinateAxes(1000);
		axes.visibleProperty().bind(Env.$axesVisible);
		// first child is placeholder for scene content
		var root = new Group(new Group(), axes, light);
		// width and height of subscene are defined using data binding, see class GameScenes
		fxSubScene = new SubScene(root, 1, 1, true, SceneAntialiasing.BALANCED);
		fxSubScene.setCamera(camera);
		fxSubScene.setOnKeyPressed(camera::onKeyPressed);
		Env.$perspective.addListener(this::onPerspectiveChange);
		Env.$useMazeFloorTexture.addListener(this::onUseMazeFloorTextureChange);
	}

	@Override
	public SubScene getFXSubScene() {
		return fxSubScene;
	}

	public PlaySceneCamera getCamera() {
		return camera;
	}

	@Override
	public void setContext() {
		game = gc.game;
		r2D = switch (gc.gameVariant) {
		case MS_PACMAN -> Rendering2D_MsPacMan.get();
		case PACMAN -> Rendering2D_PacMan.get();
		};
	}

	@Override
	public void resize(double height) {
		// data binding does the job
	}

	@Override
	public void init() {
		V2i size = new V2i(game.world.numCols(), game.world.numRows()).scaled(TS);

		maze3D = new Maze3D(size.x, size.y);
		maze3D.createWallsAndDoors(game.world, //
				r2D.getMazeSideColor(game.mazeNumber), //
				r2D.getMazeTopColor(game.mazeNumber), //
				r2D.getGhostHouseDoorColor(game.mazeNumber));
		maze3D.createFood(game.world, r2D.getFoodColor(game.mazeNumber));

		player3D = new Pac3D(game.player, model3D, r2D);
		ghosts3D = game.ghosts().map(ghost -> new Ghost3D(ghost, model3D, r2D)).toArray(Ghost3D[]::new);
		bonus3D = new Bonus3D(r2D);

		score3D = new Score3D();
		score3D.setFont(r2D.getArcadeFont());
		if (gc.attractMode) {
			score3D.setComputeScoreText(false);
			score3D.txtScore.setFill(Color.RED);
			score3D.txtScore.setText("GAME OVER!");
		} else {
			score3D.setComputeScoreText(true);
		}

		livesCounter3D = new LivesCounter3D(model3D);
		livesCounter3D.getTransforms().add(new Translate(TS, TS, -HTS));
		livesCounter3D.setVisible(!gc.attractMode);

		levelCounter3D = new LevelCounter3D(r2D, size.x - TS, TS);
		levelCounter3D.update(game);

		var world3D = new Group(maze3D, score3D, livesCounter3D, levelCounter3D, player3D, bonus3D);
		world3D.getChildren().addAll(ghosts3D);
		world3D.setTranslateX(-size.x / 2);
		world3D.setTranslateY(-size.y / 2);

		Group root = (Group) fxSubScene.getRoot();
		root.getChildren().set(0, world3D);

		setPerspective(Env.$perspective.get());
		setUseMazeFloorTexture(Env.$useMazeFloorTexture.get());

		maze3D.$wallHeight.bind(Env.$mazeWallHeight);
		maze3D.$resolution.bind(Env.$mazeResolution);
		maze3D.$resolution.addListener(this::onMazeResolutionChange);
	}

	@Override
	public void end() {
		// Note: property bindings are garbage collected, no need to explicitly unbind them here
		maze3D.$resolution.removeListener(this::onMazeResolutionChange);
	}

	@Override
	public void update() {
		maze3D.update(game);
		player3D.update();
		Stream.of(ghosts3D).forEach(Ghost3D::update);
		bonus3D.update(game.bonus);
		score3D.update(game.score, game.levelNumber, game.highscorePoints, game.highscoreLevel);
		livesCounter3D.update(game.player.lives);
		camera.update(player3D);
		if (SoundManager.get().getClip(GameSound.PACMAN_MUNCH).isPlaying() && game.player.starvingTicks > 10) {
			SoundManager.get().stop(GameSound.PACMAN_MUNCH);
		}
	}

	public void onSwitchFrom2DScene() {
		if (game.player.powerTimer.isRunning()) {
			Stream.of(ghosts3D) //
					.filter(ghost3D -> ghost3D.ghost.is(GhostState.FRIGHTENED) || ghost3D.ghost.is(GhostState.LOCKED))
					.filter(ghost3D -> !ghost3D.isLooksFrightened()) //
					.forEach(Ghost3D::setFrightenedLook);
		}
		maze3D.pellets().forEach(pellet -> pellet.setVisible(!game.world.isFoodEaten(pellet.tile)));
		if (gc.state == GameState.HUNTING || gc.state == GameState.GHOST_DYING) {
			maze3D.energizerAnimations().forEach(Animation::play);
		}
		if (game.player.powerTimer.isRunning() && !SoundManager.get().getClip(GameSound.PACMAN_POWER).isPlaying()) {
			SoundManager.get().loop(GameSound.PACMAN_POWER, Animation.INDEFINITE);
		}
		if (!gc.attractMode && gc.state == GameState.HUNTING && !SoundManager.get().isAnySirenPlaying()
				&& !game.player.powerTimer.isRunning()) {
			int scatterPhase = game.huntingPhase / 2;
			SoundManager.get().startSiren(scatterPhase);
		}
	}

	private void onMazeResolutionChange(ObservableValue<? extends Number> property, Number oldValue, Number newValue) {
		if (!oldValue.equals(newValue)) {
			maze3D.createWallsAndDoors(game.world, //
					r2D.getMazeSideColor(game.mazeNumber), //
					r2D.getMazeTopColor(game.mazeNumber), //
					r2D.getGhostHouseDoorColor(game.mazeNumber));
		}
	}

	private void onPerspectiveChange(Observable $perspective, Perspective oldPerspective, Perspective newPerspective) {
		setPerspective(newPerspective);
	}

	public void setPerspective(Perspective perspective) {
		camera.setPerspective(perspective);
		fxSubScene.requestFocus();
		if (score3D != null) {
			// keep the score in plain sight
			score3D.rotationAxisProperty().bind(camera.rotationAxisProperty());
			score3D.rotateProperty().bind(camera.rotateProperty());
		}
	}

	private void onUseMazeFloorTextureChange(Observable $useMazeFloorTexture, Boolean oldValue, Boolean newValue) {
		setUseMazeFloorTexture(newValue);
	}

	private void setUseMazeFloorTexture(Boolean use) {
		if (use) {
			maze3D.getFloor().setTexture(floorTexture);
			maze3D.getFloor().setColor(floorColorWithTexture);
		} else {
			maze3D.getFloor().setTexture(null);
			maze3D.getFloor().setColor(floorColorNoTexture);
		}
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public void onScatterPhaseStarted(ScatterPhaseStartedEvent e) {
		SoundManager.get().stopSirens();
		if (!gc.attractMode) {
			SoundManager.get().startSiren(e.scatterPhase);
		}
	}

	@Override
	public void onPlayerGainsPower(GameEvent e) {
		SoundManager.get().stopSirens();
		if (!gc.attractMode) {
			SoundManager.get().loop(GameSound.PACMAN_POWER, Animation.INDEFINITE);
		}
		Stream.of(ghosts3D) //
				.filter(ghost3D -> ghost3D.ghost.is(GhostState.FRIGHTENED) || ghost3D.ghost.is(GhostState.LOCKED))
				.forEach(Ghost3D::setFrightenedLook);
	}

	@Override
	public void onPlayerLosingPower(GameEvent e) {
		Stream.of(ghosts3D) //
				.filter(ghost3D -> ghost3D.ghost.is(GhostState.FRIGHTENED)) //
				.forEach(Ghost3D::playFlashingAnimation);
		if (!gc.attractMode) {
			SoundManager.get().startSiren(game.huntingPhase / 2);
		}
	}

	@Override
	public void onPlayerLostPower(GameEvent e) {
		SoundManager.get().stop(GameSound.PACMAN_POWER);
		Stream.of(ghosts3D).forEach(Ghost3D::setNormalLook);
	}

	@Override
	public void onPlayerFoundFood(GameEvent e) {
		// when cheat "eat all pellets" is used, no tile is present
		e.tile.ifPresent(tile -> {
			maze3D.pelletAt(tile).ifPresent(maze3D::hidePellet);
			AudioClip munching = SoundManager.get().getClip(GameSound.PACMAN_MUNCH);
			if (!munching.isPlaying() && !gc.attractMode) {
				SoundManager.get().loop(GameSound.PACMAN_MUNCH, Animation.INDEFINITE);
			}
		});
	}

	@Override
	public void onBonusActivated(GameEvent e) {
		bonus3D.showSymbol(game.bonus.symbol);
	}

	@Override
	public void onBonusEaten(GameEvent e) {
		bonus3D.showPoints(game.bonus.points);
		if (!gc.attractMode) {
			SoundManager.get().play(GameSound.BONUS_EATEN);
		}
	}

	@Override
	public void onBonusExpired(GameEvent e) {
		bonus3D.setVisible(false);
	}

	@Override
	public void onExtraLife(GameEvent e) {
		showFlashMessage(1.5, Env.message("extra_life"));
		SoundManager.get().play(GameSound.EXTRA_LIFE);
	}

	@Override
	public void onGhostReturnsHome(GameEvent e) {
		if (!gc.attractMode) {
			SoundManager.get().play(GameSound.GHOST_RETURNING);
		}
	}

	@Override
	public void onGhostEntersHouse(GameEvent e) {
		if (game.ghosts(GhostState.DEAD).count() == 0) {
			SoundManager.get().stop(GameSound.GHOST_RETURNING);
		}
	}

	@Override
	public void onGhostLeavingHouse(GameEvent e) {
		e.ghost.ifPresent(ghost -> ghosts3D[ghost.id].setNormalLook());
	}

	@Override
	public void onGhostRevived(GameEvent e) {
		Ghost ghost = e.ghost.get();
		ghosts3D[ghost.id].playRevivalAnimation();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		switch (e.newGameState) {
		case READY -> {
			maze3D.reset();
			player3D.reset();
			Stream.of(ghosts3D).forEach(Ghost3D::reset);
			SoundManager.get().stopAll();
			if (!gc.attractMode && !gc.gameRunning) {
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}
		case HUNTING -> {
			maze3D.energizerAnimations().forEach(Animation::play);
		}
		case PACMAN_DYING -> {
			SoundManager.get().stopAll();
			Stream.of(ghosts3D).forEach(Ghost3D::setNormalLook);
			Color killerColor = r2D.getGhostSkinColor(
					Stream.of(game.ghosts).filter(ghost -> ghost.tile().equals(game.player.tile())).findAny().get().id);
			new SequentialTransition( //
					U.afterSec(1.0, game::hideGhosts), //
					player3D.dyingAnimation(killerColor, gc.attractMode), //
					U.afterSec(2.0, () -> gc.stateTimer().expire()) //
			).play();
		}
		case GHOST_DYING -> {
			if (!gc.attractMode) {
				SoundManager.get().play(GameSound.GHOST_EATEN);
			}
		}
		case LEVEL_STARTING -> {
			// TODO: This is not executed at the *first* level. Maybe I should change the state machine to make a transition
			// from READY to LEVEL_STARTING when the game starts?
			maze3D.createWallsAndDoors(game.world, //
					r2D.getMazeSideColor(game.mazeNumber), //
					r2D.getMazeTopColor(game.mazeNumber), //
					r2D.getGhostHouseDoorColor(game.mazeNumber));
			maze3D.createFood(game.world, r2D.getFoodColor(game.mazeNumber));
			maze3D.energizerAnimations().forEach(Animation::stop);
			levelCounter3D.update(game);
			var message = Env.message("level_starting", game.levelNumber);
			showFlashMessage(1, message);
			U.afterSec(3, () -> gc.stateTimer().expire()).play();
		}
		case LEVEL_COMPLETE -> {
			Stream.of(ghosts3D).forEach(Ghost3D::setNormalLook);
			var message = Env.LEVEL_COMPLETE_TALK.next() + "\n\n" + Env.message("level_complete", game.levelNumber);
			new SequentialTransition( //
					U.pause(2.0), //
					maze3D.createMazeFlashingAnimation(game.numFlashes), //
					U.afterSec(1.0, () -> game.player.hide()), //
					U.afterSec(0.5, () -> showFlashMessage(2, message)), //
					U.afterSec(2.0, () -> gc.stateTimer().expire()) //
			).play();
		}
		case GAME_OVER -> {
			showFlashMessage(3, Env.GAME_OVER_TALK.next());
		}
		}

		// exit HUNTING
		if (e.oldGameState == GameState.HUNTING && e.newGameState != GameState.GHOST_DYING) {
			maze3D.energizerAnimations().forEach(Animation::stop);
			bonus3D.setVisible(false);
			SoundManager.get().stopAll();
		}
	}
}