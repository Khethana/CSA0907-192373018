package Task09;

/**
 * Task 9: Abstraction - Abstract Classes vs Interfaces Demonstration.
 * 
 * Demonstrates:
 * 1. Abstract Class 'Vehicle' for stateful domain modeling and template methods.
 * 2. Interface 'Chargeable' with abstract, default, and static methods.
 * 3. Interface 'Payable' for payment settlement.
 * 4. Multiple interface implementation in 'ElectricCar'.
 * 5. Architectural analysis of reversing abstract classes and interfaces.
 */
public class AbstractionDemo {

    // --- 1. Capability Interfaces ---
    public interface Chargeable {
        double GRID_BASE_RATE = 12.0; // Implicitly public static final

        // Abstract method defining capability contract
        double chargingFee(int kWh);

        // Default method: Provides out-of-the-box standard calculation without breaking implementers
        default double expressChargeFee(int kWh) {
            return chargingFee(kWh) * 1.25; // 25% surcharge for rapid high-voltage charging
        }

        // Static method: Utility logic scoped strictly to interface namespace
        static double getGovernmentEVSubsidizedTariff() {
            return GRID_BASE_RATE * 0.85; // 15% clean energy subsidy
        }
    }

    public interface Payable {
        boolean processSettlement(double amount, String paymentMethod);
    }

    // --- 2. Abstract Base Class (Encapsulates State, Constructors & Invariants) ---
    public static abstract class Vehicle {
        private final String regNo; // Instance state impossible in an interface

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.isBlank()) {
                throw new IllegalArgumentException("Vehicle plate cannot be empty");
            }
            this.regNo = regNo.toUpperCase().trim();
        }

        public final String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        // Template Method: Common algorithm shared across entire hierarchy
        public double calculateParkingFee(int minutes) {
            if (minutes <= 0) return 0.0;
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    // --- 3. Concrete Specializations ---
    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    // Multiple Interface Implementation: inherits from Car, implements Chargeable and Payable
    public static class ElectricCar extends Car implements Chargeable, Payable {
        private final int batteryCapacity;

        public ElectricCar(String regNo, int batteryCapacity) {
            super(regNo);
            this.batteryCapacity = batteryCapacity;
        }

        @Override
        public double chargingFee(int kWh) {
            return kWh * Chargeable.GRID_BASE_RATE;
        }

        @Override
        public boolean processSettlement(double amount, String paymentMethod) {
            System.out.printf("  [Payment Gateway] Settled Rs. %.2f for %s via %s.\n", 
                    amount, getRegNo(), paymentMethod);
            return true;
        }

        public int getBatteryCapacity() { return batteryCapacity; }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 9: ABSTRACTION - ABSTRACT CLASSES VS INTERFACES DEMONSTRATION");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Instantiating Concrete Types via Abstract References ---");
        Vehicle standardCar = new Car("TN09CAR123");
        ElectricCar evCar = new ElectricCar("TN09EV999", 60);

        System.out.printf("Car %s: Rate Rs. %.2f/hr -> Fee for 75 mins: Rs. %.2f\n", 
                standardCar.getRegNo(), standardCar.ratePerHour(), standardCar.calculateParkingFee(75));

        System.out.println("\n--- 2. Exercising Multiple Capabilities via Interfaces ---");
        // EV acts as Vehicle
        double parkingFee = evCar.calculateParkingFee(75);
        // EV acts as Chargeable
        double normalCharge = evCar.chargingFee(20);
        double expressCharge = evCar.expressChargeFee(20); // Calling interface default method
        double subsidizedGrid = Chargeable.getGovernmentEVSubsidizedTariff(); // Calling interface static method

        System.out.printf("EV %s: Parking Fee: Rs. %.2f\n", evCar.getRegNo(), parkingFee);
        System.out.printf("EV %s: Normal Charging (20 kWh @ Rs. 12):  Rs. %.2f\n", evCar.getRegNo(), normalCharge);
        System.out.printf("EV %s: Express Charging (Default Method):   Rs. %.2f\n", evCar.getRegNo(), expressCharge);
        System.out.printf("Interface Static Subsidized Grid Tariff:   Rs. %.2f/kWh\n", subsidizedGrid);

        // EV acts as Payable
        double totalPayable = parkingFee + normalCharge;
        System.out.println("\n--- 3. Invoking Payable Interface Capability ---");
        evCar.processSettlement(totalPayable, "CAMPUS_STUDENT_WALLET");

        System.out.println("\n--- 4. Architectural Analysis: Why Vehicle cannot be an Interface ---");
        System.out.println("1. State Invariant: 'regNo' must be immutable and validated in a constructor.");
        System.out.println("   Interfaces cannot declare instance fields or constructors. Every subclass would");
        System.out.println("   be forced to duplicate 'private final String regNo' and validation logic!");
        System.out.println("2. Multiple Inheritance Constraint: Java disallows extending multiple classes.");
        System.out.println("   If Chargeable were an abstract class, ElectricCar could NOT extend Car!");
        System.out.println("===============================================================================");
    }
}
