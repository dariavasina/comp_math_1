package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.util.Objects.isNull;

public class SimpleIterationMethod {
    private static int max_iterations = 100;

    public static void main(String[] args) throws SolverException {
        Scanner scanner = new Scanner(System.in);

        int mode;
        do {
            System.out.println("Введите:\n1 - для ввода из консоли, \n2 - для ввода из файла.\n");
            mode = scanner.nextInt();
            scanner.nextLine();
        } while (mode != 1 && mode != 2);


        Matrix b = new Matrix(new double[1][1]);
        Matrix A = new Matrix(new double[1][1]);
        double epsilon = 0;

        if (mode == 1) {

            System.out.print("Введите размерность матрицы: ");
            int n = scanner.nextInt();

            A = Matrix.loadFromConsoleInput(n);


            double[][] b_input = new double[n][1];
            System.out.println("Введите элементы вектора b:");
            for (int i = 0; i < n; i++) {
                try {
                    b_input[i][0] = scanner.nextDouble();
                } catch (InputMismatchException e) {
                    System.out.println("Неправильный ввод. Пожалуйста, введите число.");
                    scanner.nextLine();
                    i--;
                }

            }
            b = new Matrix(b_input);

            while (true) {
                System.out.print("Введите желаемую точность: ");
                try {
                    epsilon = scanner.nextDouble();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Неправильный ввод. Пожалуйста, введите число от 0 до 1.");
                    scanner.nextLine();

                }
            }



        } else {
            System.out.println("Введите путь к файлу: \n");

            String filePath = scanner.nextLine();
            File file = new File(filePath);

            try (Scanner fileScanner = new Scanner(file)) {

                int n = fileScanner.nextInt();
                fileScanner.nextLine();

                double[][] b_input = new double[n][1];

                A = Matrix.loadFromFile(fileScanner, n);

                for (int i = 0; i < n; i++) {
                    b_input[i][0] = fileScanner.nextDouble();
                }
                b = new Matrix(b_input);

                epsilon = fileScanner.nextDouble();

            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден.");
            }
        }

        scanner.close();

        try {
            solve(A, b, epsilon);
        } catch (SolverException e) {
            System.out.println(e.getMessage());
        }


    }

    public static void solve(Matrix A, Matrix b, double epsilon) throws SolverException{
        int n = A.getRows();

        if (Matrix.determinant(A) == 0) {
            throw new SolverException("Matrix's determinant can't be 0!");
        }

        if (!Matrix.checkDiagonalDominance(A)) {
            Matrix newA = Matrix.getDiagonallyDominant(A);
            if (newA == null) {
                throw new SolverException("Матрицу невозможно привести к диагональному виду");

            }
        }


        double[][] x_array = new double[n][1];
        for (int i = 0; i < n; i++) {
            x_array[i][0] = 0;
        }
        Matrix x = new Matrix(x_array);
        Matrix prevX = new Matrix(new double[n][1]);


        double[][] c = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    c[i][j] = -A.getMatrix()[i][j] / A.getMatrix()[i][i];
                } else {
                    c[i][j] = 0;
                }
            }
        }

        Matrix C = new Matrix(c);


        double[][] d_array = new double[n][1];
        for (int i = 0; i < n; i++) {
            d_array[i][0] = b.getMatrix()[i][0] / A.getMatrix()[i][i];
        }
        Matrix d = new Matrix(d_array);

        int final_iterations = 0;

        for (int iterations = 0; iterations < max_iterations; ++iterations) {
            // Сохраняем предыдущее приближение
            prevX = new Matrix(x.getMatrix());
            System.out.println("prevX: " + prevX);

            // x = Cx + d
            x = Matrix.add(Matrix.multiply(C, x), d);
            System.out.println("x: " + x);

            // Проверка сходимости
            double error = calculateError(x, prevX);
            if (error < epsilon) {
                System.out.println("in if");
                System.out.println(x);
                final_iterations = iterations;
                break;
            }
        }

        // Вывод результата
        System.out.println("Вектор неизвестных:");
        for (int i = 0; i < n; i++) {
            System.out.printf("x%d = %.6f%n", i + 1, x.getMatrix()[i][0]);
        }

        System.out.println("Вектор погрешностей: ");
        for (int i = 0; i < n; i++) {
            System.out.println(Math.abs(x.getMatrix()[i][0] -prevX.getMatrix()[i][0]));
        }


        System.out.println("Количество итераций: " + final_iterations);
    }


    public static double calculateError(Matrix x, Matrix prevX) {
        double maxError = 0;
        for (int i = 0; i < x.getRows(); i++) {
            double error = Math.abs(x.getMatrix()[i][0] - prevX.getMatrix()[i][0]);
            if (error > maxError) {
                maxError = error;
            }
        }
        return maxError;
    }

}
