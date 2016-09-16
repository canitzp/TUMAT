package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * @author canitzp
 */
public class RebornCore implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(RebornCoreConfig.getRebornPower().internal()){
            if(tileEntity instanceof TilePowerAcceptor){
                double currentEnergy = ((TilePowerAcceptor) tileEntity).getEnergy();
                double maxEnergy = ((TilePowerAcceptor) tileEntity).getMaxPower();
                if(maxEnergy > 0){
                    component.addOneLineRenderer(new TextComponent(currentEnergy + "EU/" + maxEnergy + "EU").setFormat(TextFormatting.RED));
                }
            }
        }
        return null;
    }

}
