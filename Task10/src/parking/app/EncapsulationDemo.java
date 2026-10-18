package parking.app;

import parking.model.ParkingSlot;
import parking.service.BillingService;
import parking.service.ParkingFacility;

/**
 * Task 10: Encapsulation, Package Visibility, Setter Validation & Wrapper Pitfalls Demonstration.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 10: ENCAPSULATION, ACCESS MODIFIERS & WRAPPER PITFALLS");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Package Modifiers and Encapsulation Integrity ---");
        ParkingFacility facility = new ParkingFacility(new int[]{5, 10});
        ParkingSlot allocatedSlot = facility.allocate("TN01-CAMPUS-01", true);
        System.out.println("Allocated Slot via Public API: " + allocatedSlot);

        // Attempting to mutate private field directly is prevented by javac:
        // allocatedSlot.occupyingVehicleRegNo = "HACKED"; // Compilation Error: private access!
        System.out.println("Field 'occupyingVehicleRegNo' is private -> direct access blocked by compiler.");

        System.out.println("\n--- 2. Validating Setters Thwarting Invariant Violations ---");
        // Test A: Blank plate rejection
        try {
            allocatedSlot.assignVehicle("");
        } catch (IllegalArgumentException e) {
            System.out.println("  [Vetting Success] Rejected blank plate: " + e.getMessage());
        }

        // Test B: Double booking rejection
        try {
            allocatedSlot.assignVehicle("TN02-NEW-CAR");
        } catch (IllegalStateException e) {
            System.out.println("  [Vetting Success] Prevented double-booking: " + e.getMessage());
        }

        // Test C: Negative duration rejection
        BillingService billing = new BillingService();
        try {
            billing.computeFee("CAR", -45, false);
        } catch (IllegalArgumentException e) {
            System.out.println("  [Vetting Success] Prevented negative duration fee: " + e.getMessage());
        }

        System.out.println("\n--- 3. Wrapper Classes, Autoboxing & The Integer Cache Pitfall ---");
        // The Java Integer Cache: Values between -128 and 127 are cached by Integer.valueOf()
        Integer cachedInt1 = 127;
        Integer cachedInt2 = 127;
        System.out.printf("Comparing Integer(127) == Integer(127): %b (Same cached heap object)\n", 
                (cachedInt1 == cachedInt2));

        Integer nonCachedInt1 = 128;
        Integer nonCachedInt2 = 128;
        System.out.printf("Comparing Integer(128) == Integer(128): %b [CRITICAL BUG IF USING '==']!\n", 
                (nonCachedInt1 == nonCachedInt2));
        System.out.printf("Comparing Integer(128).equals(Integer(128)): %b [CORRECT EQUALITY]\n", 
                nonCachedInt1.equals(nonCachedInt2));

        // Autoboxing / Unboxing NullPointerException Pitfall
        Integer nullDuration = null;
        try {
            int unboxedDuration = nullDuration; // Implicit null.intValue() throws NPE!
            System.out.println("Unboxed: " + unboxedDuration);
        } catch (NullPointerException e) {
            System.out.println("  [NPE Caught] Unboxing a null wrapper object throws NullPointerException!");
        }

        System.out.println("\n--- 4. Alternative Concept: Public Fields vs Encapsulation ---");
        System.out.println("If fields were public (e.g. 'public int duration'):");
        System.out.println(" - Calling code must validate duration < 0 at every entrance/exit gate.");
        System.out.println(" - If 15 different controllers/handlers interact with slots, validation must be copied 15 times.");
        System.out.println(" - A single junior developer forgetting 'if (slot < 0)' corrupts the database.");
        System.out.println("Encapsulation centralizes validation at the boundary, ensuring 100% invariant consistency.");
        System.out.println("===============================================================================");
    }
}
