# A JavaFX UI (2D + 3D) for Pac-Man and Ms. Pac-Man

## ℹ️ Online version available

Both games (2D) can now also be played online [here](https://armin-reichert.github.io/webfx-pacman/)

"Old" WebFX versions (will soon be updated by version above):
- [Pac-Man](https://pacman.webfx.dev/)
- [Ms. Pac-Man](https://mspacman.webfx.dev/)

This has been made possible thanks to the [WebFX](https://webfx.dev/) transpiler technology (:+1: to Bruno Salmon).

## About this project

JavaFX user interfaces for my UI-agnostic/faceless [Pac-Man and Ms. Pac-Man games](https://github.com/armin-reichert/pacman-basic). There is a 2D-only user interface and
a full version where the play scene can be switched between 2D and 3D, even during the gameplay (key combination <kbd>Alt+3</kbd>).

## How to run

In the [release folder](https://github.com/armin-reichert/pacman-javafx/releases) you find executable jar files and
installers for Windows. If you have a Java runtime installed you can start the game(s) by double-clicking the jar file.
Otherwise run the installer and double-click the new desktop icon(s).

## How to build locally

- Clone repository [pacman-basic](https://github.com/armin-reichert/pacman-basic).
- Clone repository [pacman-javafx](https://github.com/armin-reichert/pacman-javafx).
- `cd \path\to\git\pacman-javafx`
- `build.bat`

This will
- build the [pacman-core](https://github.com/armin-reichert/pacman-basic/tree/main/pacman-core) Maven project (game logic and model),
- build the [pacman-ui-fx-2d](pacman-ui-fx-2d) (2D) and the [pacman-ui-fx-3d](pacman-ui-fx-3d) project (2D+3D),
- create (executable) jar-files 
  - `pacman-ui-fx-2d-1.0-shaded.jar` (in `pacman-javafx\pacman-ui-fx-2d\target`)
  - `pacman-ui-fx-3d-1.0-shaded.jar` (in `pacman-javafx\pacman-ui-fx-3d\target`)
- create Windows installers 
  - `pacman-ui-fx-2d_1.0.exe` (in `pacman-javafx\pacman-ui-fx-2d\target`)
  - `pacman-ui-fx-3d_1.0.exe` (in `pacman-javafx\pacman-ui-fx-3d\target`)

To be able to create the installers you need to have [Inno Setup](https://jrsoftware.org/isinfo.php) and [WIX toolset](https://wixtoolset.org/) installed as described in the [JavaPackager documentation](https://github.com/fvarrui/JavaPackager/blob/master/docs/windows-tools-guide.md). (I also had to add the paths "C:\Program Files (x86)\WiX Toolset v3.11\bin" and "C:\Program Files (x86)\Inno Setup 6" to my PATH variable.)

## How to use

Starting the game and switching game variant:
- <kbd>V</kbd> Switch between Pac-Man and Ms. Pac-Man (only possible on intro screen)
- <kbd>5</kbd> Add credit ("insert coin")
- <kbd>1</kbd> Start game
- <kbd>H</kbd>Show/hide context-sensitive help

Pac-Man steering:
- Pac-Man is steered using the cursor keys. When the dashboard is open, these keys are taken away by the JavaFX widgets. 
In that case, you can steer Pac-Man using key combination <kbd>CTRL</kbd>+cursor key.

General shortcuts:
- <kbd>F11</kbd> Enter fullscreen mode
- <kbd>Esc</kbd> Exit fullscreen mode
- <kbd>F1</kbd> or <kbd>Alt+B</kbd> Toggle dashboard
- <kbd>F2</kbd> Toggle picture-in-picture view
- <kbd>Alt+Z</kbd> Play all intermission scenes
- <kbd>Alt+3</kbd> Toggle using 2D/3D play scene

Play screen shortcuts:
- <kbd>Alt+LEFT</kbd> Select previous camera perspective
- <kbd>Alt+RIGHT</kbd> Select next camera perspective
- <kbd>Q</kbd>Quit play scene and show intro screen

Cheats:
  - <kbd>Alt+A</kbd> Toggle autopilot mode
  - <kbd>Alt+E</kbd> Eat all pills except the energizers
  - <kbd>Alt+I</kbd> Toggle immunity of player against ghost attacks
  - <kbd>Alt+L</kbd> Add 3 player lives
  - <kbd>Alt+N</kbd> Enter next game level
  - <kbd>Alt+X</kbd> Kill all ghosts outside of the ghosthouse 

## How it looks

### 3D Play Scene

![Play Scene](doc/pacman-maze.png)

### Dashboard

![Dashboard](doc/dashboard-general.png)

![Dashboard](doc/dashboard-shortcuts.png)

![Dashboard](doc/dashboard-appearance.png)

![Dashboard](doc/dashboard-3d-settings.png)

![Dashboard](doc/dashboard-game-control.png)

![Dashboard](doc/dashboard-game-info.png)

![Dashboard](doc/dashboard-ghost-info.png)

![Dashboard](doc/dashboard-about.png)

YouTube:

[![YouTube](doc/thumbnail.jpg)](https://www.youtube.com/watch?v=_3iQ-PKXX6Y)
