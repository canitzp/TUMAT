package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.EnergyComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import ic2.api.energy.EnergyNet;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityTransformer;
import ic2.core.init.Localization;
import ic2.core.item.block.ItemCable;
import ic2.core.ref.MetaTeBlock;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class IndustrialCraft2 implements IWorldRenderer{

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockTileEntity){
            if(InfoUtil.hasProperty(state, ((BlockTileEntity) state.getBlock()).typeProperty)){
                IProperty<MetaTeBlock> property = ((BlockTileEntity) state.getBlock()).typeProperty;
                IBlockState actualState = state.getActualState(world, pos);
                MetaTeBlock teBlock = actualState.getValue(property);
                String name = property.getName(teBlock).replace("_active", "");
                String unlocalized = "ic2.te." + name;
                if(!name.contains("cable")){
                    component.setName(new TextComponent(Localization.translate(unlocalized)));
                }
            }
        }
        return component;
    }

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        if (tileEntity instanceof TileEntityBlock) {
            InfoUtil.syncTileEntity(tileEntity, shouldCalculate, "components");
            if (((TileEntityBlock) tileEntity).hasComponent(Energy.class)) {
                Energy energy = ((TileEntityBlock) tileEntity).getComponent(Energy.class);
                List<String> desc = new ArrayList<>();
                if (ConfigBoolean.SHOW_EU.value) {
                    if (!(tileEntity instanceof IEnergyStorage)) {
                        int stored = (int) energy.getEnergy();
                        int cap = (int) energy.getCapacity();
                        if (cap > 0) {
                            if (cap < stored) {
                                cap = stored;
                            }
                            component.add(new EnergyComponent(stored, cap, "EU", TextFormatting.YELLOW), TooltipComponent.Priority.HIGH);
                        }
                    }
                    if (!energy.getSourceDirs().isEmpty()) {
                        desc.add(Localization.translate("ic2.item.tooltip.PowerTier", energy.getSourceTier()));
                    } else if (!energy.getSinkDirs().isEmpty()) {
                        desc.add(Localization.translate("ic2.item.tooltip.PowerTier", energy.getSinkTier()));
                    }
                }
                if (tileEntity instanceof TileEntityTransformer) {
                    desc.add(String.format("%s %.0f %s %s %.0f %s", Localization.translate("ic2.item.tooltip.Low"), EnergyNet.instance.getPowerFromTier(energy.getSinkTier()), Localization.translate("ic2.generic.text.EUt"), Localization.translate("ic2.item.tooltip.High"), EnergyNet.instance.getPowerFromTier(energy.getSourceTier() + 1), Localization.translate("ic2.generic.text.EUt")));
                }
                component.clear(TooltipComponent.Priority.LOW);
                component.add(new DescriptionComponent(desc), TooltipComponent.Priority.LOW);
            }
        }
        if (tileEntity instanceof TileEntityCable) {
            InfoUtil.syncTileEntity(tileEntity, shouldCalculate, "cableType", "insulation");
            NBTTagCompound nbt = new NBTTagCompound();
            tileEntity.writeToNBT(nbt);
            if (!nbt.isEmpty()) {
                try {
                    CableType cableType = CableType.values[nbt.getByte("cableType") & 255];
                    int insulation = nbt.getByte("insulation") & 255;
                    ItemStack cable = ItemCable.getCable(cableType, insulation);
                    component.setName(new TextComponent(InfoUtil.getItemName(cable)));
                    component.clear(TooltipComponent.Priority.LOW);
                    component.add(new DescriptionComponent(cable), TooltipComponent.Priority.LOW);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return component;
    }
}

