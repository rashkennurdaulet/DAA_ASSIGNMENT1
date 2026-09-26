import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmsTest {

    @Test
    public void testMergeSortAndQuickSort() {
        MergeSorter mergeSorter = new MergeSorter();
        QuickSorter quickSorter = new QuickSorter();
        Random random = new Random(123);

        int[][] testCases = {
                {},
                {42},
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                {5, 2, 5, 2, 5, 2, 5, 5, 2, 1, 1},
                random.ints(500, -1000, 1000).toArray(),
                random.ints(2000, 0, 10).toArray()
        };

        for (int[] original : testCases) {
            int[] expected = original.clone();
            int[] arrMerge = original.clone();
            int[] arrQuick = original.clone();

            Arrays.sort(expected);
            mergeSorter.sort(arrMerge);
            quickSorter.sort(arrQuick);

            assertArrayEquals(expected, arrMerge);
            assertArrayEquals(expected, arrQuick);
        }
    }

    @Test
    public void testDeterministicSelect100RandomRuns() {
        DeterministicSelector selector = new DeterministicSelector();
        Random random = new Random(456);

        for (int run = 0; run < 100; run++) {
            int size = 50 + random.nextInt(300);
            int[] arr = random.ints(size, -500, 500).toArray();
            int[] sorted = arr.clone();
            Arrays.sort(sorted);

            int k = random.nextInt(size);
            int actual = selector.select(arr, k);

            assertEquals(sorted[k], actual);
        }
    }

    @Test
    public void testClosestPairAgainstBruteForce() {
        ClosestPairSolver solver = new ClosestPairSolver();
        Random random = new Random(789);

        int[] sizes = {10, 100, 500, 1500};
        for (int n : sizes) {
            Point[] points = new Point[n];
            for (int i = 0; i < n; i++) {
                points[i] = new Point(random.nextDouble() * 1000, random.nextDouble() * 1000);
            }

            double fastResult = solver.findClosestPair(points);
            double bruteResult = solver.bruteForce(points);

            assertEquals(bruteResult, fastResult, 1e-9);
        }
    }
}