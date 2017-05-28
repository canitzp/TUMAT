package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.local.L10n;
import mod.chiselsandbits.api.IMultiStateBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ChiselsAndBits implements IWorldRenderer{

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof IMultiStateBlock){
            component.setName(new TextComponent(InfoUtil.getBlockName(state)));
            component.add(new TextComponent(L10n.getChiselAndBitsBaseBlock(InfoUtil.getBlockName(((IMultiStateBlock) state.getBlock()).getPrimaryState(world, pos)))), TooltipComponent.Priority.HIGH);
        }
        return component;
    }

}
