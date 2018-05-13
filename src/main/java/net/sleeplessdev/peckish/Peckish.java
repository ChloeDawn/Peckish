package net.sleeplessdev.peckish;

import net.minecraft.block.BlockCake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = Peckish.ID,
        name = Peckish.NAME,
        version = Peckish.VERSION,
        dependencies = Peckish.DEPENDENCIES,
        acceptedMinecraftVersions = Peckish.MC_VERSIONS
)
public final class Peckish {
    public static final String ID = "peckish";
    public static final String NAME = "Peckish";
    public static final String VERSION = "%VERSION%";
    public static final String DEPENDENCIES = "after:*";
    public static final String MC_VERSIONS = "[1.10,1.11)";

    private static final Logger LOGGER = LogManager.getLogger(Peckish.ID);

    @Mod.EventHandler
    protected void onPostInitialization(FMLPostInitializationEvent event) {
        int totalFoods = 0;

        for (final Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ItemBlockSpecial) {
                if (((ItemBlockSpecial) item).getBlock() instanceof BlockCake) {
                    totalFoods = registerFoodStack(new ItemStack(item), totalFoods);
                }
            } else if (item instanceof ItemBlock) {
                if (((ItemBlock) item).getBlock() instanceof BlockCake) {
                    totalFoods = registerFoodStack(new ItemStack(item), totalFoods);
                }
            } else if (item instanceof ItemFood) {
                final List<ItemStack> subItems = new ArrayList<>();
                item.getSubItems(item, item.getCreativeTab(), subItems);
                for (final ItemStack stack : subItems) {
                    if (stack == null) continue;
                    if (ModConfig.skip_empty_foods) {
                        final int heal = ((ItemFood) item).getHealAmount(stack);
                        final float sat = ((ItemFood) item).getSaturationModifier(stack);
                        if (heal > 0 || sat > 0) {
                            totalFoods = registerFoodStack(stack, totalFoods);
                        }
                    } else totalFoods = registerFoodStack(stack, totalFoods);
                }
            }
        }

        Peckish.LOGGER.info("Successfully registered {} foods to ore name \"{}\".", totalFoods, ModConfig.ore_name);
    }

    private int registerFoodStack(ItemStack stack, int total) {
        OreDictionary.registerOre(ModConfig.ore_name, stack);
        if (stack.hasTagCompound()) {
            Peckish.LOGGER.debug("Registered food stack <{}#{}[{}]>",
                    stack.getItem().getRegistryName(),
                    stack.getMetadata(),
                    stack.getTagCompound());
        } else Peckish.LOGGER.debug("Registered food stack <{}#{}>",
                stack.getItem().getRegistryName(),
                stack.getMetadata());
        return ++total;
    }

    @Mod.EventBusSubscriber
    @Config(modid = Peckish.ID)
    public static final class ModConfig {
        @Config.Comment("The entry name foods should be registered to in the ore dictionary.")
        public static String ore_name = "itemFood";

        @Config.Comment("Should food items be skipped if they have no hunger or saturation values?")
        public static boolean skip_empty_foods = true;

        private ModConfig() {}

        @SubscribeEvent
        protected static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (Peckish.ID.equals(event.getConfigID())) {
                ConfigManager.load(Peckish.ID, Config.Type.INSTANCE);
            }
        }
    }
}
