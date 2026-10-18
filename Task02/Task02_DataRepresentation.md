# TASK 2: Choose the Data Representation
**Bloom’s Level:** K4 – Analyse  
**Marks:** 3  
**Course Outcome:** CO1 — Data Representation, Precision Management, and Memory Layout  

---

## 1. Field Data Type Selection and Justification

The design demands robust, type-safe, and memory-conscious choices for every state attribute in the system:

| Field Name | Selected Java Type | Alternative Rejected | Justification & Rationale |
| :--- | :--- | :--- | :--- |
| **Floor Number** | `int` | `byte`, `short` | While floors are small ($\le 10$), the JVM stack operates on 32-bit words (word-tearing protection). Using `byte` causes implicit promotion to `int` during arithmetic operations. |
| **Slot Number** | `int` | `String` (e.g. `"S01"`) | Integer representation allows $\mathcal{O}(1)$ direct array index referencing (`slots[floor][slot]`) without string parsing overhead. |
| **Vehicle Reg Number** | `String` | `char[]` | Registration plates contain alphanumeric characters and hyphens (`"TN01AB1111"`). `String` provides immutability and built-in hash code caching for map lookups. |
| **Entry / Exit Time** | `long` (epoch millis) or `LocalDateTime` | `Date` (deprecated), `double` | Epoch milliseconds or `java.time.LocalDateTime` avoid timezone ambiguities and leap-second flaws. Allows monotonic, exact difference calculations. |
| **Duration (Minutes)** | `int` | `float` | Minutes are discrete whole numbers. Floating point types introduce IEEE 754 binary rounding errors (e.g., $0.1 + 0.2 \ne 0.3$). |
| **Tariff / Fee (Rs)** | `double` | `float` | `float` has only 24 bits of mantissa (~7 decimal digits of precision), accumulating errors across thousands of billing operations. `double` (53 bits mantissa) prevents truncation drift. (For enterprise banking, `BigDecimal` or integer paise/cents is preferred). |
| **Slot Status** | `String` / `Slot` object | `boolean` | Storing the registration string (`null` if free) encodes both occupancy status and vehicle identity in a single memory pointer. |

---

## 2. Floor and Slot Array Representation: Jagged Array Analysis

### 2.1 The Three Structural Options
1. **One-Dimensional Flat Array (`String[]`):**
   - Stored linearly with index mapping: $\text{index} = \text{floor} \times \text{slotsPerFloor} + \text{slot}$.
2. **Uniform Two-Dimensional Array (`String[floors][fixedSlots]`):**
   - Assumes every floor has identical slot counts.
3. **Jagged (Ragged) Array (`String[floors][]` where each row has independent length):**
   ```java
   String[][] slots = new String[slotsPerFloor.length][];
   for (int f = 0; f < slotsPerFloor.length; f++) {
       slots[f] = new String[slotsPerFloor[f]];
   }
   ```

### 2.2 Why the Jagged Array is the Architecturally Superior Choice
- **Heterogeneous Physical Architecture:** Real-world university parking structures rarely have identical capacities on all floors. The Ground Floor (Floor 0) frequently reserves space for ramps, ticket booths, and EV transformers (e.g. 20 slots), while upper floors accommodate 50 slots, and the rooftop accommodates 30 slots.
- **Zero Memory Waste:** A uniform 2D array or 1D flat array must size every floor to the maximum capacity ($\max(S) = 50$). For a facility with $\{20, 50, 30\}$ slots, a 1D flat array allocates $3 \times 50 = 150$ slots. 50 slots ($33.3\%$ of allocated memory) are completely wasted.
- **Elimination of "Phantom Slots":** In a 1D array, accessing Floor 0, Slot 35 calculates $\text{index} = 0 \times 50 + 35 = 35$, which points to a valid index in the 150-slot array! Without complex external guard logic, the system would allocate vehicles to non-existent physical spaces. In a jagged array, `slots[0][35]` immediately triggers a JVM-native `ArrayIndexOutOfBoundsException`, protecting data integrity.

---

## 3. Type Conversion, Casting Risks, and Precision Loss

### 3.1 The Integer Truncation Bug in Fee Billing
The university policy states: **"Every started hour is charged in full."**  
If a vehicle parks for $65\text{ minutes}$ at a rate of $\text{Rs. } 30/\text{hour}$:
- **Flawed Code 1 (Integer Division):**
  ```java
  int hours = minutes / 60; // 65 / 60 evaluates to 1
  double fee = hours * 30.0; // Produces Rs. 30.00 -> Undercharges customer by Rs. 30.00!
  ```
