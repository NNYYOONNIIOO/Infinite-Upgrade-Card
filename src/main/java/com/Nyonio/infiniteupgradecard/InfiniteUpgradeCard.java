package com.Nyonio.infiniteupgradecard;

import com.Nyonio.infiniteupgradecard.item.ItemRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = InfiniteUpgradeCard.MODID, name = InfiniteUpgradeCard.NAME, version = InfiniteUpgradeCard.VERSION)
public class InfiniteUpgradeCard
{
    public static final String MODID = "infinite_upgrade_card";
    public static final String NAME = "Infinite Upgrade Card";
    public static final String VERSION = "1.0";

    @Mod.Instance(MODID)
    public static InfiniteUpgradeCard instance;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        ItemRegistry.registerItems();
        logger.info("Infinite Upgrade Card pre-initialization complete.");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Infinite Upgrade Card initialization complete.");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        logger.info("Infinite Upgrade Card post-initialization complete.");
    }
}
