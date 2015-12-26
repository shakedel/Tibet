
public class ConcurrentCounter {

   private int value;


   public synchronized int get() {
      return this.value;
   }

   public synchronized int increment() {
      return ++this.value;
   }

   public synchronized int decrement() {
      return --this.value;
   }
}
