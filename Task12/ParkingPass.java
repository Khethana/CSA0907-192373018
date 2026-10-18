package Task12;

import java.time.LocalDate;

/**
 * Task 12: ParkingPass abstraction supporting One-Day Passes and Monthly Passes.
 */
public interface ParkingPass {
    String getPassId();
    String getPassType();
    boolean isValidOn(LocalDate queryDate);
    double getPurchaseCost();
}
