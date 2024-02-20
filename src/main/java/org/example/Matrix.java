package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Matrix {
    private final int cols;
    private final int rows;
    private double[][] matrix;

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.cols = matrix[0].length;
    }

    public static Matrix loadFromFile(Scanner scanner, int n) {
        Matrix matrix = new Matrix(new double[n][n]);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix.matrix[i][j] = scanner.nextDouble();
            }
        }

        return matrix;
    }


    public static Matrix loadFromConsoleInput(int dimension) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите коэффициенты матрицы построчно:");
        double[][] matrixArray = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            System.out.println("Строка " + (i + 1) + ":");
            boolean validInput = false;
            while (!validInput) {
                try {
                    for (int j = 0; j < dimension; j++) {
                        matrixArray[i][j] = scanner.nextDouble();
                    }
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Неправильный ввод. Пожалуйста, введите строку снова:");
                    scanner.nextLine();
                }
            }
        }

        return new Matrix(matrixArray);
    }

    public double[][] copy() {
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
        }
        return copy;
    }

    static boolean checkDiagonalDominance(Matrix A) throws SolverException {
        int n = A.rows;
        if (n != A.cols) {
            return false;
        }
        double[][] a = A.getMatrix();
        for (int i = 0; i < n; i++) {
            double diagonalElement = Math.abs(a[i][i]);
            double sum = 0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    sum += Math.abs(a[i][j]);
                }
            }
            if (diagonalElement <= sum) {
                return false;
            }
        }
        return true;
    }

    static Matrix getDiagonallyDominantGreedyApproach(Matrix A) {
        double[][] a = A.copy();
        int n = A.getRows();
        for (int i = 0; i < n; i++) {
            // Calculate the sum of absolute values of elements in the row except the diagonal
            double rowSum = 0.0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    rowSum += Math.abs(a[i][j]);
                }
            }

            // If the absolute value of the diagonal element is less than or equal to the row sum
            if (Math.abs(a[i][i]) < rowSum) {
                // Find the column index with the maximum absolute value
                int maxColIndex = 0;
                double maxAbsValue = Math.abs(a[i][0]);
                for (int j = 1; j < n; j++) {
                    if (Math.abs(a[i][j]) > maxAbsValue) {
                        maxAbsValue = Math.abs(a[i][j]);
                        maxColIndex = j;
                    }
                }

                // Swap the elements in the current column and the column with the maximum absolute value
                for (int k = 0; k < n; k++) {
                    double temp = a[i][k];
                    a[i][k] = a[maxColIndex][k];
                    a[maxColIndex][k] = temp;
                }
            }
        }

        return new Matrix(a);
    }

    static void permute(List<Integer> arr, int k, List<List<Integer>> result) {
        for (int i = k; i < arr.size(); i++) {
            java.util.Collections.swap(arr, i, k);
            permute(arr, k + 1, result);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() - 1) {
            result.add(new ArrayList<>(arr));
        }
    }

    static Matrix getDiagonallyDominant(Matrix A) throws SolverException {
        Matrix diagonallyDominantA = getDiagonallyDominantGreedyApproach(A);
        if (checkDiagonalDominance(diagonallyDominantA)) {
            return diagonallyDominantA;
        }

        if (A.cols > 10) {
            return null;
        }

        // try brute force
        double[][] a = A.copy();
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < A.cols; i++) {
            permutation.add(i);
        }
        List<List<Integer>> permutations = new ArrayList<>();
        permute(permutation, 0, permutations);
        for (List<Integer> p : permutations) {
            // rearrange the columns
            double[][] newA = new double[a.length][a[0].length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    newA[i][j] = a[i][p.get(j)];
                }
            }
            Matrix newMatrix = new Matrix(newA);
            if (checkDiagonalDominance(newMatrix)) {
                return newMatrix;
            }
        }

        // matrix is not diagonally dominant after all
        return null;
    }

    public static Matrix multiply(Matrix matrixA, Matrix matrixB) {
        double[][] a = matrixA.getMatrix();
        double[][] b = matrixB.getMatrix();

        int rowsA = a.length;
        int colsA = a[0].length;
        int rowsB = b.length;
        int colsB = b[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("The number of columns in the first matrix must equal the number of rows in the second matrix.");
        }

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return new Matrix(result);
    }

    public static double determinant(Matrix A) {
        int n = A.rows;

        if (n != A.cols) {
            throw new IllegalArgumentException("The matrix must be square.");
        }

        double det = 1;
        double[][] a = A.getMatrix();

        for (int i = 0; i < n; i++) {
            int maxRow = i;

            for (int j = i + 1; j < n; j++) {
                if (Math.abs(a[j][i]) > Math.abs(a[maxRow][i])) {
                    maxRow = j;
                }
            }

            if (maxRow != i) {
                double[] temp = a[i];
                a[i] = a[maxRow];
                a[maxRow] = temp;

                det *= -1;
            }

            if (a[i][i] == 0) {
                return 0;
            }

            det *= a[i][i];

            for (int j = i + 1; j < n; j++) {
                double factor = a[j][i] / a[i][i];
                for (int k = i; k < n; k++) {
                    a[j][k] -= a[i][k] * factor;
                }
            }
        }

        return det;
    }

    public static Matrix add(Matrix matrixA, Matrix matrixB) {
        int rowsA = matrixA.getRows();
        int colsA = matrixA.getCols();
        int rowsB = matrixB.getRows();
        int colsB = matrixB.getCols();


        if (rowsA != rowsB || colsA != colsB) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        double[][] result = new double[rowsA][colsA];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                result[i][j] = matrixA.getMatrix()[i][j] + matrixB.getMatrix()[i][j];
            }
        }

        return new Matrix(result);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(matrix[i][j]).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }





}
