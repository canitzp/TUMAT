package de.canitzp.tumat.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.api.ReMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * @author canitzp
 */
public class JsonReader{

    public JsonData data;

    public JsonReader(File jsonFile){
        try{
            Gson g = new Gson();
            this.data = g.fromJson(new FileReader(jsonFile), JsonData.class);
        } catch(FileNotFoundException e){
            try{
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                this.data = createExample();
                FileWriter writer = new FileWriter(jsonFile);
                g.toJson(this.data, writer);
                writer.close();
            } catch(IOException e1){
                e1.printStackTrace();
            }
        }
    }

    private JsonData createExample(){
        return new JsonData(Collections.singletonList(new JsonMod("examplemod", Collections.singletonList(new JsonModData("exampleBlock", new int[]{0, 2, 2596}, "exampleBlockRenameTest", "ExampleModRenameTest", new String[]{"ExampleDescription"})))));
    }

    public class JsonData{
        public List<JsonMod> mods;

        public JsonData(List<JsonMod> mods){
            this.mods = mods;
        }

        public void remap(ReMapper<ItemStack, String, String, String[]> remapper){
            for(JsonMod mod : mods){
                if(Loader.isModLoaded(mod.modid)){
                    for(JsonModData modData : mod.modData){
                        Item item = getItemFromName(new ResourceLocation(mod.modid, modData.objectName));
                        if(item != null){
                            for(int meta : modData.meta){
                                remapper.remap(new ItemStack(item, 1, meta), modData.newObjectName, modData.newModName, modData.newDescription);
                            }
                        }
                    }
                } else {
                    TUMAT.logger.info("Mod '" + mod.modid + "' isn't loaded.");
                }
            }
        }

    }

    public class JsonMod{
        public String modid;
        public List<JsonModData> modData;

        public JsonMod(String modid, List<JsonModData> modData){
            this.modid = modid;
            this.modData = modData;
        }

    }

    public class JsonModData{
        public String objectName;
        public int[] meta;
        public String newObjectName;
        public String newModName;
        public String[] newDescription;

        public JsonModData(String objectName, int[] meta, String newObjectName, String newModName, String[] newDescription){
            this.objectName = objectName;
            this.meta = meta;
            this.newObjectName = newObjectName;
            this.newModName = newModName;
            this.newDescription = newDescription;
        }

    }

    @Nullable
    private static Item getItemFromName(ResourceLocation loc){
        return Item.REGISTRY.containsKey(loc) ? Item.REGISTRY.getObject(loc) : null;
    }

}
