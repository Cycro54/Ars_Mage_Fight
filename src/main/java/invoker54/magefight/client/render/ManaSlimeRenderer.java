package invoker54.magefight.client.render;

import invoker54.magefight.ArsMageFight;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;

public class ManaSlimeRenderer extends SlimeRenderer {
    private static final ResourceLocation Mana_Slime_Location = new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/mana_slime.png");

    public ManaSlimeRenderer(EntityRendererManager p_i47193_1_) {
        super(p_i47193_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeEntity p_110775_1_) {
        return Mana_Slime_Location;
    }
}
