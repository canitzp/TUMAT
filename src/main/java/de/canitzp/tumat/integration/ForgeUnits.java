package de.canitzp.tumat.integration;

import com.google.common.collect.Lists;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import de.canitzp.tumat.network.SyncUtil;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ForgeUnits implements IWorldRenderer{

    public static HashMap<String, String> names = new HashMap<String, String>(){{
        put("refinedstorage", "RS");
    }};

    public static List<String> cableString = Lists.newArrayList("laserrelay", "cable", "conduit");

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)){
            IEnergyStorage storage = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
            if(storage != null){
                IBlockState state = world.getBlockState(tileEntity.getPos());
                String modid = state.getBlock().getRegistryName().getNamespace();
                String name = names.containsKey(modid) ? names.get(modid) : TextFormatting.RED + L10n.ENERGY;
                for(String s : cableString){
                    String toComp = state.getBlock().getTranslationKey();
                    if(toComp.contains("block_laser_relay")){
                        component.add(new TextComponent(L10n.ENERGY_MAXTRANSFER + storage.getMaxEnergyStored() + "CF").setColor(InitItems.itemBattery.getRGBDurabilityForDisplay(new ItemStack(InitItems.itemBattery))).setScale(0.85F), TooltipComponent.Priority.HIGH);
                        return component;
                    }
                    if(toComp.contains(s)){
                        component.add(new TextComponent(L10n.ENERGY_MAXTRANSFER + name).setFormat(TextFormatting.RED).setScale(0.85F), TooltipComponent.Priority.HIGH);
                        return component;
                    }
                }
                Pair<Integer, Integer> energy = SyncUtil.getForgeUnits(tileEntity.getPos(), side);
                if(energy.getRight() > 0){
                    this.render(component, modid, name, energy.getLeft(), energy.getRight());
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
            text.setColor(InitItems.itemBattery.getRGBDurabilityForDisplay(new ItemStack(InitItems.itemBattery)));
        } else {
            text.setFormat(TextFormatting.RED);
        }
        text.setScale(0.85F);
        return component.add(text, TooltipComponent.Priority.HIGH);
    }

}
