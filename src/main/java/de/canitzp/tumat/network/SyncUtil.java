package de.canitzp.tumat.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
public class SyncUtil{

    public static Map<SidedPosition, Integer> energyMap = new HashMap<>();

    public static int getForgeUnits(BlockPos pos, EnumFacing side){
        SidedPosition sidedPosition = new SidedPosition(pos, side);
        if(Minecraft.getMinecraft().world.getTotalWorldTime() % 10 == 0){
            NetworkHandler.network.sendToServer(new PacketSyncEnergyServer(pos, side));
        }
        if(energyMap.containsKey(sidedPosition)){
            return energyMap.get(sidedPosition);
        }
        TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
        if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, side)){
            return tile.getCapability(CapabilityEnergy.ENERGY, side).getEnergyStored();
        }
        return 0;
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
