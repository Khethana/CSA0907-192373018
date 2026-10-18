# TASK 7: Override and Specialise Behaviour
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4  
**Course Outcome:** CO1 — Method Overriding, Java Language Rules, Final Modifiers, and Identity vs Equality  

---

## 1. Method Overriding Mechanics

Method overriding allows a subclass to provide a specific implementation of a method that is already declared in its superclass:

### 1.1 Overridden Methods in Campus Smart-Parking System
1. **`ratePerHour()`:**
   - Declared as an abstract protected hook in `Vehicle`.
   - Overridden in `TwoWheeler` to return `10.0`.
   - Overridden in `Car` to return `30.0`.
2. **`toString()`:**
   - Overridden to provide meaningful, human-readable logging and ticket slip generation (`TwoWheeler[regNo=TN01A1, rate=Rs.10.00/hr]`).
3. **`equals(Object obj)` and `hashCode()`:**
   - Overridden to enforce **domain identity** over physical JVM memory address identity.

### 1.2 Rules of Overriding in the Java Language Specification (JLS)
1. **Method Signature Identity:** The method name, parameter count, and parameter types must match the superclass method exactly.
2. **Return Type Covariance:** The return type must be identical or a subtype (covariant) of the return type declared in the superclass.
3. **Access Modifier Rules:** A subclass method cannot assign a more restrictive access modifier than the superclass method:
   - Superclass `protected` $\to$ Subclass `protected` or `public` (Allowed).
   - Superclass `public` $\to$ Subclass `private` (Forbidden; compilation error).
4. **Exception Handling Hierarchy:** An overriding method cannot declare broader or new checked exceptions than those declared by the superclass method.
5. **Non-Overridable Constructs:** `private`, `static`, and `final` methods cannot be overridden.

### 1.3 Importance of the `@Override` Annotation
While optional at runtime, `@Override` instructs the compiler to verify that an actual superclass method exists with that exact signature:
- Prevents insidious typos (e.g., writing `ratePerHour(int mins)` or `tostring()`).
- In the absence of `@Override`, the compiler treats the typo as an unintended method **overload**, leaving the superclass method un-overridden and creating silent runtime logic bugs.

---

## 2. Strategic Application of the `final` Keyword

| Level | Usage in Smart-Parking System | Architectural Justification |
| :--- | :--- | :--- |
| **`final` Variable / Field** | `private final String regNo;` | Enforces immutability. A vehicle cannot alter its legal license registration plate while parked inside the campus facility. |
| **`final` Method** | `public final double calculateFee(int mins)` | **Template Method Pattern.** The mathematical rounding policy ($\lceil \text{mins}/60 \rceil \times \text{rate}$) is facility-wide policy. Subclasses must customize only the rate hook (`ratePerHour()`), but are prevented from altering the rounding formula. |
| **`final` Method** | `public final String getRegNo()` | Guarantees that callers accessing the registration plate always receive the authentic, un-intercepted license string. |
| **`final` Class** | `public final class ElectricCar` | Security and completeness. Prevents unapproved subclassing (e.g. `HackedElectricCar`) from circumventing charging fees. |

---

## 3. Logical Equality Contract: `equals()` and `hashCode()`

In standard Java `Object`, `equals()` performs reference comparison (`this == obj`), checking if two references point to the exact same memory address on the heap.

### 3.1 Domain Invariant: Vehicles are Identified by Registration Plate
In a parking facility, two vehicle records with the same registration number represent the **exact same physical automobile**, even if created as different objects on the heap.
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof Vehicle other)) return false;
    return this.regNo.equals(other.regNo);
}

@Override
public int hashCode() {
    return regNo.hashCode();
}
```

### 3.2 The `equals()` and `hashCode()` Contract
- **Contract Rule:** If two objects are equal according to `equals(Object)`, calling `hashCode()` on each must produce the identical integer hash code.
- **Consequence of Violation:** If `equals()` is overridden without `hashCode()`, inserting a vehicle into a `HashSet<Vehicle>` or `HashMap<Vehicle, ParkingSlot>` fails: duplicate vehicles will be admitted, corrupting parking inventory.

---

## 4. Alternative Concept and Complexity Analysis

### 4.1 Polymorphism via Method Overriding vs Centralized `switch` Statement

#### Monolithic Switch Approach:
```java
public double calculateFee(Vehicle v, int minutes) {
    int hours = (minutes + 59) / 60;
    switch (v.getType()) {
        case TWO_WHEELER: return hours * 10.0;
        case CAR:         return hours * 30.0;
        case ELECTRIC_CAR:return hours * 30.0 + v.getChargingFee();
        default: throw new IllegalArgumentException("Unknown type");
    }
}
```

#### Comparison Matrix:

| Metric / Consideration | Method Overriding (OO Design) | Centralized `switch` Statement |
| :--- | :--- | :--- |
| **Adding a New Vehicle Type ($k+1$)** | Create 1 new class (e.g. `EBike`). **0 existing classes modified.** Adheres to Open-Closed Principle. | Must modify the `switch` method in `BillingService`, and any other switch statements across the codebase. Violates OCP. |
| **Time Complexity per Call** | $\mathcal{O}(1)$ direct Virtual Method Table (vtable) indexed dispatch. | $\mathcal{O}(1)$ if compiled to JVM `tableswitch` (dense integers), or $\mathcal{O}(k)$ sequential branch checks if string/lookup table. |
| **Code Cohesion & Locality** | High. All rules and rates for `TwoWheeler` reside within `TwoWheeler.java`. | Low. Vehicle data resides in the model, but rate logic is scattered in procedural switch handlers. |
| **Risk of Unhandled Type** | **Zero.** The compiler forces every concrete subclass to implement `ratePerHour()`. | **High.** Developer may forget to add a `case` for the new vehicle type, resulting in a runtime `IllegalArgumentException` at the gate. |
