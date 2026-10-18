package Task11;

import Task11.CampusSmartParkingSystem.*;

/**
 * Task 11: Comprehensive Test Suite and Verification Harness.
 * 
 * Verifies:
 * 1. Normal operations (TwoWheeler, Car, ElectricCar)
 * 2. Boundary conditions (Zero mins, Slab boundaries at 60 and 120 mins, Last stall capacity)
 * 3. Invalid operations (Duplicate entry, non-existent exit, negative duration)
 * 4. Pass-holder fee exemption rules
 * 5. Hand-Calculated Day Collection Scenario with mathematical step-by-step proof.
 */
public class SystemTestSuite {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 11: COMPREHENSIVE SYSTEM VERIFICATION & TEST SUITE");
        System.out.println("===============================================================================\n");

        // Small test facility: Floor 0 has 2 slots, Floor 1 has 1 slot (Total = 3 slots)
        CampusSmartParkingSystem system = new CampusSmartParkingSystem(new int[]{2, 1});

        User visitorUser = new User("USR-V1", UserCategory.VISITOR, false);
        User studentPassUser = new User("USR-S1", UserCategory.STUDENT, true);
        User staffPassUser = new User("USR-ST1", UserCategory.STAFF, true);

        System.out.println("--- 1. Testing Normal Operation & Slot Allocation ---");
        Vehicle tw = new TwoWheeler("TN09TW1001");
        Vehicle car = new Car("TN09CR2002");
        Vehicle ev = new ElectricCar("TN09EV3003");

        ParkingTicket t1 = system.enterVehicle(tw, visitorUser, false);
        ParkingTicket t2 = system.enterVehicle(car, visitorUser, false);
        ParkingTicket t3 = system.enterVehicle(ev, staffPassUser, true);

        System.out.printf("  Allocated %s to Floor %d, Slot %d [Ticket: %s]\n", 
                tw.getRegNo(), t1.getFloor(), t1.getSlot(), t1.getTicketId());
        System.out.printf("  Allocated %s to Floor %d, Slot %d [Ticket: %s]\n", 
                car.getRegNo(), t2.getFloor(), t2.getSlot(), t2.getTicketId());
        System.out.printf("  Allocated %s to Floor %d, Slot %d [Ticket: %s]\n", 
                ev.getRegNo(), t3.getFloor(), t3.getSlot(), t3.getTicketId());

        System.out.println("\n--- 2. Testing Boundary Condition: Last Slot & Saturated Facility ---");
        System.out.println("Free slots remaining: " + system.getFreeSlotsCount());
        Vehicle extraCar = new Car("TN09EX9999");
        ParkingTicket overflowTicket = system.enterVehicle(extraCar, visitorUser, false);
        System.out.println("Attempting to enter vehicle when facility is saturated -> Result: " + overflowTicket);

        System.out.println("\n--- 3. Testing Boundary Conditions: Exact Slab Durations & Zero Minutes ---");
        // Exit t1 (TwoWheeler): 0 minutes stay
        ParkingReceipt r1 = system.exitVehicle(tw.getRegNo(), 0, 0);
        System.out.printf("  [Boundary 0 mins]   %s stayed 0m -> Billable Hrs: %d -> Fee: Rs. %.2f\n", 
                r1.regNo(), r1.billableHours(), r1.totalPayable());

        // Re-enter tw to test exactly 60 minutes
        t1 = system.enterVehicle(tw, visitorUser, false);
        r1 = system.exitVehicle(tw.getRegNo(), 60, 0);
        System.out.printf("  [Boundary 60 mins]  %s stayed 60m -> Billable Hrs: %d -> Fee: Rs. %.2f (1 hr @ Rs.10)\n", 
                r1.regNo(), r1.billableHours(), r1.totalPayable());

        // Exit t2 (Car): exactly 61 minutes (crosses boundary into 2nd hour)
        ParkingReceipt r2 = system.exitVehicle(car.getRegNo(), 61, 0);
        System.out.printf("  [Boundary 61 mins]  %s stayed 61m -> Billable Hrs: %d -> Fee: Rs. %.2f (2 hrs @ Rs.30)\n", 
                r2.regNo(), r2.billableHours(), r2.totalPayable());

        // Exit t3 (EV): Staff Pass Holder with 120 minutes stay and 10 kWh charging
        ParkingReceipt r3 = system.exitVehicle(ev.getRegNo(), 120, 10);
        System.out.printf("  [Staff Pass + EV]   %s stayed 120m, 10 kWh -> Base Fee: Rs. %.2f (Waived), EV Fee: Rs. %.2f -> Total: Rs. %.2f\n", 
                r3.regNo(), r3.baseParkingFee(), r3.evChargingFee(), r3.totalPayable());

        System.out.println("\n--- 4. Testing Invalid Cases & Exception Handling ---");
        // Invalid Test A: Duplicate registration entry
        system.enterVehicle(new Car("TN01SAME11"), visitorUser, false);
        try {
            system.enterVehicle(new Car("TN01SAME11"), visitorUser, false);
        } catch (IllegalStateException e) {
            System.out.println("  [PASS] Thwarted duplicate vehicle entry: " + e.getMessage());
        }

