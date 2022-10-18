package invoker54.magefight.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.client.Ticker;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.potion.FatalBondPotionEffect;
import invoker54.magefight.spell.effect.RewindEffect;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsMageFight.MOD_ID)
public class RenderRewindLineEvent {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation GREEN_LINE = new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/time_anchor/time_anchor_line_green.png");
    private static final ResourceLocation YELLOW_LINE = new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/time_anchor/time_anchor_line_yellow.png");
    private static final ResourceLocation RED_LINE = new ResourceLocation(ArsMageFight.MOD_ID, "textures/entity/time_anchor/time_anchor_line_red.png");

    @SubscribeEvent
    public static void onDrawRewindLine(RenderWorldLastEvent event){
        if (event.isCanceled()) return;
        if (ClientUtil.mC.isPaused()) return;

//        ArrayList<LivingEntity> checkedEntities = new ArrayList<>();

        for (Entity entity : ClientUtil.mC.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!entity.isAlive()) continue;
            //if (!((LivingEntity) entity).hasEffect(EffectInit.FATAL_BOND_EFFECT)) continue;
//            LOGGER.debug("HAS FATAL BONDS? " + (true));
            MagicDataCap cap = MagicDataCap.getCap((LivingEntity) entity);
            if (!cap.hasTag(RewindEffect.rewindString)) continue;
            if (!cap.getTag(RewindEffect.rewindString).contains(RewindEffect.anchorPackString)) continue;

            CompoundNBT anchorPack = cap.getTag(RewindEffect.rewindString).getCompound(RewindEffect.anchorPackString);
            float timeLeft = anchorPack.getFloat(RewindEffect.endTimeString) - ClientUtil.mC.level.getGameTime();

            float height = (float) (entity.getBoundingBox().getYsize()/2);
            Vector3d rewindPos = RewindEffect.unPackPosition(anchorPack);
            rewindPos = rewindPos.add(0,height,0);
            Vector3d startPos = ClientUtil.smoothLerp(new Vector3d(entity.xOld,entity.yOld,entity.zOld), entity.position(), false);
            startPos = startPos.add(0,height,0);

            MatrixStack stack = event.getMatrixStack();

            stack.pushPose();
            if (timeLeft >= 8 * 20){
                ClientUtil.TEXTURE_MANAGER.bind(GREEN_LINE);
            }
            else if (timeLeft >= 3 * 20){
                ClientUtil.TEXTURE_MANAGER.bind(YELLOW_LINE);
            }
            else{
                ClientUtil.TEXTURE_MANAGER.bind(RED_LINE);
            }

            float offset = ClientUtil.mC.getDeltaFrameTime() + ClientUtil.mC.level.getGameTime();
            float range = (float) startPos.distanceTo(rewindPos)/0.5F;

            ClientUtil.drawWorldLine(stack, startPos, rewindPos, 0.25F, 0 - offset, (32F * range), 0, (32F), 32);
            stack.popPose();
        }
    }
}
