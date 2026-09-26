public class DeterministicSelector {
    private int maxDepth;
    private long comparisons;

    public int select(int[] array, int k) {
        maxDepth = 0;
        comparisons = 0;

        if (array == null || array.length == 0 || k < 0 || k >= array.length) {
            throw new IllegalArgumentException("Invalid array or k");
        }

        return select(array, 0, array.length - 1, k, 1);
    }

    private int select(int[] array, int left, int right, int k, int currentDepth) {
        while (true) {
            if (currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }

            if (left == right) {
                return array[left];
            }

            int pivotIndex = medianOfMedians(array, left, right, currentDepth);
            int pivotValue = array[pivotIndex];

            swap(array, left, pivotIndex);
            int lt = left;
            int gt = right;
            int i = left + 1;

            while (i <= gt) {
                comparisons++;
                if (array[i] < pivotValue) {
                    swap(array, lt, i);
                    lt++;
                    i++;
                } else {
                    comparisons++;
                    if (array[i] > pivotValue) {
                        swap(array, i, gt);
                        gt--;
                    } else {
                        i++;
                    }
                }
            }

            if (k >= lt && k <= gt) {
                return array[k];
            } else if (k < lt) {
                right = lt - 1;
                currentDepth++;
            } else {
                left = gt + 1;
                currentDepth++;
            }
        }
    }

    private int medianOfMedians(int[] array, int left, int right, int currentDepth) {
        int n = right - left + 1;
        if (n <= 5) {
            insertionSort(array, left, right);
            return left + n / 2;
        }

        int numGroups = 0;
        for (int i = left; i <= right; i += 5) {
            int subRight = Math.min(i + 4, right);
            insertionSort(array, i, subRight);
            int medianIdx = i + (subRight - i) / 2;
            swap(array, left + numGroups, medianIdx);
            numGroups++;
        }

        int mid = left + numGroups / 2;
        select(array, left, left + numGroups - 1, mid, currentDepth + 1);
        return mid;
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