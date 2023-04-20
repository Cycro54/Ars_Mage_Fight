package invoker54.magefight.client.render;

import invoker54.magefight.client.model.CombatBlockItemModel;
import invoker54.magefight.items.CombatBlockItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CombatBlockItemRenderer extends GeoItemRenderer<CombatBlockItem> {
    public CombatBlockItemRenderer() {
        super(new CombatBlockItemModel());
    }
}
