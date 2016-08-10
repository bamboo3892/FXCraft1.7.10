package com.okina.fxcraft.main;

public class Main {

	public static void main(String[] args) {
		System.out.println(f(0.2));
	}

	public static double f(double x) {
		for (int i = 0; i < 10000; i++){
			x = 4 * x * (1 - x);
		}
		return x;
	}

}
