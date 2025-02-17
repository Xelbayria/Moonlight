package net.mehvahdjukaar.moonlight.core.mixins.forge;

import net.mehvahdjukaar.moonlight.api.client.forge.ModFluidType;
import net.mehvahdjukaar.moonlight.api.fluids.ModFlowingFluid;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ModFlowingFluid.class)
public abstract class SelfModFlowingFluidMixin extends FlowingFluid {

    @Unique
    private FluidType type;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void afterInit(ModFlowingFluid.Properties properties, Supplier<? extends LiquidBlock> block) {
        if (properties.copyFluid != null) {
            this.type = properties.copyFluid.getFluidType();
        } else this.type = ModFluidType.create(properties, (ModFlowingFluid) (Object) this);
    }

    @Override
    public FluidType getFluidType() {
        return type;
    }
}
