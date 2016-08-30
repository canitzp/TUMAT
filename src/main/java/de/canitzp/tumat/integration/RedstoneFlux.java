package de.canitzp.tumat.integration;

import cofh.api.energy.IEnergyHandler;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketUpdateEnergy;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

/**
 * @author canitzp
 */
public class RedstoneFlux implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(Loader.isModLoaded("tesla")){
            if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)){
                return null;
            }
        }
        if(tileEntity instanceof IEnergyHandler){
            NetworkHandler.network.sendToServer(new PacketUpdateEnergy(tileEntity.getPos(), side));
            int stored = ((IEnergyHandler) tileEntity).getEnergyStored(side);
            int max = ((IEnergyHandler) tileEntity).getMaxEnergyStored(side);
            if(max > 0){
                component.addOneLineRenderer(new TextComponent(TextFormatting.RED.toString() + stored + "/" + max));
            }
        }
        return component;
    }

}
