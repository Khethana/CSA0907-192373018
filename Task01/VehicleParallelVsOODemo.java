package Task01;

import java.util.Arrays;

/**
 * Task 1: Alternative Concept and Complexity Analysis Demonstration.
 * 
 * Compares:
 * 1. Parallel Arrays Representation:
 *    - regNo[], type[], entryTime[]
 * 2. Object-Oriented Representation:
 *    - Vehicle[] array storing Vehicle objects
 * 
 * Evaluates:
 * - Search by registration number (Time Complexity)
 * - Adding new attributes (Extensibility & Code Fragility)
 * - Space and Memory Overhead per vehicle
 */
public class VehicleParallelVsOODemo {

    // --- 1. Object-Oriented Vehicle Model ---
    public static class VehicleRecord {
        private final String regNo;
        private final String type;
        private final long entryTime;
        // Adding a new attribute in OO requires adding a field here cleanly:
        private String ownerCategory; // e.g., "STUDENT", "STAFF", "VISITOR"

        public VehicleRecord(String regNo, String type, long entryTime, String ownerCategory) {
            this.regNo = regNo;
            this.type = type;
            this.entryTime = entryTime;
            this.ownerCategory = ownerCategory;
        }

        public String getRegNo() { return regNo; }
        public String getType() { return type; }
        public long getEntryTime() { return entryTime; }
        public String getOwnerCategory() { return ownerCategory; }

        @Override
        public String toString() {
            return String.format("Vehicle[regNo=%s, type=%s, entryTime=%d, owner=%s]", 
                    regNo, type, entryTime, ownerCategory);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 1: OBJECT-ORIENTED MODEL VS PARALLEL ARRAYS COMPARISON");
        System.out.println("===============================================================================\n");

        final int N = 100_000;
        System.out.println("Synthesizing dataset of " + N + " vehicles for empirical complexity benchmark...\n");

        // --- Setup Parallel Arrays ---
        String[] regNoArr = new String[N];
        String[] typeArr = new String[N];
        long[] entryTimeArr = new long[N];
        String[] ownerCategoryArr = new String[N]; // Simulated extension

        // --- Setup OO Objects ---
        VehicleRecord[] ooVehicles = new VehicleRecord[N];

        for (int i = 0; i < N; i++) {
            String rNo = "TN-09-CAMPUS-" + String.format("%06d", i);
            String type = (i % 3 == 0) ? "TWO_WHEELER" : (i % 3 == 1) ? "CAR" : "EV";
            long entry = 1700000000000L + (i * 60_000L);
            String owner = (i % 4 == 0) ? "STUDENT" : (i % 4 == 1) ? "STAFF" : "VISITOR";

            // Parallel arrays populate
            regNoArr[i] = rNo;
            typeArr[i] = type;
            entryTimeArr[i] = entry;
            ownerCategoryArr[i] = owner;

            // OO populate
            ooVehicles[i] = new VehicleRecord(rNo, type, entry, owner);
        }

        String targetRegNo = "TN-09-CAMPUS-" + String.format("%06d", N - 1); // Worst case: target is at the end

        System.out.println("--- 1. Search Complexity Analysis (Worst Case Target: " + targetRegNo + ") ---");

        // Warmup JIT
        for (int w = 0; w < 10; w++) {
            linearSearchParallel(regNoArr, targetRegNo);
            linearSearchOO(ooVehicles, targetRegNo);
        }

        // Measure Parallel Arrays Search
        long startParallel = System.nanoTime();
        int foundIdxParallel = linearSearchParallel(regNoArr, targetRegNo);
        long timeParallel = System.nanoTime() - startParallel;

        // Measure OO Search
        long startOO = System.nanoTime();
        VehicleRecord foundOO = linearSearchOO(ooVehicles, targetRegNo);
        long timeOO = System.nanoTime() - startOO;

        System.out.printf("Parallel Arrays Search: Found at Index %d in %.3f ms (Time Complexity: O(N))\n", 
                foundIdxParallel, timeParallel / 1_000_000.0);
        System.out.printf("OO Array Search:        Found %s in %.3f ms (Time Complexity: O(N))\n\n", 
                foundOO.getRegNo(), timeOO / 1_000_000.0);

        System.out.println("--- 2. Extensibility Analysis (Adding New Attribute 'fuelType') ---");
        System.out.println("In Parallel Arrays:");
        System.out.println(" - Requires creating an entirely separate array: String[] fuelTypeArr = new String[N];");
        System.out.println(" - Every method signature must be modified: e.g. park(regNo, type, entry, owner, fuelType)");
        System.out.println(" - Risk of index desynchronization if one array is resized, sorted, or mutated independently.");
        System.out.println("In Object-Oriented Design:");
        System.out.println(" - Requires adding one field 'fuelType' to VehicleRecord class with getter/setter.");
        System.out.println(" - Method signatures remain intact: park(VehicleRecord v); Encapsulation protects state invariants.\n");

        System.out.println("--- 3. Memory & Space Complexity Analysis ---");
        System.out.println("Theoretical Space Breakdown (64-bit JVM with Compressed OOPs):");
        System.out.println("a) Parallel Arrays:");
        System.out.println("   - Array References: 4 arrays * 24 bytes header + (4 * N * 4 bytes reference/primitive)");
        System.out.println("   - Per vehicle entry: ~16 bytes of array pointers/data + String object instances.");
        System.out.println("b) Object-Oriented Design:");
        System.out.println("   - 1 Array Reference: 24 bytes header + (N * 4 bytes reference)");
        System.out.println("   - Each VehicleRecord object: 12-byte header + 4-byte padding + 4 fields * 4 bytes = 32 bytes.");
        System.out.println("   - Trade-off: OO incurs ~16-24 bytes of object header overhead per vehicle, but yields");
        System.out.println("     type safety, encapsulation, atomic consistency, and polymorphic extensibility.");
        System.out.println("===============================================================================");
    }

    private static int linearSearchParallel(String[] regNoArr, String target) {
        for (int i = 0; i < regNoArr.length; i++) {
            if (regNoArr[i] != null && regNoArr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private static VehicleRecord linearSearchOO(VehicleRecord[] vehicles, String target) {
        for (VehicleRecord v : vehicles) {
            if (v != null && v.getRegNo().equals(target)) {
                return v;
            }
        }
        return null;
    }
}
