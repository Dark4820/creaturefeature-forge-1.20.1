package net.atired.creaturefeature.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

/**
 * PORT: the colour math of the original CreatureFeatureClient.registerBlockColors/registerItemColors, moved to a
 * class without client imports so a GameTest can check it on the dedicated server. The client registration that
 * uses it is in CreatureFeatureClient. Same colours and formulas as the original; java.awt.Color -> plain ARGB.
 * Normal and mosaic feathers share one model: only these tints tell them apart.
 */
public final class FeatherTints {
    private static final int[] FEATHER_COLS = {0xefefc3,0xc3efc7,0xefc3c7,0x81cdd8,0x9a9ee1,0xaae19a};

    private FeatherTints() {
    }

    // Item (and blockPos == null): mosaic uses the second colour of each palette, normal feathers the first one
    public static int item(boolean mosaic, int tintIndex) {
        if (tintIndex >= 0 && tintIndex <= 1)
            return FEATHER_COLS[tintIndex * 3 + (mosaic ? 1 : 0)];
        return 0xFFFFFFFF;
    }

    // Mosaic: hard stripes by position
    public static int mosaic(BlockPos blockPos, int tintIndex) {
        if (blockPos == null)
            return item(true, tintIndex);
        if (tintIndex == 1)
            return FEATHER_COLS[Math.abs(blockPos.getY() / 2 + blockPos.getX()) % 3 + 3];
        if (tintIndex == 0)
            return FEATHER_COLS[Math.abs(blockPos.getY() / 2 + blockPos.getZ()) % 3];
        return item(true, tintIndex);
    }

    // Normal feathers: smooth gradient between the colours of each palette
    public static int normal(BlockPos blockPos, int tintIndex) {
        if (blockPos == null)
            return item(false, tintIndex);
        if (tintIndex == 1)
            return gradient(Math.abs(blockPos.getY() / 7f + blockPos.getX() * 0.4f - blockPos.getZ() * 0.2f), 3);
        if (tintIndex == 0)
            return gradient(Math.abs(blockPos.getY() / 7f + blockPos.getZ() * 0.4f + blockPos.getX() * 0.2f), 0);
        return item(false, tintIndex);
    }

    private static int gradient(float full, int offset) {
        int inted = (int) (full - full % 1);
        full = full % 1;
        int col1 = FEATHER_COLS[(inted % 3) + offset];
        int col2 = FEATHER_COLS[((inted + 1) % 3) + offset];
        int r = Mth.lerpInt(full, (col1 >> 16) & 255, (col2 >> 16) & 255);
        int g = Mth.lerpInt(full, (col1 >> 8) & 255, (col2 >> 8) & 255);
        int b = Mth.lerpInt(full, col1 & 255, col2 & 255);
        // PORT: new java.awt.Color(r, g, b, 255).getRGB() -> the same ARGB int without AWT
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}
