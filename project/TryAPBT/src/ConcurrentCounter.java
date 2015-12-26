public class ConcurrentCounter {
    private int value;

    public synchronized int get() { return value; }
    public synchronized int increment() { return ++value; }
    public synchronized int decrement() { return --value; }
}