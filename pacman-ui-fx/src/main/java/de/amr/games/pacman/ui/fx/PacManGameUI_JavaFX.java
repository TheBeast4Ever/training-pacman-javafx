package de.amr.games.pacman.ui.fx;

import static de.amr.games.pacman.heaven.God.clock;
import static de.amr.games.pacman.lib.Logging.log;
import static de.amr.games.pacman.world.PacManGameWorld.TS;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.GameType;
import de.amr.games.pacman.sound.PacManGameSoundManager;
import de.amr.games.pacman.sound.PacManGameSounds;
import de.amr.games.pacman.sound.SoundManager;
import de.amr.games.pacman.ui.FlashMessage;
import de.amr.games.pacman.ui.PacManGameAnimations;
import de.amr.games.pacman.ui.PacManGameUI;
import de.amr.games.pacman.ui.fx.common.FXRendering;
import de.amr.games.pacman.ui.fx.common.GameScene;
import de.amr.games.pacman.ui.fx.common.PlayScene;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.mspacman.MsPacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx.mspacman.MsPacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx.mspacman.MsPacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx.mspacman.MsPacMan_IntroScene;
import de.amr.games.pacman.ui.fx.pacman.PacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx.pacman.PacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx.pacman.PacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx.pacman.PacMan_IntroScene;
import de.amr.games.pacman.ui.fx.rendering.MsPacMan_DefaultRendering;
import de.amr.games.pacman.ui.fx.rendering.PacMan_DefaultRendering;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * JavaFX implementation of the Pac-Man game UI.
 * 
 * @author Armin Reichert
 */
public class PacManGameUI_JavaFX implements PacManGameUI {

	public static final int SCENE_WIDTH = 28 * TS;
	public static final int SCENE_HEIGHT = 36 * TS;

	private final Deque<FlashMessage> flashMessagesQ = new ArrayDeque<>();

	private final EnumMap<GameType, FXRendering> renderings = new EnumMap<>(GameType.class);
	private final EnumMap<GameType, SoundManager> soundManagers = new EnumMap<>(GameType.class);
	private final EnumMap<GameType, List<GameScene>> scenes = new EnumMap<>(GameType.class);

	private final PacManGameController controller;
	private final Stage stage;
	private GameScene currentScene;
	private Keyboard keyboard;
	private GameModel game;
	private boolean muted;

