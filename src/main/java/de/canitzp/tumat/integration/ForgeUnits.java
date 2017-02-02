package de.canitzp.tumat.integration;

import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.ColoredText;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.network.SyncUtil;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
public class ForgeUnits implements IWorldRenderer{

    public static Map<String, String> names = new HashMap<>();

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)){
            IEnergyStorage storage = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
            if(storage != null){
                int energy = SyncUtil.getForgeUnits(tileEntity.getPos(), side);
                int cap = storage.getMaxEnergyStored();
                String modid = world.getBlockState(tileEntity.getPos()).getBlock().getRegistryName().getResourceDomain();
                String name = names.containsKey(modid) ? names.get(modid) : TextFormatting.RED + "Energy";
                if(cap > 0){
                    this.render(component, modid, name, energy, cap);
                }
            }
        }

        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_FU.value;
    }

    @Override
    public void init(){
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            names.putAll(renderer.getEnergyColor());
        }
    }

    private TooltipComponent render(TooltipComponent component, String modid, String name, int current, int cap){
        TextComponent text = new TextComponent(String.format("%d %s / %d %s", current, name, cap, name));
        if(modid.equals("actuallyadditions")){
            text = new ColoredText(text, InitItems.itemBattery.getRGBDurabilityForDisplay(new ItemStack(InitItems.itemBattery)));
        } else {
            text.setFormat(TextFormatting.RED);
        }
        return component.addOneLineRenderer(text);
    }

}
