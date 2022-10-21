package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.ComboEntity;
import invoker54.magefight.entity.ComboEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ComboEntityModel extends AnimatedGeoModel<ComboEntity> {

    @Override
    public ResourceLocation getModelLocation(ComboEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/combo_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ComboEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/combo_entity_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ComboEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/combo_entity_animations.json");
    }
}
