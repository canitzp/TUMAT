package de.canitzp.tumat.integration;

import com.google.common.collect.Lists;
import de.canitzp.tumat.Config;
import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.EnergyComponent;
import de.canitzp.tumat.api.components.TextComponent;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.init.Localization;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * @author canitzp
 */
public class IndustrialCraft2 implements IWorldRenderer{

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockTileEntity){
            if(InfoUtil.hasProperty(state, ((BlockTileEntity) state.getBlock()).typeProperty)){
                IBlockState actualState = state.getActualState(world, pos);
                String unlocalized = "ic2.te." + ((BlockTileEntity) state.getBlock()).typeProperty.getName(actualState.getValue(((BlockTileEntity) state.getBlock()).typeProperty)).replace("_active", "");
                component.setFirst(Lists.newArrayList(new TextComponent(Localization.translate(unlocalized))));
            }
        }
        if(Config.showHarvestTooltip && state.getBlock() instanceof IWrenchable){
            component.addOneLineRenderer(new TextComponent(TextFormatting.RED + "Needs a Wrench"));
        }
        return component;
    }

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(TUMAT.Energy.mainEnergy.equals(TUMAT.Energy.TESLA)){
            if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)){
                return component;
            }
        }
        if(Config.showEnergy){
            if(tileEntity instanceof IEnergyStorage){
                int current = ((IEnergyStorage) tileEntity).getStored();
                int capacity = ((IEnergyStorage) tileEntity).getCapacity();
                if(capacity > 0){
                    component.addOneLineRenderer(new EnergyComponent(current, capacity, "EU"));
                }
            } else if(tileEntity instanceof TileEntityBaseGenerator){
                Energy energy = ReflectionHelper.getPrivateValue(TileEntityBaseGenerator.class, (TileEntityBaseGenerator) tileEntity, "energy");
                if(energy.getCapacity() > 0){
                    component.addOneLineRenderer(new EnergyComponent((int)energy.getEnergy(), (int)energy.getCapacity(), "EU"));
                }
            } else if(tileEntity instanceof TileEntityElectricMachine){
                Energy energy = ReflectionHelper.getPrivateValue(TileEntityElectricMachine.class, (TileEntityElectricMachine) tileEntity, "energy");
                if(energy.getCapacity() > 0){
                    component.addOneLineRenderer(new EnergyComponent((int)energy.getEnergy(), (int)energy.getCapacity(), "EU"));
                }
            }
        }
        return component;
    }
}
