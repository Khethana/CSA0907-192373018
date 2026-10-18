package parking.service;

import parking.model.ParkingSlot;

/**
 * Task 10: Service layer coordinating slot allocation and encapsulation.
 */
public class ParkingFacility {
    private final ParkingSlot[][] slots;

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new ParkingSlot[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                // Ground floor (floor 0) first 5 slots equipped with EV chargers
                boolean evCharger = (f == 0 && s < 5);
                slots[f][s] = new ParkingSlot(f, s, evCharger);
            }
        }
    }

    /** Public API to allocate a slot */
    public ParkingSlot allocate(String regNo, boolean requiresEV) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration plate cannot be null or blank");
        }
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                ParkingSlot slot = slots[f][s];
                if (!slot.isOccupied()) {
                    if (requiresEV && !slot.hasEVCharger()) {
                        continue; // Skip non-EV stall when EV charger is requested
                    }
                    slot.assignVehicle(regNo);
                    return slot;
                }
            }
        }
        return null; // Facility full
    }

    /** Public API to release a slot */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        ParkingSlot target = slots[floor][slot];
        if (!target.isOccupied()) return false;
        target.vacate();
        return true;
    }

    // Package-private helper method: Visible within parking.service, inaccessible to outside packages
    ParkingSlot getRawSlotInternal(int floor, int slot) {
        return slots[floor][slot];
    }
}
