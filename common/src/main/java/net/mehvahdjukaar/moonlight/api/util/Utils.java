package net.mehvahdjukaar.moonlight.api.util;

import com.google.common.collect.ImmutableList;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.map.MapDecorationRegistry;
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;


public class Utils {

    public static void swapItem(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem, boolean bothSides) {
        if (!player.level.isClientSide || bothSides)
            player.setItemInHand(hand, ItemUtils.createFilledResult(oldItem.copy(), player, newItem, player.isCreative()));
    }

    public static void swapItem(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem) {
        if (!player.level.isClientSide)
            player.setItemInHand(hand, ItemUtils.createFilledResult(oldItem.copy(), player, newItem, player.isCreative()));
    }

    public static void swapItemNBT(Player player, InteractionHand hand, ItemStack oldItem, ItemStack newItem) {
        if (!player.level.isClientSide)
            player.setItemInHand(hand, ItemUtils.createFilledResult(oldItem.copy(), player, newItem, false));
    }

    public static void swapItem(Player player, InteractionHand hand, ItemStack newItem) {
        if (!player.level.isClientSide)
            player.setItemInHand(hand, ItemUtils.createFilledResult(player.getItemInHand(hand).copy(), player, newItem, player.isCreative()));
    }

    public static void addStackToExisting(Player player, ItemStack stack) {
        var inv = player.getInventory();
        boolean added = false;
        for (int j = 0; j < inv.items.size(); j++) {
            if (inv.getItem(j).is(stack.getItem()) && inv.add(j, stack)) {
                added = true;
                break;
            }
        }
        if (!added && inv.add(stack)) {
            player.drop(stack, false);
        }
    }

    //xp bottle logic
    public static int getXPinaBottle(int bottleCount, RandomSource rand) {
        int xp = 0;
        for (int i = 0; i < bottleCount; i++) xp += (3 + rand.nextInt(5) + rand.nextInt(5));
        return xp;
    }


    public static ResourceLocation getID(Block object) {
        return BuiltInRegistries.BLOCK.getKey(object);
    }

    public static ResourceLocation getID(EntityType<?> object) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(object);
    }

    public static ResourceLocation getID(Biome object) {
        return hackyGetRegistry(Registries.BIOME).getKey(object);
    }

    public static ResourceLocation getID(ConfiguredFeature<?, ?> object) {
        return hackyGetRegistry(Registries.CONFIGURED_FEATURE).getKey(object);
    }

    public static ResourceLocation getID(Item object) {
        return BuiltInRegistries.ITEM.getKey(object);
    }

    public static ResourceLocation getID(Fluid object) {
        return BuiltInRegistries.FLUID.getKey(object);
    }

    public static ResourceLocation getID(BlockEntityType<?> object) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(object);
    }

    public static ResourceLocation getID(RecipeSerializer<?> object) {
        return BuiltInRegistries.RECIPE_SERIALIZER.getKey(object);
    }

    public static ResourceLocation getID(SoftFluid object) {
        return SoftFluidRegistry.hackyGetRegistry().getKey(object);
    }

    public static ResourceLocation getID(MapDecorationType<?, ?> object) {
        return MapDecorationRegistry.hackyGetRegistry().getKey(object);
    }

    public static ResourceLocation getID(Potion object) {
        return BuiltInRegistries.POTION.getKey(object);
    }

    public static ResourceLocation getID(MobEffect object) {
        return BuiltInRegistries.MOB_EFFECT.getKey(object);
    }

    public static ResourceLocation getID(Object object) {
        if (object instanceof Block b) return getID(b);
        if (object instanceof Item b) return getID(b);
        if (object instanceof EntityType<?> b) return getID(b);
        if (object instanceof Biome b) return getID(b);
        if (object instanceof Fluid b) return getID(b);
        if (object instanceof BlockEntityType<?> b) return getID(b);
        if (object instanceof RecipeSerializer<?> b) return getID(b);
        if (object instanceof ConfiguredFeature<?, ?> c) return getID(c);
        if (object instanceof Potion c) return getID(c);
        if (object instanceof MobEffect c) return getID(c);
        if (object instanceof Supplier<?> s) return getID(s.get());
        if (object instanceof SoftFluid s) return getID(s);
        if (object instanceof MapDecorationType<?, ?> s) return getID(s);
        throw new UnsupportedOperationException("Unknown class type " + object.getClass());
    }

    public static <T> boolean isTagged(T entry, Registry<T> registry, TagKey<T> tag) {
        return registry.getHolder(registry.getId(entry)).map(h -> h.is(tag)).orElse(false);
    }

    //very hacky
    public static RegistryAccess hackyGetRegistryAccess() {
        var s = PlatHelper.getCurrentServer();
        if (s != null) return s.registryAccess();
        if (!PlatHelper.getPhysicalSide().isServer()) {
            var level = Minecraft.getInstance().level;
            if (level != null) return level.registryAccess();
        }
        throw new UnsupportedOperationException("Failed to get registry access. This is a bug");
    }

    public static <T> Registry<T> hackyGetRegistry(ResourceKey<Registry<T>> registry) {
        return hackyGetRegistryAccess().registryOrThrow(registry);
    }

    /**
     * Copies block properties without keeping stupid lambdas that could include references to the wrong blockstate properties
     */
    public static BlockBehaviour.Properties copyPropertySafe(BlockBehaviour blockBehaviour) {
        var p = BlockBehaviour.Properties.copy(blockBehaviour);
        p.lightLevel(s -> 0);
        p.offsetType(BlockBehaviour.OffsetType.NONE);
        p.color(blockBehaviour.defaultMaterialColor());
        return p;
    }

    public static HitResult rayTrace(LivingEntity entity, Level world, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        return rayTrace(entity, world, blockMode, fluidMode, ForgeHelper.getReachDistance(entity));
    }

    public static HitResult rayTrace(Entity entity, Level world, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, double range) {
        Vec3 startPos = entity.getEyePosition();
        Vec3 ray = entity.getViewVector(1).scale(range);
        Vec3 endPos = startPos.add(ray);
        ClipContext context = new ClipContext(startPos, endPos, blockMode, fluidMode, entity);
        return world.clip(context);
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return targetType == type ? (BlockEntityTicker<A>) ticker : null;
    }



}