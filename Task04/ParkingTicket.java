package Task04;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Task 4: ParkingTicket Class Design.
 * 
 * Demonstrates:
 * - Parameterized constructor with validation invariants.
 * - Overloaded constructor with 'this(...)' constructor chaining.
 * - Copy constructor for duplicate ticket reissuance without aliasing bugs.
 * - Static factory methods for expressive object creation.
 */
public class ParkingTicket {
    private final String ticketId;
    private final String regNo;
    private final int floor;
    private final int slot;
    private final long entryTimeMillis;
    private boolean isPaid;

    private static long ticketSequence = 1000L;

    // 1. Parameterized Constructor (Canonical Constructor - Enforces all invariants)
    public ParkingTicket(String ticketId, String regNo, int floor, int slot, long entryTimeMillis) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or blank");
        }
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be null or blank");
        }
        if (floor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        if (slot < 0) {
            throw new IllegalArgumentException("Slot cannot be negative");
        }
        this.ticketId = ticketId;
        this.regNo = regNo.toUpperCase().trim();
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMillis = entryTimeMillis;
        this.isPaid = false;
    }

    // 2. Overloaded Constructor (Constructor Chaining using 'this(...)')
    // Automatically assigns sequence ticket ID and current system timestamp
    public ParkingTicket(String regNo, int floor, int slot) {
        this("TKT-" + (++ticketSequence), regNo, floor, slot, System.currentTimeMillis());
    }

    // 3. Copy Constructor (Creates an independent, defensively cloned snapshot)
    // Critical for issuing a duplicate/re-printed ticket or audit logging without exposing original reference
    public ParkingTicket(ParkingTicket original) {
        if (original == null) {
            throw new IllegalArgumentException("Source ticket for copy constructor cannot be null");
        }
        this.ticketId = original.ticketId + "-DUP";
        this.regNo = original.regNo;
        this.floor = original.floor;
        this.slot = original.slot;
        this.entryTimeMillis = original.entryTimeMillis;
        this.isPaid = original.isPaid;
    }

    // 4. Static Factory Method
    public static ParkingTicket issue(String regNo, int floor, int slot) {
        return new ParkingTicket(regNo, floor, slot);
    }

    public static ParkingTicket reissueDuplicate(ParkingTicket original) {
        return new ParkingTicket(original);
    }

    // Accessors
    public String getTicketId() { return ticketId; }
    public String getRegNo() { return regNo; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMillis() { return entryTimeMillis; }
    public boolean isPaid() { return isPaid; }
    public void markPaid() { this.isPaid = true; }

    @Override
    public String toString() {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(entryTimeMillis), ZoneId.systemDefault());
        String formattedTime = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("Ticket[ID=%s, RegNo=%s, Location=F%d:S%d, Entry=%s, Paid=%b]", 
                ticketId, regNo, floor, slot, formattedTime, isPaid);
    }
}
