package de.canitzp.tumat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class IconRenderer {

    private ItemStack iconStack = ItemStack.EMPTY;

    public IconRenderer(ItemStack icon){
        this.iconStack = icon;
    }

    public boolean shouldRender(){
        return !this.iconStack.isEmpty();
    }

    public void render(int x, int y){
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(1.25F, 1.25F, 1.25F);

        //Minecraft mc = Minecraft.getMinecraft();
        //boolean flagBefore = mc.fontRenderer.getUnicodeFlag();
        //mc.fontRendererObj.setUnicodeFlag(false);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(this.iconStack, 0, 0);
        //Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, 0, 0, null);
        //mc.fontRendererObj.setUnicodeFlag(flagBefore);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

}
