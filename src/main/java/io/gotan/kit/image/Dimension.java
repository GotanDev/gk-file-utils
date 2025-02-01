package io.gotan.kit.image;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Dimension implements Serializable {
    /**
     * Largeur de l'élément.
     */
    private int width;
    /**
     * Hauteur en pixels de l'élément.
     */
    private int height;

    public Dimension(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }
    public Dimension(Rectangle rect) {
        super();
        this.width = (int) rect.getWidth();
        this.height = (int) rect.getHeight();
    }

    public Dimension() {
        super();
    }

    public Dimension(java.awt.Dimension pageSize) {
        this.width = pageSize.width;
        this.height = pageSize.height;
    }

    /**
     * Redimensionne avec le facteur indiqué
     *
     * @param factor
     */
    public Dimension scale(float factor) {
        width = Math.round(width * factor);
        height = Math.round(height * factor);
        return this;
    }

    /**
     * Redimensionne un élément avec une largeur et une hauteur maximum
     *
     * @param factor    Le facteur de redimensionnement
     * @param maxWidth  Largeur maximum. 0 ou négatif pour ne pas utiliser de
     *                  maximum.
     * @param maxHeight Hauteur maximum 0 ou négatif pour ne pas utiliser de
     *                  maximum.
     */
    public Dimension scale(final float factor, int maxWidth, int maxHeight) {
        float newFactor = factor;
        if (width * factor > maxWidth || height * factor > maxHeight) {
            newFactor = Math.min(
                    maxHeight > 0 ? maxHeight / (float) height : factor,
                    maxWidth > 0 ? maxWidth / (float) width : factor
            );
        }
        width = Math.round(width * newFactor);
        height = Math.round(height * newFactor);
        return this;
    }

    /**
     * Redimensionne un élément avec une largeur et une hauteur maximum
     *
     * @param maxWidth  Largeur maximum. 0 ou négatif pour ne pas utiliser de
     *                  maximum.
     * @param maxHeight Hauteur maximum 0 ou négatif pour ne pas utiliser de
     *                  maximum.
     */
    public Dimension scale(int maxWidth, int maxHeight){
        return this.scale(1, maxWidth, maxHeight);
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    public java.awt.Dimension toDimension() {
        return new java.awt.Dimension(width, height);
    }
}
