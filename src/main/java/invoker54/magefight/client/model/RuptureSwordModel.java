package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.RuptureSwordEntity;
import invoker54.magefight.entity.RuptureSwordEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RuptureSwordModel extends AnimatedGeoModel<RuptureSwordEntity> {

    @Override
    public ResourceLocation getModelLocation(RuptureSwordEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/rupture_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RuptureSwordEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/rupture_sword_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RuptureSwordEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/rupture_sword_animations.json");
    }
}
