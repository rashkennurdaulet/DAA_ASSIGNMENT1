import java.util.Arrays;

public class ClosestPairSolver {
    private int maxDepth;
    private long comparisons;

    public double findClosestPair(Point[] points) {
        maxDepth = 0;
        comparisons = 0;

        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }

        Point[] pts = points.clone();
        Arrays.sort(pts, (p1, p2) -> Double.compare(p1.x, p2.x));

        Point[] aux = new Point[pts.length];
        return closestUtil(pts, aux, 0, pts.length - 1, 1);
    }

    private double closestUtil(Point[] pts, Point[] aux, int left, int right, int currentDepth) {
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        if (right - left <= 3) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = left; i <= right; i++) {
                for (int j = i + 1; j <= right; j++) {
                    comparisons++;
                    double dist = pts[i].distanceTo(pts[j]);
                    if (dist < min) {
                        min = dist;
                    }
                }
            }
            Arrays.sort(pts, left, right + 1, (p1, p2) -> Double.compare(p1.y, p2.y));
            return min;
        }

        int mid = left + (right - left) / 2;
        double midX = pts[mid].x;

        double d1 = closestUtil(pts, aux, left, mid, currentDepth + 1);
        double d2 = closestUtil(pts, aux, mid + 1, right, currentDepth + 1);
        double delta = Math.min(d1, d2);

        mergeByY(pts, aux, left, mid, right);

        int stripSize = 0;
        for (int i = left; i <= right; i++) {
            if (Math.abs(pts[i].x - midX) < delta) {
                aux[stripSize++] = pts[i];
            }
        }

        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1; j < stripSize && (aux[j].y - aux[i].y) < delta; j++) {
                comparisons++;
                double dist = aux[i].distanceTo(aux[j]);
                if (dist < delta) {
                    delta = dist;
                }
            }
        }

        return delta;
    }

    private void mergeByY(Point[] pts, Point[] aux, int left, int mid, int right) {
        for (int k = left; k <= right; k++) {
            aux[k] = pts[k];
        }

        int i = left;
        int j = mid + 1;

        for (int k = left; k <= right; k++) {
            if (i > mid) {
                pts[k] = aux[j++];
            } else if (j > right) {
                pts[k] = aux[i++];
            } else {
                comparisons++;
                if (aux[j].y < aux[i].y) {
                    pts[k] = aux[j++];
                } else {
                    pts[k] = aux[i++];
                }
            }
        }
    }

    public double bruteForce(Point[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }

        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = points[i].distanceTo(points[j]);
                if (dist < min) {
                    min = dist;
                }
            }
        }
        return min;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public long getComparisons() {
        return comparisons;
    }
}