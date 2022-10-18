package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.SpellShieldEntity;
import invoker54.magefight.entity.SpellShieldEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpellShieldModel extends AnimatedGeoModel<SpellShieldEntity> {

    @Override
    public ResourceLocation getModelLocation(SpellShieldEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/spell_shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SpellShieldEntity object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/spell_shield_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SpellShieldEntity animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/spell_shield_animations.json");
    }
}
