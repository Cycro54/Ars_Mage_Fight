package invoker54.magefight.items;

import invoker54.magefight.client.render.CombatBlockItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class CombatBlockItem extends BlockItem implements IAnimatable {
    public AnimationFactory factory = new AnimationFactory(this);

    public CombatBlockItem(Block block, Properties settings) {
        super(block, settings.setISTER(() -> CombatBlockItemRenderer::new));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }


//    @Override
//    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//        super.initializeClient(consumer);
//        consumer.accept(new IItemRenderProperties() {
//            private final BlockEntityWithoutLevelRenderer renderer = new ModBlockItemRenderer();
//
//            @Override
//            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
//                return renderer;
//            }
//        });
//    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
