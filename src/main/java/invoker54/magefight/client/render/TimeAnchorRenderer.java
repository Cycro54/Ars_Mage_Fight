package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.client.model.BlackHoleModel;
import invoker54.magefight.client.model.TimeAnchorModel;
import invoker54.magefight.entity.BlackHoleEntity;
import invoker54.magefight.entity.TimeAnchorEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TimeAnchorRenderer extends GeoEntityRenderer<TimeAnchorEntity> {
    public TimeAnchorRenderer(EntityRendererManager renderManager) {
        super(renderManager, new TimeAnchorModel());
    }

    @Override
    public RenderType getRenderType(TimeAnchorEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return CustomRender.opaqueGlow(textureLocation, false);
    }


    @Override
    public void render(TimeAnchorEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        float scale = 1;
        if (entity.getOwner() != null) scale = (float) (entity.getOwner().getBoundingBox().getYsize()/1.8F);
        stack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
