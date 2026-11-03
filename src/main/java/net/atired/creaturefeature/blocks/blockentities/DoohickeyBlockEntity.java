package net.atired.creaturefeature.blocks.blockentities;

import net.atired.creaturefeature.blocks.DoohickeyBlock;
import net.atired.creaturefeature.init.CFBlockEntityInit;
import net.atired.creaturefeature.init.CFItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DoohickeyBlockEntity extends BlockEntity  {
    public static ArrayList<Item> PLACEABLE = new ArrayList<>(List.of(new Item[]{
            CFItemInit.SLEEPING_POWDER.get(), CFItemInit.MINEDFLAYER_GOOP.get(),
            CFItemInit.VERTIGO_HORN.get(), CFItemInit.BACTERIUM_BALL.get(), CFItemInit.FIENDISH_ESSENCE.get()}));
    public ItemStack placedItem = Items.AIR.getDefaultInstance();
    public int aged = 0;
    public float ranged = 0.0f;
    public Vector3f color = new Vector3f();
    public DoohickeyBlockEntity(BlockPos pos, BlockState blockState) {
        super(CFBlockEntityInit.DOOHICKEY.get(), pos, blockState);
    }

    @Override
    // PORT: 1.20.5+ saveAdditional/loadAdditional/getUpdateTag/handleUpdateTag take a HolderLookup.Provider and
    // ItemStack.save/parse use it; 1.20.1: saveAdditional/load(CompoundTag), ItemStack.save(tag)/ItemStack.of(tag)
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(this.placedItem!=null&&!this.placedItem.isEmpty())
            tag.put("doohickey_item",this.placedItem.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // (ItemStack.parse(...).orElse(STONE): a missing or unreadable item gives STONE, which then means "no item")
        ItemStack parsed = tag.contains("doohickey_item", 10) ? ItemStack.of(tag.getCompound("doohickey_item")) : ItemStack.EMPTY;
        this.placedItem=parsed.isEmpty()?Items.STONE.getDefaultInstance():parsed;
        if(this.placedItem.getItem()!=Items.STONE){
            DoohickeyBlockEntity.placedItem(this);
        }
        else{
            this.placedItem=Items.AIR.getDefaultInstance();
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }
    public static void placedItem(DoohickeyBlockEntity doohickeyBlockEntity){
        doohickeyBlockEntity.color = new Vector3f(0.5f,0.7f,1.0f);
        if(doohickeyBlockEntity.placedItem.getItem()== CFItemInit.MINEDFLAYER_GOOP.get()){
            doohickeyBlockEntity.color = new Vector3f(1f,0.45f,0.8f);
        }
        if(doohickeyBlockEntity.placedItem.getItem()== CFItemInit.VERTIGO_HORN.get()){
            doohickeyBlockEntity.color = new Vector3f(0.45f,0.15f,0.52f);
        }
        if(doohickeyBlockEntity.placedItem.getItem()== CFItemInit.BACTERIUM_BALL.get()){
            doohickeyBlockEntity.color = new Vector3f(0.5f,1f,0.55f);
        }
        if(doohickeyBlockEntity.placedItem.getItem()== CFItemInit.FIENDISH_ESSENCE.get()){
            doohickeyBlockEntity.color = new Vector3f(1f,0.45f,0.35f);
        }
        doohickeyBlockEntity.setChanged();
    }
    public static void tick(Level level, BlockPos pos, BlockState state, DoohickeyBlockEntity doohickeyBlockEntity){

        if(doohickeyBlockEntity.placedItem!=null&&!doohickeyBlockEntity.placedItem.isEmpty()) {
            doohickeyBlockEntity.ranged= Mth.lerp(0.33f,doohickeyBlockEntity.ranged,1.0f);
        }
        else{
            doohickeyBlockEntity.ranged*=0.66f;
        }
        doohickeyBlockEntity.aged+=1;
    }

    // PORT: the render box was BlockEntityRenderer.getRenderBoundingBox in NeoForge; in Forge 1.20.1 it is the block
    // entity's (IForgeBlockEntity), same value: the block's box grown by 9 (the sphere's radius is 8.6)
    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return new net.minecraft.world.phys.AABB(this.getBlockPos()).inflate(9);
    }
}
