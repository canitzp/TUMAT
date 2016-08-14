package de.canitzp.tumat;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.canitzp.tumat.api.IComponentRender;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class RenderOverlay implements IWorldRenderer{

    private static final double distance = 32;

    @Override
    public void render(WorldClient world, EntityPlayerSP player, ScaledResolution resolution, FontRenderer fontRenderer, RenderGameOverlayEvent.ElementType type, float partialTicks){
        RayTraceResult trace = createRayTraceForDistance(world, player, distance, partialTicks);
        if(trace != null){
            List<TooltipComponent> componentsForRendering = new ArrayList<TooltipComponent>();
            switch(trace.typeOfHit){
                case BLOCK: {
                    addToListIfNotNull(componentsForRendering, renderBlock(world, player, trace.getBlockPos(), trace.sideHit, resolution, fontRenderer));
                    break;
                }
                case ENTITY:{
                    addToListIfNotNull(componentsForRendering, renderEntity(world, player, trace.getBlockPos(), trace.sideHit, resolution, fontRenderer));
                    break;
                }
                case MISS:{
                    addToListIfNotNull(componentsForRendering, renderMiss(world, player, trace.getBlockPos(), trace.sideHit, resolution, fontRenderer));
                    break;
                }
            }
            renderComponents(fontRenderer, resolution.getScaledWidth()/2, 5, componentsForRendering);
        }
    }

    private TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, ScaledResolution resolution, FontRenderer fontRenderer){
        TooltipComponent component = new TooltipComponent().addRenderer(new TextComponent(TooltipComponent.getBlockName(world.getBlockState(pos))));
        return component;
    }

    private TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, ScaledResolution resolution, FontRenderer fontRenderer){
        return null;
    }

    private TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, ScaledResolution resolution, FontRenderer fontRenderer){
        return null;
    }

    private void renderComponents(FontRenderer fontRenderer, int x, int y, List<TooltipComponent> lines){
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

    private <T> void addToListIfNotNull(List<T> list, T object){
        if(object != null){
            list.add(object);
        }
    }

    private RayTraceResult createRayTraceForDistance(World world, EntityPlayer player, double maxDistance, float partialTicks){
        Entity pointedEntity;
        RayTraceResult traceResult = null;
        if (player != null){
            if (world != null){
                traceResult = player.rayTrace(maxDistance, partialTicks);
                Vec3d vec3d = player.getPositionEyes(partialTicks);
                double d1 = maxDistance;

                if (traceResult != null){
                    d1 = traceResult.hitVec.distanceTo(vec3d);
                }

                Vec3d vec3d1 = player.getLook(partialTicks);
                Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * maxDistance, vec3d1.yCoord * maxDistance, vec3d1.zCoord * maxDistance);
                pointedEntity = null;
                Vec3d vec3d3 = null;
                List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(vec3d1.xCoord * maxDistance, vec3d1.yCoord * maxDistance, vec3d1.zCoord * maxDistance).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(@Nullable Entity p_apply_1_)
                    {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for(Entity entity : list){
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expandXyz((double) entity.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if(axisalignedbb.isVecInside(vec3d)){
                        if(d2 >= 0.0D){
                            pointedEntity = entity;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    } else if(raytraceresult != null){
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);
                        if(d3 < d2 || d2 == 0.0D){
                            if(entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !player.canRiderInteract()){
                                if(d2 == 0.0D){
                                    pointedEntity = entity;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                if (pointedEntity != null && (d2 < d1 || traceResult == null)){
                    traceResult = new RayTraceResult(pointedEntity, vec3d3);
                }
            }
        }
        return traceResult;
    }

}
