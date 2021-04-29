package de.amr.games.pacman.ui.fx.entities._3d;

import static de.amr.games.pacman.lib.Logging.log;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.model.common.GhostState;
import de.amr.games.pacman.ui.fx.model3D.GianmarcosModel3D;
import de.amr.games.pacman.ui.fx.rendering.Rendering2D;
import de.amr.games.pacman.ui.fx.rendering.Rendering2D_Assets;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * 3D ghost shape.
 * 
 * @author Armin Reichert
 */
public class Ghost3D extends Group {

	//@formatter:off
	private static final int[][][] ROTATION_INTERVALS = {
		{ {  0, 0}, {  0, 180}, {  0, 90}, {  0, -90} },
		{ {180, 0}, {180, 180}, {180, 90}, {180, 270} },
		{ { 90, 0}, { 90, 180}, { 90, 90}, { 90, 270} },
		{ {-90, 0}, {270, 180}, {-90, 90}, {-90, -90} },
	};
	//@formatter:on

	private static int indexOfDir(Direction dir) {
		return dir == Direction.LEFT ? 0 : dir == Direction.RIGHT ? 1 : dir == Direction.UP ? 2 : 3;
	}

	private static int[] rotationInterval(Direction from, Direction to) {
		int row = indexOfDir(from), col = indexOfDir(to);
		return ROTATION_INTERVALS[row][col];
	}

	private class FlashingAnimation extends Transition {

		public FlashingAnimation() {
			setCycleCount(INDEFINITE);
			setCycleDuration(Duration.seconds(0.1));
			setAutoReverse(true);
		}

		@Override
		protected void interpolate(double frac) {
			flashingSkin.setDiffuseColor(Color.rgb((int) (frac * 120), (int) (frac * 180), 255));
		}
	};

	public final Ghost ghost;

	private final Rendering2D rendering2D;
	private final PhongMaterial normalSkin, blueSkin, flashingSkin;

	private final Group ghostShape;
	private final RotateTransition ghostShapeRot;
	private final FlashingAnimation flashingAnimation = new FlashingAnimation();

	private final Group eyesShape;
	private final RotateTransition eyesShapeRot;

	private final Box bountyShape;

	private Direction targetDir;

	public Ghost3D(Ghost ghost, Rendering2D rendering2D) {
		this.ghost = ghost;
		this.rendering2D = rendering2D;

		targetDir = ghost.dir();
		int[] rotationInterval = rotationInterval(ghost.dir(), targetDir);

		Color ghostColor = Rendering2D_Assets.getGhostColor(ghost.id);
		normalSkin = new PhongMaterial(ghostColor);
		normalSkin.setSpecularColor(ghostColor.brighter());

		Color blueColor = Rendering2D_Assets.getGhostBlueColor();
		blueSkin = new PhongMaterial(blueColor);
		blueSkin.setSpecularColor(blueColor.brighter());

		flashingSkin = new PhongMaterial();

		ghostShape = GianmarcosModel3D.createGhost();
		ghostShape.setRotationAxis(Rotate.Z_AXIS);
		ghostShape.setRotate(rotationInterval[0]);

		ghostShapeRot = new RotateTransition(Duration.seconds(0.25), ghostShape);
		ghostShapeRot.setAxis(Rotate.Z_AXIS);

		eyesShape = GianmarcosModel3D.createGhostEyes();
		eyesShape.setRotationAxis(Rotate.Z_AXIS);
		eyesShape.setRotate(rotationInterval[0]);

		eyesShapeRot = new RotateTransition(Duration.seconds(0.25), eyesShape);
		eyesShapeRot.setAxis(Rotate.Z_AXIS);

		bountyShape = new Box(8, 8, 8);
		bountyShape.setMaterial(new PhongMaterial());

		getColoredGhostBody().setMaterial(normalSkin);
	}

	public void update() {
		setVisible(ghost.visible);
		setTranslateX(ghost.position.x);
		setTranslateY(ghost.position.y);
		if (ghost.bounty > 0) {
			if (getChildren().get(0) != bountyShape) {
				Rectangle2D sprite = rendering2D.getBountyNumberSpritesMap().get(ghost.bounty);
				Image image = rendering2D.subImage(sprite);
				PhongMaterial material = (PhongMaterial) bountyShape.getMaterial();
				material.setBumpMap(image);
				material.setDiffuseMap(image);
				getChildren().setAll(bountyShape);
				log("Set bounty mode for %s", ghost);
			}
			setRotationAxis(Rotate.X_AXIS);
			setRotate(0);
		} else if (ghost.is(GhostState.DEAD)) {
			getChildren().setAll(eyesShape);
			rotateTowardsMoveDir();
		} else {
			getChildren().setAll(ghostShape);
			rotateTowardsMoveDir();
		}
	}

	private MeshView getColoredGhostBody() {
		return (MeshView) ghostShape.getChildren().get(0);
	}

	public void startBlueMode() {
		MeshView body = getColoredGhostBody();
		body.setMaterial(blueSkin);
		log("Start blue mode for %s", ghost);
	}

	public void stopBlueMode() {
		MeshView body = getColoredGhostBody();
		body.setMaterial(normalSkin);
		log("Stop blue mode for %s", ghost);
	}

	public void startFlashing() {
		MeshView body = getColoredGhostBody();
		body.setMaterial(flashingSkin);
		flashingAnimation.playFromStart();
		log("Start flashing mode for %s", ghost);
	}

	public void stopFlashing() {
		flashingAnimation.stop();
		MeshView body = getColoredGhostBody();
		body.setMaterial(normalSkin);
		log("Stop flashing mode for %s", ghost);
	}

	private void rotateTowardsMoveDir() {
		if (targetDir != ghost.dir()) {
			int[] rotationInterval = rotationInterval(targetDir, ghost.dir());
			ghostShapeRot.stop();
			ghostShapeRot.setFromAngle(rotationInterval[0]);
			ghostShapeRot.setToAngle(rotationInterval[1]);
			ghostShapeRot.play();
			eyesShapeRot.stop();
			eyesShapeRot.setFromAngle(rotationInterval[0]);
			eyesShapeRot.setToAngle(rotationInterval[1]);
			eyesShapeRot.play();
			targetDir = ghost.dir();
		}
	}
}