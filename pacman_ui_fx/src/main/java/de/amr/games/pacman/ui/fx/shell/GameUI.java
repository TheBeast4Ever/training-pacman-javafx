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
package de.amr.games.pacman.ui.fx.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameSoundController;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameEventAdapter;
import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.fx._2d.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.fx._2d.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.fx._2d.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.fx._2d.rendering.pacman.SpritesheetPacMan;
import de.amr.games.pacman.ui.fx._2d.scene.common.PlayScene2D;
import de.amr.games.pacman.ui.fx._3d.model.Model3D;
import de.amr.games.pacman.ui.fx._3d.scene.PlayScene3D;
import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.app.GameLoop;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene.SceneContext;
import de.amr.games.pacman.ui.fx.scene.SceneManager;
import de.amr.games.pacman.ui.fx.shell.info.Dashboard;
import de.amr.games.pacman.ui.fx.sound.mspacman.MsPacManGameSounds;
import de.amr.games.pacman.ui.fx.sound.pacman.PacManGameSounds;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX UI for Pac-Man / Ms. Pac-Man game.
 * <p>
 * The play scene is available in 2D and 3D. The intro scenes and intermission scenes are all 2D.
 * 
 * @author Armin Reichert
 */
public class GameUI implements GameEventAdapter {

	private static final Logger logger = LogManager.getFormatterLogger();

	private static final GameSoundController PACMAN_SOUNDS = new PacManGameSounds();
	private static final GameSoundController MS_PACMAN_SOUNDS = new MsPacManGameSounds();
	private static final Image APP_ICON = Ufx.image("icons/pacman.png");

	private final GameController gameController;
	private final Stage stage;
	private final Scene scene;
	private final StackPane sceneRoot;
	private final StackPane gameScenePlaceholder;
	private final Dashboard dashboard;
	private final FlashMessageView flashMessageView;
	private final SceneContext sceneContext;
	private GameScene currentGameScene;

	public GameUI(GameController gameController, Stage stage, double width, double height) {
		this.gameController = gameController;
		this.stage = stage;

		GameEvents.addEventListener(this);

		// UI has 3 layers. From bottom to top: game scene, dashboard, flash message view.
		flashMessageView = new FlashMessageView();

		dashboard = new Dashboard(this, gameController);

		gameScenePlaceholder = new StackPane();
		Env.drawMode3D.addListener((x, y, z) -> updateBackground());
		Env.bgColor.addListener((x, y, z) -> updateBackground());
		updateBackground();

		sceneRoot = new StackPane(gameScenePlaceholder, dashboard, flashMessageView);

		scene = new Scene(sceneRoot, width, height);
		SceneManager.allGameScenes().forEach(gameScene -> gameScene.setParent(scene));
		logger.info("Main scene created. Size: %.0f x %.0f", scene.getWidth(), scene.getHeight());

		var pacSteering = new KeyboardPacSteering(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT);
		gameController.setPacSteering(pacSteering);

		sceneContext = new SceneContext();
		sceneContext.gameController = gameController;
		updateCurrentGameScene(true);

		var introMessage = new PauseTransition(Duration.seconds(3));
		introMessage.setOnFinished(e -> Actions.playVoiceMessage(Actions.SOUND_PRESS_KEY_TO_START));
		introMessage.play();

		scene.setOnKeyPressed(Keyboard::processEvent);
		Keyboard.addHandler(this::onKeyPressed);
		Keyboard.addHandler(pacSteering::onKeyPressed);
		Keyboard.addHandler(() -> currentGameScene.onKeyPressed());

		stage.setMinHeight(328);
		stage.setMinWidth(241);
		stage.getIcons().add(APP_ICON);
		stage.setOnCloseRequest(e -> GameLoop.get().stop());
		stage.setTitle(gameController.game().variant == GameVariant.PACMAN ? "Pac-Man" : "Ms. Pac-Man");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}

	private void updateBackground() {
		var mode = Env.drawMode3D.get();
		var bgColor = Env.bgColor.get();
		gameScenePlaceholder.setBackground(Ufx.colorBackground(mode == DrawMode.FILL ? bgColor : Color.BLACK));
	}

	public double getWidth() {
		return stage.getWidth();
	}

	public double getHeight() {
		return stage.getHeight();
	}

	public Scene getScene() {
		return scene;
	}

	public double getMainSceneWidth() {
		return scene.getWidth();
	}

	public double getMainSceneHeight() {
		return scene.getHeight();
	}

