package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;

import java.util.concurrent.CopyOnWriteArraySet;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int lowerBound = 0;
        int upperBound = 1000;

        System.out.print("Введіть довжину масиву (від 40 до 60): ");
        int arraySize = scanner.nextInt();

        if (arraySize < 40 || arraySize > 60) {
            System.out.println("Довжина масиву повинна бути між 40 і 60. Завершення програми.");
            return;
        }

        int[] array = new int[arraySize];
        Random random = new Random();
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
        }

        List<Future<Double>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Set<Double> results = new CopyOnWriteArraySet<>();

        long startTime = System.currentTimeMillis();

        int partSize = 20;
        for (int i = 0; i < array.length; i += partSize) {
            int start = i;
            int end = Math.min(i + partSize, array.length);

            Callable<Double> task = () -> {
                double sum = 0;
                for (int j = start; j < end; j++) {
                    sum += array[j];
                }
                double average = sum / (end - start);
                results.add(average);
                return average;
            };

            futures.add(executor.submit(task));
        }

        try {
            for (Future<Double> future : futures) {
                if (future.isCancelled()) {
                    System.out.println("Завдання було скасовано.");
                } else if (future.isDone()) {
                    System.out.println("Середнє значення частини: " + future.get());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        double totalSum = 0;
        for (Double avg : results) {
            totalSum += avg;
        }
        double totalAverage = totalSum / results.size();
        System.out.println("Загальне середнє значення масиву: " + totalAverage);

        long endTime = System.currentTimeMillis();
        System.out.println("Час виконання програми: " + (endTime - startTime) + " ms");

        executor.shutdown();
        scanner.close();
    }
}
