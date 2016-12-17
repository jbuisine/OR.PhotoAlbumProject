package com.projet.ro.utilities;
/**
 *
 * Minimal examples for the project in JAVA
 *
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author verel
 * @date 2015/11/07
 */
public class ComputeAlbum {
	// Distance between photos
	private static double[][] photoDist;

	// Inverse of the distance between positions in the album
	private static double[][] albumInvDist;

	private static DecimalFormat df = new java.text.DecimalFormat("0.##");

	public static void init(String pathPhoto, String pathAlbum) {
		computeDistances(pathPhoto, pathAlbum);
	}

	/**
	 *
	 * Example of json file parsing
	 *
	 * see: https://code.google.com/p/json-simple/ for more example to decode
	 * json under java
	 *
	 */
	private static void readPhotoExample(String fileName) {
		try {
			FileReader reader = new FileReader(fileName);

			JSONParser parser = new JSONParser();

			// parser the json file
			Object obj = parser.parse(reader);
			// System.out.println(obj);

			// extract the array of image information
			JSONArray array = (JSONArray) obj;
			System.out.println("The first element:\n" + array.get(0));

			JSONObject obj2 = (JSONObject) array.get(0);
			System.out.println("the id of the first element is: " + obj2.get("id"));

			JSONArray arraytag = (JSONArray) ((JSONObject) obj2.get("tags")).get("classes");
			System.out.println("Tag list of the first element:");
			for (int i = 0; i < arraytag.size(); i++)
				System.out.print(" " + arraytag.get(i));
			System.out.println();

		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Compute the matrice of distance between solutions and of inverse distance
	 * between positions
	 */
	private static void computeDistances(String photoFileName, String albumFileName) {
		computePhotoDistances(photoFileName);
		computeAlbumDistances(albumFileName);
	}

	private static void computeAlbumDistances(String fileName) {
		try {
			FileReader reader = new FileReader(fileName);

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);

			JSONObject album = (JSONObject) obj;

			// number of pages
			long nPage = (long) album.get("page");

			// number of photo in each page
			JSONArray pageSize = (JSONArray) album.get("pagesize");

			// number on the first page
			int size = (int) (long) pageSize.get(0);
			// total number of photo in the album
			int nbPhoto = 0;
			for (int i = 0; i < pageSize.size(); i++)
				nbPhoto += (int) (long) pageSize.get(i);

			albumInvDist = new double[nbPhoto][nbPhoto];

			// compute the distance
			for (int i = 0; i < nbPhoto; i++)
				for (int j = 0; j < nbPhoto; j++)
					albumInvDist[i][j] = inverseDistance(size, i, j);

			/*
			 * for(int i = 0; i < albumDist.length; i++) { for(int j = 0; j <
			 * albumDist.length; j++) { System.out.print(" " + albumDist[i][j]);
			 * } System.out.println(); }
			 */

		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static double inverseDistance(int size, int i, int j) {
		// number of pages
		int pagei = i / size;
		int pagej = j / size;

		if (pagei != pagej)
			// not on the same page: distance is infinite. Another choice is
			// possible of course!
			return 0;
		else {
			// positions in the page
			int posi = i % size;
			int posj = j % size;

			// coordinate on the page
			int xi = posi % 2;
			int yi = posi / 2;
			int xj = posj % 2;
			int yj = posj / 2;

			// Manhatthan distance
			return ((double) 1) / (double) (Math.abs(xi - xj) + Math.abs(yi - yj));
		}
	}

	private static void computePhotoDistances(String fileName) {
		try {
			FileReader reader = new FileReader(fileName);

			JSONParser parser = new JSONParser();

			Object obj = parser.parse(reader);

			JSONArray array = (JSONArray) obj;

			photoDist = new double[array.size()][array.size()];

			// distance based on the distance between average hash
			for (int i = 0; i < array.size(); i++) {
				JSONObject image = (JSONObject) array.get(i);
				JSONArray d = (JSONArray) image.get("ahashdist");
				for (int j = 0; j < d.size(); j++) {
					photoDist[i][j] = (double) d.get(j);
				}
			}

			/*
			 * for(int i = 0; i < photoDist.length; i++) { for(int j = 0; j <
			 * photoDist.length; j++) { System.out.print(" " + photoDist[i][j]);
			 * } System.out.println(); }
			 */

		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Un exemple de fonction objectif (à minimiser): distance entre les photos
	 * pondérées par l'inverse des distances spatiales sur l'album Modélisaiton
	 * comme un problème d'assignement quadratique (QAP)
	 *
	 * Dans cette fonction objectif, pas de prise en compte d'un effet de page
	 * (harmonie/cohérence de la page) par le choix de distance, pas
	 * d'intéraction entre les photos sur des différentes pages
	 */
	public static double eval(int[] solution) {
		double sum = 0;

		for (int i = 0; i < albumInvDist.length; i++) {
			for (int j = i + 1; j < albumInvDist.length; j++) {
				sum += photoDist[solution[i]][solution[j]] * albumInvDist[i][j];
			}
		}

		return sum;
	}

	/**
	 * Generate random solution with permutations of int value
	 *
	 * @param number
	 * @return
	 */
	public static int[] generateRandomSolution(int number) {
		Random random = new Random();
		int[] randomArray = new int[number];

		for (int i = 0; i < number; i++) {
			randomArray[i] = i;
		}
		for (int i = 0; i < number; i++) {
			int randomValue = random.nextInt(number);
			int temporyValue = randomArray[i];
			randomArray[i] = randomArray[randomValue];
			randomArray[randomValue] = temporyValue;
		}
		return randomArray;
	}

	/**
	 * Method used to do mutation into elements
	 *
	 * @param number
	 *            with ceil number
	 */
	public static void pertubationIterated(int[] solution, int number, scala.util.Random r) {

		int nbMutations = r.nextInt(number) + 1;
		for (int i = 0; i < nbMutations; i++) {
			int oldValue = 0;
			int firstBoxElement = r.nextInt(solution.length);
			int secondBoxElement = r.nextInt(solution.length);
			oldValue = solution[firstBoxElement];
			solution[firstBoxElement] = solution[secondBoxElement];
			solution[secondBoxElement] = oldValue;
		}
	}

	/**
	 * Method which wrote into the file & generate the order
	 *
	 * @param filename
	 * @param solution
	 */
	public static void writeSolution(String filename, int[] bestSolution, double bestResult, String typeResult) {
		FileClass file = new FileClass(filename);
		String line = "\n\n";
		for (int i = 0; i < bestSolution.length; i++) {
			line += bestSolution[i] + " ";
		}
		file.writeLine(line, false);
		if (typeResult != null) {
			file.writeLine("\n" + typeResult + " : " + bestResult, true);
		}
	}
}