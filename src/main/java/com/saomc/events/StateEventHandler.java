package com.saomc.events;

import com.saomc.colorstates.ColorState;
import com.saomc.colorstates.ColorStateHandler;
import com.saomc.util.OptionCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

import static com.saomc.events.EventCore.mc;

/**
 * This is purely for the ColorStateHandler
 */
public class StateEventHandler {

    private static int ticks = 0;

    static void checkTicks (TickEvent.RenderTickEvent e){
        if (!OptionCore.DISABLE_TICKS.getValue() && mc.theWorld != null && e.phase.equals(TickEvent.Phase.END)) {
            if (ticks >= 10) {
                checkRadius();
                resetState();
                ticks = 0;
            } else ++ticks;

        }
    }

    static void checkRadius (){
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().expand(19.0D, 19.0D, 19.0D));
        Entity living;

        for (Entity entity : entities) {
            living = entity;
            if (living != null && living instanceof EntityLivingBase && ColorStateHandler.getInstance().getSavedState((EntityLivingBase) living) == ColorState.VIOLENT)
                ColorStateHandler.getInstance().set((EntityLivingBase) living, ColorState.KILLER, true);
        }
    }

    static void resetState(){
        if (OptionCore.AGGRO_SYSTEM.getValue())ColorStateHandler.getInstance().updateKeeper();
        else if (!ColorStateHandler.getInstance().isEmpty())ColorStateHandler.getInstance().clean();
    }

    static void genStateMaps(EntityEvent.EntityConstructing e){
        if (e.entity instanceof EntityLivingBase)
            if (ColorStateHandler.getInstance().getDefault((EntityLivingBase)e.entity) == null && !(e.entity instanceof EntityPlayer))
                ColorStateHandler.getInstance().genDefaultState((EntityLivingBase)e.entity);
    }

    public static void getColor(EntityLivingBase entity){
        ColorStateHandler.getInstance().stateColor(entity);
    }

}
