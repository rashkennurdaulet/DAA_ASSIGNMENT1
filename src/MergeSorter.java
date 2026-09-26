public class MergeSorter {
    private static final int CUTOFF = 16;

    private int maxDepth;
    private long comparisons;

    public void sort(int[] array) {
        maxDepth = 0;
        comparisons = 0;

        if (array == null || array.length <= 1) {
            return;
        }

        int[] aux = new int[array.length];
        mergeSort(array, aux, 0, array.length - 1, 1);
    }

    private void mergeSort(int[] array, int[] aux, int left, int right, int currentDepth) {
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        if (right - left + 1 <= CUTOFF) {
            insertionSort(array, left, right);
            return;
        }

        int mid = left + (right - left) / 2;
        mergeSort(array, aux, left, mid, currentDepth + 1);
        mergeSort(array, aux, mid + 1, right, currentDepth + 1);

        comparisons++;
        if (array[mid] <= array[mid + 1]) {
            return;
        }

        merge(array, aux, left, mid, right);
    }

    private void insertionSort(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= left) {
                comparisons++;
                if (array[j] > key) {
                    array[j + 1] = array[j];
                    j--;
                } else {
                    break;
                }
            }
            array[j + 1] = key;
        }
    }

    private void merge(int[] array, int[] aux, int left, int mid, int right) {
        for (int k = left; k <= right; k++) {
            aux[k] = array[k];
        }

        int i = left;
        int j = mid + 1;

        for (int k = left; k <= right; k++) {
            if (i > mid) {
                array[k] = aux[j++];
            } else if (j > right) {
                array[k] = aux[i++];
            } else {
                comparisons++;
                if (aux[j] < aux[i]) {
                    array[k] = aux[j++];
                } else {
                    array[k] = aux[i++];
                }
            }
        }
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public long getComparisons() {
        return comparisons;
    }
}