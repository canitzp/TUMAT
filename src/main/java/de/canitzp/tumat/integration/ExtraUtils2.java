package de.canitzp.tumat.integration;

import com.rwtema.extrautils2.api.machine.Machine;
import com.rwtema.extrautils2.machine.BlockMachine;
import com.rwtema.extrautils2.machine.TileMachine;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
public class ExtraUtils2 implements IWorldRenderer {

    private static Map<BlockPos, Machine> cached = new HashMap<>();

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        if(tileEntity instanceof TileMachine){
            Machine machine;
            if((world.getTotalWorldTime() % 20 != 0) && cached.containsKey(tileEntity.getPos())){
                machine = cached.get(tileEntity.getPos());
            } else {
                machine = ReflectionHelper.getPrivateValue(TileMachine.class, (TileMachine) tileEntity, "machine");
                if(machine != null){
                    cached.put(tileEntity.getPos(), machine);
                }
            }
            if(machine != null){
                return component.setName(new TextComponent(BlockMachine.getDisplayName(machine)));
            }
        }
        return null;
    }
}
