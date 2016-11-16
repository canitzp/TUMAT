package de.canitzp.tumat;

import de.canitzp.tumat.api.*;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import de.canitzp.tumat.configuration.cats.ConfigString;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class RenderOverlay{

    private static RayTraceResult savedTrace;

    public static void render(WorldClient world, EntityPlayerSP player, ScaledResolution resolution, FontRenderer fontRenderer, RenderGameOverlayEvent.ElementType type, float partialTicks, boolean shouldCalculate) {
        boolean calculate = savedTrace == null || shouldCalculate;
        RayTraceResult trace;
        if (calculate) {
            float distance = 0F;
            NetHandlerPlayClient clientHandler = Minecraft.getMinecraft().getConnection();
            if (clientHandler != null) {
                switch (clientHandler.getPlayerInfo(player.getGameProfile().getId()).getGameType()) {
                    case CREATIVE: {
                        distance = ConfigFloat.DISTANCE_CREATIVE.value;
                        break;
                    }
                    case SURVIVAL: {
                        distance = ConfigFloat.DISTANCE_SURVIVAL.value;
                        break;
                    }
                    case ADVENTURE: {
                        distance = ConfigFloat.DISTANCE_ADVENTURE.value;
                        break;
                    }
                    case SPECTATOR: {
                        distance = ConfigFloat.DISTANCE_SPECTATOR.value;
                        break;
                    }
                }
            }
            trace = savedTrace = createRayTraceForDistance(world, player, distance, partialTicks);
        } else {
            trace = savedTrace;
        }
        if (trace != null) {
            List<TooltipComponent> componentsForRendering = new ArrayList<>();
            switch (trace.typeOfHit) {
                case BLOCK: {
                    if (ConfigBoolean.SHOW_BLOCKS.value) {
                        addToListIfNotNull(componentsForRendering, renderBlock(world, player, trace.getBlockPos(), trace.sideHit, calculate));
                    }
                    break;
                }
                case ENTITY: {
                    addToListIfNotNull(componentsForRendering, renderEntity(world, player, trace.entityHit, calculate));
                    break;
                }
                case MISS: {
                    if (ConfigBoolean.SHOW_FLUIDS.value) {
                        addToListIfNotNull(componentsForRendering, renderMiss(world, player, trace, calculate));
                    }
                    break;
                }
            }
            renderComponents(fontRenderer, componentsForRendering);
        }
    }

    private static TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(pos)){
            IBlockState state = world.getBlockState(pos);
            //component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + "T" + TextFormatting.GREEN.toString() + "U" + TextFormatting.RED.toString() + "M" + TextFormatting.YELLOW.toString() + "A" + TextFormatting.AQUA.toString() + "T"));
            component.addOneLineRenderer(new TextComponent(InfoUtil.getBlockName(state)));
            component.addOneLineRenderer(new DescriptionComponent(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state))));
            String modName = InfoUtil.getModName(state.getBlock());
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    TileEntity tile = world.getTileEntity(pos);
                    if(ConfigBoolean.SHOW_TILES.value && tile != null){
                        renderer.renderTileEntity(world, player, tile, side, component.newLine(), shouldCalculate);
                    }
                    renderer.renderBlock(world, player, pos, side, component.newLine(), shouldCalculate);
                }
            }
            component.setModName(modName);
        }
        return component;
    }

    private static TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(ConfigBoolean.SHOW_DROPPED_ITEMS.value && entity instanceof EntityItem){
            component.addOneLineRenderer(new TextComponent("Item " + InfoUtil.getItemName(((EntityItem) entity).getEntityItem()) + TextFormatting.RESET +  " x " + ((EntityItem) entity).getEntityItem().func_190916_E())); //TODO wait for mappings and change this to getStacksize();
            component.addOneLineRenderer(new DescriptionComponent(((EntityItem) entity).getEntityItem()));
            String modname = InfoUtil.getModName(((EntityItem) entity).getEntityItem().getItem());
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntityItem(world, player, (EntityItem) entity, ((EntityItem) entity).getEntityItem(), component, shouldCalculate);
                }
            }
            component.setModName(modname);
        } else if(ConfigBoolean.SHOW_ENTITIES.value && entity instanceof EntityLivingBase){
            component.addOneLineRenderer(new TextComponent(TooltipComponent.getEntityName(entity)));
            component.addOneLineRenderer(new TextComponent(TextFormatting.RED.toString() + ((EntityLivingBase) entity).getHealth() + "/" + ((EntityLivingBase) entity).getMaxHealth()));
            String modName = InfoUtil.getModName(entity);
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderLivingEntity(world, player, (EntityLivingBase) entity, component, shouldCalculate);
                }
            }
            component.setModName(modName);
        } else if(ConfigBoolean.SHOW_ENTITIES.value){
            component.addOneLineRenderer(new TextComponent(TooltipComponent.getEntityName(entity)));
            String modName = InfoUtil.getModName(entity);
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntity(world, player, entity, component, shouldCalculate);
                }
            }
            component.setModName(modName);
        }
        return component;
    }

    private static TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(trace.getBlockPos())){
            IBlockState state = world.getBlockState(trace.getBlockPos());
            if(state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase){
                component.addOneLineRenderer(new TextComponent(state.getBlock().getLocalizedName()));
                component.addOneLineRenderer(new TextComponent(InfoUtil.getModName(state.getBlock())));
            }
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderMiss(world, player, trace, component.newLine(), shouldCalculate);
                }
            }
        }
        return component;
    }

    public static void renderComponents(FontRenderer fontRenderer, List<TooltipComponent> lines){
        for(TooltipComponent tooltipComponent : lines){
            if(tooltipComponent != null){
                int y2 = GuiTUMAT.getYFromPercantage();
                GlStateManager.pushMatrix();
                for(List<IComponentRender> lists : tooltipComponent.endComponent()){
                    int lineAmount = 0;
                    if(lists != null){
                        for(IComponentRender component : lists){
                            if(component != null){
                                int x = GuiTUMAT.getXFromPercantage();
                                lineAmount = component.getLines(fontRenderer);
                                renderBackground(x, y2, tooltipComponent.getLength(), lineAmount);
                                GlStateManager.scale(ConfigFloat.SCALE.value, ConfigFloat.SCALE.value, ConfigFloat.SCALE.value);
                                component.render(fontRenderer, GuiTUMAT.getXFromPercantage(), y2, 0xFFFFFF);
                            }
                        }
                    }
                    y2 += 10*lineAmount;
                }
                GlStateManager.popMatrix();
            }
        }
    }

    private static void renderBackground(int x, int y, int width, int lines){
        if(ConfigBoolean.SHOW_BACKGROUND.value){
            long color = 0x806A9BC3;
            String hexConf = ConfigString.BACKGROUND_COLOR.value;
            if(hexConf.length() == 10){
                try {
                    color = Long.parseLong(hexConf.substring(2, 10), 16);
                } catch (Exception e){
                    if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 150 == 0){
                        TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                    }
                }
            } else{
                if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 150 == 0){
                    TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                }
            }
            x = x - width/2;
            Gui.drawRect(x - 3, y - 1, x + width + 3, y + 10 * lines - 1, (int) color);
        }
    }

    private static <T> void addToListIfNotNull(List<T> list, T object){
        if(object != null){
            list.add(object);
        }
    }

    private static RayTraceResult createRayTraceForDistance(World world, EntityPlayer player, double maxDistance, float partialTicks){
        Entity pointedEntity;
        RayTraceResult traceResult = null;
        if(player != null){
            if(world != null){
                traceResult = player.rayTrace(maxDistance, partialTicks);
                Vec3d eyeVec = player.getPositionEyes(partialTicks);
                double currentDistance = maxDistance;

                if(traceResult != null){
                    currentDistance = traceResult.hitVec.distanceTo(eyeVec);
                }

                Vec3d lookVec = player.getLook(partialTicks);
                Vec3d lookingEyeVec = eyeVec.addVector(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance);
                pointedEntity = null;
                Vec3d calcVec = null;
                List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance).expand(1.0D, 1.0D, 1.0D), entity -> entity != null && !(entity instanceof EntityItem) || ConfigBoolean.SHOW_DROPPED_ITEMS.value);
                double d2 = currentDistance;

                for(Entity entity : list){
                    double collisionBorder = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(collisionBorder, collisionBorder + (entity instanceof EntityItem ? 0.35 : 0), collisionBorder);
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyeVec, lookingEyeVec);

                    if(axisalignedbb.isVecInside(eyeVec)){
                        if(d2 >= 0.0D){
                            pointedEntity = entity;
                            calcVec = raytraceresult == null ? eyeVec : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    } else if(raytraceresult != null){
                        double d3 = eyeVec.distanceTo(raytraceresult.hitVec);
                        if(d3 < d2 || d2 == 0.0D){
                            if(entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !player.canRiderInteract()){
                                if(d2 == 0.0D){
                                    pointedEntity = entity;
                                    calcVec = raytraceresult.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                calcVec = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                if(pointedEntity != null && (d2 < currentDistance || traceResult == null)){
                    traceResult = new RayTraceResult(pointedEntity, calcVec);
                }
            }
        }
        return traceResult;
    }

}
