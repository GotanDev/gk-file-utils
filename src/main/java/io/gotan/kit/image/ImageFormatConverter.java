package io.gotan.kit.image;

import io.gotan.kit.file.Base64Utils;
import io.gotan.kit.file.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageFormatConverter {

	/**
	 * Préfixe base64 pour les URLs pour les fichiers PNG.
	 */
	public static final String MIMETYPE_PNG = "image/png";

	/**
	 * Préfixe base64 pour les URLs pour les fichiers GiF.
	 */
	public static final String MIMETYPE_GIF = "image/gif";

	/**
	 * Préfixe base64 pour les URLs pour les fichiers JPG.
	 */
	public static final String MIMETYPE_JPEG = "image/jpeg";

	/**
	 * Préfixe base64 pour les URLs pour les fichiers TIFF.
	 */
	public static final String MIMETYPE_TIFF = "image/tiff";

	/**
	 * Préfixe base64 pour les URLs pour les fichiers WEBP.
	 */
	public static final String MIMETYPE_WEBP = "image/webp";

	/**
	 * Private Log4J Logger.
	 */
	private static Logger logger = LogManager.getLogger(ImageFormatConverter.class);

	/**
	 * Transforme une image d'entrée (peut importe le format vers un PNG)
	 *
	 * @param inputFile       Fichier d'image d'entrée (peu importe le format)
	 * @param backgroundColor Couleur d'arrière plan <br />
	 *                        Example: Color.WHITE
	 * @param outputFormat
	 * @return La nouvelle image
	 * @throws IOException Erreur à la lecture ou l'écriture du fichier
	 */
	public static File convertImage(File inputFile, Color backgroundColor, ImageFormat outputFormat) throws IOException {
		BufferedImage imageToConvert = ImageIO.read(inputFile);
		BufferedImage newBufferedImage = new BufferedImage(imageToConvert.getWidth(), imageToConvert.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(imageToConvert, 0, 0, backgroundColor, null);
		File outputFile = File.createTempFile("temp", outputFormat.getExtension());
		outputFile.deleteOnExit();
		ImageIO.write(newBufferedImage, outputFormat.getExtension(), outputFile);
		return outputFile;
	}

	/**
	 * Convertit une image vers un autre format.
	 *
	 * @param byteData     Données binaires
	 * @param outputFormat Format de sortie attendu
	 * @return La nouvelle image en binaire
	 * @throws IOException Problème de sérialisation sur les fichiers temporaires
	 */
	public static byte[] convertImage(byte[] byteData, ImageFormat outputFormat) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteData));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, outputFormat.getExtension(), byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	public static BufferedImage removeAlphaChannel(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return newImage;
	}

	public static byte[] convertImage(BufferedImage bufferedImage, ImageFormat outputFormat) throws IOException, BadImageFormatException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		boolean result = false;
		switch (outputFormat) {
			case JPEG:
				bufferedImage = removeAlphaChannel(bufferedImage);
				result = ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
				break;
			case PNG:
				result = ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
				break;
			case GIF:
				result = ImageIO.write(bufferedImage, "gif", byteArrayOutputStream);
				break;
			case WEBP:
				result = ImageIO.write(bufferedImage, "webp", byteArrayOutputStream);
				break;
			case TIFF:
				bufferedImage = removeAlphaChannel(bufferedImage);
				result = ImageIO.write(bufferedImage, "tiff", byteArrayOutputStream);
				break;
		}

		if (!result) {
			throw new BadImageFormatException("Unable to change format to %s".formatted(outputFormat.getExtension()));
		}

		return byteArrayOutputStream.toByteArray();
	}


	/**
	 * Convertit une image en base64 vers un autre format.
	 *
	 * @param base64Input         Chaîne encryptée
	 * @param backgroundColor     Couleur d'arrière plan
	 * @param outputFormat        Format de sortie attendu
	 * @param withBase64URLHeader Est ce que la chaîne BASE64 contient un header
	 * @return La nouvelle image en base64
	 * @throws IOException             Problème de sérialisation sur les fichiers temporaires
	 * @throws BadImageFormatException
	 */
	public static String convertImage(String base64Input, Color backgroundColor, ImageFormat outputFormat,
									  boolean withBase64URLHeader) throws IOException, BadImageFormatException {
		try {
			String shrinkBase64 = withBase64URLHeader ? Base64Utils.trimBase64Header(base64Input) : base64Input;

			switch (Base64Utils.getMimeTypeFromHeader(base64Input)) {
				case ImageFormatConverter.MIMETYPE_JPEG:
				case ImageFormatConverter.MIMETYPE_GIF:
				case ImageFormatConverter.MIMETYPE_WEBP:
				case ImageFormatConverter.MIMETYPE_TIFF:
				case ImageFormatConverter.MIMETYPE_PNG:
					return FileUtils
							.encodeToBase64(convertImage(FileUtils.decodeBase64(shrinkBase64, (String) null), backgroundColor, outputFormat));
				default:
					throw new BadImageFormatException();
			}

		} catch (IOException e) {
			logger.fatal("Unable to read/write to temporary disk");
			throw e;
		}
	}


	/**
	 * Convertit une image en BASE64 vers un autre format sans header.
	 *
	 * @param base64Input     Chaîne encryptée
	 * @param backgroundColor Couleur d'arrière plan
	 * @param outputFormat    Format de sortie attendu
	 * @return La nouvelle image en base64
	 * @throws IOException             Problème de sérialisation sur les fichiers temporaires
	 * @throws BadImageFormatException
	 */
	public static String convertImage(String base64Input, Color backgroundColor, ImageFormat outputFormat)
			throws IOException, BadImageFormatException {
		return convertImage(base64Input, backgroundColor, outputFormat, false);
	}

	/**
	 * Convertit une image en BASE64 vers un autre format sans header et avec un
	 * fond blanc.
	 *
	 * @param base64Input  Chaine encryptée
	 * @param outputFormat Format de sortie attendu
	 * @return La nouvelle image en base64
	 * @throws IOException             Problème de sérialisation sur les fichiers temporaires
	 * @throws BadImageFormatException
	 */
	public static String convertImage(final String base64Input, final ImageFormat outputFormat) throws IOException, BadImageFormatException {
		return convertImage(base64Input, Color.WHITE, outputFormat, false);
	}

	/**
	 * Convertit une image en BASE64 vers un autre format avec un fond blanc.
	 *
	 * @param base64Input         Chaine encryptée
	 * @param withBase64URLHeader Est ce que la chaîne BASE64 contient un header
	 * @param outputFormat        Format de sortie attendu
	 * @return La nouvelle image en base64
	 * @throws IOException             Problème de sérialisation sur les fichiers temporaires
	 * @throws BadImageFormatException
	 */
	public static String convertImage(final String base64Input, final ImageFormat outputFormat,
									  final boolean withBase64URLHeaders) throws IOException, BadImageFormatException {
		return convertImage(base64Input, Color.WHITE, outputFormat, withBase64URLHeaders);
	}


	/**
	 * Est ce que le contenu BASE64 est une image
	 *
	 * @param base64input
	 * @return
	 */
	public static boolean isImage(String base64input) {
		return Base64Utils.getMimeTypeFromHeader(base64input).startsWith("image/");
	}

	public enum ImageFormat {
		JPEG("jpg"), GIF("gif"), PNG("png"), WEBP("webp"), TIFF("tiff");

		private String extension;

		ImageFormat(String extension) {
			this.extension = extension;
		}

		public String getExtension() {
			return this.extension;
		}
	}

	public static class BadImageFormatException extends Exception {
		private static final long serialVersionUID = -8312869382197758525L;

		BadImageFormatException() {
			super("Unable to manage this kind of image file ");
		}

		BadImageFormatException(String message) {
			super(message);
		}
	}

	public static class Base64ImageFormatException extends IllegalArgumentException {
		private static final long serialVersionUID = -715401418520733826L;

	}

}
