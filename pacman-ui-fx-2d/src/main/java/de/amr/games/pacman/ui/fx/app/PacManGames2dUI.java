/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.app;

import static de.amr.games.pacman.controller.GameState.INTRO;
import static de.amr.games.pacman.lib.Globals.checkNotNull;
import static de.amr.games.pacman.ui.fx.util.ResourceManager.fmtMessage;

import org.tinylog.Logger;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameEventListener;
import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.event.SoundEvent;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.GameVariant;
import de.amr.games.pacman.model.IllegalGameVariantException;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.input.KeyboardSteering;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.GhostAnimationsMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.PacAnimationsMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.mspacman.SpritesheetMsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.GhostAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.PacAnimationsPacManGame;
import de.amr.games.pacman.ui.fx.rendering2d.pacman.SpritesheetPacManGame;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene.GameSceneConfiguration;
import de.amr.games.pacman.ui.fx.scene.GameSceneContext;
import de.amr.games.pacman.ui.fx.scene2d.BootScene;
import de.amr.games.pacman.ui.fx.scene2d.GameScene2D;
import de.amr.games.pacman.ui.fx.scene2d.HelpMenus;
import de.amr.games.pacman.ui.fx.scene2d.MsPacManCreditScene;
import de.amr.games.pacman.ui.fx.scene2d.MsPacManIntermissionScene1;
import de.amr.games.pacman.ui.fx.scene2d.MsPacManIntermissionScene2;
import de.amr.games.pacman.ui.fx.scene2d.MsPacManIntermissionScene3;
import de.amr.games.pacman.ui.fx.scene2d.MsPacManIntroScene;
import de.amr.games.pacman.ui.fx.scene2d.PacManCreditScene;
import de.amr.games.pacman.ui.fx.scene2d.PacManCutscene1;
import de.amr.games.pacman.ui.fx.scene2d.PacManCutscene2;
import de.amr.games.pacman.ui.fx.scene2d.PacManCutscene3;
import de.amr.games.pacman.ui.fx.scene2d.PacManIntroScene;
import de.amr.games.pacman.ui.fx.scene2d.PlayScene2D;
import de.amr.games.pacman.ui.fx.util.FlashMessageView;
import de.amr.games.pacman.ui.fx.util.GameClock;
import de.amr.games.pacman.ui.fx.util.ResourceManager;
import de.amr.games.pacman.ui.fx.util.Theme;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 2D-only user interface for Pac-Man and Ms. Pac-Man games. No dashboard, no picture-in-picture view.
 * 
 * @author Armin Reichert
 */
public class PacManGames2dUI implements PacManGamesUserInterface, GameEventListener {

	protected GameSceneConfiguration gameSceneConfigMsPacMan;
	protected GameSceneConfiguration gameSceneConfigPacMan;
	protected GameClock clock;
	protected Theme theme;
	protected Stage stage;
	protected StartPage startPage;
	protected FlashMessageView flashMessageView = new FlashMessageView();
	protected HelpMenus helpMenus;
	protected GameController gameController;
	protected Pane mainSceneRoot;
	protected KeyboardSteering keyboardPlayerSteering;
	protected GameScene currentGameScene;
	protected SoundHandler soundHandler;

	protected boolean canvasScaled;

	@Override
	public void init(Stage stage, Settings settings, Theme theme) {
		checkNotNull(stage);
		checkNotNull(settings);
		checkNotNull(theme);

		this.stage = stage;
		stage.setFullScreen(settings.fullScreen);
		this.theme = theme;
		gameController = new GameController(settings.variant);
		soundHandler = new SoundHandler(this);
		startPage = new StartPage(this);

		canvasScaled = true;

		configureGameScenes();
		createMainScene(stage, settings);
		configureHelpMenus();
		configurePacSteering();
		configureBindings(settings);
		GameEvents.addListener(this);

		clock = new GameClock(this::onTick, this::onRender);
		clock.pausedPy.addListener((py, ov, nv) -> updateStage());
		clock.targetFrameratePy.set(GameModel.FPS);
	}

