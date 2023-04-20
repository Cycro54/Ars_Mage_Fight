package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.client.model.RuptureSwordModel;
import invoker54.magefight.entity.RuptureSwordEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RuptureSwordRenderer extends GeoEntityRenderer<RuptureSwordEntity> {
    private static final Logger POGGER = LogManager.getLogger();

    public RuptureSwordRenderer(EntityRendererManager renderManager) {
        super(renderManager, new RuptureSwordModel());
    }

    @Override
    public void render(RuptureSwordEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        float size = 1;
        Entity rupturedEntity = entity.getVehicle();
        if (rupturedEntity != null && rupturedEntity.isAlive()){
            size = (float) (rupturedEntity.getBoundingBox().getYsize()/1.8F);
        }
        stack.scale(size, size, size);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
