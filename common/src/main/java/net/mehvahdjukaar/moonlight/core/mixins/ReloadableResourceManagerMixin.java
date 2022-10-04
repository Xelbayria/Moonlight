package net.mehvahdjukaar.moonlight.core.mixins;

import net.mehvahdjukaar.moonlight.api.events.EarlyPackReloadEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin implements ResourceManager{

    @Shadow @Final private PackType type;

    @Shadow public abstract Optional<Resource> getResource(ResourceLocation arg);

    //should fire right before add reload listener, before packs are reloaded and listeners called
    @Inject(method = "createReload", at = @At(value = "HEAD"))
    private void dynamicPackEarlyReload(Executor executor, Executor executor2,
                                        CompletableFuture<Unit> completableFuture, List<PackResources> packs,
                                        CallbackInfoReturnable<ReloadInstance> cir) {
        //fires on world load or on /reload
        //token to assure that modded resources are included
        if (type == PackType.SERVER_DATA && this.getResource(Moonlight.res("moonlight/token.json")).isPresent()) {
            //this assumes that it includes all pack including all mod assets
            //reload dynamic packs before reloading data packs
            MoonlightEventsHelper.postEvent(new EarlyPackReloadEvent(packs, this), EarlyPackReloadEvent.class);
            //ServerEarlyResourceManager.loadResources(packs, this);
        }
    }
}
