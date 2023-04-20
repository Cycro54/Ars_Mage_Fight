package invoker54.magefight.blocks.tile;

import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.TileInit;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;

public class CombatBlockTile extends TileEntity implements IAnimatable {
    private static final Logger LOGGER = LogManager.getLogger();

    public CombatBlockTile() {
        super(TileInit.COMBAT_BLOCK_TILE);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<CombatBlockTile>(this, "mainControl", 3, this::mainController));
    }

    private PlayState mainController(AnimationEvent<CombatBlockTile> event){
        AnimationController control = event.getController();
        double distance = new Vector3d(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()).distanceTo(ClientUtil.mC.player.position());
        if (distance <= 4) {
            if (control.getCurrentAnimation() == null || Objects.equals(control.getCurrentAnimation().animationName, "close")) {
                control.setAnimation(new AnimationBuilder().addAnimation("open"));
            }
            else if (Objects.equals(control.getCurrentAnimation().animationName, "open") && control.getAnimationState() == AnimationState.Stopped){
                // LOGGER.debug("IDLING");
                control.setAnimation(new AnimationBuilder().addAnimation("idle_open"));
            }
        }
        else if (distance > 4 && control.getCurrentAnimation() != null) {
            control.setAnimation(new AnimationBuilder().addAnimation("close"));
        }
        return PlayState.CONTINUE;
    }
    private final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}
