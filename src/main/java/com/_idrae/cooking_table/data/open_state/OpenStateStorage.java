package com._idrae.cooking_table.data.open_state;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.data.open_state.OpenState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import javax.annotation.Nullable;

public class OpenStateStorage implements Capability.IStorage<OpenState> {

    private static final String NBT_KEY = "open_state";

    public OpenStateStorage() {
    }

    @Nullable
    @Override
    public INBT writeNBT(Capability<OpenState> capability, OpenState instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean(NBT_KEY, instance.isOpenState());
        return nbt;
    }
    

    @Override
    public void readNBT(Capability<OpenState> capability, OpenState instance, Direction side, INBT nbt) {
        instance.setOpenState(((CompoundNBT) nbt).getBoolean(NBT_KEY));
    }
}


