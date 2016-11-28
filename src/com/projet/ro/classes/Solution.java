package com.projet.ro.classes;

import java.util.Arrays;

import com.projet.ro.main.MainJava;

/**
 * @author Jerome
 *
 */
public class Solution {

	private int[] solution;

	private double result;

	private int nbEval;

	public Solution() {
		// TODO Auto-generated constructor stub
	}

	public Solution(int[] solution) {
		super();
		this.nbEval = 0;
		this.setSolution(solution);
	}

	public Solution(int[] solution, int nbEval) {
		super();
		this.nbEval = nbEval;
		this.setSolution(solution);
	}

	public void calc() {
		this.result = MainJava.eval(this.solution);
	}

	public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
		calc();
	}

	public double getResult() {
		return result;
	}

	public void setNbEval(int nbEval) {
		this.nbEval = nbEval;
	}

	public int getNbEval() {
		return nbEval;
	}

	public Solution clone() {
		return new Solution(this.solution, this.nbEval);
	}

	@Override
	public String toString() {
		return "Solution [solution=" + Arrays.toString(solution) + ", result=" + result + "]";
	}
}
