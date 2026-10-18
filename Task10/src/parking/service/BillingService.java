package parking.service;

/**
 * Task 10: Service managing billing computations and input validation.
 */
public class BillingService {

    public static final double BASE_RATE_TWO_WHEELER = 10.0;
    public static final double BASE_RATE_CAR = 30.0;
    public static final double EV_CHARGING_RATE_KWH = 12.0;

    /**
     * Computes parking fee with validating logic against invalid durations.
     */
    public double computeFee(String vehicleType, int minutes, boolean isPassHolder) {
        if (isPassHolder) {
            return 0.0;
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative: " + minutes);
        }
        if (minutes == 0) {
            return 0.0;
        }

        int hours = (minutes + 59) / 60;
        double rate = switch (vehicleType.toUpperCase()) {
            case "TWO_WHEELER" -> BASE_RATE_TWO_WHEELER;
            case "CAR", "ELECTRIC_CAR" -> BASE_RATE_CAR;
            default -> throw new IllegalArgumentException("Unknown vehicle category: " + vehicleType);
        };

        return hours * rate;
    }
}
