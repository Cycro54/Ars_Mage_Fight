package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.model.SpellShieldModel;
import invoker54.magefight.entity.BlackHoleEntity;
import invoker54.magefight.entity.SpellShieldEntity;
import invoker54.magefight.potion.StalwartPotionEffect;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import static invoker54.magefight.potion.StalwartPotionEffect.stalwartString;

public class SpellShieldRenderer extends GeoEntityRenderer<SpellShieldEntity> {
    private static final Logger LOGGER = LogManager.getLogger();

    public SpellShieldRenderer(EntityRendererManager renderManager) {
        super(renderManager, new SpellShieldModel());
    }

    @Override
    public RenderType getRenderType(SpellShieldEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return CustomRender.opaqueGlow(textureLocation, false);
    }

    @Override
    public void render(SpellShieldEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        stack.pushPose();
        float size = 1;

        Entity shieldEntity = entity.getVehicle();
        if (shieldEntity != null){
            size = (float) shieldEntity.getBoundingBox().getYsize();
        }
        stack.scale(size, size, size);
//        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(p_225628_4_)));
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
