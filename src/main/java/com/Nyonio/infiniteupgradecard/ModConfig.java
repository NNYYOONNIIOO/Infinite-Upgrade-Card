package com.Nyonio.infiniteupgradecard;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = InfiniteUpgradeCard.MODID)
public class ModConfig {
    
    @Config.Name("Infinite Upgrade Speed Count")
    @Config.Comment("Speed upgrade count for Infinite Upgrade item")
    public static int infiniteUpgradeSpeed = 8;
    
    @Config.Name("Infinite Upgrade Energy Count")
    @Config.Comment("Energy upgrade count for Infinite Upgrade item")
    public static int infiniteUpgradeEnergy = 8;
    
    @Config.Name("Super Infinite Upgrade Speed Count")
    @Config.Comment("Speed upgrade count for Super Infinite Upgrade item")
    public static int superInfiniteUpgradeSpeed = 17;
    
    @Config.Name("Super Infinite Upgrade Energy Count")
    @Config.Comment("Energy upgrade count for Super Infinite Upgrade item")
    public static int superInfiniteUpgradeEnergy = 32;
    
    @Config.Name("Super Infinite Upgrade Filter Count")
    @Config.Comment("Filter upgrade count for Super Infinite Upgrade item. -1 means use max installable from Mekanism config")
    public static int superInfiniteUpgradeFilter = -1;
    
    @Config.Name("Super Infinite Upgrade Gas Count")
    @Config.Comment("Gas upgrade count for Super Infinite Upgrade item. -1 means use max installable from Mekanism config")
    public static int superInfiniteUpgradeGas = -1;
    
    @Config.Name("Super Infinite Upgrade Muffling Count")
    @Config.Comment("Muffling upgrade count for Super Infinite Upgrade item. -1 means use max installable from Mekanism config")
    public static int superInfiniteUpgradeMuffling = -1;
    
    @Config.Name("Super Infinite Upgrade Stone Generator Count")
    @Config.Comment("Stone Generator upgrade count for Super Infinite Upgrade item. -1 means use max installable from Mekanism config")
    public static int superInfiniteUpgradeStoneGenerator = -1;
    
    @Config.Name("Super Infinite Upgrade Anchor Enabled")
    @Config.Comment("Whether to install Anchor upgrade for Super Infinite Upgrade item. Default: false")
    public static boolean superInfiniteUpgradeAnchorEnabled = false;
    
    @Config.Name("Super Infinite Upgrade Anchor Count")
    @Config.Comment("Anchor upgrade count for Super Infinite Upgrade item when enabled. -1 means use max installable from Mekanism config")
    public static int superInfiniteUpgradeAnchorCount = -1;
    
    @Mod.EventBusSubscriber(modid = InfiniteUpgradeCard.MODID)
    private static class ConfigHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(InfiniteUpgradeCard.MODID)) {
                ConfigManager.sync(InfiniteUpgradeCard.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
            }
        }
    }
}
