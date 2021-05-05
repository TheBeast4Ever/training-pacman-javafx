package de.amr.games.pacman.ui.fx;

import de.amr.games.pacman.lib.TickTimer;
import de.amr.games.pacman.ui.fx.scenes.common._2d.AbstractGameScene2D;
import de.amr.games.pacman.ui.fx.scenes.common._3d.PlayScene3D;
import javafx.scene.Camera;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Displays information about the current game UI.
 * 
 * @author Armin Reichert
 */
public class HUD extends HBox {

	private static String yesNo(boolean b) {
		return b ? "YES" : "NO";
	}

	private static String onOff(boolean b) {
		return b ? "ON" : "OFF";
	}

	private final PacManGameUI_JavaFX ui;
	private final Text textView;
	private String text;

	public HUD(PacManGameUI_JavaFX ui) {
		this.ui = ui;
		PacManGameUI_JavaFX.$TOTAL_TICKS.addListener((source, oldValue, newValue) -> update());
		textView = new Text();
		textView.setFill(Color.LIGHTGREEN);
		textView.setFont(Font.font("Monospace", 14));
		getChildren().add(textView);
		visibleProperty().bind(Env.$hudVisible);
	}

	private void line(String column1, String fmtColumn2, Object... args) {
		String column2 = String.format(fmtColumn2, args) + "\n";
		text += String.format("%-20s: %s", column1, column2);
	}

	private void line() {
		text += "\n";
	}

	private String cameraInfo(Camera camera) {
		return camera == null ? "No camera"
				: String.format("x=%.0f y=%.0f z=%.0f rot=%.0f", camera.getTranslateX(), camera.getTranslateY(),
						camera.getTranslateZ(), camera.getRotate());
	}

	public void update() {
		TickTimer stateTimer = ui.gameController.stateTimer();
		text = "";
		line("Frame rate", "%d Hz", PacManGameUI_JavaFX.$FPS.get());
		line("Speed (CTRL/SHIFT+S)", "%.0f%%", 100.0 / Env.$slowDown.get());
		line("Total Ticks", "%d", PacManGameUI_JavaFX.$TOTAL_TICKS.get());
		line("Paused (CTRL+P)", "%s", yesNo(Env.$paused.get()));
		line();
		line("Game Variant", "%s", ui.gameController.gameVariant());
		line("Playing", "%s", yesNo(ui.gameController.isGameRunning()));
		line("Attract Mode", "%s", yesNo(ui.gameController.isAttractMode()));
		line("Game Level", "%d", ui.gameController.game().currentLevel().number);
		line("Game State", "%s", ui.gameController.state);
		line("", "Running:   %s", stateTimer.ticked());
		line("", "Remaining: %s",
				stateTimer.ticksRemaining() == Long.MAX_VALUE ? "indefinite" : stateTimer.ticksRemaining());
		line("Game Scene", "%s", ui.currentGameScene.getClass().getSimpleName());
		line("Game Scene Size", "w=%.0f h=%.0f", ui.currentGameScene.getSubScene().getWidth(),
				ui.currentGameScene.getSubScene().getHeight());
		line();
		line("Autopilot (A)", "%s", onOff(ui.gameController.autopilotOn));
		line("Immunity (I)", "%s", onOff(ui.gameController.isPlayerImmune()));
		line();
		line("Window Size", "w=%.0f h=%.0f", ui.mainScene.getWindow().getWidth(), ui.mainScene.getWindow().getHeight());
		line("Main Scene Size", "w=%.0f h=%.0f", ui.mainScene.getWidth(), ui.mainScene.getHeight());
		line();
		line("3D Scenes (CTRL+3)", "%s", onOff(Env.$use3DScenes.get()));
		if (ui.currentGameScene instanceof AbstractGameScene2D) {
			line("Canvas2D", "w=%.0f h=%.0f", ui.canvas2D.getWidth(), ui.canvas2D.getHeight());
		} else {
			if (ui.currentGameScene instanceof PlayScene3D) {
				PlayScene3D playScene = (PlayScene3D) ui.currentGameScene;
				playScene.selectedCamera().ifPresent(camera -> {
					line("Camera (CTRL+C)", "%s %s", camera, cameraInfo(camera));
				});
			}
			line("Draw Mode (CTRL+L)", "%s", Env.$drawMode.get());
			line("Axes (CTRL+X)", "%s", onOff(Env.$axesVisible.get()));
		}
		textView.setText(text);
	}
}