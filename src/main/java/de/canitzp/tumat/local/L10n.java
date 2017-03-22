package de.canitzp.tumat.local;

import net.minecraft.client.resources.I18n;

/**
 * @author canitzp
 */
public class L10n {

    public static final String EMPTY = I18n.format("tumat.general.empty");
    public static final String ERROR_TEXT = I18n.format("tumat.error.text");
    public static final String SNEAKFORMORE = I18n.format("tumat.sneak_for_more");
    public static final String ENERGY = I18n.format("tumat.energy.energy");
    public static final String ENERGY_MAXTRANSFER = I18n.format("tumat.energy.max_transfer");
    public static final String INVENTORY_LAG_WARN = I18n.format("tumat.inventory.lag_warning");
    public static final String INVENTORY_OVER_1000 = I18n.format("tumat.inventory.its_over_1000");

    public static final String COMMONCAPS_WORK_ON = I18n.format("tumat.commoncapabilities.work.on");
    public static final String COMMONCAPS_WORK_OFF = I18n.format("tumat.commoncapabilities.work.off");
    public static final String IC2_WRENCHABLE = I18n.format("tumat.ic2.wrenchable");
    public static final String REDSTONE_LOCKED = I18n.format("tumat.vanilla.redstone_locked");


    public static String getItemText(String name, String count){
        return I18n.format("tumat.item.text", name, count);
    }

    public static String getHarvestEffectiveTool(String color, String toolName){
        return I18n.format("tumat.harvest.effective_tool", color, toolName);
    }

    public static String getHarvestLevel(String color, String level){
        return I18n.format("tumat.harvest.level", color, level);
    }

    public static String getInventoryFreeSlots(int free, int max){
        return I18n.format("tumat.inventory.free_slots", free, max);
    }

    public static String getChiselAndBitsBaseBlock(String blockName){
        return I18n.format("tumat.chiselandbits.base_block", blockName);
    }

    public static String getCommonCapsTemp(String currentTemp, String maxTemp){
        return I18n.format("tumat.commoncapabilities.temp", currentTemp, maxTemp);
    }

    public static String getVanillaGrowRate(String percent){
        return I18n.format("tumat.vanilla.grow_status", percent);
    }

    public static String getVanillaRedstoneStrength(int power){
        return I18n.format("tumat.vanilla.redstone", power);
    }

    public static String getVanillaRedstoneDelay(int delay){
        return I18n.format("tumat.vanilla.redstone_delay", delay);
    }

    public static String getVanillaLight(int light){
        return I18n.format("tumat.vanilla.light", light);
    }

    public static String getVanillaLightSource(int source){
        return I18n.format("tumat.vanilla.light_source", source);
    }

    public static String getStorageDrawersContent(int slot, String itemName){
        return I18n.format("tumat.storagedrawers.content", slot, itemName);
    }

}
