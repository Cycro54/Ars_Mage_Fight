package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.client.model.DeathGripModel;
import invoker54.magefight.entity.DeathGripEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DeathGripRenderer extends GeoEntityRenderer<DeathGripEntity> {
    public DeathGripRenderer(EntityRendererManager renderManager) {
        super(renderManager, new DeathGripModel());
    }

    @Override
    public void render(DeathGripEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        float size = (float) entity.ownerScale/1.8F;
        if (entity.ownerScale < 1.8f) size = (float) entity.ownerScale;
        stack.scale(size, size, size);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
