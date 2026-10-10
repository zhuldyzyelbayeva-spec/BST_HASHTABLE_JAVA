public class HashTable {
    private static class Entry {
        final int key; Entry next;
        Entry(int key, Entry next) { this.key = key; this.next = next; }
    }
    private Entry[] buckets = new Entry[16];
    private int size;
    public int size() { return size; }
    private int index(int key) { return Math.floorMod(key, buckets.length); }
    public boolean contains(int key) {
        for (Entry e = buckets[index(key)]; e != null; e = e.next)
            if (e.key == key) return true;
        return false;
    }
    public boolean insert(int key) {
        if (contains(key)) return false;
        if ((size + 1) > buckets.length * 0.75) resize();
        int i = index(key);
        buckets[i] = new Entry(key, buckets[i]);
        size++;
        return true;
    }
    private void resize() {
        Entry[] old = buckets;
        buckets = new Entry[old.length * 2];
        for (Entry head : old) {
            while (head != null) {
                Entry next = head.next;
                int i = index(head.key);
                head.next = buckets[i];
                buckets[i] = head;
                head = next;
            }
        }
    }
    public boolean delete(int key) {
        int i = index(key);
        Entry previous = null, current = buckets[i];
        while (current != null) {
            if (current.key == key) {
                if (previous == null) buckets[i] = current.next;
                else previous.next = current.next;
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }
}
