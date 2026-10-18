package Task11;

import java.util.HashMap;
import java.util.Map;

/**
 * Task 11: Complete Campus Smart-Parking System Implementation.
 * 
 * Integrates:
 * - Jagged floor array layout
 * - Multi-category Vehicle hierarchy (TwoWheeler, Car, ElectricCar)
 * - User entitlement model (Student/Staff pass holders vs Visitors)
 * - Dual billing engine (Parking duration + EV kilowatt-hour energy)
 * - Encapsulated state and invariant enforcement
 */
public class CampusSmartParkingSystem {

    // --- Domain Models ---
    public enum UserCategory { STUDENT, STAFF, VISITOR }

    public static class User {
        private final String userId;
        private final UserCategory category;
        private final boolean hasMonthlyPass;

        public User(String userId, UserCategory category, boolean hasMonthlyPass) {
            this.userId = userId;
            this.category = category;
            this.hasMonthlyPass = hasMonthlyPass;
        }

        public boolean hasMonthlyPass() { return hasMonthlyPass; }
        public UserCategory getCategory() { return category; }
        public String getUserId() { return userId; }
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.isBlank()) {
                throw new IllegalArgumentException("Registration plate cannot be blank");
            }
            this.regNo = regNo.toUpperCase().trim();
        }

        public String getRegNo() { return regNo; }
        public abstract double ratePerHour();

        public double calculateBaseFee(int minutes) {
            if (minutes <= 0) return 0.0;
            int hours = (minutes + 59) / 60; // Pure integer ceiling
            return hours * ratePerHour();
        }

        @Override public boolean equals(Object o) {
            return (o instanceof Vehicle other) && regNo.equals(other.regNo);
        }

        @Override public int hashCode() { return regNo.hashCode(); }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override public double ratePerHour() { return 10.0; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override public double ratePerHour() { return 30.0; }
    }

    public interface Chargeable {
        double chargingFee(int kWh);
    }

    public static class ElectricCar extends Car implements Chargeable {
        public ElectricCar(String regNo) { super(regNo); }
        @Override public double chargingFee(int kWh) {
            if (kWh < 0) throw new IllegalArgumentException("Energy cannot be negative");
            return kWh * 12.0; // Rs. 12/kWh
        }
    }

    // --- Transaction Ticket ---
    public static class ParkingTicket {
        private final String ticketId;
        private final Vehicle vehicle;
        private final User user;
        private final int floor;
        private final int slot;
        private final long entryTimeMillis;

        public ParkingTicket(String ticketId, Vehicle vehicle, User user, int floor, int slot) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.user = user;
            this.floor = floor;
            this.slot = slot;
            this.entryTimeMillis = System.currentTimeMillis();
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public User getUser() { return user; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
    }

    // --- Receipt Record ---
    public record ParkingReceipt(String ticketId, String regNo, String vehicleType, 
                                 int durationMinutes, int billableHours, 
                                 double baseParkingFee, double evChargingFee, 
                                 double totalPayable, boolean passWaiverApplied) {}

    // --- Facility State ---
    private final String[][] slots; // slots[floor][slot] holds regNo, or null
    private final Map<String, ParkingTicket> activeTickets; // regNo -> ParkingTicket
    private long ticketSequence = 1000;
    private double dailyTotalRevenue = 0.0;

    public CampusSmartParkingSystem(int[] slotsPerFloor) {
        this.slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.slots[f] = new String[slotsPerFloor[f]];
        }
        this.activeTickets = new HashMap<>();
    }

    /**
     * Vehicle Entry Workflow: Validates, allocates first free slot, and issues ticket.
     */
    public synchronized ParkingTicket enterVehicle(Vehicle vehicle, User user, boolean requestCharging) {
        if (vehicle == null || user == null) {
            throw new IllegalArgumentException("Vehicle and User must not be null");
        }
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Vehicle " + vehicle.getRegNo() + " is ALREADY parked inside the facility!");
        }

        // Allocate slot
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicle.getRegNo();
                    String tktId = "CAMPUS-" + (++ticketSequence);
                    ParkingTicket ticket = new ParkingTicket(tktId, vehicle, user, f, s);
                    activeTickets.put(vehicle.getRegNo(), ticket);
                    return ticket;
                }
            }
        }
        return null; // Facility full
    }

    /**
     * Vehicle Exit Workflow: Computes billing, releases slot, and updates revenue.
     */
    public synchronized ParkingReceipt exitVehicle(String regNo, int durationMinutes, int chargingKWh) {
        if (regNo == null || !activeTickets.containsKey(regNo.toUpperCase().trim())) {
            throw new IllegalArgumentException("No active parking record found for vehicle: " + regNo);
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + durationMinutes);
        }

        ParkingTicket ticket = activeTickets.remove(regNo.toUpperCase().trim());
        slots[ticket.getFloor()][ticket.getSlot()] = null; // Vacate stall

        Vehicle v = ticket.getVehicle();
        User u = ticket.getUser();

        int billableHours = (durationMinutes == 0) ? 0 : (durationMinutes + 59) / 60;
        double baseFee = 0.0;
        boolean passWaiver = false;

        if (u.hasMonthlyPass()) {
            baseFee = 0.0; // Pass holders park free!
            passWaiver = true;
        } else {
            baseFee = v.calculateBaseFee(durationMinutes);
        }

        double chargingFee = 0.0;
        if (v instanceof Chargeable c && chargingKWh > 0) {
            chargingFee = c.chargingFee(chargingKWh);
        }

        double totalAmount = baseFee + chargingFee;
        dailyTotalRevenue += totalAmount;

        return new ParkingReceipt(ticket.getTicketId(), v.getRegNo(), v.getClass().getSimpleName(), 
                durationMinutes, billableHours, baseFee, chargingFee, totalAmount, passWaiver);
    }

    public synchronized int getFreeSlotsCount() {
        int count = 0;
        for (String[] floor : slots) {
            for (String s : floor) {
                if (s == null) count++;
            }
        }
        return count;
    }

    public double getDailyTotalRevenue() { return dailyTotalRevenue; }
}
