package de.canitzp.tumat;

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
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class RenderOverlay{

    private static Map<ResourceLocation, IconRenderer> bucketCache = new HashMap<>();
    private static List<String> ignoredEntities = null;

    private static RayTraceResult savedTrace;

    public static void render(WorldClient world, EntityPlayerSP player, FontRenderer fontRenderer, float partialTicks, boolean shouldCalculate) {
        boolean calculate = savedTrace == null || shouldCalculate;
        RayTraceResult trace;
        if (calculate) {
            if(ignoredEntities == null){
                ignoredEntities = new ArrayList<>();
                for(IWorldRenderer worldRenderer : TUMATApi.getRegisteredComponents()){
                    ignoredEntities.addAll(worldRenderer.getInvisibleEntities());
                }
            }
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
            switch (trace.typeOfHit) {
                case BLOCK: {
                    if (ConfigBoolean.SHOW_BLOCKS.value) {
                        renderComponents(fontRenderer, renderBlock(world, player, trace.getBlockPos(), trace.sideHit, calculate));
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

    private static TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(pos)){
            IBlockState state = world.getBlockState(pos);
            component.setName(TextComponent.createWithSensitiveName(world, player, savedTrace, pos, state));
            component.add(new DescriptionComponent(InfoUtil.newStackFromBlock(world, pos, state, player, savedTrace)), TooltipComponent.Priority.LOW);
            component.setModName(new TextComponent(InfoUtil.getModName(state.getBlock())));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderBlock(world, player, pos, side, component, shouldCalculate);
                    TileEntity tile = world.getTileEntity(pos);
                    if(ConfigBoolean.SHOW_TILES.value && tile != null){
                        renderer.renderTileEntity(world, player, tile, side, component, shouldCalculate);
                    }
                    component.setIconRenderer(renderer.getIconRenderObject(world, player, pos, side, savedTrace, shouldCalculate));
                }
            }
        } else {
            return null;
        }
        return component;
    }

    private static TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(ConfigBoolean.SHOW_DROPPED_ITEMS.value && entity instanceof EntityItem){
            component.setName(new TextComponent(L10n.getItemText(InfoUtil.getItemName(((EntityItem) entity).getItem()) + TextFormatting.RESET, String.valueOf(((EntityItem) entity).getItem().getCount()))));
            component.add(new DescriptionComponent(((EntityItem) entity).getItem()), TooltipComponent.Priority.LOW);
            component.setModName(new TextComponent(InfoUtil.getModName(((EntityItem) entity).getItem().getItem())));
            component.setIconRenderer(new IconRenderer(((EntityItem) entity).getItem()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntityItem(world, player, (EntityItem) entity, ((EntityItem) entity).getItem(), component, shouldCalculate);
                }
            }
        } else if(ConfigBoolean.SHOW_ENTITIES.value && entity instanceof EntityLivingBase){
            component.setName(new TextComponent(InfoUtil.getEntityName(entity)));
            component.add(new TextComponent(TextFormatting.RED.toString() + ((EntityLivingBase) entity).getHealth() + "/" + ((EntityLivingBase) entity).getMaxHealth()), TooltipComponent.Priority.HIGH);
            component.setModName(new TextComponent(InfoUtil.getModName(entity)));
            ResourceLocation res = EntityList.getKey(entity);
            if(res != null){
                ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
                ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, res);
                component.setIconRenderer(new IconRenderer(spawnEgg));
            }
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderLivingEntity(world, player, (EntityLivingBase) entity, component, shouldCalculate);
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

    private static TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        if(!world.isAirBlock(trace.getBlockPos())){
            IBlockState state = world.getBlockState(trace.getBlockPos());
            if(state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase){
                component.setName(new TextComponent(state.getBlock().getLocalizedName()));
                component.setModName(new TextComponent(InfoUtil.getModName(state.getBlock())));
                IconRenderer renderer;
                if(bucketCache.containsKey(state.getBlock().getRegistryName())){
                    renderer = bucketCache.get(state.getBlock().getRegistryName());
                } else {
                    renderer = new IconRenderer(FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.lookupFluidForBlock(state.getBlock()), 1000)));
                    bucketCache.put(state.getBlock().getRegistryName(), renderer);
                }
                component.setIconRenderer(renderer);
            }
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderMiss(world, player, trace, component, shouldCalculate);
                }
            }
        }
        return component;
    }

    public static void renderComponents(FontRenderer fontRenderer, TooltipComponent component) {
        if(component != null){
            TooltipComponent.Finished finished = component.close();
            int x = GuiTUMAT.getXFromPercantage();
            int y = GuiTUMAT.getYFromPercantage() + getBossBarOffset();
            int lines = 0;
            boolean renderIcon = ConfigBoolean.RENDER_ICONS.value && component.getIconRenderer() != null && component.getIconRenderer().shouldRender();
            if(ConfigBoolean.SHOW_BACKGROUND.value){
                renderBackground(x, y, finished.getLength(), finished.getHeight(), renderIcon);
            }
            GlStateManager.pushMatrix();
            for(IComponentRender render : finished.getComponents()){
                if(render != null){
                    GlStateManager.scale(ConfigFloat.SCALE.value, ConfigFloat.SCALE.value, ConfigFloat.SCALE.value);
                    render.render(fontRenderer, x, y, 0xFFFFFF);
                    int lineY = render.getLines(fontRenderer) * render.getHeightPerLine(fontRenderer);
                    lines += lineY;
                    y += lineY;
                }
            }
            if(renderIcon){
                component.getIconRenderer().render(x - finished.getLength() / 2 - 22, GuiTUMAT.getYFromPercantage() + (lines / 2 - 10));
            }
            GlStateManager.popMatrix();
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
        if(GuiIngameForge.renderBossHealth){
            GuiIngame guiIngame = Minecraft.getMinecraft().ingameGUI;
            for(Field field : GuiIngame.class.getDeclaredFields()){
                if(field.getType() == GuiBossOverlay.class){
                    field.setAccessible(true);
                    try {
                        Map<UUID, BossInfoClient> mapBossInfos = ReflectionHelper.getPrivateValue(GuiBossOverlay.class, (GuiBossOverlay) field.get(guiIngame), 2);
                        return mapBossInfos.size() * (8 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
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
                Vec3d lookingEyeVec = eyeVec.addVector(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);
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
