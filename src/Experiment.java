import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Experiment {
    private static final int[] SIZES = {1000, 5000, 10000, 25000, 50000};
    private static final String[] INPUT_TYPES = {"Random", "Sorted", "Reverse-sorted", "Duplicate-heavy"};
    private final Random random = new Random(42);

    public void runAllExperiments(String csvFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            writer.println("Algorithm,InputType,Size,TimeNs,TimeMs,MaxDepth,Comparisons");

            MergeSorter mergeSorter = new MergeSorter();
            QuickSorter quickSorter = new QuickSorter();
            DeterministicSelector selector = new DeterministicSelector();
            ClosestPairSolver closestSolver = new ClosestPairSolver();

            for (int size : SIZES) {
                for (String type : INPUT_TYPES) {
                    int[] baseArray = generateArray(size, type);

                    int[] arrMerge = baseArray.clone();
                    long startMerge = System.nanoTime();
                    mergeSorter.sort(arrMerge);
                    long timeMerge = System.nanoTime() - startMerge;
                    writeRow(writer, "MergeSort", type, size, timeMerge, mergeSorter.getMaxDepth(), mergeSorter.getComparisons());

                    int[] arrQuick = baseArray.clone();
                    long startQuick = System.nanoTime();
                    quickSorter.sort(arrQuick);
                    long timeQuick = System.nanoTime() - startQuick;
                    writeRow(writer, "QuickSort", type, size, timeQuick, quickSorter.getMaxDepth(), quickSorter.getComparisons());

                    int[] arrSelect = baseArray.clone();
                    int k = size / 2;
                    long startSelect = System.nanoTime();
                    selector.select(arrSelect, k);
                    long timeSelect = System.nanoTime() - startSelect;
                    writeRow(writer, "DeterministicSelect", type, size, timeSelect, selector.getMaxDepth(), selector.getComparisons());
                }

                Point[] points = generatePoints(size);
                long startClosest = System.nanoTime();
                closestSolver.findClosestPair(points);
                long timeClosest = System.nanoTime() - startClosest;
                writeRow(writer, "ClosestPair", "Random", size, timeClosest, closestSolver.getMaxDepth(), closestSolver.getComparisons());
            }

            System.out.println("Experiments completed. Results saved to " + csvFilePath);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    private void writeRow(PrintWriter writer, String algo, String type, int size, long timeNs, int depth, long comparisons) {
        double timeMs = timeNs / 1_000_000.0;
        writer.printf(java.util.Locale.US, "%s,%s,%d,%d,%.4f,%d,%d%n",
                algo, type, size, timeNs, timeMs, depth, comparisons);
        System.out.printf(java.util.Locale.US, "%-20s | %-15s | n=%-6d | time=%.3f ms | depth=%-4d | comps=%d%n",
                algo, type, size, timeMs, depth, comparisons);
    }

    private int[] generateArray(int size, String type) {
        int[] arr = new int[size];
        switch (type) {
            case "Sorted":
                for (int i = 0; i < size; i++) {
                    arr[i] = i;
                }
                break;
            case "Reverse-sorted":
                for (int i = 0; i < size; i++) {
                    arr[i] = size - i;
                }
                break;
            case "Duplicate-heavy":
                for (int i = 0; i < size; i++) {
                    arr[i] = random.nextInt(10);
                }
                break;
            default:
                for (int i = 0; i < size; i++) {
                    arr[i] = random.nextInt(size * 10);
                }
                break;
        }
        return arr;
    }

    private Point[] generatePoints(int size) {
        Point[] points = new Point[size];
        for (int i = 0; i < size; i++) {
            points[i] = new Point(random.nextDouble() * 10000, random.nextDouble() * 10000);
        }
        return points;
    }
}