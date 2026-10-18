package Task12;

import java.time.LocalDate;

/**
 * Task 12: One-Day Pass implementation for visitors.
 * Grants unlimited campus parking for the specific date purchased.
 */
public class DailyPass implements ParkingPass {
    private final String passId;
    private final LocalDate validDate;
    private static final double DAILY_PASS_COST = 100.0; // Flat one-day pass fee

    public DailyPass(String passId, LocalDate validDate) {
        if (passId == null || validDate == null) {
            throw new IllegalArgumentException("Pass ID and valid date cannot be null");
        }
        this.passId = passId;
        this.validDate = validDate;
    }

    @Override public String getPassId() { return passId; }
    @Override public String getPassType() { return "ONE_DAY_VISITOR_PASS"; }
    @Override public boolean isValidOn(LocalDate queryDate) {
        return validDate.equals(queryDate);
    }
    @Override public double getPurchaseCost() { return DAILY_PASS_COST; }

    public LocalDate getValidDate() { return validDate; }
}