	public PacManGameUI_JavaFX(Stage stage, PacManGameController controller, double scaling) {
		this.controller = controller;
		this.stage = stage;
		stage.setTitle("Pac-Man / Ms. Pac-Man (JavaFX)");
		stage.getIcons().add(new Image("/pacman/graphics/pacman.png"));
		stage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0); // TODO
		});
		stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			handleGlobalKeys(e);
		});

		renderings.put(GameType.MS_PACMAN, new MsPacMan_DefaultRendering());
		renderings.put(GameType.PACMAN, new PacMan_DefaultRendering());

		soundManagers.put(GameType.MS_PACMAN, new PacManGameSoundManager(PacManGameSounds::msPacManSoundURL));
		soundManagers.put(GameType.PACMAN, new PacManGameSoundManager(PacManGameSounds::mrPacManSoundURL));

		scenes.put(GameType.MS_PACMAN, Arrays.asList(//
				new MsPacMan_IntroScene(scaling, renderings.get(GameType.MS_PACMAN), soundManagers.get(GameType.MS_PACMAN)), //
				new MsPacMan_IntermissionScene1(scaling, renderings.get(GameType.MS_PACMAN),
						soundManagers.get(GameType.MS_PACMAN)), //
				new MsPacMan_IntermissionScene2(scaling, renderings.get(GameType.MS_PACMAN),
						soundManagers.get(GameType.MS_PACMAN)), //
				new MsPacMan_IntermissionScene3(scaling, renderings.get(GameType.MS_PACMAN),
						soundManagers.get(GameType.MS_PACMAN)), //
				new PlayScene(scaling, renderings.get(GameType.MS_PACMAN), soundManagers.get(GameType.MS_PACMAN))//
		));

		scenes.put(GameType.PACMAN, Arrays.asList(//
				new PacMan_IntroScene(scaling, renderings.get(GameType.PACMAN), soundManagers.get(GameType.PACMAN)), //
				new PacMan_IntermissionScene1(scaling, renderings.get(GameType.PACMAN), soundManagers.get(GameType.PACMAN)), //
				new PacMan_IntermissionScene2(scaling, renderings.get(GameType.PACMAN), soundManagers.get(GameType.PACMAN)), //
				new PacMan_IntermissionScene3(scaling, renderings.get(GameType.PACMAN), soundManagers.get(GameType.PACMAN)), //
				new PlayScene(scaling, renderings.get(GameType.PACMAN), soundManagers.get(GameType.PACMAN))//
		));

		onGameChanged(controller.getGame());
		log("JavaFX UI created at clock tick %d", clock.ticksTotal);
	}

	private void handleGlobalKeys(KeyEvent e) {
		switch (e.getCode()) {
		case S: {
			clock.targetFreq = clock.targetFreq != 30 ? 30 : 60;
			String text = clock.targetFreq == 60 ? "Normal speed" : "Slow speed";
			showFlashMessage(text, clock.sec(1.5));
			log("Clock frequency changed to %d Hz", clock.targetFreq);
			break;
		}
		case F: {
			clock.targetFreq = clock.targetFreq != 120 ? 120 : 60;
			String text = clock.targetFreq == 60 ? "Normal speed" : "Fast speed";
			showFlashMessage(text, clock.sec(1.5));
			log("Clock frequency changed to %d Hz", clock.targetFreq);
			break;
		}
		default:
			break;
		}
	}

	private GameScene selectGameScene() {
		switch (game.state) {
		case INTRO:
			return scenes.get(controller.currentGameType()).get(0);
		case INTERMISSION:
			return scenes.get(controller.currentGameType()).get(game.intermissionNumber);
		default:
			return scenes.get(controller.currentGameType()).get(4);
		}
	}

	@Override
	public void onGameChanged(GameModel newGame) {
		game = Objects.requireNonNull(newGame);
		scenes.get(controller.currentGameType()).forEach(scene -> scene.setGame(game));
		changeScene(selectGameScene());
	}

	private void changeScene(GameScene newScene) {
		currentScene = newScene;
		keyboard = new Keyboard(currentScene.fxScene);
		currentScene.start();
	}

	@Override
	public void show() {
		stage.setScene(currentScene.fxScene);
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.show();
	}

	@Override
	public void update() {
		GameScene sceneToDisplay = selectGameScene();
		if (currentScene != sceneToDisplay) {
			log("%s: Scene changes from %s to %s", this, currentScene, sceneToDisplay);
			if (currentScene != null) {
				currentScene.end();
			}
			changeScene(sceneToDisplay);
		}
		currentScene.update();

		FlashMessage message = flashMessagesQ.peek();
		if (message != null) {
			message.timer.run();
			if (message.timer.expired()) {
				flashMessagesQ.remove();
			}
		}
	}

	@Override
	public void render() {
		// TODO Should the game loop run on the JavaFX application thread?
		Platform.runLater(() -> {
			if (stage.getScene() != currentScene.fxScene) {
				stage.setScene(currentScene.fxScene);
			}
			try {
				currentScene.clear();
				currentScene.render();
				if (!flashMessagesQ.isEmpty()) {
					currentScene.drawFlashMessage(flashMessagesQ.peek());
				}
			} catch (Exception x) {
				log("Exception occurred when rendering scene %s", currentScene);
				x.printStackTrace();
			}
		});
	}

	@Override
	public void reset() {
		currentScene.end();
		onGameChanged(game);
	}

	@Override
	public void showFlashMessage(String message, long ticks) {
		flashMessagesQ.add(new FlashMessage(message, ticks));
	}

	@Override
	public boolean keyPressed(String keySpec) {
		boolean pressed = keyboard.keyPressed(keySpec);
		keyboard.clearKey(keySpec);
		return pressed;
	}

	@Override
	public Optional<SoundManager> sound() {
		if (muted) {
			return Optional.empty(); // TODO
		}
		return Optional.of(soundManagers.get(controller.currentGameType()));
	}

	@Override
	public void mute(boolean state) {
		muted = state;
	}

	@Override
	public Optional<PacManGameAnimations> animation() {
		FXRendering rendering = renderings.get(controller.currentGameType());
		return rendering instanceof PacManGameAnimations ? Optional.of((PacManGameAnimations) rendering) : Optional.empty();
	}
}