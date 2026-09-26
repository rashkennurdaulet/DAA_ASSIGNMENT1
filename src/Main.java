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
    }
}