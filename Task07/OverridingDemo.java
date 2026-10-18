package Task07;

import java.util.HashSet;
import java.util.Set;

/**
 * Task 7: Method Overriding, Specialization, Final Keywords, and Object Contracts.
 */
public class OverridingDemo {

    public static abstract class Vehicle {
        // final field: Once initialized in constructor, cannot be mutated
        private final String regNo;

        protected Vehicle(String regNo) {
            this.regNo = regNo.toUpperCase().trim();
        }

        // final method: Invariant contract - subclasses cannot change how regNo is exposed
        public final String getRegNo() {
            return regNo;
        }

        // Hook method for subclass specialization
        protected abstract double ratePerHour();

        // Template Method: Enforces the billing rule ceil(minutes / 60) * ratePerHour()
        // Subclasses specialize ratePerHour(), but the core algorithm cannot be broken
        public final double calculateFee(int minutes) {
            if (minutes <= 0) return 0.0;
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        // Overriding java.lang.Object methods
        @Override
        public String toString() {
            return String.format("%s[regNo=%s, rate=Rs.%.2f/hr]", 
                    getClass().getSimpleName(), regNo, ratePerHour());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            // Logical equality based on vehicle registration identity
            if (!(obj instanceof Vehicle other)) return false;
            return this.regNo.equals(other.regNo);
        }

        @Override
        public int hashCode() {
            // Must satisfy contract: if a.equals(b), then a.hashCode() == b.hashCode()
            return regNo.hashCode();
        }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() {
            return 10.0;
        }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() {
            return 30.0;
        }
    }

    // final class: Prevents further subclassing for security & complete specialization
    public static final class ElectricCar extends Car {
        private final int batteryKWh;

        public ElectricCar(String regNo, int batteryKWh) {
            super(regNo);
            this.batteryKWh = batteryKWh;
        }

        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }

        @Override
        public String toString() {
            return super.toString() + " (Electric, " + batteryKWh + " kWh)";
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 7: METHOD OVERRIDING, FINAL KEYWORDS & OBJECT CONTRACTS");
        System.out.println("===============================================================================\n");

        Vehicle v1 = new TwoWheeler("TN-09-AB-1234");
        Vehicle v2 = new Car("TN-09-CD-5678");
        Vehicle v3 = new ElectricCar("TN-09-CD-5678", 50); // Same plate as v2!

        System.out.println("--- 1. Subclass Specialization & toString() Overriding ---");
        System.out.println("TwoWheeler:  " + v1);
        System.out.println("Car:         " + v2);
        System.out.println("ElectricCar: " + v3);

        System.out.println("\n--- 2. Specialized Fee Calculation via Dynamic Dispatch ---");
        System.out.printf("%s Fee for 125 min: Rs. %.2f\n", v1.getRegNo(), v1.calculateFee(125));
        System.out.printf("%s Fee for 125 min: Rs. %.2f\n", v2.getRegNo(), v2.calculateFee(125));

        System.out.println("\n--- 3. Testing Object Identity vs Logical Equality (equals & hashCode) ---");
        System.out.println("v2 (Car) reference:         " + System.identityHashCode(v2));
        System.out.println("v3 (ElectricCar) reference: " + System.identityHashCode(v3));
        System.out.println("Reference Equality (v2 == v3):     " + (v2 == v3));
        System.out.println("Logical Equality (v2.equals(v3)):  " + v2.equals(v3) + " [Both share plate TN-09-CD-5678]");
        System.out.println("Symmetric Equality (v3.equals(v2)): " + v3.equals(v2));
        System.out.println("v2.hashCode(): " + v2.hashCode());
        System.out.println("v3.hashCode(): " + v3.hashCode());
        System.out.println("Hash Codes Identical? " + (v2.hashCode() == v3.hashCode()));

        System.out.println("\n--- 4. Verification in Java Collections (HashSet Deduplication) ---");
        Set<Vehicle> parkedSet = new HashSet<>();
        parkedSet.add(v2);
        boolean addedDuplicate = parkedSet.add(v3); // Should return false because v2.equals(v3)
        System.out.println("Added Car 'TN-09-CD-5678' to Set. Current Set Size: " + parkedSet.size());
        System.out.println("Attempting to add ElectricCar with same plate: Success? " + addedDuplicate);
        System.out.println("Set Size after duplicate insertion attempt: " + parkedSet.size());
        System.out.println("Set contains v3? " + parkedSet.contains(v3));

        System.out.println("\n--- 5. Architectural Value of 'final' ---");
        System.out.println(" - 'final regNo' prevents runtime plate tampering.");
        System.out.println(" - 'final calculateFee()' enforces template method integrity across all subclasses.");
        System.out.println(" - 'final class ElectricCar' stops unapproved inheritance extensions.");
        System.out.println("===============================================================================");
    }
}
