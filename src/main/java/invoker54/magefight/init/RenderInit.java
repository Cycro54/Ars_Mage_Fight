package invoker54.magefight.init;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.client.render.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.TileRegistry;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderInit {

    @SubscribeEvent
    public static void initializeRenderers(final FMLClientSetupEvent event){
        //For mobs
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.DEATH_GRIP_ENTITY, DeathGripRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.BLACK_HOLE_ENTITY, BlackHoleRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.TIME_ANCHOR_ENTITY, TimeAnchorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.LIFE_SIGIL_ENTITY, LifeSigilRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.BLOOD_SLIME_ENTITY, BloodSlimeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.MANA_SLIME_ENTITY, ManaSlimeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.SPELL_SHIELD_ENTITY, SpellShieldRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.RUPTURE_SWORD_ENTITY, RuptureSwordRenderer::new);

        //For tiles
        ClientRegistry.bindTileEntityRenderer(TileInit.COMBAT_BLOCK_TILE, CombatBlockRenderer::new);
//        ClientRegistry.bindTileEntityRenderer(TileInit.COMBAT_BLOCK_TILE, CombatBlockRenderer::new);
    }
}
