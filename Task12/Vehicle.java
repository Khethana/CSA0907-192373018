package Task12;

/**
 * Task 12: Base abstract Vehicle class for Day 4 adaptation.
 */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be null or blank");
        }
        this.regNo = regNo.toUpperCase().trim();
    }

    public String getRegNo() { return regNo; }

    public abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Vehicle other) && regNo.equals(other.regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + regNo + "]";
    }
}
