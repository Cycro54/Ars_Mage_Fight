package invoker54.magefight.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.potion.FatalBondPotionEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, value = Dist.CLIENT)
public class renderFatalChainsEvent {
//    private static final int effectColor = new Color(255, 88, 50,255).getRGB();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(ArsMageFight.MOD_ID, "textures/fatal_chain.png");

    @SubscribeEvent
    public static void onDrawChain(RenderWorldLastEvent event){
        if (event.isCanceled()) return;
        if (ClientUtil.mC.isPaused()) return;

//        ArrayList<LivingEntity> checkedEntities = new ArrayList<>();

        for (Entity entity : ClientUtil.mC.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!entity.isAlive()) continue;
            //if (!((LivingEntity) entity).hasEffect(EffectInit.FATAL_BOND_EFFECT)) continue;
//            LOGGER.debug("HAS FATAL BONDS? " + (true));
            MagicDataCap cap = MagicDataCap.getCap((LivingEntity) entity);
            if (!cap.hasTag(FatalBondPotionEffect.fatalBondString)) continue;

            CompoundNBT fatalNBT = cap.getTag(FatalBondPotionEffect.fatalBondString);
            if (!fatalNBT.contains(FatalBondPotionEffect.slave_linkID)) continue;

            LivingEntity slaveEntity = (LivingEntity) ClientUtil.mC.level.getEntity(fatalNBT.getInt(FatalBondPotionEffect.slave_linkID));
            if (slaveEntity == null) continue;

            MatrixStack stack = event.getMatrixStack();
            Vector3d masterPos = ClientUtil.smoothLerp(new Vector3d(entity.xOld,entity.yOld,entity.zOld), entity.position(), false);
            masterPos = masterPos.add(0,entity.getBoundingBox().getYsize()/2F,0);

            Vector3d slavePos = ClientUtil.smoothLerp(new Vector3d(slaveEntity.xOld,slaveEntity.yOld,slaveEntity.zOld), slaveEntity.position(), false);
            slavePos = slavePos.add(0,slaveEntity.getBoundingBox().getYsize()/2F,0);

            ClientUtil.TEXTURE_MANAGER.bind(CHAIN_TEXTURE);
            float offset = ClientUtil.mC.getDeltaFrameTime() + ClientUtil.mC.level.getGameTime();
            float range = (float) masterPos.distanceTo(slavePos)/0.5F;

            ClientUtil.drawWorldLine(stack, masterPos, slavePos, 0.25F, 0 + offset, (32F * range), 0, (32F), 32);
            ClientUtil.TEXTURE_MANAGER.release(CHAIN_TEXTURE);
        }
    }
}
