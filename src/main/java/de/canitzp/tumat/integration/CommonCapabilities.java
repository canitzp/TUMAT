package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.ColoredText;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.commoncapabilities.api.capability.temperature.ITemperature;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;
import org.cyclops.commoncapabilities.capability.temperature.TemperatureConfig;
import org.cyclops.commoncapabilities.capability.worker.WorkerConfig;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class CommonCapabilities implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tile, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        //Temperature
        if(ConfigBoolean.SHOW_TEMPERATURE_COMMONCAPABILITIES.value && tile.hasCapability(TemperatureConfig.CAPABILITY, side)){
            ITemperature temp = tile.getCapability(TemperatureConfig.CAPABILITY, side);
            if(temp != null){
                double max = Math.round(temp.getMaximumTemperature() * 10F) / 10F;
                ColoredText.createOneLine(component, L10n.getCommonCapsTemp(Math.round(temp.getTemperature() * 10F) / 10F, max > 10000 ? "<10000" : String.valueOf(max)), ColoredText.Colors.ORANGE_BRIGHT);
            }
        }
        //Worker
        if(ConfigBoolean.SHOW_WORK_COMMONCAPABILITIES.value && tile.hasCapability(WorkerConfig.CAPABILITY, side)){
            IWorker worker = tile.getCapability(WorkerConfig.CAPABILITY, side);
            if(worker != null){
                ColoredText.createOneLine(component, worker.canWork() && worker.hasWork() ? L10n.COMMONCAPS_WORK_ON : L10n.COMMONCAPS_WORK_OFF, ColoredText.Colors.BROWN_BRIGHT);
            }
        }
        return component;
    }

}
