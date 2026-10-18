# TASK 9: Introduce Abstraction
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4  
**Course Outcome:** CO1 — Abstraction, Abstract Classes vs Interfaces, Default/Static Interface Methods  

---

## 1. Abstract Classes vs. Interfaces: Separation of Concerns

Abstraction decouples the **high-level specification** (what the system does) from the **low-level implementation** (how it does it).

### 1.1 Why `Vehicle` is an Abstract Class (Not an Interface)
1. **Shared Stateful Invariants:** Every motor vehicle entering campus possesses a registration plate string (`regNo`). An abstract class allows declaring a private, immutable instance field `private final String regNo;` that is stored directly on the heap. Interfaces cannot declare instance fields (all interface fields are implicitly `public static final` compile-time constants).
2. **Mandatory Constructor Invariants:** `Vehicle` enforces that no vehicle object can ever be instantiated without passing a valid, non-blank registration number via `protected Vehicle(String regNo)`. Interfaces cannot have constructors.
3. **Template Method Pattern:** The billing duration formula $\lceil \text{minutes} / 60 \rceil \times \text{ratePerHour()}$ is identical across all subclasses. Declaring `calculateParkingFee(int minutes)` as a concrete method in `Vehicle` eliminates boilerplate duplication across subclasses.
4. **Abstract Method Hook:** Only `ratePerHour()` is declared `protected abstract double ratePerHour();`, forcing concrete subclasses to supply their unique tariff while inheriting common state and calculations.

### 1.2 Why `Chargeable` and `Payable` are Interfaces
1. **Orthogonal Capabilities (Cross-Cutting Concerns):**
   - Charging an electric battery is a **capability**, not an identity. A vehicle is fundamentally a transport device (`is-a Vehicle`). Some vehicles happen to have electric batteries (`implements Chargeable`).
   - If `Chargeable` were a class, we could not easily grant charging capabilities to disparate non-vehicle devices on campus (e.g., `ElectricLawnmower`, `SecurityDrone`, `LaptopChargingKiosk`).
2. **Multiple Behaviour Composition:**
   - Java does not support multiple class inheritance (a class can have only one superclass).
   - Interfaces allow a class to inherit its primary lineage while acquiring multiple external contracts:
     ```java
     public class ElectricCar extends Car implements Chargeable, Payable
     ```
     `ElectricCar` simultaneously behaves as a `Vehicle`, a `Car`, a `Chargeable` energy consumer, and a `Payable` financial settlement party.

### 1.3 Role of Default and Static Methods in Interfaces
- **Default Methods (`default`):** Added in Java 8 to permit interface evolution without breaking backwards compatibility. In `Chargeable`, `default double expressChargeFee(int kWh)` provides an out-of-the-box calculation (charging fee + 25% rapid-charge surcharge) that any implementer can immediately use or choose to override.
- **Static Methods (`static`):** Provide pure utility functions scoped directly under the interface namespace. Example: `Chargeable.getGovernmentEVSubsidizedTariff()` computes regional clean-energy tariff reductions without requiring an instantiated object.

---

## 2. Alternative Concept and Complexity Analysis

### 2.1 Reversing the Design: `Vehicle` as Interface & `Chargeable` as Abstract Class

Suppose the architecture were reversed:
```java
// Alternative Concept
public interface Vehicle {
    // Default method for fee
    default double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }
    double ratePerHour();
    String getRegNo();
}

public abstract class Chargeable {
    private int batteryCapacity;
    public abstract double chargingFee(int kWh);
}
```

### 2.2 Critical Flaws in the Reversed Architecture
1. **Destruction of DRY (Don't Repeat Yourself) & Encapsulation:**
   - Because `Vehicle` is an interface, it cannot hold `private final String regNo;`.
   - Every single subclass (`TwoWheeler`, `Car`, `DeliveryVan`, `Truck`) must independently declare:
     ```java
     private final String regNo;
     public TwoWheeler(String regNo) { ... } // Duplicated validation in 10+ classes!
     ```
   - If license plate formatting or validation changes, 10 separate classes must be edited.
2. **Catastrophic Multiple Inheritance Collision in Java:**
   - Java strictly limits classes to **single inheritance**.
   - If `Chargeable` is an abstract class, then `ElectricCar` must extend `Chargeable` (`class ElectricCar extends Chargeable implements Vehicle`).
   - Consequently, `ElectricCar` **CANNOT EXTEND `Car`**!
   - All car-specific logic (dimensions, standard 4-wheel parking slot compatibility, base rate `30.0`) must be copy-pasted into `ElectricCar`. The clean hierarchy `Vehicle -> Car -> ElectricCar` collapses.
3. **Bytecode Invocation Overhead:**
   - Method calls on interfaces generate the `invokeinterface` bytecode instruction. While modern JVMs with Monomorphic Inline Caching (MIC) optimize this, `invokeinterface` traditionally required searching an Interface Table (itable), whereas abstract class method dispatch uses direct index lookups in a Virtual Method Table (vtable).
4. **Memory Space Impact:**
   - Duplicating fields across multiple subclasses increases code footprint in Metaspace and risks inconsistent field alignment on the heap.

**Conclusion:** The original design (`Vehicle` as an abstract class encapsulating mandatory identity, `Chargeable` and `Payable` as capability interfaces) is the only robust, extensible object-oriented solution.
