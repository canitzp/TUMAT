package de.canitzp.tumat;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TUMAT.MODID)
public class TUMATForge {

    public TUMATForge() {
        EventBuses.registerModEventBus(TUMAT.MODID, FMLJavaModLoadingContext.get().getModEventBus());

        TUMAT.init();
    }

}
