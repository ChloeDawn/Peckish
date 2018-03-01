package net.sleeplessdev.peckish;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Peckish.ID,
     name = Peckish.NAME,
     version = Peckish.VERSION,
     dependencies = "after:*")
public final class Peckish {

    public static final String ID = "peckish";
    public static final String NAME = "Peckish";
    public static final String VERSION = "%VERSION%";

    public static final Logger LOGGER = LogManager.getLogger(Peckish.ID);

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        int totalFoods = 0;

        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof ItemFood)) continue;
            CreativeTabs tab = item.getCreativeTab();
            if (tab == null) tab = CreativeTabs.FOOD;
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(tab, subItems);
            for (ItemStack stack : subItems) {
                if (stack.isEmpty()) continue;
                int heal = ((ItemFood) item).getHealAmount(stack);
                float sat = ((ItemFood) item).getSaturationModifier(stack);
                if (!ModConfig.skipEmptyFoods || heal > 0 || sat > 0) {
                    OreDictionary.registerOre(ModConfig.oreName, stack);
                    if (stack.hasTagCompound()) {
                        LOGGER.debug("Registered food stack <{}#{}[{}]>",
                                stack.getItem().getRegistryName(),
                                stack.getMetadata(),
                                stack.getTagCompound());
                    } else LOGGER.debug("Registered food stack <{}#{}>",
                            stack.getItem().getRegistryName(),
                            stack.getMetadata());
                    totalFoods++;
                }
            }
        }

        LOGGER.info("Successfully registered {} foods to ore name \"{}\".", totalFoods, ModConfig.oreName);
    }

    @Config(modid = Peckish.ID)
    @Mod.EventBusSubscriber(modid = Peckish.ID)
    public static final class ModConfig {
        @Config.Name("ore_name")
        @Config.Comment("The entry name foods should be registered to in the Ore Dictionary.")
        @Config.RequiresMcRestart
        public static String oreName = "itemFood";

        @Config.Name("skip_empty_foods")
        @Config.Comment("Should food items be skipped if they have no hunger or saturation values?")
        @Config.RequiresMcRestart
        public static boolean skipEmptyFoods = true;

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent event) {
            if (Peckish.ID.equals(event.getConfigID())) {
                ConfigManager.sync(Peckish.ID, Config.Type.INSTANCE);
            }
        }
    }

}
