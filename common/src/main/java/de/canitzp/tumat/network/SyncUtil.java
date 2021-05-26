package de.canitzp.tumat.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author canitzp
 */
public class SyncUtil{

    public static Map<SidedPosition, Pair<Integer, Integer>> energyMap = new ConcurrentHashMap<>();

    public static Pair<Integer, Integer> getForgeUnits(BlockPos pos, EnumFacing side){
        if(energyMap.size() >= 5000){
            energyMap.clear();
        }
        SidedPosition sidedPosition = new SidedPosition(pos, side);
        if(Minecraft.getMinecraft().world.getTotalWorldTime() % 10 == 0){
            NetworkHandler.network.sendToServer(new PacketSyncEnergyServer(pos, side));
        }
        for(Map.Entry<SidedPosition, Pair<Integer, Integer>> entry : energyMap.entrySet()){
            if(entry.getKey().equals(sidedPosition)){
                return entry.getValue();
            }
        }
        TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
        if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, side)){
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side);
            if(storage != null){
                return Pair.of(storage.getEnergyStored(), storage.getMaxEnergyStored());
            }
        }
        return Pair.of(0, 0);
    }

    public static class SidedPosition{
        private BlockPos pos;
        private EnumFacing side;
        public SidedPosition(BlockPos pos, EnumFacing side){
            this.pos = pos;
            this.side = side;
        }

        public BlockPos getPos(){
            return pos;
        }

        public EnumFacing getSide(){
            return side;
        }

        @Override
        public boolean equals(Object obj){
            if(obj instanceof SidedPosition){
                SidedPosition first = (SidedPosition) obj;
                return first.pos.equals(this.pos) && first.side.ordinal() == this.side.ordinal();
            }
            return false;
        }
    }

}
