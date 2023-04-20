package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.TimeAnchorEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TimeAnchorModel extends AnimatedGeoModel<TimeAnchorEntity> {

    @Override
    public ResourceLocation getModelLocation(TimeAnchorEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/time_anchor.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TimeAnchorEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/time_anchor/time_anchor_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TimeAnchorEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/time_anchor_animations.json");
    }
}
