package Task06;

/**
 * Task 6: Subclass representing standard four-wheel cars.
 * Demonstrates Hierarchical Inheritance (Vehicle -> Car).
 */
public class Car extends Vehicle {

    public Car(String regNo) {
        super(regNo);
        System.out.println("  [Constructor Step 2] Car(String regNo) completed.");
    }

    @Override
    protected double ratePerHour() {
        return 30.0; // Car base rate: Rs 30/hr
    }
}
