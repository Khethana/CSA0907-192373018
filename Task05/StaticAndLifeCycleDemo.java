package Task05;

/**
 * Task 5: Static Members, Multi-Facility Counters, Pass-by-Value, and Object Life Cycle.
 */
public class StaticAndLifeCycleDemo {

    // --- Facility Model demonstrating Instance vs Static Counters ---
    public static class Facility {
        private final String facilityCode; // e.g. "NORTH_GATE", "SOUTH_GATE"
        private long instanceTicketCounter; // Per-facility instance counter
        private static long globalTicketCounter = 0; // Global static counter across all facilities

        // Static configuration constant
        public static final String CURRENCY = "INR";
        public static final int MAX_SLOTS_DEFAULT = 100;

        // Static initialization block
        static {
            System.out.println(">> [JVM Class Loader] Static block executed: Facility class metadata loaded into Metaspace.");
            globalTicketCounter = 5000; // Starting baseline sequence
        }

        public Facility(String facilityCode) {
            this.facilityCode = facilityCode;
            this.instanceTicketCounter = 1000; // Each facility starts numbering from 1000
        }

        /** Issues ticket using per-facility instance counter (Partitioned Namespace) */
        public String issueWithInstanceCounter() {
            instanceTicketCounter++;
            return facilityCode + "-" + instanceTicketCounter;
        }

        /** Issues ticket using JVM-wide static counter (Shared Global Sequence) */
        public String issueWithGlobalStaticCounter() {
            globalTicketCounter++;
            return facilityCode + "-G" + globalTicketCounter;
        }

        // Static utility method: No object state needed
        public static int roundUpToCeilHours(int minutes) {
            if (minutes <= 0) return 0;
            return (minutes + 59) / 60;
        }
    }

    // --- Ticket representation for Life Cycle and Pass-by-Value verification ---
    public static class Ticket {
        private final String id;
        private boolean settled;

        public Ticket(String id) {
            this.id = id;
            this.settled = false;
        }

        public void markSettled() { this.settled = true; }
        public boolean isSettled() { return settled; }
        public String getId() { return id; }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 5: STATIC MEMBERS, MULTI-FACILITY COUNTERS & OBJECT LIFE CYCLE");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Static vs Instance Counters across Multiple Facilities ---");
        Facility northGate = new Facility("NG");
        Facility southGate = new Facility("SG");

        System.out.println("A) Using Instance Counters (Independent Prefixed Sequences):");
        System.out.println("   North Gate Ticket 1: " + northGate.issueWithInstanceCounter());
        System.out.println("   North Gate Ticket 2: " + northGate.issueWithInstanceCounter());
        System.out.println("   South Gate Ticket 1: " + southGate.issueWithInstanceCounter());
        System.out.println("   North Gate Ticket 3: " + northGate.issueWithInstanceCounter());
        System.out.println("   South Gate Ticket 2: " + southGate.issueWithInstanceCounter());
        System.out.println("   -> Each facility maintains clean, continuous serial auditing: NG-1001..1003 and SG-1001..1002.");

        System.out.println("\nB) Using JVM-wide Static Counter (Global Interleaved Sequence):");
        System.out.println("   North Gate Global 1: " + northGate.issueWithGlobalStaticCounter());
        System.out.println("   South Gate Global 2: " + southGate.issueWithGlobalStaticCounter());
        System.out.println("   North Gate Global 3: " + northGate.issueWithGlobalStaticCounter());
        System.out.println("   -> Sequence jumps between gates: NG-G5001, SG-G5002, NG-G5003.");

        System.out.println("\n--- 2. Static Utility Methods ---");
        System.out.println("Calling Facility.roundUpToCeilHours(75): " + Facility.roundUpToCeilHours(75) + " hours");
        System.out.println("Facility.CURRENCY constant: " + Facility.CURRENCY);

        System.out.println("\n--- 3. Java Parameter Passing: Pass-by-Value Mechanics ---");
        Ticket originalTicket = new Ticket("TKT-ALPHA-01");
        System.out.println("Initial State: id=" + originalTicket.getId() + ", settled=" + originalTicket.isSettled());

        modifyTicketState(originalTicket);
        System.out.println("After modifyTicketState(originalTicket): settled=" + originalTicket.isSettled() 
                + " (Object mutation through copied reference pointer succeeds)");

        reassignTicketReference(originalTicket);
        System.out.println("After reassignTicketReference(originalTicket): id=" + originalTicket.getId() 
                + " (Caller's reference pointer remains completely UNCHANGED)");

        System.out.println("\n--- 4. Object Life Cycle & Garbage Collection Eligibility ---");
        demonstrateObjectLifeCycle();
        System.out.println("===============================================================================");
    }

    private static void modifyTicketState(Ticket t) {
        // Mutates the heap object via the copied pointer
        t.markSettled();
    }

    private static void reassignTicketReference(Ticket t) {
        // Rebinds only the local stack variable 't'; caller's reference is unaffected!
        t = new Ticket("TKT-NEW-REBOUND");
    }

    private static void demonstrateObjectLifeCycle() {
        System.out.println("Trace of Ticket Object Life Cycle:");
        System.out.println("1. Creation: 'Ticket t = new Ticket(...)' allocates memory in Eden Space (Young Gen Heap).");
        System.out.println("2. Active Use: Stored in parking slot or registry. Reachable from GC Root.");
        System.out.println("3. Vehicle Exit: Slot set to null ('slots[f][s] = null').");
        System.out.println("4. GC Eligibility: When no live stack/static reference holds the ticket, it becomes unreachable.");
        System.out.println("5. Reclamation: G1GC or ZGC sweeps dead objects during minor collection cycles.");
    }
}
