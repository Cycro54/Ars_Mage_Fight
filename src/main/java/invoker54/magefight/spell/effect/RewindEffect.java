package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class RewindEffect extends AbstractEffect {

    public static RewindEffect INSTANCE = new RewindEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String rewindString = "REWIND_DATA";
    private static final String posString = "POSITION_VECTOR";
    private static final String healthString = "HEALTH_FLOAT";
    private static final String currIndexString = "INDEX_INT";
    private static final String oldIndexString = "OLD_INDEX_INT";
    public static final String anchorPackString = "TIME_ANCHOR_NBT";
    public static final String anchorIDString = "TIME_ANCHOR_ID";
    public static final String endTimeString = "END_TIME_FLOAT";


    private RewindEffect() {
        super("rewind", "Rewind");
    }

    //This would make it so the effect only works on entities (might not need it though.)
//    @Override
//    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        return rayTraceResult instanceof EntityRayTraceResult;
//    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Then do the spell thing here
        MagicDataCap cap = MagicDataCap.getCap(hitEntity);

        if (spellStats.hasBuff(AugmentExtendTime.INSTANCE)){
            int time = 5 + (2 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE));

            if (hitEntity.hasEffect(EffectInit.REWIND_EFFECT)){
                //First remove the effect
                hitEntity.removeEffect(EffectInit.REWIND_EFFECT);

                //Then move the entity
                moveToAnchor(hitEntity);
            }
            else {
                //Construct the anchor pack
                CompoundNBT anchorPack = new CompoundNBT();
                //Position
                packPosition(anchorPack, hitEntity.position());

                //Health
                anchorPack.putFloat(healthString, hitEntity.getHealth());

                //Time the effect should end
                anchorPack.putFloat(endTimeString, hitEntity.level.getGameTime() + (time * 20F));

                //Now place it into the rewind nbt
                cap.getTag(rewindString).put(anchorPackString, anchorPack);

                //Then finally give them the rewind effect
                hitEntity.addEffect(new EffectInstance(EffectInit.REWIND_EFFECT, time * 20, 0));
            }
        }

        else {
            //MAKE SURE ITS A PLAYER
            if (!(rayTraceResult.getEntity() instanceof PlayerEntity)) return;

            if (!cap.hasTag(rewindString)) return;
            CompoundNBT rewindTag = cap.getTag(rewindString);

            int oldestIndex = rewindTag.getInt(oldIndexString);
            LOGGER.debug("OLDEST INDEX WAS: " + oldestIndex);

            //Grab the rewind Pack
            CompoundNBT rewindPack = rewindTag.getCompound("" + oldestIndex);

            //First lets do position
            hitEntity.moveTo(unPackPosition(rewindPack));

            //Then health
            float prevHealth = rewindPack.getFloat(healthString);
            if (prevHealth > hitEntity.getHealth()) {
                hitEntity.setHealth(prevHealth);
            }

            //Remove the rest of the rewindpacks and set the currIndex to be in front of the oldestIndex
            int currIndex = oldestIndex;
            LOGGER.debug("INDEX NOT TO DELETE IS " + currIndex);
            rewindTag.putInt(currIndexString, oldestIndex);
            oldestIndex = (oldestIndex == 5) ? 1 : oldestIndex + 1;
            while (oldestIndex != currIndex) {
                if (rewindTag.contains("" + oldestIndex)) {
                    rewindTag.remove("" + oldestIndex);
                    LOGGER.debug("DELETING INDEX " + oldestIndex);
                }

                oldestIndex = (oldestIndex == 5) ? 1 : oldestIndex + 1;
            }

            //Save the current health
//            rewindTag.getCompound(""+currIndex).putFloat(healthString, hitEntity.getHealth());
        }

        MagicDataCap.syncToClient(hitEntity);
    }

    public static void packPosition(CompoundNBT rewindPack, Vector3d pos){
        if (rewindPack == null) {
            LOGGER.debug("FOR REWIND EFFECT: YOU FORGOT THE COMPOUND!! packPosition" );
            return;
        }

        rewindPack.putDouble(posString + "X", pos.x());
        rewindPack.putDouble(posString + "Y", pos.y());
        rewindPack.putDouble(posString + "Z", pos.z());
    }
    public static Vector3d unPackPosition(CompoundNBT rewindPack){
        return new Vector3d(
                rewindPack.getDouble(posString + "X"),
                rewindPack.getDouble(posString + "Y"),
                rewindPack.getDouble(posString + "Z")
        );
    }

    public static void moveToAnchor(LivingEntity hitEntity){
        CompoundNBT rewindTag = MagicDataCap.getCap(hitEntity).getTag(rewindString);
        CompoundNBT rewindPack = rewindTag.getCompound(anchorPackString);

        //First lets do position
        hitEntity.moveTo(unPackPosition(rewindPack));

        //Then health
        float prevHealth = rewindPack.getFloat(healthString);
        if (prevHealth > hitEntity.getHealth()) {
            hitEntity.setHealth(prevHealth);
        }

        //Finally delete the time anchor pack
        rewindTag.remove(anchorPackString);

        MagicDataCap.syncToClient(hitEntity);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 100;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    //Get a book description (I don't think this is actually used.)
    @Override
    public String getBookDescription() {
        return "Example text. (Uses Patchouli formatting)";
    }

    //What is the main item for crafting this glyph
    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class SpellEvents{
        @SubscribeEvent
        public static void onTick(TickEvent.PlayerTickEvent event){
            if (event.side.isClient()) return;
            if (event.phase == TickEvent.Phase.START) return;
            if (event.player.level.getGameTime() % 20 != 0) return;

            PlayerEntity player = event.player;
            MagicDataCap cap = MagicDataCap.getCap(player);

            CompoundNBT rewindTag = cap.getTag(rewindString);
            int currIndex = rewindTag.getInt(currIndexString);
            currIndex = (currIndex == 5) ? 1 : (currIndex + 1);
            CompoundNBT rewindPack = rewindTag.contains("" + currIndex) ? rewindTag.getCompound("" + currIndex) :  new CompoundNBT();

            //First position
            packPosition(rewindPack, player.position());

            //Next up is health
            rewindPack.putFloat(healthString, player.getHealth());

            //Then place that pack into the main Compound NBT with the currIndex as its name
            rewindTag.put("" + currIndex, rewindPack);

            //Make sure to save the currIndex in main tag
            rewindTag.putInt(currIndexString, currIndex);
//            LOGGER.debug("ITS BEEN A SECOND, SAVING TO INDEX " + (currIndex));

            //AND find the oldest Index and save that
            int oldestIndex = (currIndex == 5) ? 1 : (currIndex + 1);
            while (oldestIndex != currIndex){
                if (rewindTag.contains("" + oldestIndex)) break;

                oldestIndex = (oldestIndex == 5) ? 1 : (oldestIndex + 1);
            }

            rewindTag.putInt(oldIndexString, oldestIndex);
        }
    }
}
