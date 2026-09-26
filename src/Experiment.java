import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        generatePlots(csvFilePath);
    }

    public void generatePlots(String csvFilePath) {
        Map<String, List<double[]>> timeData = new LinkedHashMap<>();
        Map<String, List<double[]>> depthData = new LinkedHashMap<>();
        String[] algos = {"MergeSort", "QuickSort", "DeterministicSelect", "ClosestPair"};
        for (String a : algos) {
            timeData.put(a, new ArrayList<>());
            depthData.put(a, new ArrayList<>());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].equals("Random")) {
                    String algo = parts[0];
                    double size = Double.parseDouble(parts[2]);
                    double timeMs = Double.parseDouble(parts[4]);
                    double depth = Double.parseDouble(parts[5]);
                    if (timeData.containsKey(algo)) {
                        timeData.get(algo).add(new double[]{size, timeMs});
                        depthData.get(algo).add(new double[]{size, depth});
                    }
                }
            }
            new File("docs/plots").mkdirs();
            saveChart("Execution Time vs Input Size (Random Input)", "Input Size (n)", "Time (ms)", timeData, "docs/plots/time_vs_n.png");
            saveChart("Max Recursion Depth vs Input Size (Random Input)", "Input Size (n)", "Max Recursion Depth", depthData, "docs/plots/depth_vs_n.png");
            System.out.println("Plots saved to docs/plots/");
        } catch (IOException e) {
            System.err.println("Error generating plots: " + e.getMessage());
        }
    }

    private void saveChart(String title, String xLabel, String yLabel, Map<String, List<double[]>> series, String outputPath) throws IOException {
        int width = 800, height = 500;
        int padLeft = 80, padRight = 40, padTop = 60, padBottom = 70;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        double maxX = 50000, maxY = 1.0;
        for (List<double[]> pts : series.values()) {
            for (double[] p : pts) {
                if (p[1] > maxY) maxY = p[1];
            }
        }
        maxY *= 1.1;

        int plotW = width - padLeft - padRight;
        int plotH = height - padTop - padBottom;

        g.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 5; i++) {
            int y = padTop + plotH - (i * plotH / 5);
            g.drawLine(padLeft, y, padLeft + plotW, y);
            g.setColor(Color.DARK_GRAY);
            String val = String.format(java.util.Locale.US, "%.1f", (maxY * i / 5));
            g.drawString(val, padLeft - 50, y + 5);
            g.setColor(new Color(230, 230, 230));
        }

        for (int s : SIZES) {
            int x = padLeft + (int) ((s / maxX) * plotW);
            g.drawLine(x, padTop, x, padTop + plotH);
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.valueOf(s), x - 15, padTop + plotH + 20);
            g.setColor(new Color(230, 230, 230));
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(padLeft, padTop, padLeft, padTop + plotH);
        g.drawLine(padLeft, padTop + plotH, padLeft + plotW, padTop + plotH);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(title, width / 2 - g.getFontMetrics().stringWidth(title) / 2, 35);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString(xLabel, padLeft + plotW / 2 - 40, height - 20);
        g.drawString(yLabel, 15, padTop - 15);

        Color[] colors = {new Color(31, 119, 180), new Color(255, 127, 14), new Color(44, 160, 44), new Color(214, 39, 40)};
        int colorIdx = 0;
        int legendX = padLeft + 20;
        int legendY = padTop + 20;

        for (Map.Entry<String, List<double[]>> entry : series.entrySet()) {
            g.setColor(colors[colorIdx % colors.length]);
            g.setStroke(new BasicStroke(2.5f));
            List<double[]> pts = entry.getValue();
            for (int i = 0; i < pts.size(); i++) {
                int x1 = padLeft + (int) ((pts.get(i)[0] / maxX) * plotW);
                int y1 = padTop + plotH - (int) ((pts.get(i)[1] / maxY) * plotH);
                g.fillOval(x1 - 4, y1 - 4, 8, 8);
                if (i + 1 < pts.size()) {
                    int x2 = padLeft + (int) ((pts.get(i + 1)[0] / maxX) * plotW);
                    int y2 = padTop + plotH - (int) ((pts.get(i + 1)[1] / maxY) * plotH);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
            g.fillRect(legendX, legendY + colorIdx * 22 - 10, 14, 14);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), legendX + 22, legendY + colorIdx * 22 + 2);
            colorIdx++;
        }

        g.dispose();
        ImageIO.write(img, "png", new File(outputPath));
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