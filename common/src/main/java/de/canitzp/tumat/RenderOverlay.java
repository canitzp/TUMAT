package de.canitzp.tumat;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import de.canitzp.tumat.configuration.cats.ConfigString;
import de.canitzp.tumat.local.L10n;
import me.shedaniel.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.*;

import java.util.*;
import java.util.function.Predicate;

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
                    renderComponents(pose, fontRenderer, renderEntity(world, player, ((EntityHitResult) trace), calculate));
                    break;
                }
                case MISS: {
                    if (ConfigBoolean.SHOW_FLUIDS.value) {
                        renderComponents(pose, fontRenderer, renderMiss(world, player, trace, calculate));
                    }
                    break;
                }
            }
        }
    }

    private static TooltipComponent renderBlock(ClientLevel world, LocalPlayer player, BlockHitResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        BlockPos pos = trace.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if(state.is(Blocks.AIR)){
            return null;
        }
        
        component.setName(InfoUtil.getBlockName(state));
        for(FormattedText descLine : InfoUtil.getDescription(state.getBlock().asItem().getDefaultInstance())){
            component.add(new TextComponent(descLine.getString()), TooltipComponent.Priority.LOW);
        }
        component.setModName(InfoUtil.getModName(state.getBlock().asItem().getDefaultInstance()));
        
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            if(renderer.shouldBeActive()){
                renderer.renderBlock(world, player, pos, trace.getDirection(), component, shouldCalculate);
                BlockEntity tile = world.getBlockEntity(pos);
                if(ConfigBoolean.SHOW_TILES.value && tile != null){
                    try {
                        renderer.renderTileEntity(world, player, tile, trace.getDirection(), component, shouldCalculate);
                    } catch (Exception e){
                        component.add(new TextComponent("An error occured while getting tile data!"), TooltipComponent.Priority.HIGH);
                        if(world.getGameTime() % 150 == 0){
                            TUMAT.logger.error("An error occured while getting tile data from: " + state.getBlock().getDescriptionId() + " at: " + pos, e);
                        }
                    }
                }
                component.setIconRenderer(renderer.getIconRenderObject(world, player, pos, trace.getDirection(), savedTrace, shouldCalculate));
            }
        }
        
        return component;
    }

    private static TooltipComponent renderEntity(ClientLevel world, LocalPlayer player, EntityHitResult trace, boolean shouldCalculate){
        TooltipComponent component = new TooltipComponent();
        Entity entity = trace.getEntity();
        if(ConfigBoolean.SHOW_DROPPED_ITEMS.value && entity instanceof ItemEntity){
            component.setName(InfoUtil.getItemName(((ItemEntity) entity).getItem()).append(" (x" + ((ItemEntity) entity).getItem().getCount() + ")"));
            for(FormattedText formattedText : InfoUtil.getDescription(((ItemEntity) entity).getItem())){
                component.add(new TextComponent(formattedText.getString()), TooltipComponent.Priority.LOW);
            }
            component.setModName(InfoUtil.getModName(((ItemEntity) entity).getItem()));
            component.setIconRenderer(new IconRenderer(((ItemEntity) entity).getItem()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderEntityItem(world, player, (ItemEntity) entity, ((ItemEntity) entity).getItem(), component, shouldCalculate);
                }
            }
        } else if(ConfigBoolean.SHOW_ENTITIES.value && entity instanceof LivingEntity){
            component.setName(new TextComponent(InfoUtil.getEntityName(entity)));
            component.add(new TextComponent(((LivingEntity) entity).getHealth() + "/" + ((LivingEntity) entity).getMaxHealth()).withStyle(ChatFormatting.RED), TooltipComponent.Priority.HIGH);
            component.setModName(InfoUtil.getModName(entity));
            SpawnEggItem spawnEggItem = SpawnEggItem.byId(entity.getType());
            component.setIconRenderer(new IconRenderer(spawnEggItem.getDefaultInstance()));
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                if(renderer.shouldBeActive()){
                    renderer.renderLivingEntity(world, player, (LivingEntity) entity, component, shouldCalculate);
                }
            }
        } else if(ConfigBoolean.SHOW_ENTITIES.value){
            component.setName(new TextComponent(InfoUtil.getEntityName(entity)));
            component.setModName(InfoUtil.getModName(entity));
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
        BlockState state = world.getBlockState(new BlockPos(trace.getLocation()));
        if(state.is(Blocks.AIR)){
            return component;
        }
        if(state.getBlock() instanceof LiquidBlock){
            component.setName(InfoUtil.getBlockName(state));
            component.setModName(InfoUtil.getModName(state.getBlock().asItem().getDefaultInstance()));
            Fluid fluid = ReflectionHelper.getFluid((LiquidBlock) state.getBlock());
            component.setIconRenderer(new IconRenderer(fluid.getBucket().getDefaultInstance()));
        }
        for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
            if(renderer.shouldBeActive()){
                renderer.renderMiss(world, player, trace, component, shouldCalculate);
            }
        }
        
        return component;
    }

    public static void renderComponents(PoseStack pose, Font fontRenderer, TooltipComponent component) {
        if(component != null){
            
            int x = 0;
            int y = getBossBarOffset();
            boolean renderIcon = ConfigBoolean.RENDER_ICONS.value && component.getIconRenderer() != null && component.getIconRenderer().shouldRender();
            if(ConfigBoolean.SHOW_BACKGROUND.value){
                //renderBackground(x, y, finished.getLength(), finished.getHeight(), renderIcon);
            }
            component.render(pose, fontRenderer, x, y);
        }
    }

    private static void renderBackground(PoseStack poseStack, int x, int y, int width, int height, boolean renderIcon){
        if(ConfigBoolean.SHOW_BACKGROUND.value && height > 0){
            Level world = Minecraft.getInstance().level;
            long color = 0x806A9BC3;
            String hexConf = ConfigString.BACKGROUND_COLOR.value;
            if(hexConf.length() == 10){
                try {
                    color = Long.parseLong(hexConf.substring(2, 10), 16);
                } catch (Exception e){
                    if(world != null && world.getGameTime() % 150 == 0){
                        TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                    }
                }
            } else{
                if(world != null && world.getGameTime() % 150 == 0){
                    TUMAT.logger.error("There is a error with the background hex code! The correct format is AARRGGBB with a '0x' in front of the code");
                }
            }
            x = x - width/2;
            poseStack.pushPose();
            //Gui.drawRect(renderIcon ? x - 23 : x - 1, y - 1, x + width + 3, y + height + 1, (int) color);
            poseStack.popPose();
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

    private static HitResult createRayTraceForDistance(Level world, Player player, double maxDistance, float partialTicks){
        if(player == null || world == null){
            return null;
        }
        HitResult playerTrace = player.pick(maxDistance, partialTicks, true);
        if(playerTrace.getType() == HitResult.Type.MISS){
            Vec3 playerEyePosition = player.getEyePosition(partialTicks);
            Vec3 playerViewVec = player.getViewVector(partialTicks);
            Vec3 lookingEyeVec = playerEyePosition.add(playerViewVec.x * maxDistance, playerViewVec.y * maxDistance, playerViewVec.z * maxDistance);
    
            AABB aabb = player.getBoundingBox().expandTowards(playerViewVec.scale(maxDistance)).inflate(1.0D);
    
            Predicate<Entity> validEntities = entity -> {
                return entity != null && (ConfigBoolean.SHOW_DROPPED_ITEMS.value == (entity instanceof ItemEntity)) && !ignoredEntities.contains(entity.getClass().getName());
            };
            
            return ProjectileUtil.getEntityHitResult(player, playerViewVec, lookingEyeVec, aabb, validEntities, maxDistance);
        }
        
        return playerTrace;
    }

}
