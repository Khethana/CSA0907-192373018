package Task11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Task 11 Alternative Concept:
 * Empirical Benchmark comparing Active Ticket Storage Structures for Exit-Processing:
 * 1. Fixed Array: ParkingTicket[]
 * 2. Dynamic List: ArrayList<ParkingTicket>
 * 3. Hash Map: HashMap<String, ParkingTicket> keyed by Registration Number
 */
public class TicketStorageBenchmark {

    public static class SimpleTicket {
        private final String regNo;
        private final long entryTime;

        public SimpleTicket(String regNo, long entryTime) {
            this.regNo = regNo;
            this.entryTime = entryTime;
        }

        public String getRegNo() { return regNo; }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 11: ALTERNATIVE TICKET STORAGE BENCHMARK (EXIT LOOKUP)");
        System.out.println("===============================================================================\n");

        final int N = 50_000;
        System.out.println("Populating " + N + " parked vehicles across Array, ArrayList, and HashMap...\n");

        SimpleTicket[] ticketArray = new SimpleTicket[N];
        ArrayList<SimpleTicket> ticketList = new ArrayList<>(N);
        Map<String, SimpleTicket> ticketMap = new HashMap<>(N * 2);

        for (int i = 0; i < N; i++) {
            String plate = "TN-CAMPUS-" + String.format("%05d", i);
            SimpleTicket t = new SimpleTicket(plate, System.currentTimeMillis());
            ticketArray[i] = t;
            ticketList.add(t);
            ticketMap.put(plate, t);
        }

        // Target for lookup: Worst-case search target at the very end of collection
        String targetPlate = "TN-CAMPUS-" + String.format("%05d", N - 1);
        final int LOOKUP_REPETITIONS = 10_000;

        System.out.printf("Performing %d repeated lookups for worst-case plate (%s):\n\n", 
                LOOKUP_REPETITIONS, targetPlate);

        // 1. Array Linear Search (O(N))
        long startArray = System.nanoTime();
        SimpleTicket res1 = null;
        for (int r = 0; r < LOOKUP_REPETITIONS; r++) {
            for (int i = 0; i < N; i++) {
                if (ticketArray[i] != null && ticketArray[i].getRegNo().equals(targetPlate)) {
                    res1 = ticketArray[i];
                    break;
                }
            }
        }
        long timeArray = System.nanoTime() - startArray;

        // 2. ArrayList Linear Search (O(N))
        long startList = System.nanoTime();
        SimpleTicket res2 = null;
        for (int r = 0; r < LOOKUP_REPETITIONS; r++) {
            for (int i = 0; i < ticketList.size(); i++) {
                SimpleTicket item = ticketList.get(i);
                if (item != null && item.getRegNo().equals(targetPlate)) {
                    res2 = item;
                    break;
                }
            }
        }
        long timeList = System.nanoTime() - startList;

        // 3. HashMap Lookup (O(1))
        long startMap = System.nanoTime();
        SimpleTicket res3 = null;
        for (int r = 0; r < LOOKUP_REPETITIONS; r++) {
            res3 = ticketMap.get(targetPlate);
        }
        long timeMap = System.nanoTime() - startMap;

        System.out.printf("1. Fixed Array (O(N) Scan):     %8.2f ms (Time per lookup: ~%.3f microseconds)\n", 
                timeArray / 1_000_000.0, (timeArray / 1_000.0) / LOOKUP_REPETITIONS);
        System.out.printf("2. ArrayList (O(N) Scan):       %8.2f ms (Time per lookup: ~%.3f microseconds)\n", 
                timeList / 1_000_000.0, (timeList / 1_000.0) / LOOKUP_REPETITIONS);
        System.out.printf("3. HashMap (O(1) Hash Table):   %8.2f ms (Time per lookup: ~%.3f microseconds)\n\n", 
                timeMap / 1_000_000.0, (timeMap / 1_000.0) / LOOKUP_REPETITIONS);

        double speedup = (double) timeArray / timeMap;
        System.out.printf("Result: HashMap lookup is %.1fx FASTER than linear scan for large facilities!\n\n", speedup);

        System.out.println("Space Complexity Analysis:");
        System.out.println(" - Array: Exactly N references (4N bytes). Minimal heap overhead.");
        System.out.println(" - ArrayList: N references + array buffer capacity (4N to 6N bytes).");
        System.out.println(" - HashMap: Node[K, V] entries with hash, next pointer, key and value references.");
        System.out.println("   Each Node consumes ~32 bytes -> Total extra memory ~32N bytes.");
        System.out.println("Conclusion: For real-time barrier gates, HashMap's deterministic O(1) response");
        System.out.println("justifies the modest memory trade-off.");
        System.out.println("===============================================================================");
    }
}
