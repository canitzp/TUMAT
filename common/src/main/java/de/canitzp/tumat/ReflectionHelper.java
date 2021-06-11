package de.canitzp.tumat;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class ReflectionHelper {
    
    private static final Field LEFT_POS, TOP_POS, BOSS_HEALT_OVERLAY_EVENTS, FLUID, BUCKET_CONTENT;
    
    static{
        try{
            LEFT_POS = AbstractContainerScreen.class.getDeclaredField("leftPos");
            LEFT_POS.setAccessible(true);
            TOP_POS = AbstractContainerScreen.class.getDeclaredField("topPos");
            TOP_POS.setAccessible(true);
            BOSS_HEALT_OVERLAY_EVENTS = BossHealthOverlay.class.getDeclaredField("events");
            BOSS_HEALT_OVERLAY_EVENTS.setAccessible(true);
            FLUID = LiquidBlock.class.getDeclaredField("fluid");
            FLUID.setAccessible(true);
            BUCKET_CONTENT = BucketItem.class.getDeclaredField("content");
            BUCKET_CONTENT.setAccessible(true);
        } catch(NoSuchFieldException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static int getGuiLeft(AbstractContainerScreen<?> screen){
        try{
            return LEFT_POS.getInt(screen);
        } catch(IllegalAccessException e){
            e.printStackTrace();
            return 0;
        }
    }
    
    public static int getGuiTop(AbstractContainerScreen<?> screen){
        try{
            return TOP_POS.getInt(screen);
        } catch(IllegalAccessException e){
            e.printStackTrace();
            return 0;
        }
    }
    
    public static Map<UUID, LerpingBossEvent> getBossBarEvents(BossHealthOverlay overlay){
        try{
            return (Map<UUID, LerpingBossEvent>) BOSS_HEALT_OVERLAY_EVENTS.get(overlay);
        } catch(IllegalAccessException e){
            e.printStackTrace();
            return Maps.newLinkedHashMap();
        }
    }
    
    public static Fluid getFluid(LiquidBlock liquidBlock) {
        try{
            return (Fluid) FLUID.get(liquidBlock);
        } catch(IllegalAccessException e){
            e.printStackTrace();
            return Fluids.EMPTY;
        }
    }
    
    public static Fluid getBucketContent(BucketItem bucketItem){
        try{
            return (Fluid) BUCKET_CONTENT.get(bucketItem);
        } catch(IllegalAccessException e){
            e.printStackTrace();
            return Fluids.EMPTY;
        }
    }

}
