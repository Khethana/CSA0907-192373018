package Task08;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Task 8 Deep Dive:
 * 1. Compile-Time Polymorphism (Static Binding via Method Overloading).
 * 2. Runtime Polymorphism (Dynamic Binding via Dynamic Method Dispatch).
 * 3. Execution trace of declared type vs actual runtime object.
 * 4. Empirical comparison of dynamic dispatch vs instanceof branching.
 */
public class PolymorphismDeepTraceDemo {

    public static class BillingCalculator {
        // Overload 1: Duration given directly as discrete integer minutes
        public double calculateFee(Vehicle v, int minutes) {
            System.out.println("  [Compile-Time Static Binding] calculateFee(Vehicle, int) resolved by compiler.");
            return v.calculateFee(minutes);
        }

        // Overload 2: Duration calculated from LocalTime entry and exit
        public double calculateFee(Vehicle v, LocalTime entry, LocalTime exit) {
            System.out.println("  [Compile-Time Static Binding] calculateFee(Vehicle, LocalTime, LocalTime) resolved by compiler.");
            long minutes = Duration.between(entry, exit).toMinutes();
            return v.calculateFee((int) minutes);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("TASK 8: COMPILE-TIME & RUNTIME POLYMORPHISM DEEP TRACE");
        System.out.println("===============================================================================\n");

        System.out.println("--- 1. Compile-Time Polymorphism (Method Overloading) ---");
        BillingCalculator calc = new BillingCalculator();
        Vehicle vCar = new Car("TN02B2");

        // Compiler selects overload based on argument signatures at compile-time:
        calc.calculateFee(vCar, 120);
        calc.calculateFee(vCar, LocalTime.of(9, 0), LocalTime.of(11, 0));

        System.out.println("\n--- 2. Runtime Polymorphism & Dynamic Method Dispatch Trace ---");
        // Declared Type: Vehicle
        // Actual Runtime Object: ElectricCar
        Vehicle polyVehicle = new ElectricCar("TN03C3");

        System.out.println("Declared Reference Type:   " + "Task08.Vehicle");
        System.out.println("Actual Object on JVM Heap: " + polyVehicle.getClass().getName());
        System.out.println("Invoking: polyVehicle.calculateFee(130)...");
        // Trace:
        // 1. JVM bytecode executes 'invokevirtual calculateFee:(I)D'
        // 2. Looks up the Virtual Method Table (vtable) of the actual heap object (ElectricCar)
        // 3. Inherited calculateFee in Vehicle is called, which invokes ratePerHour()
        // 4. ratePerHour() is dispatched dynamically to Car.ratePerHour() returning 30.0
        double fee = polyVehicle.calculateFee(130);
        System.out.println("Resulting Dispatched Fee:  Rs. " + fee);

        System.out.println("\n--- 3. Performance & Design: Dynamic Dispatch vs 'instanceof' Branching ---");
        final int ITERATIONS = 10_000_000;
        Vehicle[] testFleet = { new TwoWheeler("T1"), new Car("C1"), new ElectricCar("E1") };

        // Test A: Dynamic Dispatch (O(1) vtable lookup)
        long startDispatch = System.nanoTime();
        double sumDispatch = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            Vehicle v = testFleet[i % 3];
            sumDispatch += v.calculateFee(65);
        }
        long timeDispatch = System.nanoTime() - startDispatch;

        // Test B: Procedural instanceof checks and manual casts (O(k) branching)
        long startInstanceOf = System.nanoTime();
        double sumInstanceOf = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            Vehicle v = testFleet[i % 3];
            int hours = (65 + 59) / 60;
            if (v instanceof TwoWheeler) {
                sumInstanceOf += hours * 10.0;
            } else if (v instanceof ElectricCar) {
                sumInstanceOf += hours * 30.0;
            } else if (v instanceof Car) {
                sumInstanceOf += hours * 30.0;
            }
        }
        long timeInstanceOf = System.nanoTime() - startInstanceOf;

        System.out.printf("Dynamic Dispatch Time:   %.2f ms (O(1) table lookup, inlinable by JIT C2 compiler)\n", 
                timeDispatch / 1_000_000.0);
        System.out.printf("Instanceof Checks Time: %.2f ms (O(k) sequential branches, branch mispredictions)\n", 
                timeInstanceOf / 1_000_000.0);
        System.out.println("===============================================================================");
    }
}
