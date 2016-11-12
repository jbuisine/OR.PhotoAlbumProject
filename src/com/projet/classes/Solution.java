package com.projet.classes;

import java.util.Arrays;

import com.projet.ro.Main;

/**
 * @author Jerome
 *
 */
/**
 * @author Jerome
 *
 */
public class Solution {

	private int[] solution;

	private double result;

	public Solution() {
		// TODO Auto-generated constructor stub
	}

	public Solution(int[] solution) {
		super();
		this.solution = solution;
		this.result = Main.eval(this.solution);
	}

	public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
		this.result = Main.eval(this.solution);
	}

	public double getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "Solution [solution=" + Arrays.toString(solution) + ", result=" + result + "]";
	}
}
