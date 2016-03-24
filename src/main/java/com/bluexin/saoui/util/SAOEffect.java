package com.bluexin.saoui.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public enum SAOEffect {

    PARALYZED,
    POISONED,
    STARVING,
    HUNGRY,
    ROTTEN,
    ILL,
    WEAK,
    CURSED,
    BLIND,
    WET,
    DROWNING,
    BURNING,
    SATURATION,
    SPEED_BOOST,
    WATER_BREATH,
    STRENGTH,
    ABSORPTION,
    FIRE_RES,
    HASTE,
    HEALTH_BOOST,
    INST_HEALTH, // Probably won't be used here
    INVISIBILITY,
    JUMP_BOOST,
    NIGHT_VISION,
    REGEN,
    RESIST;

    private static final int SRC_X = 0;
    private static final int SRC_Y = 135;
    private static final int SRC_WIDTH = 15;
    private static final int SRC_HEIGHT = 10;

    @SuppressWarnings("unchecked")
    public static List<SAOEffect> getEffects(EntityLivingBase entity) {
        final List<SAOEffect> effects = new ArrayList<>();

        entity.getActivePotionEffects().stream().filter(potionEffect0 -> potionEffect0 != null).forEach(potionEffect0 -> {

            if (potionEffect0.getPotionID() == Potion.moveSlowdown.getId() && potionEffect0.getAmplifier() > 5)
                effects.add(PARALYZED);
            else if (potionEffect0.getPotionID() == Potion.poison.getId()) effects.add(POISONED);
            else if (potionEffect0.getPotionID() == Potion.hunger.getId()) effects.add(ROTTEN);
            else if (potionEffect0.getPotionID() == Potion.confusion.getId()) effects.add(ILL);
            else if (potionEffect0.getPotionID() == Potion.weakness.getId()) effects.add(WEAK);
            else if (potionEffect0.getPotionID() == Potion.wither.getId()) effects.add(CURSED);
            else if (potionEffect0.getPotionID() == Potion.blindness.getId()) effects.add(BLIND);
            else if (potionEffect0.getPotionID() == Potion.saturation.getId()) effects.add(SATURATION);
            else if (potionEffect0.getPotionID() == Potion.moveSpeed.getId()) effects.add(SPEED_BOOST);
            else if (potionEffect0.getPotionID() == Potion.waterBreathing.getId()) effects.add(WATER_BREATH);
            else if (potionEffect0.getPotionID() == Potion.damageBoost.getId()) effects.add(STRENGTH);
            else if (potionEffect0.getPotionID() == Potion.absorption.getId()) effects.add(ABSORPTION);
            else if (potionEffect0.getPotionID() == Potion.fireResistance.getId()) effects.add(FIRE_RES);
            else if (potionEffect0.getPotionID() == Potion.digSpeed.getId()) effects.add(HASTE);
            else if (potionEffect0.getPotionID() == Potion.healthBoost.getId()) effects.add(HEALTH_BOOST);
            else if (potionEffect0.getPotionID() == Potion.heal.getId()) effects.add(INST_HEALTH);
            else if (potionEffect0.getPotionID() == Potion.invisibility.getId()) effects.add(INVISIBILITY);
            else if (potionEffect0.getPotionID() == Potion.jump.getId()) effects.add(JUMP_BOOST);
            else if (potionEffect0.getPotionID() == Potion.nightVision.getId()) effects.add(NIGHT_VISION);
            else if (potionEffect0.getPotionID() == Potion.regeneration.getId()) effects.add(REGEN);
            else if (potionEffect0.getPotionID() == Potion.resistance.getId()) effects.add(RESIST);
        });

        if (entity instanceof EntityPlayer) {
           if (((EntityPlayer) entity).getFoodStats().getFoodLevel() <= 6)
                effects.add(STARVING);
            else if (((EntityPlayer) entity).getFoodStats().getFoodLevel() <= 18)
                effects.add(HUNGRY);
            }

        if (entity.isInWater()) {
            if (entity.getAir() <= 0) effects.add(DROWNING);
            else if (entity.getAir() < 300) effects.add(WET);
        }

        if (entity.isBurning()) effects.add(BURNING);

        return effects;
    }

    private int getSrcX() {
        return SRC_X + (ordinal() % 14) * SRC_WIDTH;
    }

    private int getSrcY() {
        return SRC_Y + ordinal() / 14 * SRC_HEIGHT;
    }

    public final void glDraw(int x, int y, float z) {
        SAOGL.glBindTexture(SAOOption.SAO_UI.getValue() ? SAOResources.effects : SAOResources.effectsCustom);
        SAOGL.glTexturedRect(x, y, z, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

    public final void glDraw(int x, int y) {
        SAOGL.glBindTexture(SAOOption.SAO_UI.getValue() ? SAOResources.effects : SAOResources.effectsCustom);
        SAOGL.glTexturedRect(x, y, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

}
