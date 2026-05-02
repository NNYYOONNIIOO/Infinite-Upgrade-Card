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
