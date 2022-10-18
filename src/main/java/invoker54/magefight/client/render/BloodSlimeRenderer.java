package invoker54.magefight.client.render;

import invoker54.magefight.ArsMageFight;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;

public class BloodSlimeRenderer extends SlimeRenderer {
    private static final ResourceLocation Blood_Slime_Location = new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/blood_slime.png");

    public BloodSlimeRenderer(EntityRendererManager p_i47193_1_) {
        super(p_i47193_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeEntity p_110775_1_) {
        return Blood_Slime_Location;
    }
}
