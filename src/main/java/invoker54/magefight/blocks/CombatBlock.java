package invoker54.magefight.blocks;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.TileInit;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CombatBlock extends DirectionalBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    public CombatBlock(AbstractBlock.Properties blockProp) {
        super(blockProp);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileInit.COMBAT_BLOCK_TILE.create();
    }

    public static void syncManaAndGlyphs(PlayerEntity player){
        MagicDataCap cap = MagicDataCap.getCap(player);

        PlayerInventory inventory = player.inventory;
        for (ItemStack stack : inventory.items){
            if (stack.isEmpty()) continue;
            cap.syncItemTagSpells(stack.getOrCreateTag());
        }
        for (ItemStack stack : inventory.offhand){
            if (stack.isEmpty()) continue;
            cap.syncItemTagSpells(stack.getOrCreateTag());
        }

        cap.syncPlayerMana(player);
        if (!player.level.isClientSide)
            player.sendMessage(new TranslationTextComponent("ars_mage_fight.chat.sync_successful"), Util.NIL_UUID);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayResult) {
        if (player.isCrouching()) {
            syncManaAndGlyphs(player);
        } else if (world.isClientSide) {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientUtil::openCombatScreen);
        }

        return ActionResultType.SUCCESS;
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();
        if (direction == Direction.DOWN || direction == Direction.UP){
            direction = context.getHorizontalDirection().getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