	protected void onTick() {
		gameController.update();
		if (currentGameScene != null) {
			currentGameScene.update();
		}
	}

	protected void onRender() {
		flashMessageView.update();
		if (currentGameScene != null) {
			currentGameScene.render();
		}
	}

	protected void configureGameScenes() {
		//@formatter:off
		gameSceneConfigMsPacMan = new GameSceneConfiguration(
			new BootScene(),
			new MsPacManIntroScene(),
			new MsPacManCreditScene(),
			new PlayScene2D(),
			null,
			new MsPacManIntermissionScene1(),
			new MsPacManIntermissionScene2(),
			new MsPacManIntermissionScene3()
		);
		gameSceneConfigPacMan = new GameSceneConfiguration(
			new BootScene(),
			new PacManIntroScene(),
			new PacManCreditScene(),
			new PlayScene2D(),
			null,
			new PacManCutscene1(),
			new PacManCutscene2(),
			new PacManCutscene3()
		);
  	//@formatter:on
	}

	protected void showStartPage() {
		if (clock != null) {
			clock.stop();
		}
		startPage.setGameVariant(gameVariant());
		stage.getScene().setOnKeyPressed(startPage::handleKeyPressed);
		mainSceneRoot.getChildren().add(startPage);
	}

	protected void removeStartPage() {
		stage.getScene().setOnKeyPressed(this::handleKeyPressed);
		mainSceneRoot.getChildren().remove(startPage);
	}

