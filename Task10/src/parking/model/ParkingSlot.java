package parking.model;

/**
 * Task 10: Domain entity modeling a parking slot with strict encapsulation.
 */
public class ParkingSlot {
    // Private fields: completely protected from unauthorized external modification
    private final int floor;
    private final int slotNumber;
    private final boolean hasEVCharger;
    private String occupyingVehicleRegNo; // Mutable, package/private access

    public ParkingSlot(int floor, int slotNumber, boolean hasEVCharger) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor index cannot be negative");
        }
        if (slotNumber < 0) {
            throw new IllegalArgumentException("Slot index cannot be negative");
        }
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.hasEVCharger = hasEVCharger;
        this.occupyingVehicleRegNo = null;
    }

    // Public read-only accessors
    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean hasEVCharger() { return hasEVCharger; }
    public boolean isOccupied() { return occupyingVehicleRegNo != null; }
    public String getOccupyingVehicleRegNo() { return occupyingVehicleRegNo; }

    // Validating setter protecting state invariant
    public void assignVehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Cannot assign null or blank vehicle registration to slot");
        }
        if (isOccupied()) {
            throw new IllegalStateException("Slot F" + floor + ":S" + slotNumber + " is already occupied by " + occupyingVehicleRegNo);
        }
        this.occupyingVehicleRegNo = regNo.toUpperCase().trim();
    }

    public void vacate() {
        if (!isOccupied()) {
            throw new IllegalStateException("Slot F" + floor + ":S" + slotNumber + " is already vacant");
        }
        this.occupyingVehicleRegNo = null;
    }

    @Override
    public String toString() {
        return String.format("Slot[F%d:S%d, EV=%b, Occupied=%b]", floor, slotNumber, hasEVCharger, isOccupied());
    }
}