        // Invalid Test B: Exiting an unregistered / already exited vehicle
        try {
            system.exitVehicle("GHOST-9999", 30, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] Thwarted invalid exit request: " + e.getMessage());
        }

        // Invalid Test C: Negative parking duration
        try {
            system.exitVehicle("TN01SAME11", -15, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] Thwarted negative parking duration: " + e.getMessage());
        }
        system.exitVehicle("TN01SAME11", 10, 0); // Clean up

        // -------------------------------------------------------------
        // PART 5: Hand-Calculated Day Collection Scenario Verification
        // -------------------------------------------------------------
        System.out.println("\n--- 5. Verification of Hand-Calculated Day Collection Scenario ---");
        System.out.println("Simulating fresh day collection on a dedicated facility instance (10 slots)...");
        CampusSmartParkingSystem dayFacility = new CampusSmartParkingSystem(new int[]{10});

        System.out.println("\nHand Calculation Ledger:");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("| Tx | Vehicle Reg No | Category     | Commuter | Mins | kWh | Hand Calculation Formula               | Expected Fee |");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("| 1  | TN01A1001      | Car          | Visitor  | 130  |  0  | ceil(130/60)=3 hrs * Rs. 30           | Rs.  90.00   |");
        System.out.println("| 2  | TN02B2002      | ElectricCar  | Visitor  |  75  | 15  | ceil(75/60)=2 hrs*30 + 15 kWh*12=60+180| Rs. 240.00   |");
        System.out.println("| 3  | TN03C3003      | TwoWheeler   | Visitor  |  45  |  0  | ceil(45/60)=1 hr * Rs. 10             | Rs.  10.00   |");
        System.out.println("| 4  | TN04D4004      | Car          | Student  | 180  |  0  | Monthly Pass Exemption -> Rs. 0        | Rs.   0.00   |");
        System.out.println("| 5  | TN05E5005      | ElectricCar  | Staff    | 120  | 20  | Pass Waived Parking(0) + 20 kWh*12=240 | Rs. 240.00   |");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("Expected Hand-Calculated Total Revenue = 90 + 240 + 10 + 0 + 240 = Rs. 580.00\n");

        // Executing programmatic transactions
        Vehicle v1 = new Car("TN01A1001");
        Vehicle v2 = new ElectricCar("TN02B2002");
        Vehicle v3 = new TwoWheeler("TN03C3003");
        Vehicle v4 = new Car("TN04D4004");
        Vehicle v5 = new ElectricCar("TN05E5005");

        dayFacility.enterVehicle(v1, visitorUser, false);
        dayFacility.enterVehicle(v2, visitorUser, true);
        dayFacility.enterVehicle(v3, visitorUser, false);
        dayFacility.enterVehicle(v4, studentPassUser, false);
        dayFacility.enterVehicle(v5, staffPassUser, true);

        ParkingReceipt rc1 = dayFacility.exitVehicle("TN01A1001", 130, 0);
        ParkingReceipt rc2 = dayFacility.exitVehicle("TN02B2002", 75, 15);
        ParkingReceipt rc3 = dayFacility.exitVehicle("TN03C3003", 45, 0);
        ParkingReceipt rc4 = dayFacility.exitVehicle("TN04D4004", 180, 0);
        ParkingReceipt rc5 = dayFacility.exitVehicle("TN05E5005", 120, 20);

        System.out.println("Program Output Verification:");
        System.out.printf("  Tx 1 (%s): Actual = Rs. %.2f | Match? %b\n", rc1.regNo(), rc1.totalPayable(), rc1.totalPayable() == 90.0);
        System.out.printf("  Tx 2 (%s): Actual = Rs. %.2f | Match? %b\n", rc2.regNo(), rc2.totalPayable(), rc2.totalPayable() == 240.0);
        System.out.printf("  Tx 3 (%s): Actual = Rs. %.2f | Match? %b\n", rc3.regNo(), rc3.totalPayable(), rc3.totalPayable() == 10.0);
        System.out.printf("  Tx 4 (%s): Actual = Rs. %.2f | Match? %b\n", rc4.regNo(), rc4.totalPayable(), rc4.totalPayable() == 0.0);
        System.out.printf("  Tx 5 (%s): Actual = Rs. %.2f | Match? %b\n", rc5.regNo(), rc5.totalPayable(), rc5.totalPayable() == 240.0);

        double actualTotalRevenue = dayFacility.getDailyTotalRevenue();
        System.out.printf("\nTotal Daily Revenue Computed by System: Rs. %.2f\n", actualTotalRevenue);
        System.out.printf("Total Daily Revenue Calculated by Hand: Rs. 580.00\n");
        System.out.printf("Mathematical Integrity Confirmed?      %s\n", (actualTotalRevenue == 580.0 ? "VERIFIED (100% MATCH)" : "FAILED"));
        System.out.println("===============================================================================");
    }
}
