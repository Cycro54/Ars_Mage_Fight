package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.BlackHoleEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BlackHoleModel extends AnimatedGeoModel<BlackHoleEntity> {

    @Override
    public ResourceLocation getModelLocation(BlackHoleEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/black_hole.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/black_hole_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BlackHoleEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/black_hole_animations.json");
    }
}
