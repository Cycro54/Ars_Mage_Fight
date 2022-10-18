package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.DeathGripEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DeathGripModel extends AnimatedGeoModel<DeathGripEntity> {

    @Override
    public ResourceLocation getModelLocation(DeathGripEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/deaths_grip.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DeathGripEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/death_grip_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DeathGripEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/death_grip_animations.json");
    }
}
