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
package de.amr.games.pacman.ui.fx.shell;

import java.util.Random;

import de.amr.games.pacman.ui.fx.util.U;
import javafx.scene.layout.Background;

/**
 * A wallpaper selector.
 *
 * @author Armin Reichert
 */
public class Wallpapers {

	private static final Wallpapers it = new Wallpapers();

	public static Wallpapers get() {
		return it;
	}

	private final Background[] backgrounds = { //
			U.imageBackground("/common/wallpapers/beach.jpg"), //
			U.imageBackground("/common/wallpapers/space.jpg"), //
			U.imageBackground("/common/wallpapers/easter_island.jpg"), //
	};

	private int currentIndex;

	public Background current() {
		return backgrounds[currentIndex];
	}

	public Background random() {
		if (backgrounds.length > 1) {
			int oldIndex = currentIndex;
			do {
				currentIndex = new Random().nextInt(backgrounds.length);
			} while (currentIndex == oldIndex);
		}
		return backgrounds[currentIndex];
	}
}