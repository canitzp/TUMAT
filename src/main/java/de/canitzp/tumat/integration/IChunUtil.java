package de.canitzp.tumat.integration;

import com.google.common.collect.Lists;
import de.canitzp.tumat.api.IWorldRenderer;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * @author canitzp
 */
public class IChunUtil implements IWorldRenderer{

    @Override
    public List<String> getInvisibleEntities() {
        return Lists.newArrayList(EntityLatchedRenderer.class.getName());
    }
}
