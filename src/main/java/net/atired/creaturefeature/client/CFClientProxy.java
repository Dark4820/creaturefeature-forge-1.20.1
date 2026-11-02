package net.atired.creaturefeature.client;

import net.minecraft.world.item.ItemStack;

// PORT: client-only state, as in the original. The cached BufferSources (getDetritusSource, getFendSource,
// getSunSource, getFriendSource) need the entity renderers and arrive with their creatures (F3-F5).
public class CFClientProxy {
    public float eebyDeebyNess = 0.0f;
    public float flayed2 = 0.0f;
    public boolean searchingForHim=false;
    public float rabies2 = 0.0f;
    public float nan_title = 0.0f;
    public float gasLeak = 0.0f;
    public float wobble = 0.0f;
    public float manShader = 0.0f;
    public float bacterial = 0.0f;
    public float fiendish2 = 0.0f;
    public float fiendish = 0.0f;
    public ItemStack wobblyItem=null;
    public int attachment = 0;
    public float[] colours = {0.0f,0.0f,0.0f,0.0f};
}
