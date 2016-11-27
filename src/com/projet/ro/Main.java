
/**
 *
 * Minimal examples for the project in JAVA
 *
 */
package com.projet.ro;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.projet.classes.Solution;
import com.projet.ro.utilities.FileClass;

/**
 * @author verel
 * @date 2015/11/07
 */
public class Main {
	// Distance between photos
	public static double[][] photoDist;

	// Inverse of the distance between positions in the album
	public static double[][] albumInvDist;

	private static DecimalFormat df = new java.text.DecimalFormat("0.##");

	/**
	 *
	 * Example of json file parsing
	 *
	 * see: https://code.google.com/p/json-simple/ for more example to decode
	 * json under java
	 *
	 */
	public static void readPhotoExample(String fileName) {
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
	public static void computeDistances(String photoFileName, String albumFileName) {
		computePhotoDistances(photoFileName);
		computeAlbumDistances(albumFileName);
	}

	public static void computeAlbumDistances(String fileName) {
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

	public static double inverseDistance(int size, int i, int j) {
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

	public static void computePhotoDistances(String fileName) {
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
	static int[] generateRandomSolution(int number) {
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
	 * Hill climber first improvement heuristic
	 *
	 * @param numberElements
	 * @return the best result found
	 */
	public static int[] hillClimberFirstImprovment(int numberElements, int iteration, int[] solution) {

		if (solution == null)
			solution = generateRandomSolution(numberElements);

		double bestResult = eval(solution);

		Random random = new Random();

		boolean next = true;
		int i = 0;

		do {
			double result = 0.0;
			int firstRandomValue = 0;
			int secondRandomValue = 0;
			int temporyValue = 0;

			int j = 0;
			for (j = 0; j < numberElements; j++) {
				firstRandomValue = random.nextInt(solution.length);
				secondRandomValue = random.nextInt(solution.length);

				temporyValue = solution[firstRandomValue];
				solution[firstRandomValue] = solution[secondRandomValue];
				solution[secondRandomValue] = temporyValue;

				result = eval(solution);

				if (result < bestResult) {
					break;
				}

				solution[secondRandomValue] = solution[firstRandomValue];
				solution[firstRandomValue] = temporyValue;
			}

			if (result < bestResult) {
				bestResult = result;
			} else
				next = false;

			i++;
		} while (next && i < iteration);
		return solution;
	}

	/**
	 * Method which used the iteratedLocalSearch solution to find a solution
	 *
	 * @param numberElements
	 * @param iteration
	 * @param perturbation
	 * @return the best solution
	 */
	public static int[] iteratedLocalSearch(int numberElements, int iteration, int iterationHillClimber,
			int perturbation) {

		Random random = new Random();
		int[] solution = generateRandomSolution(numberElements);

		hillClimberFirstImprovment(numberElements, iterationHillClimber, solution);
		double bestResult = eval(solution);
		int i = 0;
		do {
			pertubationIterated(solution, perturbation, random);

			int[] currentSolution = hillClimberFirstImprovment(numberElements, iterationHillClimber, solution.clone());

			double currentResult = eval(currentSolution);
			if (currentResult < bestResult) {
				solution = currentSolution;
			}
			i++;
			// System.out.println("Iterated local search : " + df.format(i *
			// 100.0 / iteration) + "%");
		} while (i < iteration);
		return solution;
	}

	/**
	 * Method which used the genetic evolutionary algorithm
	 *
	 * @param mu
	 * @param lambda
	 * @param numberElements
	 * @param iteration
	 * @param hillClimberIteration
	 * @param numberOfPermutations
	 * @return best solution object found
	 */
	public static int[] geneticEvolutionaryAlgorithm(int mu, int lambda, int numberElements, int iteration,
			int hillClimberIteration, int numberOfHC, int numberOfPermutations) {

		// Generate all parents solutions to start the algorithm
		ArrayList<int[]> parentsSolutions = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < mu; i++) {
			int[] sol = generateRandomSolution(numberElements);
			parentsSolutions.add(sol);
		}

		// Loop which defined the stop search (Iteration number)
		for (int i = 0; i < iteration; i++) {

			ArrayList<int[]> genitorsSolutions = new ArrayList<>();
			// Parents selection which generated "genitors" after fights
			for (int j = 0; j < lambda; j++) {

				int firstSelectedIndex = random.nextInt(parentsSolutions.size());
				int secondSelectedIndex = random.nextInt(parentsSolutions.size());

				// Do fight between 2 solutions and get the winner
				if (eval(parentsSolutions.get(firstSelectedIndex)) >= eval(parentsSolutions.get(secondSelectedIndex))) {
					genitorsSolutions.add(parentsSolutions.get(firstSelectedIndex));
				} else {
					genitorsSolutions.add(parentsSolutions.get(secondSelectedIndex));
				}
			}

			// Do variations on Genitors like mutation & HC
			// Mutation needs make probability
			for (int j = 0; j < genitorsSolutions.size(); j++) {

				// Do permutation
				pertubationIterated(genitorsSolutions.get(j), numberOfPermutations, random);

				// Make hill climber on the current solution to improve the
				// genitor solution
				for (int k = 0; k < numberOfHC; k++) {
					int[] currentSolution = hillClimberFirstImprovment(genitorsSolutions.get(j).length, 1000,
							genitorsSolutions.get(j).clone());

					if (eval(currentSolution) < eval(genitorsSolutions.get(j))) {
						genitorsSolutions.set(j, currentSolution);
					}
				}
			}

			// Get the best between old parents & Genitors to make Survivors

			// First of all we need to add all children
			for (int j = 0; j < lambda; j++) {
				parentsSolutions.add(genitorsSolutions.get(j));
			}

			// Method used to order list of solution by result
			orderListOfSolution(parentsSolutions);

			// Remove all elements without good result for the next step
			for (int j = mu; j < parentsSolutions.size(); j++) {
				parentsSolutions.remove(j);
			}
			System.out.println("Genetic evolutionary algorithm : " + df.format(i * 100.0 / iteration) + "%");
		}
		// Get the best solution after all iteration and return it
		return parentsSolutions.get(0);
	}

	/**
	 * Method used to do mutation into elements
	 *
	 * @param number
	 *            with ceil number
	 */
	private static void pertubationIterated(int[] solution, int number, Random r) {

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
	 * Utility method used to order list by result of solutions
	 *
	 * @param parentsSolutions
	 */
	public static void orderListOfSolution(ArrayList<int[]> parentsSolutions) {
		Collections.sort(parentsSolutions, new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				Double libelle1 = eval(o1);
				Double libelle2 = eval(o2);
				if (libelle2.compareTo(libelle1) > 0)
					return -1;
				else if (libelle2.compareTo(libelle1) == 0)
					return 0;
				else
					return 1;
			}
		});
	}

	/**
	 * Show the solution found
	 *
	 * @param sol
	 */
	public static void showSolution(Solution sol) {
		for (int i = 0; i < sol.getSolution().length; i++) {
			System.out.print("[" + sol.getSolution()[i] + "] ");
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Path to the photo information file in json format
		String photoFileName = "/home/pi/www/projets/MasterI2L/ProjetROAlbumPhoto/data/info-photo.json";
		// Path to the album information file in json format
		String albumFileName = "/home/pi/www/projets/MasterI2L/ProjetROAlbumPhoto/data/info-album.json";

		// uncomment to test it
		// readPhotoExample(photoFileName);
		computeDistances(photoFileName, albumFileName);

		// one basic solution : order of the index
		int numberOfPhoto = 55;

		String rootSolutionFile = "/home/pi/www/projets/MasterI2L/ProjetROAlbumPhoto/fichier.sol";
		switch (args[0]) {

		/***************************************************************************
		 *************************** HILL CLIMBER RESULT ***************************
		 **************************************************************************/
		case "HC":
			System.out.println("Beginning of hill climber solution :");
			double bestResult = 1000;
			int[] bestSolution = null;
			int[] currentSolution = null;
			for (int i = 0; i < 10000; i++) {
				currentSolution = hillClimberFirstImprovment(numberOfPhoto, 10000, null);
				double currentResult = eval(currentSolution);
				if (currentResult < bestResult) {
					bestResult = currentResult;
					bestSolution = currentSolution;
				}
				System.out.println("HC : " + df.format(i * 100.0 / 10000.0) + "%");
			}
			writeSolution(rootSolutionFile, bestSolution, bestResult, "HC");

			break;

		/***************************************************************************
		 ************************** ITERATED LOCAL SEARCH **************************
		 **************************************************************************/
		case "ILC":
			int[] iteradtedLocalSearchSolution = iteratedLocalSearch(numberOfPhoto, 10000, 1000, 10);
			double result = eval(iteradtedLocalSearchSolution);
			System.out.println("Result ILS : " + result);
			writeSolution(rootSolutionFile, iteradtedLocalSearchSolution, result, "ILS");
			break;

		/***************************************************************************
		 ************************* EVOLUTIONARY ALGORIMTH **************************
		 **************************************************************************/
		case "EA":

			int[] geneticEvolutionSolution = geneticEvolutionaryAlgorithm(50, 25, numberOfPhoto, 1000, 1000, 100, 10);
			double resultEvolutionSolution = eval(geneticEvolutionSolution);
			System.out.println("Result EA : " + resultEvolutionSolution);
			writeSolution(rootSolutionFile, geneticEvolutionSolution, resultEvolutionSolution, "EA");
			break;
		default:
			break;
		}

	}
}