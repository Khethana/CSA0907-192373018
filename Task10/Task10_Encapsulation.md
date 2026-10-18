# TASK 10: Enforce Encapsulation through Packages and Access Modifiers
**Bloom’s Level:** K4 – Analyse  
**Marks:** 3  
**Course Outcome:** CO1 — Encapsulation, Package Architecture, Access Protection, and Wrapper Pitfalls  

---

## 1. Package Architecture and Structural Modularization

The codebase is organized into a clean three-tier layered architecture:

```text
Task10/src/
└── parking/
    ├── model/               <-- Domain Entities (Data + Invariants)
    │   ├── ParkingSlot.java
    │   ├── ParkingTicket.java
    │   └── Vehicle.java
    ├── service/             <-- Business Logic & Facility Management
    │   ├── ParkingFacility.java
    │   └── BillingService.java
    └── app/                 <-- Main Entry Point, Presentation & Drivers
        └── EncapsulationDemo.java
```

### Purpose of Each Layer:
1. **`parking.model`:** Contains core entities representing physical and conceptual things (`ParkingSlot`, `Vehicle`, `Ticket`). Zero dependencies on services or UI.
2. **`parking.service`:** Coordinates workflows, multi-floor slot searches, pricing policies, and capacity enforcement. Interacts with models via encapsulated APIs.
3. **`parking.app`:** Presentation layer simulating entry/exit gates, user interactions, and test harnesses.

---

## 2. Access Modifier Matrix and Architectural Rationale

| Access Modifier | Applied To | Architectural Purpose & Protection Rationale |
| :--- | :--- | :--- |
| **`private`** | All internal fields (`occupyingVehicleRegNo`, `slots`, `regNo`, `entryTimeMillis`) | **Complete Information Hiding.** External callers cannot bypass validation or directly corrupt memory pointers. |
| **`default` (package-private)** | `ParkingFacility.getRawSlotInternal(int, int)` | **Intra-Package Collaboration.** Exposes lower-level slot handles strictly to sibling classes inside `parking.service` without leaking them to external packages. |
| **`protected`** | `Vehicle.ratePerHour()`, superclass constructors | **Inheritance Customization.** Accessible to subclasses located in any package (e.g., custom plugins extending `Vehicle`), but completely hidden from general client code. |
| **`public`** | `allocate()`, `release()`, `calculateFee()`, getters | **Public API Contract.** Stable, documented interface exposed to outside world and driver applications. |

---

## 3. Validating Mutators (Setters) and Defensive Programming

Encapsulation ensures that an object transition from one valid state to another is governed strictly by domain invariants:

### 3.1 Guards Implemented in Mutators
```java
public void assignVehicle(String regNo) {
    // 1. Guard against null or whitespace-only inputs
    if (regNo == null || regNo.isBlank()) {
        throw new IllegalArgumentException("Cannot assign null or blank vehicle registration to slot");
    }
    // 2. Guard against double-booking invariant violation
    if (isOccupied()) {
        throw new IllegalStateException("Slot is already occupied by " + occupyingVehicleRegNo);
    }
    this.occupyingVehicleRegNo = regNo.toUpperCase().trim();
}
```

---

## 4. Wrapper Classes, Autoboxing, and Sneaky Pitfalls

### 4.1 The Java `Integer` Cache Pitfall (`==` vs `.equals()`)
- **Autoboxing:** Java automatically converts primitive `int` to wrapper `Integer` via `Integer.valueOf(int)`.
- **The Cache Range:** The JVM specification mandates caching for `Integer` objects in the range $[-128, 127]$.
  ```java
  Integer a = 127;
  Integer b = 127;
  System.out.println(a == b); // TRUE (Identical cached reference on Heap)

  Integer c = 128;
  Integer d = 128;
  System.out.println(c == d); // FALSE! (Distinct object allocations on Heap)
  System.out.println(c.equals(d)); // TRUE (Logical value equality)
  ```
- **Hazard in Parking System:** If slot numbers or duration minutes exceed 127 and are compared using `==`, business logic fails silently. In Java, **wrapper objects must always be compared using `.equals()`**.

### 4.2 The Unboxing `NullPointerException` Hazard
```java
Integer durationMinutes = null; // Unsettled ticket field
int primitiveMins = durationMinutes; // Implicitly invokes durationMinutes.intValue() -> THROWS NPE!
```
- **Remedy:** Use primitives (`int`, `long`, `double`) for domain arithmetic, or add explicit null guards prior to unboxing.

---

## 5. Alternative Concept and Complexity Analysis

### 5.1 Public Fields vs Encapsulated Private Fields with Validating Mutators

#### Public Fields Paradigm:
```java
// Vulnerable Public Field Approach
public class ParkingSlot {
    public String occupyingVehicleRegNo;
    public boolean isOccupied;
}
```

#### Comparison Matrix:

| Metric | Encapsulated (Our Design) | Public Fields (Alternative) |
| :--- | :--- | :--- |
| **Location of Invariant Validation** | **Centralized (1 place).** Handled directly inside `assignVehicle()`. | **Scattered ($M$ places).** Every caller across the entire codebase must remember to validate before writing. |
| **Maintenance Burden** | $\mathcal{O}(1)$ update when business rules change (e.g. plate format). | $\mathcal{O}(M)$ updates across all gate controllers, UI handlers, batch scripts. |
| **Risk of Data Corruption** | **Zero.** Impossible to bypass the setter. | **Catastrophic.** A single missed check anywhere in the code corrupts the parking facility inventory. |
| **Time Cost of Validation** | $\mathcal{O}(1)$ check executed once at setter boundary ($\approx 2$ CPU nanoseconds). | $\mathcal{O}(1)$ check, but duplicated across caller call stacks. |
| **Refactoring Flexibility** | Can change internal data structure (e.g. from String to `Vehicle` reference) without breaking callers. | Callers are directly bound to the field type; changing internal storage breaks all $M$ callers. |
