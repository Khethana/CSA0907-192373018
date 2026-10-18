# Campus Smart-Parking System (SPS)
**Course Outcome CO1:** Java fundamentals and Object-Oriented Programming  
**Course Code:** CSA0907  
**Student Name:** Khethana  
**Register Number:** 192373018  
**Repository:** [https://github.com/Khethana/CSA0907-192373018](https://github.com/Khethana/CSA0907-192373018)  
**Java Version:** Java 26 (Oracle Corporation / OpenJDK 26.0.2)  

---

## Executive Overview
This repository contains the complete design, implementation, empirical benchmarking, and architectural defense for the **Campus Smart-Parking System (SPS)**, fulfilling all requirements for Assignment 1 (Tasks 1 through 12, including the Day-4 Design Challenge).

Every task is organized into a dedicated, self-contained directory containing:
1. Java source files implementing all required features and "Complete the Code" milestones.
2. Comprehensive theoretical documentation with Bloom's taxonomy analysis (K4 Analyse / K5 Evaluate), UML class diagrams, SIMATS Engineering standard pseudocode, memory models, and Big-O complexity analyses.
3. Automated test harnesses and benchmark drivers.
4. Execution logs captured in `output.txt`.

---

## Progressive Commit History
In strict accordance with the mandatory submission instructions, development was executed and committed progressively across all 12 tasks:
- `160f287` - `feat(Task01): Complete problem analysis, object identification, CRC cards, and parallel arrays benchmark`
- `e8fd72b` - `feat(Task02): Choose data representation, integer division bug demo, and array complexity analysis`
- `e18291f` - `feat(Task03): Complete ParkingFacility code, control logic design, SIMATS pseudocode, and O(1) stack optimization`
- `247d415` - `feat(Task04): Implement classes, constructors, chaining, copy constructor, and JVM memory benchmark`
- `fcbb7df` - `feat(Task05): Apply static members, analyze multi-facility counter, and demonstrate GC lifecycle and pass-by-value`
- `8aa27e7` - `feat(Task06): Implement Vehicle inheritance hierarchy, UML diagram, constructor trace, and composition analysis`
- `8260b94` - `feat(Task07): Implement method overriding, final modifiers, equals and hashCode contracts, and switch complexity comparison`
- `a5885cb` - `feat(Task08): Complete FeeDemo code, trace dynamic dispatch, analyze equality semantics, and benchmark polymorphism`
- `e3f782d` - `feat(Task09): Introduce abstraction with abstract classes, capability interfaces, and reversed architecture analysis`
- `7b82a67` - `feat(Task10): Enforce encapsulation with layered package architecture, validating setters, and wrapper pitfall demos`
- `de27b0e` - `feat(Task11): Implement complete system, run test suite with hand-calculated day collection, and benchmark ticket storage`
- `f3b89e1` - `feat(Task12): Complete Day-4 Design Challenge with 5 irregular floors, EBike, DeliveryVan peak pricing, and DailyPass`

---

## Directory Structure and Task Summary

```text
SPS/
├── .gitignore
├── README.md
├── Task01/
│   ├── Task01_ProblemAnalysis.md
│   ├── VehicleParallelVsOODemo.java
│   └── output.txt
├── Task02/
│   ├── Task02_DataRepresentation.md
│   ├── DataRepresentationDemo.java
│   └── output.txt
├── Task03/
│   ├── Task03_ControlLogic.md
│   ├── ParkingFacility.java           <-- Complete the Code Milestone
│   ├── ParkingFacilityOptimized.java  <-- O(1) Stack Optimization
│   └── output.txt
├── Task04/
│   ├── Task04_ClassesAndConstructors.md
│   ├── ParkingTicket.java
│   ├── ConstructorDemo.java
│   └── output.txt
├── Task05/
│   ├── Task05_StaticAndLifeCycle.md
│   ├── StaticAndLifeCycleDemo.java
│   └── output.txt
├── Task06/
│   ├── Task06_InheritanceHierarchy.md
│   ├── Vehicle.java
│   ├── TwoWheeler.java
│   ├── Car.java
│   ├── ElectricCar.java
│   ├── InheritanceTraceDemo.java
│   └── output.txt
├── Task07/
│   ├── Task07_OverrideAndSpecialise.md
│   ├── OverridingDemo.java
│   └── output.txt
├── Task08/
│   ├── Task08_Polymorphism.md
│   ├── FeeDemo.java                   <-- Complete the Code Milestone
│   ├── PolymorphismDeepTraceDemo.java
│   └── output.txt
├── Task09/
│   ├── Task09_Abstraction.md
│   ├── AbstractionDemo.java
│   └── output.txt
├── Task10/
│   ├── Task10_Encapsulation.md
│   ├── src/parking/
│   │   ├── model/ParkingSlot.java
│   │   ├── service/ParkingFacility.java
│   │   ├── service/BillingService.java
│   │   └── app/EncapsulationDemo.java
│   └── output.txt
├── Task11/
│   ├── Task11_SystemImplementationAndTesting.md
│   ├── CampusSmartParkingSystem.java
│   ├── SystemTestSuite.java           <-- Hand-calculated Audit & Tests
│   ├── TicketStorageBenchmark.java    <-- Array vs List vs Map
│   └── output.txt
└── Task12/
    ├── Task12_Day4DesignChallenge.md   <-- 4-Part K5 Evaluation & Defense
    ├── Vehicle.java
    ├── EBike.java                     <-- Flat fee
    ├── DeliveryVan.java               <-- Peak 9-5 double rate
    ├── ParkingPass.java
    ├── DailyPass.java                 <-- Visitor One-Day Pass
    ├── MonthlyPass.java
    ├── AdaptedParkingFacility.java     <-- 5 Irregular Floors
    ├── DesignChallengeDemo.java
    └── output.txt
```

---

## Compilation and Execution Guide

### Prerequisites
- JDK 17, 21, or 26 installed.
- Verify installation:
  ```bash
  javac -version
  java -version
  ```

### Task 1: Problem Analysis and Parallel Arrays Benchmark
```bash
javac Task01/VehicleParallelVsOODemo.java
java Task01.VehicleParallelVsOODemo
```
*Result Note:* Measures $N=100,000$ vehicle searches. Demonstrates that while parallel arrays require slightly less heap (~16 vs 36 bytes), they destroy type safety and maintainability.

### Task 2: Data Representation and Precision
```bash
javac Task02/DataRepresentationDemo.java
java Task02.DataRepresentationDemo
```
*Result Note:* Demonstrates the catastrophic integer division bug where $65 / 60 = 1\text{ hr}$ undercharges by Rs. 30. Proves pure integer ceiling `(minutes + 59) / 60` and exposes 33.3% memory waste in 1D flat arrays.

### Task 3: Control Logic (Complete the Code)
```bash
javac Task03/ParkingFacility.java Task03/ParkingFacilityOptimized.java
java Task03.ParkingFacility
java Task03.ParkingFacilityOptimized
```
*Result Note:* Produces exact expected output: `[0, 0]`, `[0, 1]`, `[1, 0]`, `null`, `true false false`, `[0, 1]`. The optimized version achieves deterministic $\mathcal{O}(1)$ slot allocation using free-slot deques.

### Task 4: Classes, Objects, and Constructors
```bash
javac Task04/ParkingTicket.java Task04/ConstructorDemo.java
java Task04.ConstructorDemo
```
*Result Note:* Demonstrates `this(...)` constructor chaining, copy constructor for re-issuing duplicate tickets, and benchmarks $N=100,000$ object creations comparing Parameterized Constructors vs JavaBeans vs Static Factories.

### Task 5: Static Members and Object Life Cycle
```bash
javac Task05/StaticAndLifeCycleDemo.java
java Task05.StaticAndLifeCycleDemo
```
*Result Note:* Proves why multi-facility operations (North vs South Gate) mandate instance counters to prevent sequence collision and lock contention. Demonstrates JVM pass-by-value and GC lifecycle.

### Task 6: Inheritance Hierarchy and Constructor Trace
```bash
javac Task06/*.java
java Task06.InheritanceTraceDemo
```
*Result Note:* Traces constructor execution order when instantiating `ElectricCar`: `Vehicle` $\to$ `Car` $\to$ `ElectricCar`. Compares inheritance (`is-a`) vs composition (`has-a`).

### Task 7: Method Overriding and Specialization
```bash
javac Task07/OverridingDemo.java
java Task07.OverridingDemo
```
*Result Note:* Demonstrates `@Override` rules, `final` method template locking, and verifies the logical equality contract (`equals()` and `hashCode()`) with `HashSet` deduplication.

### Task 8: Polymorphism (Complete the Code)
```bash
javac Task08/FeeDemo.java Task08/PolymorphismDeepTraceDemo.java
java Task08.FeeDemo
java Task08.PolymorphismDeepTraceDemo
```
*Result Note:* Produces exact expected output:
```text
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```
Explains why `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` returns `true` based on physical vehicle registration identity and Liskov Substitution Principle.

### Task 9: Abstraction (Abstract Classes vs Interfaces)
```bash
javac Task09/AbstractionDemo.java
java Task09.AbstractionDemo
```
*Result Note:* Demonstrates why `Vehicle` must be an abstract class (to encapsulate mutable instance state and constructor invariants) while `Chargeable` and `Payable` are capability interfaces. Proves multiple interface implementation and interface default/static methods.

### Task 10: Encapsulation and Access Modifiers
```bash
javac -d Task10/bin Task10/src/parking/model/*.java Task10/src/parking/service/*.java Task10/src/parking/app/*.java
java -cp Task10/bin parking.app.EncapsulationDemo
```
*Result Note:* Demonstrates package protection across `parking.model`, `parking.service`, and `parking.app`. Catches invalid durations, double-bookings, blank plates, and exposes the dangerous JVM Integer Cache bug (`128 == 128` evaluates to `false`).

### Task 11: Full System Implementation and Test Suite
```bash
javac Task11/*.java
java Task11.SystemTestSuite
java Task11.TicketStorageBenchmark
```
*Result Note:* 
- Executes all normal, boundary, and invalid test cases.
- **Hand-Calculated Day Collection Verification:** Pre-calculated ledger of 5 transactions totaling **Rs. 580.00** matched the program output with **100% mathematical precision**.
- **Exit Lookup Benchmark ($N=50,000$):** `HashMap` achieves $1.73\text{ ms}$ ($\mathcal{O}(1)$), outperforming linear array search ($5,417.36\text{ ms}$) by **3,134.7x**.

### Task 12: Day-4 Design Challenge
```bash
javac Task12/*.java
java Task12.DesignChallengeDemo
```
*Result Note:*
- Configures 5 irregular floors: $\{20, 30, 40, 25, 15\}$ (Total 130 slots).
- Verifies E-Bike flat fee (Rs. 20.00).
- Verifies Delivery Van peak double rate (10:00 AM $\to$ Rs. 60/hr) vs off-peak (06:00 AM $\to$ Rs. 30/hr).
- Verifies Visitor One-Day Pass (Rs. 0.00 waiver on pass date, expired on subsequent dates).
- Demonstrates 100% adherence to Open-Closed Principle (**0 existing classes modified**).

---

## Viva & Assessment Quick Reference Cheat Sheet

| Question | Core Concept & Answer |
| :--- | :--- |
| **Why is `Vehicle` abstract rather than an interface?** | An interface cannot declare instance state (`private final String regNo`) or enforce constructor invariant validation. `Vehicle` requires shared state and a template billing method. |
| **Why does `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` print `true`?** | In the physical world, a state registration plate uniquely identifies an automobile chassis. Two vehicle objects with the same plate represent the same car regardless of reference type. |
| **How does your design avoid the integer truncation bug?** | Uses integer ceiling: `(minutes + 59) / 60`. A 65-minute stay evaluates to `(65 + 59) / 60 = 124 / 60 = 2 hours`. Standard `65 / 60` truncates to 1 hour, improperly undercharging. |
| **Why use a jagged array for floors?** | Uniform arrays waste memory if floors have differing capacities. In a $\{20, 30, 40, 25, 15\}$ garage, a 2D uniform array wastes 70 slots ($35\%$) and introduces dangerous "phantom slots". Jagged arrays allocate exact physical sizes. |
| **How did you satisfy the Day-4 Design Challenge without breaking existing code?** | Applied the Open-Closed Principle. Created `EBike` and `DeliveryVan` as subclasses of `Vehicle`, and modeled `ParkingPass` as an interface implemented by `DailyPass` and `MonthlyPass`. Zero pre-existing classes were modified. |
