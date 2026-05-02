package com.Nyonio.infiniteupgradecard.item;

import com.Nyonio.infiniteupgradecard.InfiniteUpgradeCard;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.tier.BaseTier;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemInfiniteFactoryInstaller extends Item {
    
    private static Class<?> needRepeatTierUpgradeClass;
    private static Class<?> tierMachineClass;
    
    static {
        try {
            needRepeatTierUpgradeClass = Class.forName("mekceumoremachine.common.tile.interfaces.INeedRepeatTierUpgrade");
        } catch (ClassNotFoundException e) {
        }
        try {
            tierMachineClass = Class.forName("mekceumoremachine.common.tile.interfaces.ITierMachine");
        } catch (ClassNotFoundException e) {
        }
    }
    
    public ItemInfiniteFactoryInstaller() {
        this.setMaxStackSize(1);
    }
    
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);
        
        if (isBusy(tile)) {
            return EnumActionResult.FAIL;
        }
        
        installBaseTierIfNeeded(tile);
        
        tile = world.getTileEntity(pos);
        if (isBusy(tile)) {
            return EnumActionResult.FAIL;
        }
        
        if (needRepeatTierUpgradeClass != null && needRepeatTierUpgradeClass.isInstance(tile)) {
            return upgradeFactory(player, world, pos, stack);
        }
        
        if (tierMachineClass != null && tierMachineClass.isInstance(tile)) {
            return upgradeMachine(player, tile, stack);
        }
        
        if (tile instanceof ITierUpgradeable) {
            return upgradeToUltimate(player, world, pos, stack, (ITierUpgradeable) tile);
        }
        
        return EnumActionResult.PASS;
    }
    
    private static boolean isBusy(TileEntity tile) {
        return tile instanceof TileEntityBasicBlock && !((TileEntityBasicBlock) tile).playersUsing.isEmpty();
    }
    
    private static void installBaseTierIfNeeded(TileEntity tile) {
        if (tile instanceof ITierUpgradeable) {
            ITierUpgradeable upgradeable = (ITierUpgradeable) tile;
            try {
                java.lang.reflect.Method canInstalledMethod = upgradeable.getClass().getMethod("CanInstalled");
                Boolean canInstalled = (Boolean) canInstalledMethod.invoke(upgradeable);
                if (canInstalled) {
                    upgradeable.upgrade(BaseTier.BASIC);
                }
            } catch (Exception e) {
            }
        }
    }
    
    private EnumActionResult upgradeFactory(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
        try {
            TileEntity tile = world.getTileEntity(pos);
            if (tile == null || !needRepeatTierUpgradeClass.isInstance(tile)) {
                return EnumActionResult.PASS;
            }
            
            Object factory = tile;
            java.lang.reflect.Method getNowTierMethod = factory.getClass().getMethod("getNowTier");
            Object tier = getNowTierMethod.invoke(factory);
            java.lang.reflect.Method getBaseTierMethod = tier.getClass().getMethod("getBaseTier");
            BaseTier baseTier = (BaseTier) getBaseTierMethod.invoke(tier);
            
            if (baseTier == BaseTier.ULTIMATE || baseTier == BaseTier.CREATIVE) {
                player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.already_ultimate")));
                return EnumActionResult.FAIL;
            }
            
            BaseTier lastTier = null;
            int maxIterations = 10;
            int iterations = 0;
            
            while (iterations < maxIterations) {
                TileEntity currentTile = world.getTileEntity(pos);
                if (currentTile == null || !needRepeatTierUpgradeClass.isInstance(currentTile)) {
                    break;
                }
                
                Object machine = currentTile;
                Object currentTier = getNowTierMethod.invoke(machine);
                BaseTier current = (BaseTier) getBaseTierMethod.invoke(currentTier);
                
                if (current == BaseTier.ULTIMATE || current == BaseTier.CREATIVE || current == lastTier) {
                    break;
                }
                
                BaseTier next = getNextTier(current);
                if (next == null) {
                    break;
                }
                
                lastTier = current;
                java.lang.reflect.Method upgradeMethod = machine.getClass().getMethod("upgrade", BaseTier.class);
                upgradeMethod.invoke(machine, next);
                iterations++;
            }
            
            TileEntity finalTile = world.getTileEntity(pos);
            if (finalTile != null && needRepeatTierUpgradeClass.isInstance(finalTile)) {
                Object finalMachine = finalTile;
                Object finalTierObj = getNowTierMethod.invoke(finalMachine);
                BaseTier finalTier = (BaseTier) getBaseTierMethod.invoke(finalTierObj);
                if (finalTier == BaseTier.ULTIMATE) {
                    player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.DARK_GREEN + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_success")));
                    return EnumActionResult.SUCCESS;
                }
            }
            
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
            
        } catch (Exception e) {
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
        }
    }
    
    private EnumActionResult upgradeMachine(EntityPlayer player, Object machine, ItemStack stack) {
        try {
            java.lang.reflect.Method getTierMethod = machine.getClass().getMethod("getTier");
            Object tier = getTierMethod.invoke(machine);
            java.lang.reflect.Method getBaseTierMethod = tier.getClass().getMethod("getBaseTier");
            BaseTier baseTier = (BaseTier) getBaseTierMethod.invoke(tier);
            
            if (baseTier == BaseTier.ULTIMATE) {
                player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.already_ultimate")));
                return EnumActionResult.FAIL;
            }
            
            java.lang.reflect.Method canInstalledMethod = machine.getClass().getMethod("CanInstalled");
            Boolean canInstalled = (Boolean) canInstalledMethod.invoke(machine);
            if (!canInstalled) {
                return EnumActionResult.PASS;
            }
            
            BaseTier lastTier = null;
            while (true) {
                Object currentTier = getTierMethod.invoke(machine);
                BaseTier current = (BaseTier) getBaseTierMethod.invoke(currentTier);
                if (current == BaseTier.ULTIMATE || current == BaseTier.CREATIVE || current == lastTier) {
                    break;
                }
                BaseTier next = getNextTier(current);
                if (next == null) {
                    break;
                }
                lastTier = current;
                java.lang.reflect.Method upgradeMethod = machine.getClass().getMethod("upgrade", BaseTier.class);
                upgradeMethod.invoke(machine, next);
            }
            
            Object finalTierObj = getTierMethod.invoke(machine);
            BaseTier finalTier = (BaseTier) getBaseTierMethod.invoke(finalTierObj);
            if (finalTier == BaseTier.ULTIMATE) {
                player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.DARK_GREEN + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_success")));
                return EnumActionResult.SUCCESS;
            }
            
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
            
        } catch (Exception e) {
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
        }
    }
    
    private EnumActionResult upgradeToUltimate(EntityPlayer player, World world, BlockPos pos, ItemStack stack, ITierUpgradeable upgradeable) {
        try {
            BaseTier currentTier = getCurrentTier(upgradeable);
            
            if (currentTier == BaseTier.ULTIMATE || currentTier == BaseTier.CREATIVE) {
                player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.already_ultimate")));
                return EnumActionResult.FAIL;
            }
            
            BaseTier lastTier = null;
            int maxIterations = 10;
            int iterations = 0;
            
            while (iterations < maxIterations) {
                TileEntity currentTile = world.getTileEntity(pos);
                if (!(currentTile instanceof ITierUpgradeable)) {
                    break;
                }
                
                ITierUpgradeable currentUpgradeable = (ITierUpgradeable) currentTile;
                BaseTier current = getCurrentTier(currentUpgradeable);
                if (current == BaseTier.ULTIMATE || current == BaseTier.CREATIVE || current == lastTier) {
                    break;
                }
                
                BaseTier next = getNextTier(current);
                if (next == null) {
                    break;
                }
                
                lastTier = current;
                currentUpgradeable.upgrade(next);
                iterations++;
            }
            
            TileEntity finalTile = world.getTileEntity(pos);
            if (finalTile instanceof ITierUpgradeable) {
                ITierUpgradeable finalUpgradeable = (ITierUpgradeable) finalTile;
                BaseTier finalTier = getCurrentTier(finalUpgradeable);
                if (finalTier == BaseTier.ULTIMATE) {
                    player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.DARK_GREEN + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_success")));
                    return EnumActionResult.SUCCESS;
                }
            }
            
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
            
        } catch (Exception e) {
            player.sendMessage(new TextComponentString(EnumColor.DARK_BLUE + Mekanism.LOG_TAG + " " + EnumColor.RED + mekanism.common.util.LangUtils.localize("message.infinite_upgrade_card.factory_upgrade_failed")));
            return EnumActionResult.FAIL;
        }
    }
    
    private static BaseTier getCurrentTier(ITierUpgradeable upgradeable) {
        try {
            java.lang.reflect.Method getTierMethod = upgradeable.getClass().getMethod("getTier");
            Object tier = getTierMethod.invoke(upgradeable);
            if (tier instanceof mekanism.common.tier.FactoryTier) {
                java.lang.reflect.Method getBaseTierMethod = tier.getClass().getMethod("getBaseTier");
                return (BaseTier) getBaseTierMethod.invoke(tier);
            } else if (tier instanceof BaseTier) {
                return (BaseTier) tier;
            } else if (tier != null) {
                java.lang.reflect.Method getBaseTierMethod = tier.getClass().getMethod("getBaseTier");
                return (BaseTier) getBaseTierMethod.invoke(tier);
            }
        } catch (Exception e) {
        }
        return BaseTier.BASIC;
    }
    
    private static BaseTier getNextTier(BaseTier tier) {
        int nextOrdinal = tier.ordinal() + 1;
        BaseTier[] tiers = BaseTier.values();
        return nextOrdinal >= 0 && nextOrdinal < tiers.length ? tiers[nextOrdinal] : null;
    }
}
