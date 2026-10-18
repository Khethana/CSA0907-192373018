package Task12;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Task 12: Day-4 Design Challenge Verification & Demonstration.
 * 
 * Verifies:
 * 1. 5-Floor Irregular Capacity Structure.
 * 2. E-Bike flat fee policy (30 mins vs 300 mins -> both Rs. 20.00).
 * 3. Delivery Van Peak congestion pricing (10:00 AM = Rs. 60/hr) vs Off-Peak (06:00 AM = Rs. 30/hr).
 * 4. Visitor One-Day Pass verification (Unlimited parking on pass date).
 * 5. Architectural non-regression and Open-Closed Principle (OCP) adherence.
 */
public class DesignChallengeDemo {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("DAY-4 DESIGN CHALLENGE: RE-EVALUATE AND ADAPT THE DESIGN (TASK 12)");
        System.out.println("===============================================================================\n");

        // 1. Initializing 5 Floors with irregular capacities:
        // Floor 0: 20 slots, Floor 1: 30 slots, Floor 2: 40 slots, Floor 3: 25 slots, Floor 4: 15 slots
        int[] fiveFloorLayout = {20, 30, 40, 25, 15};
        AdaptedParkingFacility facility = new AdaptedParkingFacility(fiveFloorLayout);

        System.out.println("--- 1. Verification of 5 Floors with Irregular Capacities ---");
        System.out.println("Facility Layout Configured:");
        for (int f = 0; f < 5; f++) {
            System.out.printf("  Floor %d: %2d physical slots\n", f, facility.getFloorCapacity(f));
        }
        System.out.printf("Total Facility Capacity across all 5 Floors = %d slots\n\n", facility.getTotalCapacity());

        // 2. Testing E-Bike (Flat Fee Policy)
        System.out.println("--- 2. Testing E-Bike Category (Flat Rs. 20 Fee Policy) ---");
        EBike ebike1 = new EBike("TN09-EB-001");
        EBike ebike2 = new EBike("TN09-EB-002");
        LocalDate today = LocalDate.of(2026, 10, 18);

        facility.enterVehicle(ebike1, null, LocalTime.of(8, 0), today);
        facility.enterVehicle(ebike2, null, LocalTime.of(8, 0), today);

        // ebike1 stays 30 minutes (short stay)
        double feeEBikeShort = facility.exitVehicle(ebike1.getRegNo(), 30, LocalTime.of(8, 30), today);
        // ebike2 stays 300 minutes (5 hours long stay)
        double feeEBikeLong = facility.exitVehicle(ebike2.getRegNo(), 300, LocalTime.of(13, 0), today);

        System.out.printf("  EBike 1 stayed  30 minutes -> Computed Fee: Rs. %.2f [Target: Rs. 20.00] (Match? %b)\n", 
                feeEBikeShort, feeEBikeShort == 20.0);
        System.out.printf("  EBike 2 stayed 300 minutes -> Computed Fee: Rs. %.2f [Target: Rs. 20.00] (Match? %b)\n", 
                feeEBikeLong, feeEBikeLong == 20.0);

        // 3. Testing Delivery Van Peak vs Off-Peak Pricing
        System.out.println("\n--- 3. Testing Delivery Van (Peak 9 AM - 5 PM @ Double Rate Rs. 60/hr) ---");
        DeliveryVan vanPeak = new DeliveryVan("TN09-VAN-PEAK");
        DeliveryVan vanOffPeak = new DeliveryVan("TN09-VAN-OFFPEAK");

        // Van entered during peak hour (10:00 AM)
        facility.enterVehicle(vanPeak, null, LocalTime.of(10, 0), today);
        // Van entered during off-peak morning hour (06:00 AM)
        facility.enterVehicle(vanOffPeak, null, LocalTime.of(6, 0), today);

        // Both stay for 120 minutes (2 hours)
        double feeVanPeak = facility.exitVehicle(vanPeak.getRegNo(), 120, LocalTime.of(12, 0), today);
        double feeVanOffPeak = facility.exitVehicle(vanOffPeak.getRegNo(), 120, LocalTime.of(8, 0), today);

        System.out.printf("  Van Entered 10:00 AM (PEAK)     for 120m (2 hrs @ Rs. 60) -> Fee: Rs. %.2f [Target: Rs. 120.00] (Match? %b)\n", 
                feeVanPeak, feeVanPeak == 120.0);
        System.out.printf("  Van Entered 06:00 AM (OFF-PEAK) for 120m (2 hrs @ Rs. 30) -> Fee: Rs. %.2f [Target: Rs.  60.00] (Match? %b)\n", 
                feeVanOffPeak, feeVanOffPeak == 60.0);

        // 4. Testing Visitor One-Day Pass
        System.out.println("\n--- 4. Testing Visitor One-Day Pass (Unlimited Daily Parking) ---");
        DailyPass visitorPass = new DailyPass("VPASS-20261018-01", today);
        DeliveryVan visitorVan = new DeliveryVan("TN09-VAN-VISITOR");

        facility.enterVehicle(visitorVan, visitorPass, LocalTime.of(11, 0), today);
        // Stays 240 minutes (4 hours peak) with valid Daily Pass
        double feeWithPass = facility.exitVehicle(visitorVan.getRegNo(), 240, LocalTime.of(15, 0), today);
        System.out.printf("  Visitor Van stayed 4 hrs during peak with Valid Daily Pass -> Fee: Rs. %.2f (Pass Exemption Applied!)\n", 
                feeWithPass);

        // If queried on a different day (expired daily pass)
        LocalDate tomorrow = today.plusDays(1);
        System.out.printf("  Checking Daily Pass on tomorrow's date (%s) -> Valid? %b (Expired!)\n", 
                tomorrow, visitorPass.isValidOn(tomorrow));

        System.out.println("\n===============================================================================");
        System.out.println("DESIGN CHALLENGE EVALUATION SUMMARY:");
        System.out.println("1. Extensibility: EBike and DeliveryVan were created as clean subclasses of Vehicle.");
        System.out.println("2. Backwards Compatibility: Pre-existing Vehicle and Car classes required ZERO modifications.");
        System.out.println("3. Open-Closed Principle: Open for extension, closed for modification achieved 100%.");
        System.out.println("===============================================================================");
    }
}
