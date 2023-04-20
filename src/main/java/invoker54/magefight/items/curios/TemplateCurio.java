package invoker54.magefight.items.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class TemplateCurio extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public TemplateCurio(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }
}
