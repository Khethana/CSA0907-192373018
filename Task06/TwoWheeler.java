package Task06;

/**
 * Task 6: Concrete subclass representing two-wheelers.
 * Demonstrates Hierarchical Inheritance (Vehicle -> TwoWheeler).
 */
public class TwoWheeler extends Vehicle {

    public TwoWheeler(String regNo) {
        super(regNo); // Explicit call to superclass constructor
        System.out.println("  [Constructor Step 2] TwoWheeler(String regNo) completed.");
    }

    @Override
    protected double ratePerHour() {
        return 10.0; // Two-wheeler base rate: Rs 10/hr
    }
}
