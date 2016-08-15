package de.canitzp.tumat;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@Mod(name = TUMAT.MODNAME, modid = TUMAT.MODID, version = TUMAT.MODVERSION)
public class TUMAT{

    public static final String MODNAME = "TUMAT";
    public static final String MODID = "tumat";
    public static final String MODVERSION = "@VERSION@";


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){

        System.out.println("Start TUMAT");

        TUMATApi.addGuiWhereToRender(GuiInventory.class);

        MinecraftForge.EVENT_BUS.register(TUMAT.class);

    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void gameOverlayRenderEvent(RenderGameOverlayEvent.Post event){
        if(event.getType().equals(RenderGameOverlayEvent.ElementType.ALL)){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null || TUMATApi.guisWhereToRender.contains(mc.currentScreen.getClass())){
                RenderOverlay.render(mc.theWorld, mc.thePlayer, event.getResolution(), mc.fontRendererObj, event.getType(), event.getPartialTicks(), mc.theWorld.getTotalWorldTime() % 10 == 0);
            }
        }
    }

}
