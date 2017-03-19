package de.canitzp.tumat.api.components;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IComponentRender;
import de.canitzp.tumat.api.TooltipComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TextComponent implements IComponentRender{

    protected String displayString;

    public TextComponent(String displayString){
        this.displayString = displayString;
    }

    public TextComponent setFormat(TextFormatting... formatting){
        if(formatting != null){
            for(TextFormatting format : formatting){
                this.displayString = format.toString() + this.displayString;
            }
        }
        return this;
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color){
        InfoUtil.drawCenteredString(fontRenderer, TextFormatting.RESET.toString() + this.displayString, x, y, color);
    }

    @Override
    public int getLength(FontRenderer fontRenderer){
        return fontRenderer.getStringWidth(this.displayString);
    }

    public static void createOneLine(TooltipComponent component, String displayString, TextFormatting... formatting){
        component.addOneLineRenderer(new TextComponent(displayString).setFormat(formatting));
    }

    public static TextComponent createWithSensitiveName(World world, EntityPlayer player, RayTraceResult trace, BlockPos pos, IBlockState state){
        String s = InfoUtil.getBlockName(state);
        if(s.endsWith(".name") || (s.contains(".") && s.split("\\.").length >= 2 && !s.contains(" "))){
            try {
                s = InfoUtil.getItemName(state.getBlock().getPickBlock(state, trace, world, pos, player));
            } catch (Exception ignored){}
        }
        return new TextComponent(s);
    }

}
