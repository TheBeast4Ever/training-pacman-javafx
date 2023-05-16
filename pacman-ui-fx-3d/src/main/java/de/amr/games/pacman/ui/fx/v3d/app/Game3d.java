/*
MIT License

Copyright (c) 2023 Armin Reichert

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
package de.amr.games.pacman.ui.fx.v3d.app;

import static de.amr.games.pacman.ui.fx.util.ResourceManager.fmtMessage;
import static de.amr.games.pacman.ui.fx.util.Ufx.alt;
import static de.amr.games.pacman.ui.fx.util.Ufx.just;

import java.util.Locale;

import org.tinylog.Logger;

import de.amr.games.pacman.lib.Globals;
import de.amr.games.pacman.model.world.World;
import de.amr.games.pacman.ui.fx.app.Game2d;
import de.amr.games.pacman.ui.fx.app.Game2dAssets;
import de.amr.games.pacman.ui.fx.app.Settings;
import de.amr.games.pacman.ui.fx.util.Ufx;
import de.amr.games.pacman.ui.fx.v3d.scene.Perspective;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.stage.Stage;

/**
 * @author Armin Reichert
 */
public class Game3d extends Application {

	//@formatter:off
	public static final DoubleProperty              pipOpacityPy            = new SimpleDoubleProperty(0.66);
	public static final DoubleProperty              pipHeightPy             = new SimpleDoubleProperty(World.TILES_Y * Globals.TS);

	public static final BooleanProperty             d3_axesVisiblePy        = new SimpleBooleanProperty(false);
	public static final ObjectProperty<DrawMode>    d3_drawModePy           = new SimpleObjectProperty<>(DrawMode.FILL);
	public static final BooleanProperty             d3_enabledPy            = new SimpleBooleanProperty(true);
	public static final ObjectProperty<Color>       d3_floorColorPy         = new SimpleObjectProperty<>(Color.grayRgb(0x60));
	public static final StringProperty              d3_floorTexturePy       = new SimpleStringProperty("Knobs & Bumps");
	public static final BooleanProperty             d3_floorTextureRandomPy = new SimpleBooleanProperty(false);
	public static final BooleanProperty             d3_foodOscillationPy    = new SimpleBooleanProperty(false);
	public static final ObjectProperty<Color>       d3_lightColorPy         = new SimpleObjectProperty<>(Color.GHOSTWHITE);
	public static final DoubleProperty              d3_mazeWallHeightPy     = new SimpleDoubleProperty(1.75);
	public static final DoubleProperty              d3_mazeWallThicknessPy  = new SimpleDoubleProperty(1.25);
	public static final BooleanProperty             d3_pacLightedPy         = new SimpleBooleanProperty(true);
	public static final ObjectProperty<Perspective> d3_perspectivePy        = new SimpleObjectProperty<>(Perspective.NEAR_PLAYER);
	public static final BooleanProperty             d3_energizerExplodesPy  = new SimpleBooleanProperty(true);

	public static final BooleanProperty             wokePussyMode           = new SimpleBooleanProperty(false); 
	//@formatter:on

	//@formatter:off
	public static final KeyCodeCombination KEY_TOGGLE_DASHBOARD   = just(KeyCode.F1);
	public static final KeyCodeCombination KEY_TOGGLE_DASHBOARD_2 = alt(KeyCode.B);
	public static final KeyCodeCombination KEY_TOGGLE_PIP_VIEW    = just(KeyCode.F2);
	public static final KeyCodeCombination KEY_TOGGLE_2D_3D       = alt(KeyCode.DIGIT3);
	public static final KeyCodeCombination KEY_PREV_PERSPECTIVE   = alt(KeyCode.LEFT);
	public static final KeyCodeCombination KEY_NEXT_PERSPECTIVE   = alt(KeyCode.RIGHT);
	//@formatter:on

	public static Game3d app;
	public static Game3dAssets assets;
	public static Game3dUI ui;

	private static Settings cfg;

	@Override
	public void init() {
		Game2d.app = new Game2d();
		Game2d.assets = new Game2dAssets();

		Game3d.app = this;
		Game3d.assets = new Game3dAssets();
		Game3d.cfg = new Settings(getParameters().getNamed());

		Game2d.assets.load();
		Game3d.assets.load();

		Logger.info("Game configuration: {}", Game3d.cfg);
	}

	@Override
	public void start(Stage stage) {
		stage.setFullScreen(cfg.fullScreen);
		Game2d.ui = Game3d.ui = new Game3dUI(cfg.variant, stage, cfg.zoom * 28 * 8, cfg.zoom * 36 * 8);

		ui.init(cfg);
		ui.startClockAndShowStage();

		Game2d.app.reboot();
		Logger.info("Game started. {} Hz language={}", ui.clock().targetFrameratePy.get(), Locale.getDefault());
	}

	@Override
	public void stop() {
		ui.clock().stop();
		Logger.info("Game stopped.");
	}

	// --- Actions ---

	public void togglePipVisibility() {
		Ufx.toggle(ui.pip().visiblePy);
		var message = fmtMessage(Game3d.assets.messages, ui.pip().visiblePy.get() ? "pip_on" : "pip_off");
		ui.showFlashMessage(message);
	}

	public void toggleDashboardVisible() {
		ui.dashboard().setVisible(!ui.dashboard().isVisible());
	}

	public void selectNextPerspective() {
		var next = Game3d.d3_perspectivePy.get().next();
		Game3d.d3_perspectivePy.set(next);
		String perspectiveName = fmtMessage(Game3d.assets.messages, next.name());
		ui.showFlashMessage(fmtMessage(Game3d.assets.messages, "camera_perspective", perspectiveName));
	}

	public void selectPrevPerspective() {
		var prev = Game3d.d3_perspectivePy.get().prev();
		Game3d.d3_perspectivePy.set(prev);
		String perspectiveName = fmtMessage(Game3d.assets.messages, prev.name());
		ui.showFlashMessage(fmtMessage(Game3d.assets.messages, "camera_perspective", perspectiveName));
	}

	public void toggleDrawMode() {
		Game3d.d3_drawModePy.set(Game3d.d3_drawModePy.get() == DrawMode.FILL ? DrawMode.LINE : DrawMode.FILL);
	}
}