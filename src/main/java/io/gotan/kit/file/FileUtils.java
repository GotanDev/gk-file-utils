package io.gotan.kit.file;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

/** Classe utilitaire pour récupérer des meta infos sur les fichiers
 * 
 * @author Damien Cuvillier <damien@gotan.io>
 * @since 10 avr. 2013
 *
 */
public class FileUtils {


	private static final Logger logger = LogManager.getLogger();


	/** Récupère l'extension d'un fichier
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName){
		String extension = "";
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	/** Récupère le nom de fichier sans son extension.
	 * @param name Nom de fichier
	 * @return Nom 
	 */
	public static String getFilenameWithoutExtension(String name) {
		String filename = "";
		int i = name.lastIndexOf('.');
		int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
		if (i > p) {
			filename = name.substring(p+1,i);
		}else{
			filename = name;
		}
		return filename;
	}
	
	/** Récupère le contenu d'un fichier
	 * @param file Nom de fichier
	 * @return Nom 
	 * @throws IOException 
	 */
	public static String getFileContentToString(File file) throws IOException{
		String result = "", ligne;
		InputStream ips = new FileInputStream(file);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);

		while ((ligne = br.readLine()) != null) {
			result += ligne + "\n";
		}
		br.close();
		return result;
	} 
	/**
	 * Convertit un fichier en une liste de Tokens (jetons)
	 * 
	 * @param file String à convertir
	 * @return Liste de string contenant es tokens
	 */
	public static List<String> fileToTokens(final File file) throws IOException {
		String string = getFileContentToString(file);
		StringTokenizer token = new StringTokenizer(string);
		List<String> liste = new ArrayList<String>();

		while (token.hasMoreTokens()) {
			liste.add(token.nextToken());
		}
		return liste;
	}

	public static File getFileFromInputStream(InputStream inputStream) {
		File file = null;
		OutputStream outputStream = null;
		try {
			file = File.createTempFile("tmp", ".tmp");
			outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException e) {
			logger.fatal(e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.fatal(e.getMessage());
				}
			}
		}
		return file;
	}

	/**
	 * Récupère un fichier (texte) à partir d'une chaine de caractères.
	 * L'ensemble du contenu sera placé dans le fichier. 
	 * 
	 * @param text texte à ecrire dans le fichier
	 * @param outputFilePath nom du fichier de sortie
	 * @return file contenant le texte
	 */
	public static File getFileFromString(String text, String outputFilePath){
		File file = new File(outputFilePath);
		 BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(file));
	        output.write(text);
	        output.close();
		} catch (IOException e) {
			logger.fatal(e.getMessage());
		}
        return file;
	}
	
	/** Transforme un fichier en base64
	 * 
	 * @param inputFile Fichier à ouvrir
	 * @return Chaine encodée
	 * @throws IOException Problème à l'ouverture
	 */
	public static String encodeToBase64(final File inputFile) throws IOException{
	  if (inputFile == null) {
        return null;
      }
	  try (FileInputStream fileInputStreamReader = new FileInputStream(inputFile)) {
        byte[] bytes = new byte[(int)inputFile.length()];
        fileInputStreamReader.read(bytes);
        return Base64.getEncoder().encodeToString(bytes);
	  } catch (IOException e) {
	    logger.warn(String.format("Unable to read file %s content: %s", inputFile.getAbsolutePath(), e.getMessage()));
	    throw e;
	  }
	}
	
	/** Transforme une base64 en fichier
     * 
     * @param inputString Chaine encodée
     * @param fileExtension
     * @return Fichier de sortie
     * @throws IOException Problème à l'ouverture
     */
    public static File decodeBase64(final String inputString, final String  fileExtension) throws IOException{
      if (inputString == null) {
        return null;
      }
      File outputFile = File.createTempFile("tmp", fileExtension);
      return decodeBase64(inputString, outputFile);
    }
    /** Transforme une base64 en fichier
     * 
     * @param inputString Chaine encodée
     * @param outputFile fichier de sortie
     * @return Fichier de sortie
     * @throws IOException Problème à l'ouverture
     */
    public static File decodeBase64(final String inputString, final File outputFile) throws IOException{
      if (inputString == null) {
        return null;
      }
      try (FileOutputStream fileOuputWriter = new FileOutputStream(outputFile)) {
        fileOuputWriter.write(Base64.getDecoder().decode(inputString));
        return outputFile;
      } catch(IOException e) {
        logger.warn("Unable to write to temporary file from base64 string due to: " + e.getMessage());
        throw e;
      }
    }
}
