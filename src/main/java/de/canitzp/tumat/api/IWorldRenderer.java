package de.canitzp.tumat.api;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public interface IWorldRenderer{

    void render(WorldClient world, EntityPlayerSP player, ScaledResolution resolution, FontRenderer fontRenderer, RenderGameOverlayEvent.ElementType type, float partialTicks);

}
