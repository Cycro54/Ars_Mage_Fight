package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.model.LifeSigilModel;
import invoker54.magefight.entity.LifeSigilEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LifeSigilRenderer extends GeoEntityRenderer<LifeSigilEntity> {
    public LifeSigilRenderer(EntityRendererManager renderManager) {
        super(renderManager, new LifeSigilModel());
    }

    @Override
    public RenderType getRenderType(LifeSigilEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return CustomRender.translucentGlow(textureLocation, true);
    }

    @Override
    public void render(LifeSigilEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        MagicDataCap cap = MagicDataCap.getCap(entity);
        float size = 1 + (cap.hasTag(LifeSigilEntity.lifeSigilString) ? cap.getTag(LifeSigilEntity.lifeSigilString).getInt(LifeSigilEntity.rangeString)/1.5F : 0);
        stack.scale(size, 1, size);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