- **Flawed Code 2 (Casting without Ceil):**
  ```java
  int hours = (int)(minutes / 60.0); // (int)(1.08333) truncates to 1 -> Loss of precision!
  ```
- **Flawed Code 3 (Double Cast on Truncated Result):**
  ```java
  double fee = (double)(minutes / 60) * 30.0; // Still evaluates to 1.0 * 30.0 = Rs. 30.00!
  ```

### 3.2 Robust Solutions Implemented in Our Design
1. **Pure Integer Arithmetic Ceiling Formula:**
   $$\text{hours} = \frac{\text{minutes} + 59}{60}$$
   - When $\text{minutes} = 60 \implies (60 + 59) / 60 = 119 / 60 = 1\text{ hour}$.
   - When $\text{minutes} = 61 \implies (61 + 59) / 60 = 120 / 60 = 2\text{ hours}$.
   - When $\text{minutes} = 120 \implies (120 + 59) / 60 = 179 / 60 = 2\text{ hours}$.
   - Zero floating-point conversion; zero precision drift; executing in 1 CPU cycle.
2. **Standard Library Floating Ceiling:**
   $$\text{hours} = (\text{int}) \lceil \text{minutes} / 60.0 \rceil$$
   ```java
   int hours = (int) Math.ceil(minutes / 60.0);
   ```

---

## 4. Input Mechanism Strategy (Scanner vs Command-Line Arguments)

1. **Command-Line Arguments (`String[] args`):**
   - **Best for:** Batch script execution, automated testing, continuous integration, headless CI/CD pipelines.
   - **Limitation:** Inconvenient for interactive university barrier gate simulations.
2. **`java.util.Scanner` on `System.in`:**
   - **Best for:** Interactive console menus, simulating gate attendants entering registration numbers.
   - **Safety Caveat:** Must handle input mismatch exceptions (`InputMismatchException`, `NoSuchElementException`) and trailing newline buffering (`scanner.nextLine()`).
3. **Selected Design:** A dual approach where core services (`ParkingFacility`) expose clean programmatic APIs, with a driver supporting both programmatic automated test harnesses and an interactive `Scanner` facade.

---

## 5. Alternative Concept and Complexity Analysis

### 5.1 Comparing Jagged Array vs 1D Array vs `ArrayList<String[]>`

| Feature / Metric | Jagged Array `String[][]` | 1D Flat Array `String[]` | Dynamic `ArrayList<String[]>` |
| :--- | :--- | :--- | :--- |
| **Locating a Slot `(f, s)`** | `slots[f][s]` $\implies \mathcal{O}(1)$ | `slots[f * S + s]` $\implies \mathcal{O}(1)$ | `list.get(f)[s]` $\implies \mathcal{O}(1)$ |
| **Memory Space (Irregular Floors)** | Exactly $\sum_{f=0}^{F-1} S_f$ pointers. Zero wasted cells. | $F \times \max(S)$ cells. Wastes $(F \times \max(S) - \sum S_f)$ memory cells. | $\sum S_f$ pointers + `ArrayList` internal object and array overhead. |
| **Bounds Safety** | Automatic per-floor bounds check. | Dangerous "phantom slot" aliasing unless manual checks added. | Automatic floor and slot bounds check. |
| **Runtime Floor Expansion** | Fixed at instantiation; requires manual copy to resize. | Fixed; resizing requires complete recalculation of indices. | Highly flexible; `list.add(new String[k])` in $\mathcal{O}(1)$ amortized time. |

### 5.2 Mathematical Analysis of Space Overhead
For a facility with $F$ floors where floor capacities are $S_0, S_1, \dots, S_{F-1}$:
- **Jagged Array Overhead:**
  $$\text{Memory} = 24\text{ bytes (outer array header)} + 4F\text{ bytes} + \sum_{f=0}^{F-1} (24 + 4S_f)\text{ bytes}$$
- **1D Flat Array Overhead:**
  $$\text{Memory} = 24\text{ bytes} + 4(F \times S_{\max})\text{ bytes}$$
  When $S_{\max} \gg S_{\min}$, the flat array wastes substantial heap space. Hence, **Jagged Array** is the optimal static representation.
