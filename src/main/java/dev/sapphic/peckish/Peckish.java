package dev.sapphic.peckish;

import net.minecraft.block.BlockCake;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Optional;

@Config(modid = Peckish.ID)
@Mod(modid = Peckish.ID, useMetadata = true, acceptedMinecraftVersions = "[1.12,1.13)")
public final class Peckish {
    @Config.Ignore
    public static final String ID = "peckish";

    @Config.RequiresMcRestart
    @Config.Name("ore_name")
    @Config.Comment("The dictionary entry to register into")
    public static String oreName = "itemFood";

    @Config.RequiresMcRestart
    @Config.Name("skip_empty_foods")
    @Config.Comment("Skip food with no nutritional value")
    public static boolean skipEmptyFoods = true;

    @Mod.EventHandler
    public static void registerFoods(final FMLPostInitializationEvent event) {
        ForgeRegistries.ITEMS.getValuesCollection().stream()
            .filter(item -> (item instanceof ItemFood)
                || ((item instanceof ItemBlockSpecial) && (((ItemBlockSpecial) item).getBlock() instanceof BlockCake))
                || ((item instanceof ItemBlock) && (((ItemBlock) item).getBlock() instanceof BlockCake)))
            .forEach(item -> {
                final NonNullList<ItemStack> stacks = NonNullList.create();
                item.getSubItems(Optional.ofNullable(item.getCreativeTab()).orElse(CreativeTabs.FOOD), stacks);
                stacks.stream().filter(stack -> !stack.isEmpty()).filter(stack ->
                    !(item instanceof ItemFood) || !skipEmptyFoods
                        || (((ItemFood) item).getHealAmount(stack) > 0)
                        || (((ItemFood) item).getSaturationModifier(stack) > 0)
                ).forEach(stack -> OreDictionary.registerOre(oreName, stack));
            });
    }

    @Override
    public String toString() {
        return "Peckish";
    }
}
