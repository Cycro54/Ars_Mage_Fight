package invoker54.magefight.client.model;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.blocks.tile.CombatBlockTile;
import invoker54.magefight.entity.BlackHoleEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CombatBlockModel extends AnimatedGeoModel<CombatBlockTile> {

    @Override
    public ResourceLocation getModelLocation(CombatBlockTile object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "geo/combat_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(CombatBlockTile object) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "textures/combat_block_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(CombatBlockTile animatable) {
        return new ResourceLocation(ArsMageFight.MOD_ID, "animations/combat_block_animations.json");
    }
}
