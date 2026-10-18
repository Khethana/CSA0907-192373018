package Task02;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Task 2: Data Representation and Precision Analysis.
 * 
 * Demonstrates:
 * 1. Integer division truncation & casting pitfalls in fee calculations.
 * 2. Robust ceiling arithmetic using pure integer arithmetic and Math.ceil().
 * 3. Array structures comparison:
 *    - Jagged Array: String[floor][slot]
 *    - 1D Flat Array: String[floor * maxSlotsPerFloor + slot]
 *    - Dynamic Collection: ArrayList<String[]>
 * 4. Space waste analysis when floors have heterogeneous slot counts.
 */
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 2: DATA REPRESENTATION & CASTING PRECISION ANALYSIS");
        System.out.println("===============================================================================\n");

        // -------------------------------------------------------------
        // PART 1: The Integer Division & Casting Pitfall in Fee Billing
        // -------------------------------------------------------------
        System.out.println("--- 1. Billing Precision & Truncation Bug Demonstration ---");
        int parkedMinutes = 65; // 1 hour and 5 minutes. Policy: Every started hour charged in full.
        double ratePerHour = 30.0; // Standard car rate

        // Case A: Flawed integer division (truncation)
        int flawedHoursA = parkedMinutes / 60; // 65 / 60 evaluates to 1
        double flawedFeeA = flawedHoursA * ratePerHour;

        // Case B: Flawed casting after floating division without ceiling
        int flawedHoursB = (int) (parkedMinutes / 60.0); // 1.08333 cast to int truncates to 1
        double flawedFeeB = flawedHoursB * ratePerHour;

        // Case C: Flawed integer division with floating cast
        double flawedFeeC = (double) (parkedMinutes / 60) * ratePerHour; // Still 1 * 30.0 = 30.0

        // Case D: Correct approach 1: Pure integer arithmetic ceiling
        // Formula: (minutes + 59) / 60
        int correctHours1 = (parkedMinutes + 59) / 60;
        double correctFee1 = correctHours1 * ratePerHour;

        // Case E: Correct approach 2: Math.ceil with floating point
        int correctHours2 = (int) Math.ceil(parkedMinutes / 60.0);
        double correctFee2 = correctHours2 * ratePerHour;

        System.out.printf("Stay Duration: %d minutes | Hourly Rate: Rs. %.2f\n", parkedMinutes, ratePerHour);
        System.out.printf("  [BUG] Flawed integer division (65 / 60):          %d hr -> Fee: Rs. %.2f (Undercharged by Rs. 30!)\n", 
                flawedHoursA, flawedFeeA);
        System.out.printf("  [BUG] Truncated float cast (int)(65 / 60.0):      %d hr -> Fee: Rs. %.2f (Undercharged by Rs. 30!)\n", 
                flawedHoursB, flawedFeeB);
        System.out.printf("  [BUG] Cast of truncated int (double)(65/60)*rate: %d hr -> Fee: Rs. %.2f (Undercharged by Rs. 30!)\n", 
                flawedHoursA, flawedFeeC);
        System.out.printf("  [FIX] Integer Ceiling ((minutes + 59) / 60):      %d hr -> Fee: Rs. %.2f [CORRECT]\n", 
                correctHours1, correctFee1);
        System.out.printf("  [FIX] Math.ceil (Math.ceil(minutes / 60.0)):      %d hr -> Fee: Rs. %.2f [CORRECT]\n\n", 
                correctHours2, correctFee2);

        // Boundary testing for Ceiling formula
        System.out.println("Testing Ceiling Formula across Boundary Durations:");
        int[] testDurations = {0, 1, 59, 60, 61, 119, 120, 121};
        for (int d : testDurations) {
            int billableHrs = (d == 0) ? 0 : (d + 59) / 60;
            System.out.printf("  Duration: %3d mins -> Billable Hours: %d -> Fee: Rs. %.2f\n", 
                    d, billableHrs, billableHrs * ratePerHour);
        }
        System.out.println();

        // -------------------------------------------------------------
        // PART 2: Array Representation: Jagged vs 1D Flat vs ArrayList
        // -------------------------------------------------------------
        System.out.println("--- 2. Floor & Slot Array Representation Analysis ---");
        // Realistic campus facility: Floor 0 (Ground) = 20 slots, Floor 1 = 50 slots, Floor 2 = 30 slots
        int[] slotsPerFloor = {20, 50, 30};
        int totalPhysicalSlots = 20 + 50 + 30; // 100 slots

        System.out.println("Facility Layout: Floor 0 = 20 slots, Floor 1 = 50 slots, Floor 2 = 30 slots.");
        System.out.println("Total Actual Physical Slots = " + totalPhysicalSlots);

        // Structure 1: Jagged 2D Array
        String[][] jaggedSlots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            jaggedSlots[f] = new String[slotsPerFloor[f]];
        }

        // Structure 2: 1D Flat Array
        // Must use max(slotsPerFloor) = 50 to prevent index overlapping
        int maxSlots = 50;
        int flatArrayLength = slotsPerFloor.length * maxSlots; // 3 * 50 = 150
        String[] flatSlots = new String[flatArrayLength];

        // Structure 3: ArrayList<String[]>
        ArrayList<String[]> listSlots = new ArrayList<>(slotsPerFloor.length);
        for (int slotsCount : slotsPerFloor) {
            listSlots.add(new String[slotsCount]);
        }

        System.out.println("\nMemory Allocation & Space Utilization Comparison:");
        System.out.printf("1. Jagged Array (String[][]):\n");
        System.out.printf("   - Total elements allocated: %d\n", (20 + 50 + 30));
        System.out.printf("   - Memory Wasted: 0 slots (0.0%% waste)\n");
        System.out.printf("   - Access: slots[f][s] directly in O(1) time.\n");
        System.out.printf("   - Bounds Check: Naturally throws ArrayIndexOutOfBoundsException on invalid floor/slot.\n\n");

        System.out.printf("2. Flat 1D Array (String[] with index = f * maxSlots + s):\n");
        System.out.printf("   - Total elements allocated: %d (3 floors * 50 max slots)\n", flatArrayLength);
        System.out.printf("   - Memory Wasted: %d slots (%.1f%% wasted memory!)\n", 
                (flatArrayLength - totalPhysicalSlots), 
                ((flatArrayLength - totalPhysicalSlots) * 100.0 / flatArrayLength));
        System.out.printf("   - CRITICAL FLAW: Phantom slot vulnerability! Accessing Floor 0, Slot 35 maps to index %d,\n" +
                          "     which exists in the array despite Floor 0 having only 20 physical slots!\n\n", (0 * 50 + 35));

        System.out.printf("3. Dynamic Collection (ArrayList<String[]>):\n");
        System.out.printf("   - Total elements allocated: 100 slot references + ArrayList wrapper overhead.\n");
        System.out.printf("   - Access: list.get(f)[s] in O(1) time.\n");
        System.out.printf("   - Resizing flexibility: Allows runtime floor additions.\n");
        System.out.println("===============================================================================");
    }
}
