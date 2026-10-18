package Task12;

import java.time.LocalDate;

/**
 * Task 12: Monthly Pass implementation for students and staff.
 */
public class MonthlyPass implements ParkingPass {
    private final String passId;
    private final LocalDate startDate;
    private final LocalDate expiryDate;
    private static final double MONTHLY_PASS_COST = 500.0;

    public MonthlyPass(String passId, LocalDate startDate, LocalDate expiryDate) {
        if (passId == null || startDate == null || expiryDate == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (expiryDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Expiry date cannot precede start date");
        }
        this.passId = passId;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }

    @Override public String getPassId() { return passId; }
    @Override public String getPassType() { return "MONTHLY_PASS"; }
    @Override public boolean isValidOn(LocalDate queryDate) {
        return !queryDate.isBefore(startDate) && !queryDate.isAfter(expiryDate);
    }
    @Override public double getPurchaseCost() { return MONTHLY_PASS_COST; }
}
