package de.amr.games.pacman.ui.fx.app;

import javafx.scene.Node;

/**
 * @author Armin Reichert
 */
public interface Page {
  Node root();
  void setSize(double width, double height);
}
