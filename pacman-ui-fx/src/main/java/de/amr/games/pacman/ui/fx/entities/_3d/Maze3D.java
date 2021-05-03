package de.amr.games.pacman.ui.fx.entities._3d;

import static de.amr.games.pacman.model.world.PacManGameWorld.TS;
import static de.amr.games.pacman.ui.fx.rendering.Rendering2D_Assets.getFoodColor;

import java.util.stream.Stream;

import de.amr.games.pacman.model.common.GameLevel;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.world.PacManGameWorld;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

/**
 * 3D-model for a maze. Creates boxes representing walls from the world map.
 * 
 * @author Armin Reichert
 */
public class Maze3D extends Group {

	private Group foodGroup = new Group();

	public Maze3D(PacManGameWorld world, PhongMaterial wallMaterial, double wallHeight, Image floorTexture, double sizeX, double sizeY) {
		Box floor = new Box(sizeX, sizeY, 0.1);
		floor.getTransforms().add(new Translate(sizeX / 2 - TS / 2, sizeY / 2 - TS / 2, 3));
		var material = new PhongMaterial();
		material.setDiffuseMap(floorTexture);
		material.setDiffuseColor(Color.rgb(30, 30, 120));
		material.setSpecularColor(Color.rgb(60, 60, 240));
		floor.setMaterial(material);

		var wallBuilder = new WallBuilder();
		wallBuilder.setWallMaterial(wallMaterial);
		wallBuilder.setWallHeight(wallHeight);

		int resolution = 4;
		world.getWallMap(resolution);
		Group wallRoot = new Group();
		wallRoot.getChildren().setAll(wallBuilder.build(world, resolution));

		getChildren().addAll(floor, wallRoot, foodGroup);
	}

	public Stream<Node> foodNodes() {
		return foodGroup.getChildren().stream();
	}

	public void resetFood(GameVariant variant, GameLevel gameLevel) {
		var foodMaterial = new PhongMaterial(getFoodColor(variant, gameLevel.mazeNumber));
		foodGroup.getChildren().clear();
		gameLevel.world.tiles().filter(gameLevel.world::isFoodTile).forEach(foodTile -> {
			double size = gameLevel.world.isEnergizerTile(foodTile) ? 2.5 : 1;
			var pellet = new Sphere(size);
			pellet.setMaterial(foodMaterial);
			pellet.setTranslateX(foodTile.x * TS);
			pellet.setTranslateY(foodTile.y * TS);
			pellet.setTranslateZ(1);
			pellet.setUserData(foodTile);
			foodGroup.getChildren().add(pellet);
		});
	}
}