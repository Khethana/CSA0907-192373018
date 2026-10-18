# TASK 3: Design the Control Logic
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4  
**Course Outcome:** CO1 — Control Flow, Decision Structures, Algorithmic Pseudocode, and Loop Optimizations  

---

## 1. Control Logic Design for Core Operations

### 1.1 Searching Floors and Slots for a Free Stall
- **Algorithm Mechanism:** A nested iteration structure scans the facility sequentially: outer loop iterates through floor index $f \in [0, F-1]$, and inner loop iterates through slot index $s \in [0, S_f-1]$.
- **Termination Condition:** The search implements **early termination**. The instant an unallocated stall is identified (`slots[f][s] == null`), the vehicle registration number is assigned, and a direct `return new int[]{f, s}` immediately aborts all outer and inner loop executions.
- **Handling Capacity Saturation (Facility Full):** If both nested loops run to completion without finding any vacant cell (`null`), the algorithm falls through and returns `null` (or raises an `AllocationException`), which triggers the display banner: `"FACILITY FULL"`.

### 1.2 Fee Slab Design
The fee logic supports tiered pricing:
1. **Tier 1 (Base Hour):** First 60 minutes charged at flat base rate $R$.
2. **Tier 2 (Additional Hours):** Subsequent hours charged at discounted/standard rate $R_{\text{add}}$.
3. **Tier 3 (Daily Maximum Cap):** Daily ceiling cap $M_{\text{daily}}$ to protect long-stay campus commuters.
4. **Special Exemptions:** Zero charge ($Rs.\ 0.00$) for verified monthly pass holders.

```java
// Expression of Fee Slabs with Daily Cap and Pass Check
public double computeFeeWithSlabs(int minutes, double baseRate, double addRate, double dailyMax, boolean hasPass) {
    if (hasPass) {
        return 0.0; // Pass holders park free
    }
    if (minutes <= 0) {
        return 0.0;
    }
    int billableHours = (minutes + 59) / 60; // Integer ceiling
    double totalFee;
    if (billableHours == 1) {
        totalFee = baseRate;
    } else {
        totalFee = baseRate + (billableHours - 1) * addRate;
    }
    return Math.min(totalFee, dailyMax); // Apply daily maximum cap
}
```

### 1.3 Strategic Usage of `break`, `continue`, and `return`
- **`return` (Immediate Exit):** Used in `allocate()` to return the coordinates vector `{f, s}` immediately when a free slot is claimed, eliminating $O(F \times S)$ redundant checks.
- **`continue` (Skip Filter):** When allocating specialized stalls (e.g., EV Charging), if `slot.hasEVCharger() == false`, `continue` skips immediately to the next slot without checking occupancy.
- **`break` (Early Loop Exit):** Used when searching for vehicles by registration number; once a match is found, `break` stops scanning the current floor.

---

## 2. Algorithmic Pseudocode (SIMATS Engineering Standard)

### 2.1 Algorithm 1: Slot Allocation (`allocate`)
```text
ALGORITHM AllocateSlot(facilitySlots, vehicleNo)
// Input: facilitySlots as 2D array of String/Slot references, vehicleNo as String
// Output: Array [floor, slot] of allocated coordinates, or NULL if facility is saturated

BEGIN
    IF vehicleNo IS NULL OR vehicleNo IS EMPTY THEN
        RETURN NULL
    ENDIF

    FOR floorIndex <- 0 TO LENGTH(facilitySlots) - 1 DO
        FOR slotIndex <- 0 TO LENGTH(facilitySlots[floorIndex]) - 1 DO
            IF facilitySlots[floorIndex][slotIndex] IS NULL THEN
                facilitySlots[floorIndex][slotIndex] <- vehicleNo
                RETURN [floorIndex, slotIndex]  // Early exit on success
            ENDIF
        ENDFOR
    ENDFOR

    RETURN NULL  // Facility saturated
END
```

