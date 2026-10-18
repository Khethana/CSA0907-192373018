package Task06;

/**
 * Task 6: Inheritance Trace and Alternative Composition Analysis Demonstration.
 */
public class InheritanceTraceDemo {

    // --- Composition Model for Alternative Concept Comparison ---
    public enum VehicleType {
        TWO_WHEELER(10.0),
        CAR(30.0),
        ELECTRIC_CAR(30.0);

        private final double ratePerHour;
        VehicleType(double rate) { this.ratePerHour = rate; }
        public double getRatePerHour() { return ratePerHour; }
    }

    public static class ComposedVehicle {
        private final String regNo;
        private final VehicleType type; // HAS-A relationship
        private final boolean isElectric;

        public ComposedVehicle(String regNo, VehicleType type, boolean isElectric) {
            this.regNo = regNo;
            this.type = type;
            this.isElectric = isElectric;
        }

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * type.getRatePerHour();
        }

        public double chargingFee(int kWh) {
            return isElectric ? kWh * 12.0 : 0.0;
        }

        public String getRegNo() { return regNo; }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 6: INHERITANCE HIERARCHY & CONSTRUCTOR TRACE DEMONSTRATION");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Constructor Execution Order Trace: Creating ElectricCar ---");
        System.out.println("Executing: Vehicle ev = new ElectricCar(\"TN03C3\", 60);");
        Vehicle ev = new ElectricCar("TN03C3", 60);
        System.out.println("Object successfully created: " + ev);

        System.out.println("\n--- 2. Verifying Polymorphic Behavior & Dynamic Method Dispatch ---");
        Vehicle[] fleet = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            ev
        };

        for (Vehicle v : fleet) {
            System.out.printf("Vehicle %-10s -> Rate/Hr: Rs. %.2f | Fee for 130 min: Rs. %.2f\n", 
                    v.getRegNo(), v.ratePerHour(), v.calculateFee(130));
        }

        System.out.println("\n--- 3. Alternative Concept: Inheritance (IS-A) vs Composition (HAS-A) ---");
        ComposedVehicle composedCar = new ComposedVehicle("TN02B2", VehicleType.CAR, false);
        ComposedVehicle composedEV = new ComposedVehicle("TN03C3", VehicleType.ELECTRIC_CAR, true);

        System.out.println("Composed Car Fee: Rs. " + composedCar.calculateFee(130));
        System.out.println("Composed EV Fee with 10 kWh: Rs. " + (composedEV.calculateFee(130) + composedEV.chargingFee(10)));

        System.out.println("\nExtensibility Comparison:");
        System.out.println("A) Inheritance (IS-A):");
        System.out.println("   - Adding a new vehicle (e.g. EBike): create 'class EBike extends Vehicle'.");
        System.out.println("   - Zero modifications to existing classes. Follows Open-Closed Principle (OCP).");
        System.out.println("B) Composition (HAS-A):");
        System.out.println("   - Adding a new vehicle requires modifying the enum VehicleType and ComposedVehicle class.");
        System.out.println("   - If vehicles have complex distinct states (battery BMS telemetry, trailer permits),");
        System.out.println("     ComposedVehicle becomes a bloated God-class with unused fields.");
        System.out.println("===============================================================================");
    }
}
