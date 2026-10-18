package Task06;

/**
 * Task 6: Multilevel subclass representing Electric Cars.
 * Demonstrates Multilevel Inheritance (Vehicle -> Car -> ElectricCar).
 */
public class ElectricCar extends Car {
    private final int batteryCapacityKWh;

    public ElectricCar(String regNo, int batteryCapacityKWh) {
        super(regNo); // Chains to Car constructor, which chains to Vehicle constructor
        this.batteryCapacityKWh = batteryCapacityKWh;
        System.out.println("  [Constructor Step 3] ElectricCar(String regNo, int batteryCapacityKWh) completed with capacity: " 
                + batteryCapacityKWh + " kWh");
    }

    public ElectricCar(String regNo) {
        this(regNo, 40); // Overloaded constructor defaulting to 40 kWh
    }

    public int getBatteryCapacityKWh() {
        return batteryCapacityKWh;
    }

    public double chargingFee(int kWh) {
        return kWh * 12.0; // Rs 12 per kWh
    }

    @Override
    public String toString() {
        return super.toString() + " (EV " + batteryCapacityKWh + "kWh)";
    }
}
