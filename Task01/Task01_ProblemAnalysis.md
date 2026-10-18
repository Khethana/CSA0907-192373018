# TASK 1: Understand the Problem and Identify the Objects
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4  
**Course Outcome:** CO1 — Object-Oriented Analysis & Design for Campus Smart-Parking System  

---

## 1. System Inputs and Outputs Analysis

### 1.1 Inputs Received by the System
| Input Parameter | Source / Medium | Data Format / Type | Purpose / Invariants |
| :--- | :--- | :--- | :--- |
| **Vehicle Registration Number** | ANPR Camera / Driver Entry Console | `String` (e.g. `"TN09AB1234"`) | Unique alphanumeric identifier; non-empty, standardized. |
| **Vehicle Category** | Driver Selection / Sensor Detection | `Enum` / Subtype (`TWO_WHEELER`, `CAR`, `ELECTRIC_CAR`) | Determines slot dimensions, eligibility, and hourly base fee. |
| **EV Charging Requirement** | Driver Toggle at Entry Console | `boolean` (true/false) | Determines whether the EV must be routed to an EV charging slot. |
| **Driver / User Category** | Campus ID / RFID Pass Reader | `Enum` (`STUDENT`, `STAFF`, `VISITOR`) | Determines billing exemption (pass holders) vs standard tariff. |
| **Pass Identifier / Validity** | RFID Card Scan | `String` pass ID + Expiry Date | Verified against active campus registry for fee waiver. |
| **Entry Timestamp** | System Hardware Clock (`Clock.now()`) | `LocalDateTime` / Epoch minutes | Marks start of parking stay; immutable once issued. |
| **Exit Timestamp** | System Hardware Clock at Exit Gate | `LocalDateTime` / Epoch minutes | Marks end of parking stay; must be $\ge$ entry time. |
| **Energy Consumed (kWh)** | EV Charging Station Meter | `int` / `double` ($\ge 0$) | Energy consumed for EV billing at specific charging tariff. |

### 1.2 Outputs Produced by the System
| Output Artifact | Destination | Format / Content | Description |
| :--- | :--- | :--- | :--- |
| **Parking Ticket** | Printed Thermal Slip / Mobile SMS | Alphanumeric Ticket ID, Slot Number (Floor, Index), Entry Time, Vehicle Reg No | Issued at entry; proof of occupancy; must be surrendered at exit. |
| **Slot Allocation Directive** | LED Gate Display / Guidance System | E.g. `"Floor 2, Slot 14 (EV Charging)"` | Guides driver to assigned parking stall. |
| **Exit Invoice / Receipt** | Screen Display & Printout | Ticket ID, Reg No, Duration (Minutes & Billable Hours), Base Fee, Charging Fee, Total Payable | Itemized financial receipt. |
| **Slot Release Notification** | Internal State / Monitoring Dashboard | Floor, Slot ID, Free Status | Updates facility occupancy count; marks stall available for next car. |
| **Operational Alerts** | Entry Barrier Console | E.g. `"FACILITY FULL"`, `"NO EV SLOTS FREE"` | Signals admission denial when capacity is reached. |

---

## 2. Noun-Verb Analysis (Candidate Classes & Methods)

Following the linguistic analysis approach for Object-Oriented Modeling:

### 2.1 Identification of Nouns (Candidate Classes & Attributes)
| Problem Noun | Category | Candidate Class / Field | Justification |
| :--- | :--- | :--- | :--- |
| **Parking Facility** | Physical System / Aggregate Root | `ParkingFacility` | Represents the physical building/lot managing floors, slots, and operations. |
| **Floor** | Sub-structure of Facility | `Floor` / `int floorNumber` | Contains an array/collection of parking slots. |
| **Parking Slot** | Physical Space Unit | `ParkingSlot` | Models individual parking stall; tracks status (free/occupied), type, EV charger. |
| **Vehicle** | Entity / Domain Abstraction | `Vehicle` (Abstract) | Represents vehicles entering campus; holds registration number, entry time. |
| **Two-Wheeler, Car, Electric Vehicle** | Specialized Entities | `TwoWheeler`, `Car`, `ElectricCar` | Concrete specializations with distinct dimensions, charging capabilities, and rate tariffs. |
| **Ticket** | Transaction Token | `ParkingTicket` | Immutable record linking vehicle, allocated slot, timestamps, and payment status. |
| **User / Driver** | Entity / Actor | `User` (`Student`, `Staff`, `Visitor`) | Models customer profile; encapsulates monthly pass privilege vs fee payment. |
| **Pass** | Value / Entitlement Object | `MonthlyPass` | Grants parking fee waiver for verified students and staff. |
| **Parking Fee / Tariff** | Value Computation | `FeeCalculator` / `BillingService` | Encapsulates rate rules, hourly ceiling, EV energy charges, and exemptions. |

