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
package de.amr.games.pacman.ui.fx.shell;

import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.ui.fx._2d.scene.common.PlayScene2D;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntroScene;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntroScene;
import de.amr.games.pacman.ui.fx._3d.model.GianmarcosModel3D;
import de.amr.games.pacman.ui.fx._3d.scene.PlayScene3D;
import de.amr.games.pacman.ui.fx.scene.AbstractGameScene;

/**
 * The game scenes.
 * 
 * @author Armin Reichert
 */
public class GameScenes {

	private final AbstractGameScene scenes_PacMan[][] = new AbstractGameScene[5][2];
	private final AbstractGameScene scenes_MsPacMan[][] = new AbstractGameScene[5][2];

	public GameScenes() {
		//@formatter:off
		scenes_PacMan  [0][0] = 
		scenes_PacMan  [0][1] = new PacMan_IntroScene();
		scenes_PacMan  [1][0] = 
		scenes_PacMan  [1][1] = new PacMan_IntermissionScene1();
		scenes_PacMan  [2][0] = 
		scenes_PacMan  [2][1] = new PacMan_IntermissionScene2();
		scenes_PacMan  [3][0] = 
		scenes_PacMan  [3][1] = new PacMan_IntermissionScene3();
		scenes_PacMan  [4][0] = new PlayScene2D();
		scenes_PacMan  [4][1] = new PlayScene3D(GianmarcosModel3D.get());
		
		scenes_MsPacMan[0][0] = 
		scenes_MsPacMan[0][1] = new MsPacMan_IntroScene();
		scenes_MsPacMan[1][0] = 
		scenes_MsPacMan[1][1] = new MsPacMan_IntermissionScene1();
		scenes_MsPacMan[2][0] = 
		scenes_MsPacMan[2][1] = new MsPacMan_IntermissionScene2();
		scenes_MsPacMan[3][0] = 
		scenes_MsPacMan[3][1] = new MsPacMan_IntermissionScene3();
		scenes_MsPacMan[4][0] = new PlayScene2D();
		scenes_MsPacMan[4][1] = new PlayScene3D(GianmarcosModel3D.get());
		//@formatter:on
	}

	public AbstractGameScene getScene(GameVariant gameVariant, int sceneIndex, int sceneVariant) {
		return gameVariant == GameVariant.MS_PACMAN //
				? scenes_MsPacMan[sceneIndex][sceneVariant]
				: scenes_PacMan[sceneIndex][sceneVariant];
	}
}