package org.example;

import java.util.Arrays;

public class Solution {
    private final double[] result;
    private final long iterations;
    private final double[] errors;

    public Solution(double[] result, long iterations, double[] error) {
        this.result = result;
        this.iterations = iterations;
        this.errors = error;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Amount of iterations -> ").append(iterations).append("\n");
        stringBuilder.append("Result: ").append(Arrays.toString(result)).append("\n");
        stringBuilder.append("Errors: ").append( Arrays.toString(errors)).append("\n");
        return stringBuilder.toString();
    }

}