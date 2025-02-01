package io.gotan.kit.file;


import io.gotan.kit.image.ImageFormatConverter;

public class Base64Utils {

  /*
   * Prefixe situé entre le mime type et le contenu Base64
   * 
   */
  public final static String BASE64_MIDDLE_PREFIX = ";base64,";
  /** Prefix au démarrage d'une URL BASE64. */
  public final static String BASE64_URL_PREFIX = "data:";

  /**
   * Récupère une image en base64 sans le header
   * 
   * @param base64Input Base avec header
   * @return Base64 sans header
   */
  public static String trimBase64Header(final String base64Input) {
    if (base64Input.startsWith(BASE64_URL_PREFIX)){
      return base64Input.substring(base64Input.indexOf(BASE64_MIDDLE_PREFIX) + BASE64_MIDDLE_PREFIX.length());
    } else {
      return base64Input;
    }
  }

  /**
   * Récupère le header d'une URL base64
   *
   * @param base64Input Base64 avec header
   * @return header
   * @throws ImageFormatConverter.BadImageFormatException
   */
  public static String getMimeTypeFromHeader(final String base64Input) throws ImageFormatConverter.Base64ImageFormatException {
    if (base64Input.startsWith(BASE64_URL_PREFIX)) {
      return base64Input.substring(BASE64_URL_PREFIX.length(), base64Input.indexOf(BASE64_MIDDLE_PREFIX));
    } else {
      throw new ImageFormatConverter.Base64ImageFormatException();
    }
  }
  
  /** Récupère le prefix complet d'une URL base64
   * 
   * @param mimeType Mime Type retourné
   * @return
   */
  public static String getFullBase64Prefix(String mimeType) {
    return BASE64_URL_PREFIX + mimeType + BASE64_MIDDLE_PREFIX;
  }
}
