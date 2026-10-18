package Task12;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Task 12: Adapted Parking Facility handling:
 * - 5 floors with heterogeneous/jagged slot capacities.
 * - Integration of E-Bikes (flat fee) and Delivery Vans (peak 9-5 double rate).
 * - Visitor One-Day Pass verification.
 */
public class AdaptedParkingFacility {
    private final String[][] slots; // 5 floors with irregular slots
    private final Map<String, AdaptedTicket> activeTickets;

    public record AdaptedTicket(String ticketId, Vehicle vehicle, ParkingPass pass, 
                                 int floor, int slot, LocalTime entryTime, LocalDate entryDate) {}

    public AdaptedParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor.length != 5) {
            System.out.println("Notice: Initializing facility with " + slotsPerFloor.length + " floors.");
        }
        this.slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.slots[f] = new String[slotsPerFloor[f]];
        }
        this.activeTickets = new HashMap<>();
    }

    public synchronized AdaptedTicket enterVehicle(Vehicle vehicle, ParkingPass pass, LocalTime entryTime, LocalDate entryDate) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null");
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Vehicle already parked: " + vehicle.getRegNo());
        }

        // Allocate first available stall across the 5 irregular floors
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicle.getRegNo();
                    String tktId = "ADAPT-" + (activeTickets.size() + 1001);
                    AdaptedTicket ticket = new AdaptedTicket(tktId, vehicle, pass, f, s, entryTime, entryDate);
                    activeTickets.put(vehicle.getRegNo(), ticket);
                    return ticket;
                }
            }
        }
        return null; // All 5 floors saturated
    }

    public synchronized double exitVehicle(String regNo, int durationMinutes, LocalTime exitTime, LocalDate exitDate) {
        AdaptedTicket ticket = activeTickets.remove(regNo);
        if (ticket == null) {
            throw new IllegalArgumentException("No active ticket for: " + regNo);
        }

        slots[ticket.floor()][ticket.slot()] = null; // Vacate slot

        // 1. Check Pass Exemption
        if (ticket.pass() != null && ticket.pass().isValidOn(exitDate)) {
            return 0.0; // Pass holder (Daily or Monthly) parks free!
        }

        // 2. Specialized Vehicle Billing
        Vehicle v = ticket.vehicle();
        if (v instanceof EBike) {
            return v.calculateFee(durationMinutes); // Flat Rs. 20
        } else if (v instanceof DeliveryVan van) {
            return van.calculateTimeAwareFee(durationMinutes, ticket.entryTime());
        } else {
            return v.calculateFee(durationMinutes);
        }
    }

    public int getFloorCapacity(int floor) {
        return slots[floor].length;
    }

    public int getTotalCapacity() {
        int total = 0;
        for (String[] floor : slots) total += floor.length;
        return total;
    }
}
