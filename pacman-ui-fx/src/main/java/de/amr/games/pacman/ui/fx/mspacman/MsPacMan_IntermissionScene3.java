package de.amr.games.pacman.ui.fx.mspacman;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.ui.fx.common.AbstractGameScene2D;
import de.amr.games.pacman.ui.fx.rendering.PacManGameRendering2D;
import de.amr.games.pacman.ui.mspacman.MsPacMan_IntermissionScene3_Controller;
import de.amr.games.pacman.ui.sound.SoundManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Intermission scene 3: "Junior".
 * 
 * <p>
 * Pac-Man and Ms. Pac-Man gradually wait for a stork, who flies overhead with a little blue bundle.
 * The stork drops the bundle, which falls to the ground in front of Pac-Man and Ms. Pac-Man, and
 * finally opens up to reveal a tiny Pac-Man. (Played after rounds 9, 13, and 17)
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntermissionScene3 extends AbstractGameScene2D {

	private MsPacMan_IntermissionScene3_Controller sceneController;

	public MsPacMan_IntermissionScene3(PacManGameController controller, PacManGameRendering2D rendering, SoundManager sounds) {
		super(controller, rendering, sounds);
	}

	@Override
	public void start() {
		sceneController = new MsPacMan_IntermissionScene3_Controller(controller, rendering, sounds);
		sceneController.start();
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
		sceneController.update();
		clearCanvas();
		GraphicsContext g = canvas.getGraphicsContext2D();
		rendering.drawFlap(g, sceneController.flap);
		rendering.drawStork(g, sceneController.stork);
		rendering.drawPlayer(g, sceneController.msPacMan);
		rendering.drawSpouse(g, sceneController.pacMan);
		rendering.drawJuniorBag(g, sceneController.bag);
	}
}