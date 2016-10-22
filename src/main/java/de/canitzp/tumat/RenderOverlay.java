package de.canitzp.tumat;

import de.canitzp.tumat.api.*;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.json.JsonReader;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
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
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class RenderOverlay{

    public static String modNameFormat = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();
    //                Block/Item,       Name,   ModName, block/item desc
    public static ReMapper<ItemStack, String, String, String[]> remaped = new ReMapper<>();
    private static RayTraceResult savedTrace;
    private static Map<String, ModContainer> modMap;

    static{
        modMap = Loader.instance().getIndexedModList();
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            renderer.remap(remaped);
        }
        new JsonReader(new File(Config.config.getConfigFile().getParentFile() + File.separator + "tumat_rename.json")).data.remap(remaped);
    }

    public static void render(WorldClient world, EntityPlayerSP player, ScaledResolution resolution, FontRenderer fontRenderer, RenderGameOverlayEvent.ElementType type, float partialTicks, boolean shouldCalculate){
        boolean calculate = savedTrace == null || shouldCalculate;
        RayTraceResult trace;
        if(calculate){
            float distance = player.isCreative() ? Config.distanceToRenderCreative : Config.distanceToRenderSurvival;
            trace = savedTrace = createRayTraceForDistance(world, player, distance, partialTicks);
        } else {
            trace = savedTrace;
        }
        if(trace != null){
            List<TooltipComponent> componentsForRendering = new ArrayList<>();
            switch(trace.typeOfHit){
                case BLOCK:{
                    addToListIfNotNull(componentsForRendering, renderBlock(world, player, trace.getBlockPos(), trace.sideHit, calculate));
                    break;
                }
                case ENTITY:{
                    addToListIfNotNull(componentsForRendering, renderEntity(world, player, trace.entityHit, calculate));
                    break;
                }
                case MISS:{
                    addToListIfNotNull(componentsForRendering, renderMiss(world, player, trace, calculate));
                    break;
                }
            }
            renderComponents(fontRenderer, resolution, (resolution.getScaledWidth() / 2) + Math.round(Config.x), Math.round(Config.y), componentsForRendering);
        }
    }

    private static TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(pos)){
            IBlockState state = world.getBlockState(pos);
            //component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + "T" + TextFormatting.GREEN.toString() + "U" + TextFormatting.RED.toString() + "M" + TextFormatting.YELLOW.toString() + "A" + TextFormatting.AQUA.toString() + "T"));
            component.addOneLineRenderer(new TextComponent(InfoUtil.getBlockName(state)));
            String[] desc = InfoUtil.getDescription(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
            if(desc != null){
                for(String s : desc){
                    component.addOneLineRenderer(new TextComponent(TextFormatting.GRAY + s));
                }
            }
            TileEntity tile = world.getTileEntity(pos);
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    if(tile != null){
                        renderer.renderTileEntity(world, player, tile, side, component.newLine(), shouldCalculate);
                    }
                    renderer.renderBlock(world, player, pos, side, component.newLine(), shouldCalculate);
                }
            }
            String modName = InfoUtil.getModName(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
            component.setModName(modName != null ? modName : InfoUtil.getModNameFromBlock(state.getBlock()));
        }
        return component;
    }

    private static TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(entity instanceof EntityItem){
            component.addOneLineRenderer(new TextComponent("Item " + InfoUtil.getItemName(((EntityItem) entity).getEntityItem()) + TextFormatting.RESET +  " x " + ((EntityItem) entity).getEntityItem().stackSize));
            String[] desc = InfoUtil.getDescription(((EntityItem) entity).getEntityItem());
            if(desc != null){
                for(String s : desc){
                    component.addOneLineRenderer(new TextComponent(TextFormatting.GRAY + s));
                }
            }
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntityItem(world, player, (EntityItem) entity, ((EntityItem) entity).getEntityItem(), component, shouldCalculate);
                }
            }
            component.setModName(InfoUtil.getModName(new ItemStack(((EntityItem) entity).getEntityItem().getItem(), 1, ((EntityItem) entity).getEntityItem().getItemDamage())));
        } else if(entity instanceof EntityLivingBase){
            component.addOneLineRenderer(new TextComponent(TooltipComponent.getEntityName(entity)));
            component.addOneLineRenderer(new TextComponent(TextFormatting.RED.toString() + ((EntityLivingBase) entity).getHealth() + "/" + ((EntityLivingBase) entity).getMaxHealth()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderLivingEntity(world, player, (EntityLivingBase) entity, component, shouldCalculate);
                }
            }
            component.setModName(InfoUtil.getModName(entity));
        } else {
            component.addOneLineRenderer(new TextComponent(TooltipComponent.getEntityName(entity)));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntity(world, player, entity, component, shouldCalculate);
                }
            }
            component.setModName(InfoUtil.getModName(entity));
        }
        return component;
    }

    private static TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(trace.getBlockPos())){
            IBlockState state = world.getBlockState(trace.getBlockPos());
            if(state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase){
                component.addOneLineRenderer(new TextComponent(state.getBlock().getLocalizedName()));
                component.addOneLineRenderer(new TextComponent(modNameFormat + getModName(state.getBlock().getRegistryName().getResourceDomain())));
            }
        }
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            if(renderer.shouldBeActive()){
                renderer.renderMiss(world, player, trace, component.newLine(), shouldCalculate);
            }
        }
        return component;
    }

    public static void renderComponents(FontRenderer fontRenderer, ScaledResolution res, int x, int y, List<TooltipComponent> lines){
        for(TooltipComponent tooltipComponent : lines){
            if(tooltipComponent != null){
                int y2 = GuiTUMAT.getYFromPercantage(Config.y);
                for(List<IComponentRender> lists : tooltipComponent.endComponent()){
                    if(lists != null){
                        for(IComponentRender component : lists){
                            if(component != null){
                                GlStateManager.pushMatrix();
                                GlStateManager.scale(Config.scale, Config.scale, Config.scale);
                                component.render(fontRenderer, GuiTUMAT.getXFromPercantage(Config.x), y2, 0xFFFFFF);
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                    y2 += 10;
                }
            }
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
                List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance).expand(1.0D, 1.0D, 1.0D), entity -> entity != null && !(entity instanceof EntityItem) || Config.showEntityItems);
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

    public static String getModName(String modid){
        if(!modid.equals("minecraft")){
            for(ModContainer mod : modMap.values()){
                if(mod.getModId().toLowerCase().equals(modid)){
                    return StringUtils.capitalize(mod.getName());
                }
            }
        }
        return StringUtils.capitalize(modid);
    }

}
