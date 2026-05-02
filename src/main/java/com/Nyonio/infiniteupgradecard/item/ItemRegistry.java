package com.Nyonio.infiniteupgradecard.item;

import com.Nyonio.infiniteupgradecard.InfiniteUpgradeCard;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemRegistry
{
    public static ItemInfiniteUpgrade infiniteUpgrade;
    public static ItemInfiniteUpgrade superInfiniteUpgrade;
    public static ItemInfiniteFactoryInstaller infiniteFactoryInstaller;

    public static void registerItems()
    {
        MinecraftForge.EVENT_BUS.register(new ItemRegistry());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        infiniteUpgrade = new ItemInfiniteUpgrade(false);
        infiniteUpgrade.setUnlocalizedName(InfiniteUpgradeCard.MODID + ".infinite_upgrade");
        infiniteUpgrade.setRegistryName("infinite_upgrade");
        infiniteUpgrade.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);

        superInfiniteUpgrade = new ItemInfiniteUpgrade(true);
        superInfiniteUpgrade.setUnlocalizedName(InfiniteUpgradeCard.MODID + ".super_infinite_upgrade");
        superInfiniteUpgrade.setRegistryName("super_infinite_upgrade");
        superInfiniteUpgrade.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);

        infiniteFactoryInstaller = new ItemInfiniteFactoryInstaller();
        infiniteFactoryInstaller.setUnlocalizedName(InfiniteUpgradeCard.MODID + ".infinite_factory_installer");
        infiniteFactoryInstaller.setRegistryName("infinite_factory_installer");
        infiniteFactoryInstaller.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);

        event.getRegistry().registerAll(infiniteUpgrade, superInfiniteUpgrade, infiniteFactoryInstaller);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(infiniteUpgrade, 0, 
            new ModelResourceLocation(infiniteUpgrade.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(superInfiniteUpgrade, 0, 
            new ModelResourceLocation(superInfiniteUpgrade.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(infiniteFactoryInstaller, 0, 
            new ModelResourceLocation(infiniteFactoryInstaller.getRegistryName(), "inventory"));
    }
}
