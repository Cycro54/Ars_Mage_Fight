package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.client.model.BlackHoleModel;
import invoker54.magefight.client.model.RuptureSwordModel;
import invoker54.magefight.entity.BlackHoleEntity;
import invoker54.magefight.entity.DeathGripEntity;
import invoker54.magefight.entity.RuptureSwordEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
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
