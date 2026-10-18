package Task06;

/**
 * Task 6: Base abstract class in the Vehicle inheritance hierarchy.
 */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        System.out.println("  [Constructor Step 1] Vehicle(String regNo) invoked for: " + regNo);
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be null or blank");
        }
        this.regNo = regNo;
    }

    public String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[regNo=" + regNo + "]";
    }
}
