# pacman-javafx

(WTF! I got >20 stars today! How comes?)

A JavaFX user interface for my Pac-Man/Ms. Pac-Man game implementation, see [pacman-basic](https://github.com/armin-reichert/pacman-basic). Both games can be played in 2D and 3D (work in progress).

The 3D model (unfortunately I have no animated model yet) has been generously provided to me by Gianmarco Cavallaccio (https://www.artstation.com/gianmart). Cudos to Gianmarco! 

Currently, the game can only be started with the supplied batch file (because I am still too dumb to create a JavaFX executable jar file in Eclipse using Maven). Adapt the batch file to your local environment and run it as `pacman.bat`or as `pacman -mspacman`to start in Ms. Pac-Man mode. Switching between the game versions is possible from the intro scenes by pressing <kbd>V</kbd>.

![Play Scene](https://github.com/armin-reichert/pacman-javafx/blob/main/pacman-ui-fx/doc/playscene3D.png)

YouTube video

[![YouTube](https://github.com/armin-reichert/pacman-javafx/blob/main/pacman-ui-fx/doc/thumbnail.jpg)](https://www.youtube.com/watch?v=6ztHwLJuPNw&t=298s)

### Keys:

On the intro screen, you can switch between the game versions by pressing <kbd>v</kbd>. You can switch between window and fullscreen mode using the standard keys <kbd>F11</kbd> and <kbd>Esc</kbd>.

On the 3D play screen, the following functionality is available:
- <kbd>CTRL+C</kbd> Switch camera (3 cameras currently implemented)
- <kbd>CTRL+I</kbd> Toggle information about current scene (HUD)
- <kbd>CTRL+L</kbd> Toggle draw mode (line vs. shaded)
- <kbd>CTRL+P</kbd> Toggle pause game play
- <kbd>CTRL+S</kbd> Increase speed
- <kbd>CTRL+SHIFT+S</kbd> Decrease speed
- <kbd>CTRL+3</kbd> Toggle using 2D/3D play scene
- <kbd>A</kbd> Toggle autopilot mode
- <kbd>I</kbd> Toggle immunity of player against ghost attacks
- <kbd>Q</kbd> Quit play scene
- Cheats:
  - <kbd>E</kbd> Eat all pills
  - <kbd>L</kbd> Add one life
  - <kbd>N</kbd> Enter next level
  - <kbd>X</kbd> Kill ghosts 
