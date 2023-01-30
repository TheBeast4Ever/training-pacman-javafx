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
package de.amr.games.pacman.ui.fx.dashboard;

import de.amr.games.pacman.ui.fx.app.Actions;
import de.amr.games.pacman.ui.fx.app.Env;
import de.amr.games.pacman.ui.fx.app.ResourceMgr;
import de.amr.games.pacman.ui.fx.shell.GameUI;
import de.amr.games.pacman.ui.fx.util.Ufx;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.text.Font;

/**
 * 3D related settings.
 * 
 * @author Armin Reichert
 */
public class Section3D extends Section {

	private final CheckBox cbSquirting;
	private final Slider sliderWallHeight;
	private final Slider sliderWallThickness;
	private final ColorPicker pickerLightColor;
	private final ComboBox<String> comboFloorTexture;
	private final ColorPicker pickerFloorColor;
	private final CheckBox cbAxesVisible;
	private final CheckBox cbWireframeMode;
	private final CheckBox cbPacLighted;

	public Section3D(GameUI ui, String title, int minLabelWidth, Color textColor, Font textFont, Font labelFont) {
		super(ui, title, minLabelWidth, textColor, textFont, labelFont);
		pickerLightColor = addColorPicker("Light color", Env.ThreeD.lightColorPy.get());
		pickerLightColor.setOnAction(e -> Env.ThreeD.lightColorPy.set(pickerLightColor.getValue()));
		sliderWallHeight = addSlider("Wall height", 0.1, 10.0, Env.ThreeD.mazeWallHeightPy.get());
		sliderWallHeight.valueProperty()
				.addListener((obs, oldVal, newVal) -> Env.ThreeD.mazeWallHeightPy.set(newVal.doubleValue()));
		sliderWallThickness = addSlider("Wall thickness", 0.1, 2.0, Env.ThreeD.mazeWallThicknessPy.get());
		sliderWallThickness.valueProperty()
				.addListener((obs, oldVal, newVal) -> Env.ThreeD.mazeWallThicknessPy.set(newVal.doubleValue()));
		comboFloorTexture = addComboBox("Floor texture", ResourceMgr.FLOOR_TEXTURES.toArray(String[]::new));
		comboFloorTexture.setOnAction(e -> Env.ThreeD.floorTexturePy.set(comboFloorTexture.getValue()));
		pickerFloorColor = addColorPicker("Floor color", Env.ThreeD.floorColorPy.get());
		pickerFloorColor.setOnAction(e -> Env.ThreeD.floorColorPy.set(pickerFloorColor.getValue()));
		cbSquirting = addCheckBox("Squirting", () -> Ufx.toggle(Env.ThreeD.squirtingEnabledPy));
		cbPacLighted = addCheckBox("Pac-Man lighted", () -> Ufx.toggle(Env.ThreeD.pacLightedPy));
		cbAxesVisible = addCheckBox("Show axes", () -> Ufx.toggle(Env.ThreeD.axesVisiblePy));
		cbWireframeMode = addCheckBox("Wireframe mode", Actions::toggleDrawMode);
	}

	@Override
	public void update() {
		super.update();
		sliderWallHeight.setValue(Env.ThreeD.mazeWallHeightPy.get());
		sliderWallHeight.setDisable(!gameScene().is3D());
		comboFloorTexture.setValue(Env.ThreeD.floorTexturePy.get());
		comboFloorTexture.setDisable(!gameScene().is3D());
		cbSquirting.setSelected(Env.ThreeD.squirtingEnabledPy.get());
		cbPacLighted.setSelected(Env.ThreeD.pacLightedPy.get());
		cbAxesVisible.setSelected(Env.ThreeD.axesVisiblePy.get());
		cbAxesVisible.setDisable(!gameScene().is3D());
		cbWireframeMode.setSelected(Env.ThreeD.drawModePy.get() == DrawMode.LINE);
		cbWireframeMode.setDisable(!gameScene().is3D());
	}
}