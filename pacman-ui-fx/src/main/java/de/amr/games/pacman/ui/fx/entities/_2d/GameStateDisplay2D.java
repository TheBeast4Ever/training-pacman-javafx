package de.amr.games.pacman.ui.fx.entities._2d;

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.util.function.Supplier;

import de.amr.games.pacman.controller.PacManGameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameStateDisplay2D {

	private final Supplier<PacManGameState> stateSupplier;
	private Font font;

	public GameStateDisplay2D(Supplier<PacManGameState> stateSupplier) {
		this.stateSupplier = stateSupplier;
	}

	public void render(GraphicsContext g) {
		PacManGameState state = stateSupplier.get();
		if (state == PacManGameState.GAME_OVER) {
			g.setFont(font);
			g.setFill(Color.RED);
			g.fillText("GAME", t(9), t(21));
			g.fillText("OVER", t(15), t(21));
		} else if (state == PacManGameState.READY) {
			g.setFont(font);
			g.setFill(Color.YELLOW);
			g.fillText("READY", t(11), t(21));
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}