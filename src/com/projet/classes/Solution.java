package com.projet.classes;

import java.util.Arrays;

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

	private boolean available;

	public Solution() {
		// TODO Auto-generated constructor stub
	}

	public Solution(int[] solution, double result) {
		super();
		this.solution = solution;
		this.result = result;
	}

	public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public String toString() {
		return "Solution [solution=" + Arrays.toString(solution) + ", result=" + result + "]";
	}
}
