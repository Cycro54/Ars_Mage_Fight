package invoker54.magefight.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.magefight.blocks.tile.CombatBlockTile;
import invoker54.magefight.client.model.CombatBlockItemModel;
import invoker54.magefight.client.model.CombatBlockModel;
import invoker54.magefight.items.CombatBlockItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CombatBlockItemRenderer extends GeoItemRenderer<CombatBlockItem> {
    public CombatBlockItemRenderer() {
        super(new CombatBlockItemModel());
    }
}
