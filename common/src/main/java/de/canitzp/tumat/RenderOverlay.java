package de.canitzp.tumat;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.tumat.api.IComponentRender;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import de.canitzp.tumat.configuration.cats.ConfigString;
import de.canitzp.tumat.local.L10n;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class RenderOverlay{

    private static Map<ResourceLocation, IconRenderer> bucketCache = new HashMap<>();
    private static List<String> ignoredEntities = null;

    private static HitResult savedTrace;

    public static void render(ClientLevel world, LocalPlayer player, PoseStack pose, Font fontRenderer, float partialTicks, boolean shouldCalculate) {
        boolean calculate = savedTrace == null || shouldCalculate;
        HitResult trace;
        if (calculate) {
            if(ignoredEntities == null){
                ignoredEntities = new ArrayList<>();
                for(IWorldRenderer worldRenderer : TUMATApi.getRegisteredComponents()){
                    ignoredEntities.addAll(worldRenderer.getInvisibleEntities());
                }
            }
            float distance = 0F;
            ClientPacketListener clientHandler = Minecraft.getInstance().getConnection();
            if (clientHandler != null) {
                switch (clientHandler.getPlayerInfo(player.getGameProfile().getId()).getGameMode()) {
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
            switch (trace.getType()) {
                case BLOCK: {
                    if (ConfigBoolean.SHOW_BLOCKS.value) {
                        renderComponents(pose, fontRenderer, renderBlock(world, player, ((BlockHitResult) trace), calculate));
                    }
                    break;
                }
                case ENTITY: {
                    renderComponents(fontRenderer, renderEntity(world, player, trace.entityHit, calculate));
                    break;
                }
                case MISS: {
                    if (ConfigBoolean.SHOW_FLUIDS.value) {
                        renderComponents(fontRenderer, renderMiss(world, player, trace, calculate));
                    }
                    break;
                }
            }
        }
    }

    private static TooltipComponent renderBlock(ClientLevel world, LocalPlayer player, BlockHitResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(pos)){
            BlockState state = world.getBlockState(pos);
            component.setName(TextComponent.createWithSensitiveName(world, player, savedTrace, pos, state));
            component.add(new DescriptionComponent(state.getBlock().asItem().getDefaultInstance()), TooltipComponent.Priority.LOW);
            component.setModName(new TextComponent(InfoUtil.getModName(state.getBlock())));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderBlock(world, player, pos, side, component, shouldCalculate);
                    TileEntity tile = world.getTileEntity(pos);
                    if(ConfigBoolean.SHOW_TILES.value && tile != null){
                        try {
                            renderer.renderTileEntity(world, player, tile, side, component, shouldCalculate);
                        } catch (Exception e){
                            component.add(new DescriptionComponent(Lists.newArrayList("An error occured", "while getting tile data!")), TooltipComponent.Priority.HIGH);
                            if(world.getTotalWorldTime() % 150 == 0){
                                TUMAT.logger.error("An error occured while getting tile data from: " + state.getBlock().getTranslationKey() + " at: " + pos, e);
                            }
                        }
                    }
                    component.setIconRenderer(renderer.getIconRenderObject(world, player, pos, side, savedTrace, shouldCalculate));
                }
            }
        } else {
            return null;
        }
        return component;
    }

    private static TooltipComponent renderEntity(ClientLevel world, LocalPlayer player, EntityHitResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        Entity entity = trace.getEntity();
        if(ConfigBoolean.SHOW_DROPPED_ITEMS.value && entity instanceof ItemEntity){
            component.setName(new TextComponent(L10n.getItemText(InfoUtil.getItemName(((ItemEntity) entity).getItem()) + ChatFormatting.RESET, String.valueOf(((ItemEntity) entity).getItem().getCount()))));
            component.add(new DescriptionComponent(((ItemEntity) entity).getItem()), TooltipComponent.Priority.LOW);
            component.setModName(new TextComponent(InfoUtil.getModName(((ItemEntity) entity).getItem())));
            component.setIconRenderer(new IconRenderer(((ItemEntity) entity).getItem()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntityItem(world, player, (ItemEntity) entity, ((ItemEntity) entity).getItem(), component, shouldCalculate);
                }
            }
        } else if(ConfigBoolean.SHOW_ENTITIES.value && entity instanceof LivingEntity){
            component.setName(new TextComponent(InfoUtil.getEntityName(entity)));
            component.add(new TextComponent(TextFormatting.RED.toString() + ((LivingEntity) entity).getHealth() + "/" + ((LivingEntity) entity).getMaxHealth()), TooltipComponent.Priority.HIGH);
            component.setModName(new TextComponent(InfoUtil.getModName(entity)));
            SpawnEggItem spawnEggItem = SpawnEggItem.byId(entity.getType());
            component.setIconRenderer(new IconRenderer(spawnEggItem.getDefaultInstance()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderLivingEntity(world, player, (LivingEntity) entity, component, shouldCalculate);
                }
            }
        } else if(ConfigBoolean.SHOW_ENTITIES.value){
            component.setName(new TextComponent(InfoUtil.getEntityName(entity)));
            component.setModName(new TextComponent(InfoUtil.getModName(entity)));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntity(world, player, entity, component, shouldCalculate);
                }
            }
        }
        return component;
    }

    private static TooltipComponent renderMiss(ClientLevel world, LocalPlayer player, HitResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(trace.getBlockPos())){
            BlockState state = world.getBlockState(trace.getBlockPos());
            if(state.getBlock() instanceof LiquidBlock){
                component.setName(new TextComponent(state.getBlock().getName()));
                component.setModName(new TextComponent(InfoUtil.getModName(state.getBlock())));
                IconRenderer renderer;
                if(bucketCache.containsKey(state.getBlock().getRegistryName())){
                    component.setIconRenderer(bucketCache.get(state.getBlock().getRegistryName()));
                } else {
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());
                    if(fluid != null){
                        renderer = new IconRenderer(FluidUtil.getFilledBucket(new FluidStack(fluid, 1000)));
                        bucketCache.put(state.getBlock().getRegistryName(), renderer);
                        component.setIconRenderer(renderer);
                    }
                }
            }
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderMiss(world, player, trace, component, shouldCalculate);
                }
            }
        }
        return component;
    }

    public static void renderComponents(PoseStack pose, Font fontRenderer, TooltipComponent component) {
        if(component != null){
            
            int x = GuiTUMAT.getXFromPercantage();
            int y = GuiTUMAT.getYFromPercantage() + getBossBarOffset();
            boolean renderIcon = ConfigBoolean.RENDER_ICONS.value && component.getIconRenderer() != null && component.getIconRenderer().shouldRender();
            if(ConfigBoolean.SHOW_BACKGROUND.value){
                renderBackground(x, y, finished.getLength(), finished.getHeight(), renderIcon);
            }
            component.render(pose, fontRenderer, x, y);
        }
    }

    private static void renderBackground(int x, int y, int width, int height, boolean renderIcon){
        if(ConfigBoolean.SHOW_BACKGROUND.value && height > 0){
            World world = Minecraft.getMinecraft().world;
            long color = 0x806A9BC3;
            String hexConf = ConfigString.BACKGROUND_COLOR.value;
            if(hexConf.length() == 10){
                try {
                    color = Long.parseLong(hexConf.substring(2, 10), 16);
                } catch (Exception e){
                    if(world != null && world.getTotalWorldTime() % 150 == 0){
                        TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                    }
                }
            } else{
                if(world != null && world.getTotalWorldTime() % 150 == 0){
                    TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                }
            }
            x = x - width/2;
            GlStateManager.pushMatrix();
            Gui.drawRect(renderIcon ? x - 23 : x - 1, y - 1, x + width + 3, y + height + 1, (int) color);
            GlStateManager.popMatrix();
        }
    }

    private static <T> void addToListIfNotNull(List<T> list, T object){
        if(object != null){
            list.add(object);
        }
    }

    private static int getBossBarOffset(){
        Gui gui = Minecraft.getInstance().gui;
        if(gui != null){
            return ReflectionHelper.getBossBarEvents(gui.getBossOverlay()).size() * (8 + Minecraft.getInstance().font.lineHeight);
        }
        return 0;
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
                Vec3d lookingEyeVec = eyeVec.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);
                pointedEntity = null;
                Vec3d calcVec = null;
                List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().grow(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance).expand(1.0D, 1.0D, 1.0D), entity -> entity != null && (!(entity instanceof EntityItem) || ConfigBoolean.SHOW_DROPPED_ITEMS.value) && !ignoredEntities.contains(entity.getClass().getName()));
                double d2 = currentDistance;

                for(Entity entity : list){
                    double collisionBorder = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(collisionBorder, collisionBorder + (entity instanceof EntityItem ? 0.35 : 0), collisionBorder);
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyeVec, lookingEyeVec);

                    if(axisalignedbb.contains(eyeVec)){
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
