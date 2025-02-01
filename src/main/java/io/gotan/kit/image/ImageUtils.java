package io.gotan.kit.image;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageUtils {

    /**
     * Récupère une image bufferisé.
     *
     * @param img Image générique
     * @return Image bufférisée
     */
    public static BufferedImage toBufferedImage(final Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }


    /**
     * Supprime le fond transparent d'une image et le remplace par une couleur
     *
     * @param image
     * @return
     * @throws IOException
     * @see Color
     */
    public static BufferedImage normalizeImageTransparency(final BufferedImage image, final Color color) {

        if (image.getColorModel().hasAlpha()) {
            BufferedImage transparencyImg = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = transparencyImg.createGraphics();
            g2d.setColor(color);
            g2d.fillRect(0, 0, transparencyImg.getWidth(), transparencyImg.getHeight());
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            return transparencyImg;
        } else {
            return image;
        }
    }

    /**
     * Redimensionne une image en respectant
     *
     * @param image     Image originale à redimensionner
     * @param maxWidth  Largeur maximum
     * @param maxHeight Hauteur maximum
     * @return Image redimensionnée
     */
    public static BufferedImage resize(final BufferedImage image, int maxWidth, int maxHeight) {
        // Redimensionne l'image
        Dimension dim = new Dimension(image.getWidth(), image.getHeight()).scale(maxWidth, maxHeight);
        BufferedImage resizedImage = new BufferedImage(dim.getWidth(), dim.getHeight(), image.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, dim.getWidth(), dim.getHeight(), null);
        graphics2D.dispose();
        return resizedImage;
    }


    /**
     * Redimensionne une image en respectant
     *
     * @param imageData    Image au format binaire
     * @param maxWidth  Largeur maximum
     * @param maxHeight Hauteur maximum
     * @return Image redimensionnée
     */
    public static BufferedImage resize(final byte[] imageData, int maxWidth, int maxHeight) throws IOException {
        BufferedImage image = ImageUtils.getImage(imageData);
        return resize(image, maxWidth, maxHeight);
    }


    /**
     * Redimensionne une image en respectant
     *
     * @param imageFile    Fichier de l'image
     * @param maxWidth  Largeur maximum
     * @param maxHeight Hauteur maximum
     * @return Image redimensionnée
     */
    public static BufferedImage resize(final File imageFile, int maxWidth, int maxHeight) throws IOException {
        BufferedImage image = ImageUtils.getImage(Files.readAllBytes(imageFile.toPath()));
        return resize(image, maxWidth, maxHeight);
    }



    /**
     * Get image byte array data from BufferedImage
     *
     * @param image  BufferedImage
     * @param format Format de sortie
     */
    public static byte[] getData(BufferedImage image, ImageFormatConverter.ImageFormat format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    /**
     * Get PNG image byte array data from BufferedImage
     *
     * @param image  BufferedImage
     */
    public static byte[] getPNGData(BufferedImage image) throws IOException {
        return getData(image, ImageFormatConverter.ImageFormat.PNG);
    }
    /**
     * Get JPEG image byte array data from BufferedImage
     *
     * @param image  BufferedImage
     */
    public static byte[] getJPEGData(BufferedImage image) throws IOException {
        return getData(image, ImageFormatConverter.ImageFormat.JPEG);
    }


    public static BufferedImage getImage(byte[] data) throws IOException {
        return ImageIO.read(new MemoryCacheImageInputStream(new ByteArrayInputStream(data)));
    }
}
