package Task03;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Task 3 Alternative Concept:
 * O(1) Slot Allocation and Release using Free-Slot Deques / Stacks and Free Counts per Floor.
 */
public class ParkingFacilityOptimized {
    private final String[][] slots;
    private final int[] freeSlotsCount;
    @SuppressWarnings("unchecked")
    private final Deque<Integer>[] freeSlotStacks; // Stack per floor

    @SuppressWarnings("unchecked")
    public ParkingFacilityOptimized(int[] slotsPerFloor) {
        int floors = slotsPerFloor.length;
        slots = new String[floors][];
        freeSlotsCount = new int[floors];
        freeSlotStacks = new Deque[floors];

        for (int f = 0; f < floors; f++) {
            int slotCount = slotsPerFloor[f];
            slots[f] = new String[slotCount];
            freeSlotsCount[f] = slotCount;
            freeSlotStacks[f] = new ArrayDeque<>(slotCount);
            // Push from slotCount - 1 down to 0 so slot 0 is popped first
            for (int s = slotCount - 1; s >= 0; s--) {
                freeSlotStacks[f].push(s);
            }
        }
    }

    /**
     * O(1) amortized allocation:
     * Directly checks freeSlotsCount and pops from freeSlotStacks.
     */
    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {
            if (freeSlotsCount[f] > 0) {
                int freeSlot = freeSlotStacks[f].pop();
                slots[f][freeSlot] = vehicleNo;
                freeSlotsCount[f]--;
                return new int[]{f, freeSlot};
            }
        }
        return null;
    }

    /**
     * O(1) release:
     * Directly marks null, increments count, pushes slot onto stack.
     */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;

        slots[floor][slot] = null;
        freeSlotStacks[floor].push(slot);
        freeSlotsCount[floor]++;
        return true;
    }

    public static void main(String[] args) {
        System.out.println("=== Testing Optimized O(1) Facility with Same Driver Test ===");
        ParkingFacilityOptimized p = new ParkingFacilityOptimized(new int[]{2, 1});
        System.out.println(Arrays.toString(p.allocate("TN01AB1111")));
        System.out.println(Arrays.toString(p.allocate("TN02CD2222")));
        System.out.println(Arrays.toString(p.allocate("TN03EF3333")));
        System.out.println(Arrays.toString(p.allocate("TN04GH4444")));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " " + p.release(5, 0));
        System.out.println(Arrays.toString(p.allocate("TN04GH4444")));
    }
}
