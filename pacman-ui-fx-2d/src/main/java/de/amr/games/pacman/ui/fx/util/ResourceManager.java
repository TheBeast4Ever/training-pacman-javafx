/*
Copyright (c) 2021-2023 Armin Reichert (MIT License)
See file LICENSE in repository root directory for details.
*/
package de.amr.games.pacman.ui.fx.util;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.text.Font;
import org.tinylog.Logger;

import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static de.amr.games.pacman.lib.Globals.checkNotNull;

/**
 * @author Armin Reichert
 */
public interface ResourceManager {

	/**
	 * Builds a resource key from the given key pattern and the arguments and returns the corresponding text from the
	 * resource bundle.
	 * 
	 * @param bundle	resource bundle
	 * @param key 		key in resource bundle
	 * @param args		optional arguments merged into the message (if pattern)
	 * @return localized text with arguments merged or a string indicating a missing text
	 */
	public static String message(ResourceBundle bundle, String key, Object... args) {
		checkNotNull(bundle);
		checkNotNull(key);
		return MessageFormat.format(bundle.getString(key), args);
	}

	public static Background coloredBackground(Color color) {
		checkNotNull(color);
		return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
	}

	public static Background coloredRoundedBackground(Color color, int radius) {
		checkNotNull(color);
		return new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY));
	}

	public static Border roundedBorder(Color color, double cornerRadius, double width) {
		checkNotNull(color);
		return new Border(
				new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(cornerRadius), new BorderWidths(width)));
	}

	public static Border border(Color color, double width) {
		checkNotNull(color);
		return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, null, new BorderWidths(width)));
	}

	public static PhongMaterial coloredMaterial(Color color) {
		checkNotNull(color);
		var material = new PhongMaterial(color);
		material.setSpecularColor(color.brighter());
		return material;
	}

	public static Color color(Color color, double opacity) {
		checkNotNull(color);
		return Color.color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
	}

	/**
	 * Creates a URL from a resource path. If the path does not start with a slash, the path to the resource
	 * root directory is prepended.
	 * 
	 * @param path path of resource
	 * @return URL of resource addressed by this path
	 */
	default URL url(String path) {
		checkNotNull(path);
		URL url = getClass().getResource(path);
		if (url == null) {
			throw new MissingResourceException(
					String.format("Resource '%s' not found relative to class '%s'", path, getClass()),
					getClass().getName(), path);
		}
		return url;
	}

	/**
	 * @param relPath relative path (without leading slash) starting from resource root directory
	 * @return audio clip from resource addressed by this path
	 */
	default AudioClip audioClip(String relPath) {
		var url = url(relPath);
		return new AudioClip(url.toExternalForm());
	}

	/**
	 * @param relPath relative path (without leading slash) starting from resource root directory
	 * @param size    font size (must be a positive number)
	 * @return font loaded from resource addressed by this path. If no such font can be loaded, a default font is returned
	 */
	default Font font(String relPath, double size) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("Font size must be positive but is %.2f", size));
		}
		var url = url(relPath);
		var font = Font.loadFont(url.toExternalForm(), size);
		if (font == null) {
			Logger.error("Font with URL '{}' could not be loaded", url);
			return Font.font(Font.getDefault().getFamily(), size);
		}
		return font;
	}

	/**
	 * @param relPath relative path to image (without leading slash) starting from resource root directory
	 * @return image loaded from resource addressed by this path.
	 */
	default Image image(String relPath) {
		var url = url(relPath);
		return new Image(url.toExternalForm());
	}

	/**
	 * @param relPath relative path to image (without leading slash) starting from resource root directory
	 * @return image background with default properties, see {@link BackgroundImage}
	 */
	default Background imageBackground(String relPath) {
		var image = image(relPath);
		return new Background(new BackgroundImage(image, null, null, null, null));
	}

	/**
	 * @param relPath relative path to image (without leading slash) starting from resource root directory
	 * @return image background with specified attributes
	 */
	default Background imageBackground(String relPath, BackgroundRepeat repeatX, BackgroundRepeat repeatY,
			BackgroundPosition position, BackgroundSize size) {
		var image = image(relPath);
		return new Background(new BackgroundImage(image, repeatX, repeatY, position, size));
	}
}