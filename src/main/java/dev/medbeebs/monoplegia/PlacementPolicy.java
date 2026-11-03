package dev.medbeebs.monoplegia;

/** Pure policy, deliberately limited to the two ordinary vanilla items. */
public final class PlacementPolicy {
    private PlacementPolicy() {}

    public static boolean blocks(boolean blocked, boolean offhand, String itemId) {
        return blocked && offhand
                && ("minecraft:torch".equals(itemId) || "minecraft:lantern".equals(itemId));
    }
}
