package net.mehvahdjukaar.moonlight.api.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.function.Function;

/**
 * Simple implementation of a dynamic model that accepts another model as a parameter
 */
public class NestedModelLoader implements CustomModelLoader {

    private final Function<BakedModel, CustomBakedModel> factory;
    private final String path;

    public NestedModelLoader(String modelPath, Function<BakedModel, CustomBakedModel> bakedModelFactory) {
        this.factory = bakedModelFactory;
        this.path = modelPath;
    }

    @Override
    public CustomGeometry deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        return new Geometry(ResourceLocation.tryParse(GsonHelper.getAsString(json, path)));
    }

    private class Geometry implements CustomGeometry {

        private final ResourceLocation modelLoc;

        private Geometry(ResourceLocation model) {
            this.modelLoc = model;
        }

        @Override
        public CustomBakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
            UnbakedModel model = modelBaker.getModel(modelLoc);
            BakedModel bakedModel = model.bake(modelBaker, spriteGetter, transform, modelLoc);
            if (model == modelBaker.getModel(ModelBakery.MISSING_MODEL_LOCATION)) {
                throw new JsonParseException("Found missing model for location " + modelLoc + " while parsing nested model " + location);
            }
            return NestedModelLoader.this.factory.apply(bakedModel);
        }
    }
}
