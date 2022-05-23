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
package de.amr.games.pacman.ui.fx.shell.info;

import de.amr.games.pacman.ui.fx.shell.GameUI;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * UI layer showing different info sections.
 * 
 * @author Armin Reichert
 */
public class InfoLayer extends BorderPane {

	private final VBox leftSide = new VBox();
	private final VBox rightSide = new VBox();
	private SectionGeneral sectionGeneral;
	private SectionGame sectionGame;
	private Section3D section3D;
	private Section2D section2D;
	private SectionKeys sectionKeys;

	public InfoLayer(GameUI ui) {
		int minLabelWidth = 160;
		Color textColor = Color.WHITE;
		Font textFont = Font.font("Sans", 11);
		Font labelFont = Font.font("Sans", 11);
		sectionGame = new SectionGame(ui, "Game", minLabelWidth, textColor, textFont, labelFont);
		sectionGeneral = new SectionGeneral(ui, "General", minLabelWidth, textColor, textFont, labelFont);
		sectionKeys = new SectionKeys(ui, "Keyboard Shortcuts", minLabelWidth, textColor, textFont, labelFont);
		sectionKeys.setExpanded(false);
		section2D = new Section2D(ui, "2D Settings", minLabelWidth, textColor, textFont, labelFont);
		section3D = new Section3D(ui, "3D Settings", minLabelWidth, textColor, textFont, labelFont);
		leftSide.getChildren().addAll(sectionGeneral, sectionGame);
		rightSide.getChildren().addAll(section3D, section2D, sectionKeys);
		setLeft(leftSide);
		setRight(rightSide);
		setVisible(false);
	}

	public void update() {
		sectionGame.update();
		sectionGeneral.update();
		sectionKeys.update();
		section3D.update();
		section2D.update();
	}
}