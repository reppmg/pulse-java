public class Sand {
    public static void main(String[] args) {
        long stamp = System.currentTimeMillis();
        double realTime = 0;
        for (int i = 0; i < 256; i++) {
//            System.out.print(String.format("%dL, ", (long) realTime));
            System.out.println(i + " " + realTime);
            realTime += 1000.0/30;
        }
    }
}
