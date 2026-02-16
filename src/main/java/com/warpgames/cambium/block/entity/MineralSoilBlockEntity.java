package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup; // Import 1
import net.minecraft.nbt.CompoundTag;   // Import 2
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MineralSoilBlockEntity extends BlockEntity {

    private int charge = 0;
    public static final int MAX_CHARGE = 100;

    public MineralSoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINERAL_SOIL_BE, pos, state);
    }

    // --- 1.21.1 SAVE/LOAD LOGIC ---

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Charge", this.charge);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.charge = tag.getInt("Charge");
    }

    // [KEEP REST OF LOGIC UNCHANGED]
    // --- LOGIC: CALLED BY LEAVES ---
    // Returns true if successful (charge was consumed)
    public boolean tryConsumeCharge(int amount) {
        if (charge >= amount) {
            charge -= amount;
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    // --- LOGIC: CALLED BY PLAYER ---
    public void addCharge(int amount) {
        this.charge = Math.min(charge + amount, MAX_CHARGE);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // --- SYNCING (For visual effects later) ---
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }


    public int getCharge() {
        return charge;
    }
}