package Task12;

/**
 * Task 12: E-Bike vehicle category.
 * Business Rule: Charged a flat fee (e.g. Rs. 20.00 flat) regardless of duration.
 */
public class EBike extends Vehicle {
    private static final double FLAT_FEE = 20.0;

    public EBike(String regNo) {
        super(regNo);
    }

    @Override
    public double ratePerHour() {
        return 0.0; // Flat fee structure; not hourly based
    }

    /**
     * Overrides calculateFee to return the mandatory flat fee regardless of stay duration.
     */
    @Override
    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        return FLAT_FEE;
    }
}