	public FlashMessageView getFlashMessageView() {
		return flashMessageView;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public GameScene getCurrentGameScene() {
		return currentGameScene;
	}

	/**
	 * Called on every tick (if simulation is not paused). Game scene is updated *and* rendered such that when simulation
	 * is paused it gets redrawn nevertheless
	 */
	public void update() {
		gameController.update();
		currentGameScene.update();
	}

	@Override
	public void onGameEvent(GameEvent event) {
		GameEventAdapter.super.onGameEvent(event);
		currentGameScene.onGameEvent(event);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		updateCurrentGameScene(false);
		dashboard.init();
	}

	@Override
	public void onUIForceUpdate(GameEvent e) {
		updateCurrentGameScene(true);
	}

	/**
	 * Called on every tick (also if simulation is paused).
	 */
	public void render() {
		flashMessageView.update();
		dashboard.update();
	}

	public void setFullScreen(boolean fullscreen) {
		stage.setFullScreen(fullscreen);
	}

	public void toggle3D() {
		var game = gameController.game();
		var state = gameController.state();
		Env.toggle(Env.use3D);
		if (SceneManager.sceneExistsInBothDimensions(game, state)) {
			updateCurrentGameScene(true);
			if (currentGameScene instanceof PlayScene2D) {
				((PlayScene2D) currentGameScene).onSwitchFrom3D();
			} else if (currentGameScene instanceof PlayScene3D) {
				dashboard.secPiP.init();
				dashboard.secPiP.update();
				((PlayScene3D) currentGameScene).onSwitchFrom2D();
			}
		}
	}

	private void updateCurrentGameScene(boolean forcedSceneUpdate) {
		var game = gameController.game();
		var state = gameController.state();
		var dimension = Env.use3D.get() ? SceneManager.SCENE_3D : SceneManager.SCENE_2D;
		var newGameScene = SceneManager.findGameScene(game, state, dimension);
		if (newGameScene == null) {
			throw new IllegalStateException("No fitting game scene found for game state " + state);
		}
		if (newGameScene == currentGameScene && !forcedSceneUpdate) {
			return;
		}
		if (currentGameScene != null) {
			currentGameScene.end();
		}
		// just here for simplicity
		stage.setTitle(gameController.game().variant == GameVariant.PACMAN ? "Pac-Man" : "Ms. Pac-Man");
		logger.info("Current scene changed from %s to %s", currentGameScene, newGameScene);
		currentGameScene = newGameScene;
		updateSceneContext();
		currentGameScene.setSceneContext(sceneContext);
		currentGameScene.init();
		gameScenePlaceholder.getChildren().setAll(currentGameScene.getFXSubScene());
		currentGameScene.resize(scene.getHeight());
	}

	public SceneContext getSceneContext() {
		return sceneContext;
	}

	private void updateSceneContext() {
		sceneContext.game = gameController.game();
		sceneContext.r2D = switch (sceneContext.game.variant) {
		case MS_PACMAN -> SpritesheetMsPacMan.get();
		case PACMAN -> SpritesheetPacMan.get();
		};
		sceneContext.model3D = Model3D.get();
		var sounds = switch (sceneContext.game.variant) {
		case MS_PACMAN -> MS_PACMAN_SOUNDS;
		case PACMAN -> PACMAN_SOUNDS;
		};
		gameController.setSounds(sounds);

		var world = (ArcadeWorld) sceneContext.game.world();
		world.setFlashingAnimation(sceneContext.r2D.createMazeFlashingAnimation(sceneContext.game.level.mazeNumber));
		sceneContext.game.pac.setAnimations(new PacAnimations(sceneContext.r2D));
		for (var ghost : sceneContext.game.theGhosts) {
			ghost.setAnimations(new GhostAnimations(ghost.id, sceneContext.r2D));
		}
		logger.info("Scene context updated. Game variant: %s, Rendering2D: %s", sceneContext.game.variant,
				sceneContext.r2D);
	}

	private void onKeyPressed() {
		if (Keyboard.pressed(Keyboard.ALT, KeyCode.A)) {
			Actions.toggleAutopilot();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.D)) {
			Env.toggle(Env.debugUI);
		} else if (Keyboard.pressed(Keyboard.CTRL, KeyCode.I)) {
			Actions.toggleDashboardVisible();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.I)) {
			Actions.toggleImmunity();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.M)) {
			Actions.toggleSoundMuted();
		} else if (Keyboard.pressed(KeyCode.P)) {
			Actions.togglePaused();
		} else if (Keyboard.pressed(Keyboard.SHIFT, KeyCode.P)) {
			Actions.singleStep();
		} else if (Keyboard.pressed(KeyCode.Q)) {
			Actions.restartIntro();
		} else if (Keyboard.pressed(Keyboard.ALT, KeyCode.DIGIT3)) {
			Actions.toggleUse3DScene();
		} else if (Keyboard.pressed(KeyCode.F1)) {
			Actions.toggleDashboardVisible();
		} else if (Keyboard.pressed(KeyCode.F11)) {
			stage.setFullScreen(true);
		}
	}
}