	protected void createMainScene(Stage stage, Settings settings) {
		mainSceneRoot = new StackPane();
		mainSceneRoot.getChildren().add(flashMessageView);

		var mainScene = new Scene(mainSceneRoot, settings.zoom * 28 * 8, settings.zoom * 36 * 8, Color.BLACK);
		stage.setScene(mainScene);
		mainScene.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				resizeStageToFitCurrentGameScene();
			}
		});

		showStartPage();
		updateStage();
	}

	protected void configureHelpMenus() {
		helpMenus = new HelpMenus(PacManGames2d.TEXTS);
		helpMenus.setFont(theme.font("font.monospaced", 12));
	}

	protected void resizeStageToFitCurrentGameScene() {
		if (currentGameScene != null && !currentGameScene.is3D() && !stage.isFullScreen()) {
			stage.setWidth(currentGameScene.sceneContainer().getWidth() + 16); // don't ask me why
		}
	}

	protected void configurePacSteering() {
		keyboardPlayerSteering = new KeyboardSteering();
		gameController.setManualPacSteering(keyboardPlayerSteering);
	}

	@Override
	public void show() {
		stage.setMinWidth(241);
		stage.setMinHeight(328);
		stage.centerOnScreen();
		stage.requestFocus();
		stage.show();
	}

	@Override
	public Theme theme() {
		return theme;
	}

	@Override
	public SoundHandler soundHandler() {
		return soundHandler;
	}

	protected void updateStage() {
		mainSceneRoot.setBackground(theme.background("wallpaper.background"));
		switch (gameVariant()) {
		case MS_PACMAN: {
			String messageKey = "app.title.ms_pacman";
			if (clock != null && clock.isPaused()) {
				messageKey = "app.title.ms_pacman.paused";
			}
			stage.setTitle(ResourceManager.fmtMessage(PacManGames2d.TEXTS, messageKey, ""));
			stage.getIcons().setAll(theme.image("mspacman.icon"));
			break;
		}
		case PACMAN: {
			String messageKey = "app.title.pacman";
			if (clock != null && clock.isPaused()) {
				messageKey = "app.title.pacman.paused";
			}
			stage.setTitle(ResourceManager.fmtMessage(PacManGames2d.TEXTS, messageKey, ""));
			stage.getIcons().setAll(theme.image("pacman.icon"));
			break;
		}
		default:
			throw new IllegalGameVariantException(gameVariant());
		}
	}

	/**
	 * @param settings application settings
	 */
	protected void configureBindings(Settings settings) {
		// snooze...
	}

	protected GameScene sceneMatchingCurrentGameState() {
		var config = gameVariant() == GameVariant.MS_PACMAN ? gameSceneConfigMsPacMan : gameSceneConfigPacMan;
		switch (gameState()) {
		case BOOT:
			return config.bootScene();
		case CREDIT:
			return config.creditScene();
		case INTRO:
			return config.introScene();
		case INTERMISSION:
			return config.cutScene(game().level().get().intermissionNumber);
		case INTERMISSION_TEST:
			return config.cutScene(game().intermissionTestNumber);
		default:
			return config.playScene();
		}
	}

	protected void updateOrReloadGameScene(boolean reload) {
		var nextGameScene = sceneMatchingCurrentGameState();
		if (nextGameScene == null) {
			throw new IllegalStateException(String.format("No game scene found for game state %s.", gameState()));
		}
		if (reload || nextGameScene != currentGameScene) {
			setGameScene(nextGameScene);
		}
		updateStage();
	}

	protected void setGameScene(GameScene newGameScene) {
		var prevGameScene = currentGameScene;
		if (prevGameScene != null) {
			prevGameScene.end();
		}
		currentGameScene = newGameScene;
		currentGameScene.setParentScene(stage.getScene());
		currentGameScene.setContext(new GameSceneContext(gameController, this));
		currentGameScene.init();
		mainSceneRoot.getChildren().set(0, currentGameScene.sceneContainer());
		updatePlayerSteering(currentGameScene);
		if (currentGameScene instanceof GameScene2D) {
			var scene2D = (GameScene2D) currentGameScene;
			scene2D.setCanvasScaled(canvasScaled);
			// to draw rounded canvas corners, background color must be set
			scene2D.setWallpaperColor(theme.color("wallpaper.color"));
		}
		Logger.trace("Game scene changed from {} to {}", prevGameScene, currentGameScene);
	}

	private void updatePlayerSteering(GameScene gameScene) {
		boolean playScene = false;
		if (gameVariant() == GameVariant.MS_PACMAN) {
			playScene = gameScene == gameSceneConfigMsPacMan.playScene()
					|| gameScene == gameSceneConfigMsPacMan.playScene3D();
		} else {
			playScene = gameScene == gameSceneConfigPacMan.playScene() || gameScene == gameSceneConfigPacMan.playScene3D();
		}
		if (playScene) {
			stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyboardPlayerSteering);
		} else {
			stage.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, keyboardPlayerSteering);
		}
	}

	protected void handleKeyPressed(KeyEvent keyEvent) {
		Keyboard.accept(keyEvent);
		handleKeyboardInput();
		if (currentGameScene != null) {
			currentGameScene.handleKeyboardInput();
		}
		Keyboard.clearState();
	}

	protected void handleKeyboardInput() {
		if (Keyboard.pressed(PacManGames2d.KEY_SHOW_HELP)) {
			showHelp();
		} else if (Keyboard.pressed(PacManGames2d.KEY_AUTOPILOT)) {
			toggleAutopilot();
		} else if (Keyboard.pressed(PacManGames2d.KEY_BOOT)) {
			if (gameController().state() != GameState.BOOT) {
				reboot();
			}
		} else if (Keyboard.pressed(PacManGames2d.KEY_DEBUG_INFO)) {
			Ufx.toggle(PacManGames2d.PY_SHOW_DEBUG_INFO);
		} else if (Keyboard.pressed(PacManGames2d.KEY_IMMUNITIY)) {
			toggleImmunity();
		} else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE)) {
			togglePaused();
		} else if (Keyboard.pressed(PacManGames2d.KEY_PAUSE_STEP) || Keyboard.pressed(PacManGames2d.KEY_SINGLE_STEP)) {
			oneSimulationStep();
		} else if (Keyboard.pressed(PacManGames2d.KEY_TEN_STEPS)) {
			tenSimulationSteps();
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_FASTER)) {
			changeSimulationSpeed(5);
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_SLOWER)) {
			changeSimulationSpeed(-5);
		} else if (Keyboard.pressed(PacManGames2d.KEY_SIMULATION_NORMAL)) {
			resetSimulationSpeed();
		} else if (Keyboard.pressed(PacManGames2d.KEY_QUIT)) {
			restartIntro();
		} else if (Keyboard.pressed(PacManGames2d.KEY_TEST_LEVELS)) {
			startLevelTestMode();
		} else if (Keyboard.pressed(PacManGames2d.KEY_FULLSCREEN)) {
			stage.setFullScreen(true);
		} else if (Keyboard.pressed(PacManGames2d.KEY_CANVAS_SCALED)) {
			toggleCanvasScaled();
		}
	}

	@Override
	public void onGameEvent(GameEvent e) {
		Logger.trace("Event received: {}", e);
		// call event specific handler
		GameEventListener.super.onGameEvent(e);
		if (currentGameScene != null) {
			currentGameScene.onGameEvent(e);
		}
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		updateOrReloadGameScene(false);
	}

	@Override
	public void onUnspecifiedChange(GameEvent e) {
		updateOrReloadGameScene(true);
	}

	@Override
	public void onLevelCreated(GameEvent e) {
		// Found no better point in time to create and assign the sprite animations to the guys
		e.game.level().ifPresent(level -> {
			switch (level.game().variant()) {
			case MS_PACMAN: {
				var ss = (SpritesheetMsPacManGame) spritesheet();
				level.pac().setAnimations(new PacAnimationsMsPacManGame(level.pac(), ss));
				level.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimationsMsPacManGame(ghost, ss)));
				Logger.trace("Created Ms. Pac-Man game creature animations for level #{}", level.number());
				break;
			}
			case PACMAN: {
				var ss = (SpritesheetPacManGame) spritesheet();
				level.pac().setAnimations(new PacAnimationsPacManGame(level.pac(), ss));
				level.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimationsPacManGame(ghost, ss)));
				Logger.trace("Created Pac-Man game creature animations for level #{}", level.number());
				break;
			}
			default:
				throw new IllegalGameVariantException(level.game().variant());
			}
		});
		updateOrReloadGameScene(true);
	}

	@Override
	public void onSoundEvent(SoundEvent e) {
		soundHandler.onSoundEvent(e);
	}

	public void showHelp() {
		if (currentGameScene instanceof GameScene2D) {
			var scene2D = (GameScene2D) currentGameScene;
			scene2D.showHelpMenu(helpMenus, Duration.seconds(2));
		}
	}

	public void showFlashMessage(String message, Object... args) {
		showFlashMessageSeconds(1, message, args);
	}

	public void showFlashMessageSeconds(double seconds, String message, Object... args) {
		flashMessageView.showMessage(String.format(message, args), seconds);
	}

	@Override
	public GameClock clock() {
		return clock;
	}

	@Override
	public GameController gameController() {
		return gameController;
	}

	@Override
	public GameScene currentGameScene() {
		return currentGameScene;
	}

	// Actions

	@Override
	public void startGame() {
		if (game().hasCredit()) {
			soundHandler.stopVoice();
			gameController.startPlaying();
		}
	}

	@Override
	public void startCutscenesTest() {
		gameController.startCutscenesTest(1);
		showFlashMessage("Cut scenes");
	}

	@Override
	public void restartIntro() {
		if (currentGameScene != null) {
			currentGameScene.end();
			soundHandler.stopAllSounds();
			GameEvents.setSoundEventsEnabled(true);
			if (game().isPlaying()) {
				game().changeCredit(-1);
			}
			gameController.restart(INTRO);
		}
	}

	public void reboot() {
		if (currentGameScene != null) {
			currentGameScene.end();
		}
		gameController.restart(GameState.BOOT);
	}

	@Override
	public void addCredit() {
		GameEvents.setSoundEventsEnabled(true);
		gameController.addCredit();
	}

	@Override
	public void enterLevel(int newLevelNumber) {
		if (gameState() == GameState.CHANGING_TO_NEXT_LEVEL) {
			return;
		}
		game().level().ifPresent(level -> {
			if (newLevelNumber > level.number()) {
				for (int n = level.number(); n < newLevelNumber - 1; ++n) {
					game().nextLevel();
				}
				gameController.changeState(GameState.CHANGING_TO_NEXT_LEVEL);
			} else if (newLevelNumber < level.number()) {
				// not implemented
			}
		});
	}

	@Override
	public void togglePaused() {
		Ufx.toggle(clock.pausedPy);
		if (clock.pausedPy.get()) {
			theme.audioClips().forEach(AudioClip::stop);
		}
	}

	public void oneSimulationStep() {
		if (clock.pausedPy.get()) {
			clock.executeSingleStep(true);
		}
	}

	public void tenSimulationSteps() {
		if (clock.pausedPy.get()) {
			clock.executeSteps(10, true);
		}
	}

	public void changeSimulationSpeed(int delta) {
		int newFramerate = clock.targetFrameratePy.get() + delta;
		if (newFramerate > 0) {
			clock.targetFrameratePy.set(newFramerate);
			showFlashMessageSeconds(0.75, String.format("%dHz", newFramerate));
		}
	}

	public void resetSimulationSpeed() {
		clock.targetFrameratePy.set(GameModel.FPS);
		showFlashMessageSeconds(0.75, String.format("%dHz", clock.targetFrameratePy.get()));
	}

	@Override
	public void selectGameVariant(GameVariant gameVariant) {
		gameController.selectGameVariant(gameVariant);
		if (clock.isRunning()) {
			clock.stop();
		} else {
			mainSceneRoot.getChildren().remove(startPage);
		}
		showStartPage();
	}

	@Override
	public void toggleAutopilot() {
		gameController.toggleAutoControlled();
		var auto = gameController.isAutoControlled();
		String message = fmtMessage(PacManGames2d.TEXTS, auto ? "autopilot_on" : "autopilot_off");
		showFlashMessage(message);
		soundHandler.playVoice(auto ? "voice.autopilot.on" : "voice.autopilot.off");
	}

	@Override
	public void toggleImmunity() {
		game().setImmune(!game().isImmune());
		var immune = game().isImmune();
		String message = fmtMessage(PacManGames2d.TEXTS, immune ? "player_immunity_on" : "player_immunity_off");
		showFlashMessage(message);
		soundHandler.playVoice(immune ? "voice.immunity.on" : "voice.immunity.off");
	}

	@Override
	public void toggleCanvasScaled() {
		canvasScaled = !canvasScaled;
		if (currentGameScene instanceof GameScene2D) {
			GameScene2D scene2D = (GameScene2D) currentGameScene;
			scene2D.setCanvasScaled(canvasScaled);
			showFlashMessage(canvasScaled ? "Canvas SCALED" : "Canvas UNSCALED");
		}
	}

	public void startLevelTestMode() {
		if (gameState() == GameState.INTRO) {
			gameController.restart(GameState.LEVEL_TEST);
			showFlashMessage("Level TEST MODE");
		}
	}

	@Override
	public void cheatAddLives() {
		int newLivesCount = game().lives() + 3;
		game().setLives(newLivesCount);
		showFlashMessage(fmtMessage(PacManGames2d.TEXTS, "cheat_add_lives", newLivesCount));
	}

	@Override
	public void cheatEatAllPellets() {
		gameController.cheatEatAllPellets();
	}

	@Override
	public void cheatEnterNextLevel() {
		gameController.cheatEnterNextLevel();
	}

	@Override
	public void cheatKillAllEatableGhosts() {
		gameController.cheatKillAllEatableGhosts();
	}
}