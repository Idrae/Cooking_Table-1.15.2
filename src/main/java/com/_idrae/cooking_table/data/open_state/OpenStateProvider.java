package com._idrae.cooking_table.data.open_state;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.data.open_state.OpenState;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenStateProvider implements ICapabilityProvider {

    // @CapabilityInject(OpenState.class)
    public static Capability<OpenState> OPEN_STATE_CAPABILITY = null;

    private LazyOptional<OpenState> instance = LazyOptional.of(OPEN_STATE_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == OPEN_STATE_CAPABILITY  ? OPEN_STATE_CAPABILITY.orEmpty(cap, instance): LazyOptional.empty();
    }
}
