package invoker54.magefight.loot;

import com.google.gson.JsonObject;
import invoker54.magefight.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class CharmStructureAdditionModifier extends LootModifier {
//    private final Item addition;
    private final float chance;

    protected CharmStructureAdditionModifier(ILootCondition[] conditionsIn, float chance) {
        super(conditionsIn);
//        this.addition = addition;
        this.chance = chance;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {

        if (context.getRandom().nextFloat() <= this.chance) {
            List<Item> items =
                    ItemInit.items.stream().filter((item) -> item instanceof ICurioItem).collect(Collectors.toList());
            ItemStack chosenItem = new ItemStack(items.get(context.getRandom().nextInt(items.size())));

            generatedLoot.add(chosenItem);
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<CharmStructureAdditionModifier> {

        @Override
        public CharmStructureAdditionModifier read(ResourceLocation name, JsonObject object,
                                                   ILootCondition[] conditionsIn) {
            float chance = JSONUtils.getAsFloat(object, "chance");
            return new CharmStructureAdditionModifier(conditionsIn, chance);
        }

        @Override
        public JsonObject write(CharmStructureAdditionModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("chance", instance.chance);
            return json;
        }
    }
}
