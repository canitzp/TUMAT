package de.canitzp.tumat;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.canitzp.tumat.api.IComponentRender;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class RenderOverlay{

    private static RayTraceResult savedTrace;
    private static Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();

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
            List<TooltipComponent> componentsForRendering = new ArrayList<TooltipComponent>();
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
            renderComponents(fontRenderer, resolution.getScaledWidth() / 2, 5, componentsForRendering);
        }
    }

    private static TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(pos)){
            IBlockState state = world.getBlockState(pos);
            //component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + "T" + TextFormatting.GREEN.toString() + "U" + TextFormatting.RED.toString() + "M" + TextFormatting.YELLOW.toString() + "A" + TextFormatting.AQUA.toString() + "T"));
            component.addRenderer(new TextComponent(TooltipComponent.getBlockName(state)));
            TileEntity tile = world.getTileEntity(pos);
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(tile != null){
                    renderer.renderTileEntity(world, player, tile, side, component.newLine(), shouldCalculate);
                } else {
                    renderer.renderBlock(world, player, pos, side, component.newLine(), shouldCalculate);
                }
            }
            component.newLine().addRenderer(new TextComponent(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + getModName(state.getBlock().getRegistryName().getResourceDomain())));
        }
        return component;
    }

    private static TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(entity instanceof EntityItem){
            component.addOneLineRenderer(new TextComponent("Item " + ((EntityItem) entity).getEntityItem().getDisplayName() + " * " + ((EntityItem) entity).getEntityItem().stackSize)).newLine();
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                renderer.renderEntityItem(world, player, (EntityItem) entity, ((EntityItem) entity).getEntityItem(), component, shouldCalculate);
            }
            component.addOneLineRenderer(new TextComponent(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + getModName(((EntityItem) entity).getEntityItem().getItem().getRegistryName().getResourceDomain())));
        } else if(entity instanceof EntityLivingBase){
            component.addRenderer(new TextComponent(entity.getDisplayName().getFormattedText()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                renderer.renderLivingEntity(world, player, (EntityLivingBase) entity, component, shouldCalculate);
            }
        } else {
            component.addRenderer(new TextComponent(entity.getDisplayName().getFormattedText()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                renderer.renderEntity(world, player, entity, component, shouldCalculate);
            }
        }
        return component;
    }

    private static TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(trace.getBlockPos())){
            IBlockState state = world.getBlockState(trace.getBlockPos());
            if(state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase){
                component.addRenderer(new TextComponent(state.getBlock().getLocalizedName()));
            }
        }
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            renderer.renderMiss(world, player, trace, component.newLine(), shouldCalculate);
        }
        return component;
    }

    private static void renderComponents(FontRenderer fontRenderer, int x, int y, List<TooltipComponent> lines){
        for(TooltipComponent tooltipComponent : lines){
            if(tooltipComponent != null){
                for(List<IComponentRender> lists : tooltipComponent.endComponent()){
                    if(lists != null){
                        for(IComponentRender component : lists){
                            if(component != null){
                                GlStateManager.pushMatrix();
                                component.render(fontRenderer, x, y, 0xFFFFFF);
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                    y += 10;
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
        if (player != null){
            if (world != null){
                traceResult = player.rayTrace(maxDistance, partialTicks);
                Vec3d eyeVec = player.getPositionEyes(partialTicks);
                double currentDistance = maxDistance;

                if (traceResult != null){
                    currentDistance = traceResult.hitVec.distanceTo(eyeVec);
                }

                Vec3d lookVec = player.getLook(partialTicks);
                Vec3d lookingEyeVec = eyeVec.addVector(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance);
                pointedEntity = null;
                Vec3d calcVec = null;
                List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance).expand(1.0D, 1.0D, 1.0D), entity -> {
                    return entity != null && !(entity instanceof EntityItem) || Config.showEntityItems;
                });
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
                if (pointedEntity != null && (d2 < currentDistance || traceResult == null)){
                    traceResult = new RayTraceResult(pointedEntity, calcVec);
                }
            }
        }
        return traceResult;
    }

    private static String getModName(String modid){
        for(ModContainer mod : modMap.values()){
            if(mod.getModId().equals(modid)){
                return mod.getName();
            }
        }
        return modid;
    }

}
