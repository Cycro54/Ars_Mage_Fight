package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.items.CombatBlockItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CombatBlockItemModel extends AnimatedGeoModel<CombatBlockItem> {

    @Override
    public ResourceLocation getModelLocation(CombatBlockItem object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/combat_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(CombatBlockItem object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/combat_block_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(CombatBlockItem animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/combat_block_animations.json");
    }
}
