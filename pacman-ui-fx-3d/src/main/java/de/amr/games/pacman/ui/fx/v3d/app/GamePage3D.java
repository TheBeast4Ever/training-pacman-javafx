/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.v3d.app;

import de.amr.games.pacman.ui.fx.app.GamePage;
import de.amr.games.pacman.ui.fx.input.Keyboard;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.scene2d.GameScene2D;
import de.amr.games.pacman.ui.fx.util.Ufx;
import de.amr.games.pacman.ui.fx.v3d.dashboard.Dashboard;
import de.amr.games.pacman.ui.fx.v3d.scene.PictureInPicture;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import static de.amr.games.pacman.ui.fx.util.ResourceManager.fmtMessage;

/**
 * @author Armin Reichert
 */
class GamePage3D extends GamePage {

	private BorderPane topLayer;
	private PictureInPicture pip;
	private Dashboard dashboard;

	public GamePage3D(PacManGames3dUI ui) {
		super(ui);

		pip = new PictureInPicture(ui);
		pip.opacityPy.bind(PacManGames3d.PY_PIP_OPACITY);
		pip.heightPy.bind(PacManGames3d.PY_PIP_HEIGHT);

		dashboard = new Dashboard(ui);
		dashboard.setVisible(false);

		topLayer = new BorderPane();
		topLayer.setLeft(dashboard);
		topLayer.setRight(pip.root());
	}

	public PictureInPicture getPip() {
		return pip;
	}

	@Override
	public void setGameScene(GameScene gameScene) {
		if (gameScene instanceof GameScene2D) {
			root().getChildren().set(0, layoutPane());
			super.setGameScene(gameScene);
		} else {
			root().getChildren().set(0, gameScene.root());
			root().setBackground(ui().theme().background("model3D.wallpaper"));
			// assume play scene 3D is only 3D scene
			root().addEventHandler(KeyEvent.KEY_PRESSED, ui().getKeyboardPlayerSteering());
			root().requestFocus();
			helpButton().setVisible(false);
		}
	}

	@Override
	public void render() {
		super.render();
		dashboard.update();
		pip.render();
	}

	@Override
	protected void handleKeyboardInput() {
		var ui3D = (PacManGames3dUI) ui();
		super.handleKeyboardInput();
		if (Keyboard.pressed(PacManGames3d.KEY_TOGGLE_2D_3D)) {
			ui3D.toggle2D3D();
		} else if (Keyboard.anyPressed(PacManGames3d.KEY_TOGGLE_DASHBOARD, PacManGames3d.KEY_TOGGLE_DASHBOARD_2)) {
			toggleDashboardVisible();
		} else if (Keyboard.pressed(PacManGames3d.KEY_TOGGLE_PIP_VIEW)) {
			togglePipVisible();
		}
	}

	/**
	 * @return if the picture-in-picture view is enabled, it might be invisible nevertheless!
	 */
	private boolean isPiPOn() {
		return PacManGames3d.PY_PIP_ON.get();
	}

	private void togglePipVisible() {
		Ufx.toggle(PacManGames3d.PY_PIP_ON);
		pip.update(ui().currentGameScene(), isPiPOn());
		var message = fmtMessage(PacManGames3d.TEXTS, isPiPOn() ? "pip_on" : "pip_off");
		ui().showFlashMessage(message);
		updateTopLayer();
	}

	private void toggleDashboardVisible() {
		dashboard.setVisible(!dashboard.isVisible());
		updateTopLayer();
	}

	private void updateTopLayer() {
		root().getChildren().remove(topLayer);
		if (dashboard.isVisible() || isPiPOn()) {
			root().getChildren().add(topLayer);
		}
		helpButton().setVisible(!root().getChildren().contains(topLayer));
		root().requestFocus();
	}
}