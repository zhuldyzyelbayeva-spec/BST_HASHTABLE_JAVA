import java.util.Arrays;
import java.util.Random;
import java.util.function.IntPredicate;

public class Benchmark {
    private static volatile long blackhole;
    private static int[] shuffled(int n, long seed) {
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) keys[i] = i;
        Random random = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = keys[i]; keys[i] = keys[j]; keys[j] = t;
        }
        return keys;
    }
    private static double median(long[] samples) {
        Arrays.sort(samples);
        return samples[samples.length / 2] / 1_000_000.0;
    }
    private static double[] measure(int n, boolean hash, boolean sorted, int trials) {
        long[][] samples = new long[3][trials];
        for (int trial = 0; trial < trials; trial++) {
            int[] keys = shuffled(n, 20261010L + trial);
            if (sorted) Arrays.sort(keys);
            BinarySearchTree bst = hash ? null : new BinarySearchTree();
            HashTable ht = hash ? new HashTable() : null;
            IntPredicate insert = hash ? ht::insert : bst::insert;
            IntPredicate contains = hash ? ht::contains : bst::contains;
            IntPredicate delete = hash ? ht::delete : bst::delete;
            long start = System.nanoTime();
            for (int k : keys) if (!insert.test(k)) throw new AssertionError("insert");
            samples[0][trial] = System.nanoTime() - start;
            start = System.nanoTime();
            for (int k : keys) if (!contains.test(k)) throw new AssertionError("search");
            samples[1][trial] = System.nanoTime() - start;
            start = System.nanoTime();
            for (int k : keys) if (!delete.test(k)) throw new AssertionError("delete");
            samples[2][trial] = System.nanoTime() - start;
            blackhole += (hash ? ht.size() : bst.size());
        }
        return new double[]{median(samples[0]), median(samples[1]), median(samples[2])};
    }
    public static void main(String[] args) {
        // Warm up JVM/JIT using untimed runs.
        for (int i = 0; i < 5; i++) {
            measure(1000, false, false, 1);
            measure(1000, true, false, 1);
        }
        System.out.println("structure,order,n,insertion_ms,search_ms,deletion_ms");
        for (int n : new int[]{100, 1000, 10000}) {
            for (boolean hash : new boolean[]{false, true}) {
                double[] t = measure(n, hash, false, 9);
                System.out.printf(java.util.Locale.US, "%s,random,%d,%.6f,%.6f,%.6f%n", hash ? "HashTable" : "BST", n, t[0], t[1], t[2]);
            }
        }
        for (int n : new int[]{100, 1000, 10000}) {
            double[] t = measure(n, false, true, 5);
            System.out.printf(java.util.Locale.US, "BST,sorted,%d,%.6f,%.6f,%.6f%n", n, t[0], t[1], t[2]);
        }
    }
}
