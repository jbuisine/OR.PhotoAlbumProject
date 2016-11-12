package com.projet.ro.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jerome
 *
 *         Class which implements the File class of java
 */
public class FileClass {

	private File fichier;
	private BufferedReader fichierReader;

	/**
	 * @param nom
	 * 
	 *            Default constructor with file name in parameter
	 */
	public FileClass(String nom) {
		fichier = new File(nom);
		this.open();
		// if (!fileExist()) {
		// writeLine("", true);
		// }
		try {
			fichierReader = new BufferedReader(new FileReader(fichier));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Used to give rights to open the file
	 */
	private void open() {
		fichier.setExecutable(true);
		fichier.setReadable(true);
		fichier.setWritable(true);
	}

	/**
	 * Return the next line of the file
	 * 
	 * @return the line String value
	 */
	public String nextLine() {
		String value = "";
		try {
			value = fichierReader.readLine();
		} catch (IOException e) {
			value = null;
		}
		return value;
	}

	/**
	 * Disabled access to this file
	 */
	public void close() {
		fichier.setExecutable(false);
		fichier.setReadable(false);
		fichier.setWritable(false);
	}

	/**
	 * Method used to add text to a file
	 * 
	 * @param pVal
	 *            String line
	 * @param b
	 *            used to precise if you want to right after the existing text
	 *            or not
	 */
	public void writeLine(String pVal, boolean b) {
		try {
			BufferedWriter fichierWriter = new BufferedWriter(new FileWriter(fichier, b));
			fichierWriter.write(pVal);
			fichierWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method which tell if the line exists or not
	 * 
	 * @param pVal
	 *            : String line value
	 * @return TRUE or FALSE if the line exists
	 */
	public boolean findLine(String pVal) {
		boolean check = false;
		try {
			BufferedReader fichierReader = new BufferedReader(new FileReader(fichier));

			while ((fichierReader.readLine()) != null && fichierReader.ready()) {

				if (pVal.equals(fichierReader.readLine().toString()))
					check = true;
			}
			fichierReader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}

	/**
	 * @return TRUE if the file exists
	 */
	public boolean fileExist() {
		return fichier.exists();
	}

	public BufferedReader getFichierReader() {
		return fichierReader;
	}

	public void setFichierReader(BufferedReader fichierReader) {
		this.fichierReader = fichierReader;
	}
}