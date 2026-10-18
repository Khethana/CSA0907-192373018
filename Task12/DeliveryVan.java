package Task12;

import java.time.LocalTime;

/**
 * Task 12: Delivery Van vehicle category.
 * Business Rule:
 * - Base rate is car rate (Rs. 30/hr).
 * - During peak business delivery window (09:00 AM to 17:00 PM / 5:00 PM),
 *   charged at DOUBLE the car rate: 2 * Rs. 30 = Rs. 60/hr.
 */
public class DeliveryVan extends Vehicle {
    private static final double STANDARD_RATE = 30.0;
    private static final double PEAK_MULTIPLIER = 2.0;

    private static final LocalTime PEAK_START = LocalTime.of(9, 0);
    private static final LocalTime PEAK_END = LocalTime.of(17, 0);

    public DeliveryVan(String regNo) {
        super(regNo);
    }

    @Override
    public double ratePerHour() {
        return STANDARD_RATE;
    }

    /**
     * Standard duration calculateFee defaulting to peak rate if assumed peak.
     */
    @Override
    public double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * STANDARD_RATE * PEAK_MULTIPLIER; // Rs. 60/hr
    }

    /**
     * Time-of-day aware fee calculation:
     * Evaluates whether entry time falls inside the 9 AM - 5 PM delivery congestion window.
     */
    public double calculateTimeAwareFee(int minutes, LocalTime entryTime) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;

        boolean isPeak = !entryTime.isBefore(PEAK_START) && entryTime.isBefore(PEAK_END);
        double effectiveRate = isPeak ? (STANDARD_RATE * PEAK_MULTIPLIER) : STANDARD_RATE;

        return hours * effectiveRate;
    }

    public static LocalTime getPeakStart() { return PEAK_START; }
    public static LocalTime getPeakEnd() { return PEAK_END; }
}
