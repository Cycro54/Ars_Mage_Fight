package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.client.model.BlackHoleModel;
import invoker54.magefight.entity.BlackHoleEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BlackHoleRenderer extends GeoEntityRenderer<BlackHoleEntity> {
    public BlackHoleRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BlackHoleModel());
    }

    @Override
    public RenderType getRenderType(BlackHoleEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return CustomRender.opaqueGlow(textureLocation, false);
    }

    //
//    @Override
//    public void render(BlackHoleEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
//        stack.pushPose();
//        stack.scale(3, 3, 3);
//        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
//        stack.popPose();
//    }
}
