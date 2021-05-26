package de.canitzp.tumat;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class IconRenderer {

    private ItemStack iconStack;

    public IconRenderer(ItemStack icon){
        this.iconStack = icon;
    }

    public boolean shouldRender(){
        return !this.iconStack.isEmpty();
    }

    public void render(PoseStack poseStack, int x, int y){
        poseStack.pushPose();
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //RenderHelper.enableGUIStandardItemLighting();
        GlStateManager._enableDepthTest();
        GlStateManager._enableRescaleNormal();
        poseStack.translate(x, y, 0);
        poseStack.scale(1.25F, 1.25F, 1.25F);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(this.iconStack, 0, 0);
        poseStack.popPose();
    }

}
