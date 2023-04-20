package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.client.model.ComboEntityModel;
import invoker54.magefight.entity.ComboEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ComboEntityRenderer extends GeoEntityRenderer<ComboEntity> {
    private static final Logger POGGER = LogManager.getLogger();

    public ComboEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ComboEntityModel());
    }

    @Override
    public RenderType getRenderType(ComboEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return CustomRender.armorCutoutNoCull(textureLocation);
    }

    @Override
    public void render(ComboEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        float size = 1;
        Entity hitEntity = entity.getVehicle();
        if (hitEntity != null && hitEntity.isAlive()){
            size = (float) (hitEntity.getBoundingBox().getYsize()/1.8F);
        }
        stack.scale(size, size, size);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    @Override
    public void render(GeoModel model, ComboEntity animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