### 2.2 Identification of Verbs (Candidate Methods)
| Problem Verb | Acting Object | Candidate Method | Responsibility |
| :--- | :--- | :--- | :--- |
| **Allocate slot** | `ParkingFacility` | `allocateSlot(Vehicle v)` | Searches for suitable free stall; reserves slot. |
| **Issue ticket** | `ParkingFacility` | `issueTicket(Vehicle v, ParkingSlot s)` | Creates unique immutable `ParkingTicket`. |
| **Record entry time** | `ParkingTicket` | `setEntryTime(LocalDateTime t)` | Locks start timestamp upon physical entry. |
| **Release slot** | `ParkingFacility` / `ParkingSlot` | `releaseSlot(int floor, int slot)` | Frees physical slot when vehicle departs. |
| **Calculate fee** | `Vehicle` / `BillingService` | `calculateFee(int minutes)` | Computes billable hours and base rate. |
| **Charge EV** | `ElectricCar` / `Chargeable` | `chargingFee(int kWh)` | Computes supplemental electric energy cost. |
| **Validate pass** | `MonthlyPass` / `User` | `isValid(LocalDate today)` | Checks expiry of student/staff monthly permit. |

---

## 3. Class Responsibilities and Collaborations (CRC Cards)

### Class 1: `ParkingFacility`
- **Responsibilities:**
  - Maintains multi-level array of `ParkingSlot` objects across floors.
  - Allocates first suitable free slot matching vehicle size and EV charging needs.
  - Releases slot upon exit and marks it free.
  - Tracks total occupancy and facility capacity.
- **Collaborations:** `ParkingSlot`, `Vehicle`, `ParkingTicket`.

### Class 2: `ParkingSlot`
- **Responsibilities:**
  - Encapsulates floor number, slot number, slot type (compact, regular, EV).
  - Tracks state: occupied (`boolean`) and reference to current parked `Vehicle`.
  - Enforces invariant: cannot be double-booked.
- **Collaborations:** `Vehicle`.

### Class 3: `Vehicle` (Abstract)
- **Responsibilities:**
  - Holds vehicle license registration number (`regNo`).
  - Holds entry timestamp and vehicle category.
  - Defines abstract contract `ratePerHour()` and common hourly ceiling fee calculation.
- **Collaborations:** `ParkingSlot`, `ParkingTicket`.

### Class 4: `ParkingTicket`
- **Responsibilities:**
  - Generates monotonically increasing unique ticket serial numbers.
  - Records vehicle reference, assigned floor/slot, entry time, and exit time.
  - Computes total parked duration.
- **Collaborations:** `Vehicle`, `ParkingSlot`.

---

## 4. Fundamental Business Rules

1. **Mutual Slot Exclusion (No Double Booking):** A single parking slot cannot hold more than one vehicle simultaneously. Slot status must transition atomically from free to occupied.
2. **Unique Ticket Lifetime:** Every ticket number must be globally unique and monotonically incremented; ticket IDs are never reused or reset during facility uptime.
3. **Type-Compatible Slot Allocation:** Two-wheelers cannot occupy EV charging bays unless designated; large cars cannot park in two-wheeler stalls; EV vehicles requesting charging must be routed to slots equipped with chargers.
4. **Tariff and Hourly Ceiling:** Every started parking hour is charged in full (e.g., 61 minutes constitutes 2 billable hours).
5. **Fee Exemption for Valid Pass Holders:** Staff and students holding a verified, active monthly pass are charged Rs. 0.00 for parking; visitors pay the full tariff.
6. **Decoupled EV Charging Surcharge:** Electric charging fees are billed strictly on consumed kWh (Rs. 12/kWh) and added on top of parking duration fees.
7. **Temporal Consistency:** Exit time must strictly occur at or after entry time ($\text{exitTime} \ge \text{entryTime}$).

---

