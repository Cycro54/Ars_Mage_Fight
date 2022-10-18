package invoker54.magefight.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class CustomRender extends RenderType {
    protected static final RenderState.AlphaState SMALLER_ALPHA = new RenderState.AlphaState(0.001F);

    public CustomRender(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static RenderType eyes(ResourceLocation p_228652_0_) {
        RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(p_228652_0_, false, false);
        return create("eyes", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true,
                RenderType.State.builder().setTextureState(renderstate$texturestate).setTransparencyState(ADDITIVE_TRANSPARENCY).
                        setWriteMaskState(COLOR_WRITE).
                        setFogState(BLACK_FOG).
                        createCompositeState(false));
    }

    public static RenderType entityTranslucent(ResourceLocation p_230168_0_, boolean p_230168_1_) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_230168_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_230168_1_);
        return create("entity_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
    }

    //Careful, this can cut entities out of the world
    public static RenderType translucentGlow(ResourceLocation textureLocation, boolean affectOutline) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new
                        RenderState.TextureState(textureLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).
                setAlphaState(DEFAULT_ALPHA).
                setCullState(NO_CULL).
                setOverlayState(OVERLAY).
                createCompositeState(affectOutline);
        return create("entity_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }

    public static RenderType opaqueGlow(ResourceLocation textureLocation, boolean affectOutline) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new
                        RenderState.TextureState(textureLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY).
                setAlphaState(DEFAULT_ALPHA).
                setCullState(NO_CULL).
                setOverlayState(OVERLAY).
                createCompositeState(affectOutline);
        return create("entity_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }
}
