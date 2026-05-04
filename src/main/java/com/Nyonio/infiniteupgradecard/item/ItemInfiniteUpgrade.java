package com.Nyonio.infiniteupgradecard.item;

import com.Nyonio.infiniteupgradecard.InfiniteUpgradeCard;
import com.Nyonio.infiniteupgradecard.ModConfig;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemInfiniteUpgrade extends Item {
    
    private final boolean isSuper;
    
    public ItemInfiniteUpgrade(boolean isSuper) {
        this.isSuper = isSuper;
        this.setMaxStackSize(1);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof IUpgradeTile)) {
            return EnumActionResult.PASS;
        }
        
        IUpgradeTile upgradeTile = (IUpgradeTile) tileEntity;
        if (!upgradeTile.supportsUpgrades()) {
            return EnumActionResult.PASS;
        }
        
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        
        TileComponentUpgrade component = upgradeTile.getComponent();
        
        int speedCount = isSuper ? ModConfig.superInfiniteUpgradeSpeed : ModConfig.infiniteUpgradeSpeed;
        int energyCount = isSuper ? ModConfig.superInfiniteUpgradeEnergy : ModConfig.infiniteUpgradeEnergy;
        
        applyUpgrades(component, speedCount, energyCount);
        
        player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.DARK_GREEN + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.upgrade_success")));
        
        return EnumActionResult.SUCCESS;
    }
    
    private void applyUpgrades(TileComponentUpgrade component, int speedCount, int energyCount) {
        if (component.supports(Upgrade.SPEED)) {
            int currentSpeed = component.getUpgrades(Upgrade.SPEED);
            if (speedCount > currentSpeed) {
                setUpgradeCountDirect(component, Upgrade.SPEED, speedCount);
            }
        }
        
        if (component.supports(Upgrade.ENERGY)) {
            int currentEnergy = component.getUpgrades(Upgrade.ENERGY);
            if (energyCount > currentEnergy) {
                setUpgradeCountDirect(component, Upgrade.ENERGY, energyCount);
            }
        }
        
        if (isSuper) {
            applyAllOtherUpgrades(component);
        }
    }
    
    private void applyAllOtherUpgrades(TileComponentUpgrade component) {
        for (Upgrade upgrade : Upgrade.values()) {
            if (upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY) {
                continue;
            }
            
            if (upgrade == Upgrade.ANCHOR) {
                if (ModConfig.superInfiniteUpgradeAnchorEnabled && component.supports(upgrade)) {
                    int count = getUpgradeCount(upgrade, ModConfig.superInfiniteUpgradeAnchorCount);
                    if (count > 0) {
                        setUpgradeCountDirect(component, upgrade, count);
                    }
                }
                continue;
            }
            
            if (component.supports(upgrade)) {
                int count = getUpgradeCountFromConfig(upgrade);
                if (count > 0) {
                    setUpgradeCountDirect(component, upgrade, count);
                }
            }
        }
    }
    
    private int getUpgradeCountFromConfig(Upgrade upgrade) {
        if (upgrade == Upgrade.FILTER) {
            return getUpgradeCount(upgrade, ModConfig.superInfiniteUpgradeFilter);
        } else if (upgrade == Upgrade.GAS) {
            return getUpgradeCount(upgrade, ModConfig.superInfiniteUpgradeGas);
        } else if (upgrade == Upgrade.MUFFLING) {
            return getUpgradeCount(upgrade, ModConfig.superInfiniteUpgradeMuffling);
        } else if (upgrade == Upgrade.STONE_GENERATOR) {
            return getUpgradeCount(upgrade, ModConfig.superInfiniteUpgradeStoneGenerator);
        }
        return 0;
    }
    
    private int getUpgradeCount(Upgrade upgrade, int configValue) {
        if (configValue == -1) {
            try {
                return upgrade.getMaxInstalled();
            } catch (Exception e) {
                return 0;
            }
        }
        return configValue;
    }
    
    private void setUpgradeCountDirect(TileComponentUpgrade component, Upgrade upgrade, int count) {
        try {
            java.lang.reflect.Field upgradesField = TileComponentUpgrade.class.getDeclaredField("upgrades");
            upgradesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<Upgrade, Integer> upgrades = (java.util.Map<Upgrade, Integer>) upgradesField.get(component);
            upgrades.put(upgrade, count);
            component.tileEntity.recalculateUpgradables(upgrade);
            component.tileEntity.markNoUpdateSync();
        } catch (Exception e) {
            component.addUpgrades(upgrade, count - component.getUpgrades(upgrade));
        }
    }
}
