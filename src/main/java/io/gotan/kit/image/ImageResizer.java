package io.gotan.kit.image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image resizing tools. *
 *
 * @author Damien Cuvillier <damien@gotan.io>
 * @since Oct 2, 2020
 */
public class ImageResizer {
	/**
	 * Private Log4J Logger.
	 */
	private static final Logger logger = LogManager.getLogger(ImageResizer.class);

	/**
	 * Avoid instantiation
	 */
	private ImageResizer() {
	}

	/**
	 * Resize image from original image with a new size
	 *
	 * @param originalImage Original Image
	 * @param newSize       Dimension containing new width & height
	 * @return new Image
	 */
	public static BufferedImage resizeImage(BufferedImage originalImage, Dimension newSize)
			throws BadDimensionException, IllegalArgumentException {
		return resizeImage(originalImage, newSize, 100);
	}

	/**
	 * Resize image from original image with a new size
	 * <p>
	 * Keeping same ratio
	 *
	 * @param originalImage Original Image
	 * @param maxWidth      New width
	 * @return new Image
	 */
	public static BufferedImage resizeImage(BufferedImage originalImage, int maxWidth) {
		if (originalImage.getWidth() <= maxWidth) {
			return originalImage;
		}
		int newHeight = (int) (originalImage.getHeight() * ((double) maxWidth / originalImage.getWidth()));
		Dimension newSize = new Dimension(maxWidth, newHeight);
		return resizeImage(originalImage, newSize, 1);
	}

	/**
	 * Resize image from original image with a new size
	 *
	 * @param originalImage        Original Image
	 * @param newSize              Dimension containing new width & height
	 * @param deformationTolerance Rate above where we throw an exception. Ex :
	 *                             0.2 means that width/height originalImage can
	 *                             not be
	 * @return new Image
	 */
	public static BufferedImage resizeImage(BufferedImage originalImage,
											Dimension newSize,
											double deformationTolerance)
			throws IllegalArgumentException, BadDimensionException {

		if (newSize.getWidth() < 0 || newSize.getHeight() < 0) {
			throw new IllegalArgumentException();
		}
		if (originalImage.getWidth(null) == newSize.getWidth() && originalImage.getHeight(null) == newSize.getHeight()) {
			logger.debug("Already in right size");
			return originalImage;
		}

		double ratio1 = originalImage.getWidth(null) / originalImage.getHeight(null);
		double ratio2 = newSize.getWidth() / newSize.getHeight();

		if ((ratio1 - ratio2) / ratio1 > deformationTolerance) {
			throw new BadDimensionException();
		}
		return ImageUtils.toBufferedImage(
				originalImage.getScaledInstance((int) newSize.getWidth(), (int) newSize.getHeight(), Image.SCALE_DEFAULT));

	}

	public static class BadDimensionException extends RuntimeException {

		private static final long serialVersionUID = 3733008973093258913L;

	}
}