### 2.2 Algorithm 2: Slot Release (`release`)
```text
ALGORITHM ReleaseSlot(facilitySlots, floor, slot)
// Input: facilitySlots as 2D array, floor as integer, slot as integer
// Output: Boolean TRUE if released successfully, FALSE otherwise

BEGIN
    // Boundary and range validation
    IF floor < 0 OR floor >= LENGTH(facilitySlots) THEN
        RETURN FALSE
    ENDIF

    IF slot < 0 OR slot >= LENGTH(facilitySlots[floor]) THEN
        RETURN FALSE
    ENDIF

    // Occupancy validation
    IF facilitySlots[floor][slot] IS NULL THEN
        RETURN FALSE  // Slot is already vacant
    ENDIF

    // Free the stall
    facilitySlots[floor][slot] <- NULL
    RETURN TRUE
END
```

### 2.3 Algorithm 3: Tiered Fee Computation with Slabs
```text
ALGORITHM CalculateParkingFee(durationMinutes, ratePerHour, isPassHolder, maxDailyCap)
// Input: durationMinutes (int), ratePerHour (real), isPassHolder (boolean), maxDailyCap (real)
// Output: Total payable parking fee as real value

BEGIN
    IF isPassHolder IS TRUE THEN
        RETURN 0.0
    ENDIF

    IF durationMinutes <= 0 THEN
        RETURN 0.0
    ENDIF

    // Integer ceiling arithmetic to round up any partial hour
    billableHours <- (durationMinutes + 59) / 60

    IF billableHours = 1 THEN
        computedFee <- ratePerHour
    ELSE
        // Tiered slab: first hour standard, subsequent hours charged normally
        computedFee <- ratePerHour + (billableHours - 1) * ratePerHour
    ENDIF

    IF computedFee > maxDailyCap THEN
        computedFee <- maxDailyCap
    ENDIF

    RETURN computedFee
END
```

---

## 3. Alternative Concept and Complexity Analysis

### 3.1 Linear Scan vs Free-Slot Stack & Counter

| Operation | Standard Sequential Scan (Task 3 Code) | Free-Slot Stack & Counter (`ParkingFacilityOptimized`) |
| :--- | :--- | :--- |
| **`allocate(vehicleNo)`** | Best Case: $\mathcal{O}(1)$<br>Worst Case: $\mathcal{O}(F \times S)$<br>Average Case: $\mathcal{O}(F \times S)$ | Best Case: $\mathcal{O}(1)$<br>Worst Case: $\mathcal{O}(F)$ (scans floor counters)<br>Average Case: $\mathcal{O}(1)$ |
| **`release(floor, slot)`** | $\mathcal{O}(1)$ (direct array indexing validation) | $\mathcal{O}(1)$ (direct indexing + stack push) |
| **Auxiliary Memory Space** | $\mathcal{O}(1)$ extra memory beyond slot storage array. | $\mathcal{O}(F \times S)$ extra space to store the stack of free slot indices and $F$ counters. |

### 3.2 Complexity Analysis & Scalability
- For small campus garages ($F=1, S=20$), the linear scan is negligible ($< 1\,\mu\text{s}$) and requires zero extra data structures.
- For mega multi-level campus parking complexes ($F=5, S=100 \implies 500\text{ slots}$, or airport garages with 5,000 slots):
  - In a nearly full garage (98% full), the linear scan must inspect up to 490 occupied slots before finding a free stall, resulting in $\approx 500$ memory reads per arriving car.
  - With the `freeSlotStacks` architecture, `freeSlotsCount[f]` immediately bypasses full floors in $\mathcal{O}(1)$ time, and the free slot is retrieved in $\mathcal{O}(1)$ time by popping from the floor's stack.
- **Trade-off:** The stack solution trades $\mathcal{O}(N)$ additional memory pointers for deterministic $\mathcal{O}(1)$ entry throughput.
