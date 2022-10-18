package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.LifeSigilEntity;
import invoker54.magefight.entity.LifeSigilEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LifeSigilModel extends AnimatedGeoModel<LifeSigilEntity> {

    @Override
    public ResourceLocation getModelLocation(LifeSigilEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/life_sigil.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(LifeSigilEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/life_sigil_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LifeSigilEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/life_sigil_animations.json");
    }
}
