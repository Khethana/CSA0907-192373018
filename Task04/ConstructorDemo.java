package Task04;

/**
 * Task 4: Constructor Patterns and Object Creation Demonstration.
 * 
 * Compares:
 * 1. Parameterized Constructor with 'this(...)' chaining (Fail-fast, Immutable, Thread-safe).
 * 2. Copy Constructor (Safe cloning for duplicate ticket reissuance).
 * 3. Static Factory Methods (Clear intent, encapsulation).
 * 4. JavaBeans Pattern (Default constructor + setters - prone to partially initialized state).
 * 
 * Evaluates memory allocation (JVM Stack vs JVM Heap) and execution speed.
 */
public class ConstructorDemo {

    // JavaBeans representation for comparison
    public static class JavaBeanTicket {
        private String ticketId;
        private String regNo;
        private int floor;
        private int slot;
        private long entryTimeMillis;

        public JavaBeanTicket() {
            // Default no-arg constructor leaves fields null/zero!
        }

        public void setTicketId(String ticketId) { this.ticketId = ticketId; }
        public void setRegNo(String regNo) { this.regNo = regNo; }
        public void setFloor(int floor) { this.floor = floor; }
        public void setSlot(int slot) { this.slot = slot; }
        public void setEntryTimeMillis(long entryTimeMillis) { this.entryTimeMillis = entryTimeMillis; }

        public String getTicketId() { return ticketId; }
        public String getRegNo() { return regNo; }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 4: CLASSES, CONSTRUCTORS & MEMORY ALLOCATION DEMONSTRATION");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Overloaded Constructors & Chaining via 'this(...)' ---");
        ParkingTicket t1 = new ParkingTicket("TN01AA1111", 1, 5);
        System.out.println("Chained Constructor Generated Ticket: " + t1);

        System.out.println("\n--- 2. Copy Constructor for Duplicate Ticket Reissue ---");
        ParkingTicket duplicateTicket = new ParkingTicket(t1);
        System.out.println("Original Ticket:  " + t1);
        System.out.println("Duplicate Ticket: " + duplicateTicket);
        System.out.println("Independent Object References? " + (t1 != duplicateTicket));
        System.out.println("Equal Ticket IDs? " + t1.getTicketId().equals(duplicateTicket.getTicketId()));

        System.out.println("\n--- 3. Static Factory Method Demonstration ---");
        ParkingTicket factoryTicket = ParkingTicket.issue("TN09EV9999", 0, 2);
        System.out.println("Factory Issued Ticket: " + factoryTicket);

        System.out.println("\n--- 4. Invariant Protection & JavaBeans Vulnerability ---");
        System.out.println("a) Invariant Protection in Parameterized Constructor:");
        try {
            new ParkingTicket(null, -1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("   [SUCCESS] Thwarted invalid object instantiation: " + e.getMessage());
        }

        System.out.println("b) JavaBeans Vulnerability (Temporal Coupling & Inconsistent State):");
        JavaBeanTicket jb = new JavaBeanTicket(); // Object is in JVM Heap right now!
        System.out.println("   Just after new JavaBeanTicket(): ticketId=" + jb.getTicketId() + ", regNo=" + jb.getRegNo());
        System.out.println("   If an exception occurs before setters are called, system is polluted with an INVALID object!");
        jb.setTicketId("TKT-JB-01");
        jb.setRegNo("TN05XX5555");
        System.out.println("   After setters: ticketId=" + jb.getTicketId() + ", regNo=" + jb.getRegNo());

        // -------------------------------------------------------------
        // PART 5: Empirical Benchmark of Object Creation (N = 100,000)
        // -------------------------------------------------------------
        System.out.println("\n--- 5. Empirical Benchmark: Object Creation Cost (N = 100,000) ---");
        final int N = 100_000;

        // Warmup
        for (int i = 0; i < 50_000; i++) {
            new ParkingTicket("TN01A" + i, 0, 1);
            JavaBeanTicket dummy = new JavaBeanTicket();
            dummy.setRegNo("TN01A" + i);
            ParkingTicket.issue("TN01A" + i, 0, 1);
        }

        // Test 1: Parameterized Constructor
        long startConstructor = System.nanoTime();
        ParkingTicket[] arr1 = new ParkingTicket[N];
        for (int i = 0; i < N; i++) {
            arr1[i] = new ParkingTicket("TKT-" + i, "TN01A" + i, i % 3, i % 50, 1000L);
        }
        long timeConstructor = System.nanoTime() - startConstructor;

        // Test 2: JavaBeans (Default constructor + 5 setters)
        long startJB = System.nanoTime();
        JavaBeanTicket[] arr2 = new JavaBeanTicket[N];
        for (int i = 0; i < N; i++) {
            JavaBeanTicket item = new JavaBeanTicket();
            item.setTicketId("TKT-" + i);
            item.setRegNo("TN01A" + i);
            item.setFloor(i % 3);
            item.setSlot(i % 50);
            item.setEntryTimeMillis(1000L);
            arr2[i] = item;
        }
        long timeJB = System.nanoTime() - startJB;

        // Test 3: Static Factory
        long startFactory = System.nanoTime();
        ParkingTicket[] arr3 = new ParkingTicket[N];
        for (int i = 0; i < N; i++) {
            arr3[i] = ParkingTicket.issue("TN01A" + i, i % 3, i % 50);
        }
        long timeFactory = System.nanoTime() - startFactory;

        System.out.printf("1. Parameterized Constructor: %.2f ms (Guarantees immutability & validation)\n", 
                timeConstructor / 1_000_000.0);
        System.out.printf("2. JavaBeans (Default + Setters): %.2f ms (Higher instruction count due to 5 method calls)\n", 
                timeJB / 1_000_000.0);
        System.out.printf("3. Static Factory Method:     %.2f ms (Clean domain semantics, JIT inlines cleanly)\n", 
                timeFactory / 1_000_000.0);

        System.out.println("\nMemory Space Summary:");
        System.out.println("All three approaches consume identical heap space per object (32 bytes object + reference).");
        System.out.println("However, JavaBeans violates encapsulation, creates temporary incomplete objects, and prevents 'final' fields.");
        System.out.println("===============================================================================");
    }
}
