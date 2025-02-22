/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.v3d.dashboard;

import de.amr.games.pacman.ui.fx.v3d.PacManGames3dUI;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Armin Reichert
 */
public class SectionAbout extends Section {

	public SectionAbout(PacManGames3dUI ui, String title) {
		super(ui, title, Dashboard.MIN_LABEL_WIDTH, Dashboard.TEXT_COLOR, Dashboard.TEXT_FONT, Dashboard.LABEL_FONT);

		var theAuthorInYoungerYears = new ImageView(ui.theme().image("image.armin1970"));
		theAuthorInYoungerYears.setFitWidth(286);
		theAuthorInYoungerYears.setPreserveRatio(true);

		var madeBy = new Text("Made by     ");
		madeBy.setFont(Font.font("Helvetica", 16));
		madeBy.setFill(Color.grayRgb(150));

		var signature = new Text("Armin Reichert");
		var font = ui.theme().font("font.handwriting", 18);
		signature.setFont(font);
		signature.setFill(Color.grayRgb(225));

		var tf = new TextFlow(madeBy, signature);
		tf.setPadding(new Insets(5, 5, 5, 5));
		content.add(theAuthorInYoungerYears, 0, 0);
		content.add(tf, 0, 1);
	}
}