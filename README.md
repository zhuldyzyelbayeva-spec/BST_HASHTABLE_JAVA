# BST vs Hash Table Performance (Java)

## Goal
Compare a regular (not self-balancing) binary search tree and a hash table using insertion, searching, and deletion. Both structures store unique integer keys.

## Implementation
- `src/BinarySearchTree.java`: an ordinary BST, with iterative insert, search and delete. Deletion handles nodes with zero, one, or two children.
- `src/HashTable.java`: a hash table using separate chaining (linked lists for collisions), integer modulo hashing, and resizing when the load factor exceeds 0.75. It does **not** use Java's `HashMap` or `HashSet`.
- `src/StructureTests.java`: correctness tests, including duplicates, missing keys, negative keys, and 10,000 insert/search/delete operations.
- `src/Benchmark.java`: repeatable timing experiment.

## Complexity analysis
Here, `n` is the number of stored keys. `h` is the height of the BST.

| Operation | BST best | BST average (random order) | BST worst | Hash best | Hash expected* | Hash worst |
|---|---|---|---|---|---|---|
| Insert | O(1) | O(log n) | O(n) | O(1) | O(1) amortized | O(n) |
| Search | O(1) | O(log n) | O(n) | O(1) | O(1) | O(n) |
| Delete | O(1) | O(log n) | O(n) | O(1) | O(1) | O(n) |

*Hash-table expected performance assumes reasonably distributed keys and a controlled load factor. A resize may make one insertion O(n). BST insertion/search/deletion cost O(h); a balanced shape has h = O(log n), but a chain has h = O(n). A plain BST does not rebalance itself.

Space: both use O(n) storage (including the hash table's bucket array). The BST preserves key order and supports ordered traversal and range queries; this hash table does not.

## Experiment

1. Test `n = 100`, `1,000`, and `10,000` keys.
2. Use the same shuffled integer keys for both structures (deterministic seeds).
3. Time each *batch* of `n` insertions, `n` successful searches, and `n` deletions separately with `System.nanoTime()`.
4. Warm up the JVM before measuring; repeat each random-order test 9 times and take the median. A separate sorted-input BST test uses 5 repetitions.
5. Do not include key generation and shuffling in the timed sections. All operations are checked for success.

Run from the project root (Java 17+):

```bash
javac -d out src/*.java
java -cp out StructureTests
java -cp out Benchmark > results/results.csv
```

Timing results vary by computer, JVM, load, and JIT optimization. This is a simple classroom microbenchmark, not a substitute for JMH. Deletion of ascending keys in a right-skewed BST repeatedly removes the root, so it can be fast even when search is slow.

## Actual measured results

The following measurements were obtained by running the Java benchmark in IntelliJ IDEA with OpenJDK 25.0.1. The full data is saved in [`results/results.csv`](results/results.csv). Times are in milliseconds for **all n operations together**, not for one operation.

| Structure | Input order | Elements | Insertion (ms) | Search (ms) | Deletion (ms) |
|---|---|---:|---:|---:|---:|
| BST | Random | 100 | 0.007400 | 0.005200 | 0.007100 |
| Hash Table | Random | 100 | 0.014900 | 0.003200 | 0.003400 |
| BST | Random | 1,000 | 0.080200 | 0.057200 | 0.070000 |
| Hash Table | Random | 1,000 | 0.132200 | 0.032700 | 0.035600 |
| BST | Random | 10,000 | 0.940900 | 0.592100 | 0.597100 |
| Hash Table | Random | 10,000 | 0.351000 | 0.037200 | 0.044100 |
| BST | Sorted | 100 | 0.012200 | 0.012600 | 0.000600 |
| BST | Sorted | 1,000 | 0.925800 | 1.236400 | 0.005300 |
| BST | Sorted | 10,000 | 99.317000 | 126.417900 | 0.038300 |

## Discussion

For random input, the hash table was faster at searching and deleting elements at every tested size. For example, at 10,000 elements, BST search took **0.5921 ms**, while hash-table search took **0.0372 ms**. This means the hash table was about **15.9 times faster** in this test.

Insertion was different. With 100 and 1,000 elements, the BST inserted keys faster. With 10,000 elements, the hash table was faster: **0.3510 ms** compared with **0.9409 ms** for the BST. The hash table may spend extra time calculating bucket positions, managing collisions, and resizing its array. That overhead can matter with small inputs.

The sorted-input experiment shows a weakness of a regular BST. If keys are inserted in increasing order, the tree becomes a long chain instead of a balanced tree. At 10,000 elements, insertion took **99.3170 ms** and searching took **126.4179 ms**. These results are much slower than for random input. Deletion remained fast in this specific sorted test because the benchmark removes keys in increasing order, repeatedly deleting the root of the chain. It does not mean deletion is always fast in an unbalanced BST.

## Why performance differs in practice

Time complexity describes how an algorithm grows as the number of elements increases, but actual speed also depends on the data and the computer. The BST must follow links from node to node, and its height depends on insertion order. The hash table uses a hash function to choose a bucket, so searching is usually quick if keys are distributed well. Hash collisions, resizing, memory allocation, CPU caching, and Java's JIT compiler can affect the measurements. Small timing differences should not be treated as universal results.

## Conclusion

In these experiments, the hash table was generally better for fast exact-key searching and deletion. The BST was competitive for inserting small random datasets, and it has the advantage of keeping keys in sorted order. However, a regular BST can become very slow when input is sorted. A self-balancing BST, such as an AVL tree or red-black tree, could prevent that problem. The best data structure depends on whether we need fast lookups or ordered data.

## Verification

The `StructureTests` program printed `All structure tests passed.` and finished with exit code 0 in IntelliJ IDEA. Benchmark results can vary between runs, so this README records the measurements from the supplied OpenJDK 25.0.1 run.
