import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] arr1 = {34, 7, 23, 32, 5, 62, 32, 2, 78, 1, 45, 90, 12, 19, 8, 55, 41, 3};
        int[] arr2 = arr1.clone();
        int[] arr3 = arr1.clone();

        System.out.println("Original: " + Arrays.toString(arr1));

        MergeSorter mergeSorter = new MergeSorter();
        mergeSorter.sort(arr1);
        System.out.println("MergeSort: " + Arrays.toString(arr1));
        System.out.println("MergeSort depth: " + mergeSorter.getMaxDepth() + ", comparisons: " + mergeSorter.getComparisons());

        QuickSorter quickSorter = new QuickSorter();
        quickSorter.sort(arr2);
        System.out.println("QuickSort: " + Arrays.toString(arr2));
        System.out.println("QuickSort depth: " + quickSorter.getMaxDepth() + ", comparisons: " + quickSorter.getComparisons());

        DeterministicSelector selector = new DeterministicSelector();
        int k = 5;
        int kthElement = selector.select(arr3, k);
        System.out.println("Select k=" + k + ": " + kthElement + " (Expected: " + arr1[k] + ")");
        System.out.println("Select depth: " + selector.getMaxDepth() + ", comparisons: " + selector.getComparisons());

        Point[] points = {
                new Point(2, 3),
                new Point(12, 30),
                new Point(40, 50),
                new Point(5, 1),
                new Point(12, 10),
                new Point(3, 4),
                new Point(7, 8),
                new Point(1, 9)
        };

        ClosestPairSolver closestSolver = new ClosestPairSolver();
        double minDist = closestSolver.findClosestPair(points);
        double expectedDist = closestSolver.bruteForce(points);
        System.out.println("Closest Pair distance: " + minDist + " (Expected: " + expectedDist + ")");
        System.out.println("Closest Pair depth: " + closestSolver.getMaxDepth() + ", comparisons: " + closestSolver.getComparisons());

        System.out.println("\n--- Running Performance Experiments ---");
        Experiment experiment = new Experiment();
        experiment.runAllExperiments("results/results.csv");
    }

}