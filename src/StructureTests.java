public class StructureTests {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        HashTable hash = new HashTable();
        for (int x : new int[]{50, 30, 70, 20, 40, 60, 80, -1}) {
            check(bst.insert(x), "BST insert"); check(hash.insert(x), "Hash insert");
        }
        check(!bst.insert(50) && !hash.insert(50), "duplicates");
        check(bst.size() == 8 && hash.size() == 8, "size");
        check(bst.contains(60) && hash.contains(60), "search existing");
        check(!bst.contains(999) && !hash.contains(999), "search missing");
        for (int x : new int[]{20, 30, 50, -1, 70, 40, 60, 80}) {
            check(bst.delete(x), "BST delete " + x);
            check(hash.delete(x), "Hash delete " + x);
            check(!bst.contains(x) && !hash.contains(x), "removed " + x);
        }
        check(bst.size() == 0 && hash.size() == 0, "empty");
        check(!bst.delete(100) && !hash.delete(100), "missing delete");
        for (int i = 0; i < 10000; i++) { check(bst.insert(i), "BST bulk"); check(hash.insert(i), "Hash bulk"); }
        for (int i = 0; i < 10000; i++) { check(bst.contains(i) && hash.contains(i), "bulk search"); }
        for (int i = 0; i < 10000; i++) { check(bst.delete(i) && hash.delete(i), "bulk delete"); }
        System.out.println("All structure tests passed.");
    }
}
