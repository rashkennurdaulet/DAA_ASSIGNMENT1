import java.util.Random;

public class QuickSorter {
    private final Random random = new Random();
    private int maxDepth;
    private long comparisons;

    public void sort(int[] array) {
        maxDepth = 0;
        comparisons = 0;

        if (array == null || array.length <= 1) {
            return;
        }

        quickSort(array, 0, array.length - 1, 1);
    }

    private void quickSort(int[] array, int left, int right, int currentDepth) {
        while (left < right) {
            if (currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }

            int randomIndex = left + random.nextInt(right - left + 1);
            swap(array, left, randomIndex);
            int pivot = array[left];

            int lt = left;
            int gt = right;
            int i = left + 1;

            while (i <= gt) {
                comparisons++;
                if (array[i] < pivot) {
                    swap(array, lt, i);
                    lt++;
                    i++;
                } else {
                    comparisons++;
                    if (array[i] > pivot) {
                        swap(array, i, gt);
                        gt--;
                    } else {
                        i++;
                    }
                }
            }

            int leftSize = lt - left;
            int rightSize = right - gt;

            if (leftSize < rightSize) {
                quickSort(array, left, lt - 1, currentDepth + 1);
                left = gt + 1;
            } else {
                quickSort(array, gt + 1, right, currentDepth + 1);
                right = lt - 1;
            }
        }
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public long getComparisons() {
        return comparisons;
    }
}