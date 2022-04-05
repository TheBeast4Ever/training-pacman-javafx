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
package de.amr.games.pacman.ui.fx.shell.info;

import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.app.GameLoop;
import de.amr.games.pacman.ui.fx.shell.GameUI;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

/**
 * General settings.
 * 
 * @author Armin Reichert
 */
public class SectionGeneral extends Section {
	private Button[] btnsSimulation;
	private Slider sliderTargetFPS;
	private CheckBox cbAutopilot;
	private CheckBox cbImmunity;
	private CheckBox cbUsePlayScene3D;

	public SectionGeneral(GameUI ui) {
		super(ui, "General");

		btnsSimulation = addButtons("Simulation", "Pause", "Step");
		btnsSimulation[0].setOnAction(e -> ui.togglePaused());
		btnsSimulation[1].setOnAction(e -> GameLoop.get().runSingleStep(true));

		sliderTargetFPS = addSlider("Target Framerate", 10, 120, 60);
		sliderTargetFPS.setShowTickLabels(false);
		sliderTargetFPS.setShowTickMarks(false);
		sliderTargetFPS.valueProperty().addListener(($value, oldValue, newValue) -> {
			GameLoop.get().setTargetFrameRate(newValue.intValue());
		});
		addInfo("", () -> String.format("Current: %d Hz (Target: %d Hz)", GameLoop.get().getFPS(),
				GameLoop.get().getTargetFrameRate()));
		addInfo("Total Ticks", GameLoop.get()::getTotalTicks);

		cbUsePlayScene3D = addCheckBox("Use 3D play scene", ui::toggleUse3DScene);
		cbAutopilot = addCheckBox("Autopilot", ui::toggleAutopilot);
		cbImmunity = addCheckBox("Player immune", ui::toggleImmunity);

		addInfo("Main scene", () -> String.format("w=%.0f h=%.0f", ui.stage.getScene().getWindow().getWidth(),
				ui.stage.getScene().getWindow().getHeight()));
	}

	@Override
	public void update() {
		super.update();

		btnsSimulation[0].setText(Env.$paused.get() ? "Resume" : "Pause");
		btnsSimulation[1].setDisable(!Env.$paused.get());
		sliderTargetFPS.setValue(GameLoop.get().getTargetFrameRate());

		cbAutopilot.setSelected(gc.autoControlled);
		cbImmunity.setSelected(gc.playerImmune);
		cbUsePlayScene3D.setSelected(Env.$3D.get());
	}
}