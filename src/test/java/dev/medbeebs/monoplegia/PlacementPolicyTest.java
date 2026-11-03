package dev.medbeebs.monoplegia;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlacementPolicyTest {
    @Test
    void blocksOnlyOrdinaryVanillaLightsInOffhandWhenEnabled() {
        for (String item : new String[]{"minecraft:torch", "minecraft:lantern"}) {
            assertTrue(PlacementPolicy.blocks(true, true, item));
            assertFalse(PlacementPolicy.blocks(false, true, item));
            assertFalse(PlacementPolicy.blocks(true, false, item));
        }
    }

    @Test
    void leavesOtherItemsAndModdedLightsAlone() {
        for (String item : new String[]{"minecraft:soul_torch", "minecraft:soul_lantern",
                "minecraft:redstone_torch", "minecraft:copper_torch", "minecraft:copper_lantern",
                "minecraft:shield", "minecraft:stone", "example:torch", "example:lantern"}) {
            assertFalse(PlacementPolicy.blocks(true, true, item), item);
        }
    }
}
