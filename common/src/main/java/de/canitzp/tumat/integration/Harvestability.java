package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class Harvestability implements IWorldRenderer{

    public static boolean isTinkersConstructLoaded = Loader.isModLoaded("tconstruct");
    private static String[] vanillaHarvestLevel = {"Wood", "Stone", "Iron", "Diamond"};
    private static String[] tinkerHarvestLevel = {"Stone", "Iron", "Diamond", "Obsidian", "Cobalt"};

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        IBlockState state = world.getBlockState(pos);
        String level = getHarvestLevel(state.getBlock().getHarvestLevel(state));
        String tool = getHarvestTool(state);
        if (tool != null) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack != ItemStack.EMPTY) {
                String color = getTextColorTool(tool, stack);
                TextComponent.createOneLine(component, L10n.getHarvestEffectiveTool(color, tool));
                if (!isTinkersConstructLoaded && "pickaxe".equals(tool) && level != null && color.equals(TextFormatting.GREEN.toString())) {
                    color = getTextColorLevel(state, player.getHeldItemMainhand());
                    TextComponent.createOneLine(component, L10n.getHarvestLevel(color, level));
                }
            }
        }
        return component;
    }

    @Override
    public boolean shouldBeActive() {
        return ConfigBoolean.SHOW_HARVESTABILITY.value;
    }

    @Nullable
    public static String getHarvestLevel(int harvestLevel){
        if(harvestLevel < 0){
            return null;
        }
        String[] tools = isTinkersConstructLoaded ? tinkerHarvestLevel : vanillaHarvestLevel;
        if(tools.length > harvestLevel){
            return tools[harvestLevel];
        }
        return "Unknown";
    }

    @Nullable
    public static String getHarvestTool(IBlockState state){
        if(state.getBlock() instanceof IShearable){
            return "shears";
        }
        return state.getBlock().getHarvestTool(state);
    }

    public static String getTextColorTool(String toolType, ItemStack activeStack){
        if(toolType.equals("shears")){
            return getColor(activeStack.getItem() instanceof ItemShears);
        }
        return getColor(activeStack.getItem().getToolClasses(activeStack).contains(toolType));
    }

    public static String getTextColorLevel(IBlockState state, ItemStack stack){
        return getColor(stack.getItem().canHarvestBlock(state) || stack.getItem().canHarvestBlock(state, stack));
    }

    private static String getColor(boolean b){
        return b ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
    }

}