## 5. Facility Assumptions
1. Single consolidated multi-floor facility with dedicated entrance and exit gates.
2. Floors are numbered starting from index 0 (Ground Floor = Floor 0).
3. Slots on each floor are numbered starting from index 0.
4. Vehicle registration numbers are validated alphanumeric strings conforming to transport authority standards.
5. In-memory data store operates for single-instance simulation without database persistence.

---

## 6. Alternative Concept and Complexity Analysis

### 6.1 Parallel Arrays vs. Object-Oriented Design

#### Parallel Arrays Approach:
Instead of a composite `Vehicle` object, vehicles are stored as loose primitive/String arrays:
```java
String[] regNo = new String[MAX_VEHICLES];
String[] type = new String[MAX_VEHICLES];
long[] entryTime = new long[MAX_VEHICLES];
```

#### Object-Oriented Approach:
```java
Vehicle[] vehicles = new Vehicle[MAX_VEHICLES];
// where each Vehicle encapsulates regNo, type, entryTime, and behaviors
```

### 6.2 Comparison Analysis

| Evaluation Criteria | Parallel Arrays Approach | Object-Oriented Approach |
| :--- | :--- | :--- |
| **Search by Reg No** | Iterates over `regNo[]`. Time Complexity: $\mathcal{O}(N)$. | Iterates over `Vehicle[]` or queried via Map. Time Complexity: $\mathcal{O}(N)$ (or $\mathcal{O}(1)$ with Map). |
| **Adding New Attribute (e.g. `ownerCategory`)** | Requires declaring a new parallel array `String[] ownerCategory = new String[N];`. Every function signature must be changed (e.g. `allocate(regNo, type, entry, owner)`). High risk of index mismatch. | Requires adding one private field and accessor in `Vehicle` class. Method signatures remain unchanged (`allocate(Vehicle v)`). Encapsulation preserved. |
| **Data Integrity & Consistency** | Vulnerable to index corruption. Deleting an element requires manually shifting 3+ arrays in lockstep. | Atomic. An object is a single cohesive unit on the JVM heap. Moving or deleting a reference affects all attributes simultaneously. |
| **Polymorphism & Behavior** | Impossible. Must use long, fragile `switch(type[i])` or `if-else` blocks for fee calculations. | Natural. Subclasses override `ratePerHour()` and dynamic method dispatch invokes correct logic at runtime. |

### 6.3 Space Complexity and Memory Overhead Analysis (64-bit JVM)

1. **Parallel Arrays:**
   - 3 Array headers: $3 \times 24\text{ bytes} = 72\text{ bytes}$.
   - Array reference slots: $3 \times N \times 8\text{ bytes}$ (or $4\text{ bytes}$ with Compressed OOPs) $= 12N\text{ bytes}$.
   - Primitives/Strings: Stored directly or referenced.
   - **Total Space per Vehicle:** $\approx 12 - 16\text{ bytes}$ of array slot overhead.
2. **Object-Oriented Array:**
   - 1 Array header: $24\text{ bytes}$.
   - 1 Array reference slot: $4N\text{ bytes}$ (Compressed OOPs).
   - Each `Vehicle` Object Instance:
     - Object Header: Mark word ($8\text{ bytes}$) + Class pointer ($4\text{ bytes}$) = $12\text{ bytes}$.
     - Instance Fields: 2 references + 1 long ($4 + 4 + 8 = 16\text{ bytes}$).
     - Alignment Padding: $4\text{ bytes}$ (to multiple of 8).
     - Total instance size = $32\text{ bytes}$.
   - **Total Space per Vehicle:** $\approx 36\text{ bytes}$.

### 6.4 Empirical Benchmark Results (from `VehicleParallelVsOODemo.java`)
Synthesizing $N = 100,000$ vehicles on Java 26:
- **Parallel Array Search Time (Worst Case):** $0.511\text{ ms}$ ($\mathcal{O}(N)$)
- **Object-Oriented Search Time (Worst Case):** $0.695\text{ ms}$ ($\mathcal{O}(N)$)

**Conclusion:** While parallel arrays consume slightly less heap memory per vehicle (~$16\text{ bytes}$ vs ~$36\text{ bytes}$) and have minor cache-locality advantages, they completely destroy encapsulation, type safety, atomicity, and maintainability. In enterprise systems, the Object-Oriented design is overwhelmingly superior.
