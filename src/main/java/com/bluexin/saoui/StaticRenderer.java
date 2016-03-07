package com.bluexin.saoui;

import com.bluexin.saoui.ui.SAOCharacterViewGUI;
import com.bluexin.saoui.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by Tencao on 14/02/2016.
 */
@SideOnly(Side.CLIENT)
class StaticRenderer{

    private static final int HEALTH_COUNT = 32;
    private static final double HEALTH_ANGLE = 0.35F;
    private static final double HEALTH_RANGE = 0.975F;
    private static final float HEALTH_OFFSET = 0.75F;
    private static final double HEALTH_HEIGHT = 0.21F;

    public static void render(RenderManager renderManager, EntityLivingBase living, double x, double y, double z) {
        if (!(ColorStateHandler.getInstance().isValid(living))) System.out.print("WARNING - " + living.getName() + " " + living.getUniqueID() + " contains no valid state" + "\n");

        final Minecraft mc = Minecraft.getMinecraft();

        boolean dead = StaticPlayerHelper.getHealth(mc, living, SAOMod.UNKNOWN_TIME_DELAY) <= 0;

        if (living.deathTime == 1) living.deathTime++;

        if (!SAOCharacterViewGUI.IS_VIEWING  && !dead && !living.isInvisibleToPlayer(mc.thePlayer)) {
            if (SAOOption.COLOR_CURSOR.getValue())
                if (!(SAOOption.DEBUG_MODE.getValue() && SAOColorState.checkValidState(living))) {
                    doRenderColorCursor(renderManager, mc, living, x, y, z, 64);
                } else if (SAOOption.DEBUG_MODE.getValue())
                    doRenderColorCursor(renderManager, mc, living, x, y, z, 64);

            if ((SAOOption.HEALTH_BARS.getValue()) && (!living.equals(mc.thePlayer))) {
                if (!(SAOOption.DEBUG_MODE.getValue() && SAOColorState.checkValidState(living)))
                    doRenderHealthBar(renderManager, mc, living, x, y, z);
                else if (SAOOption.DEBUG_MODE.getValue())
                    doRenderHealthBar(renderManager, mc, living, x, y, z);
            }
        }
    }

    public static void doRenderColorCursor(RenderManager renderManager, Minecraft mc, EntityLivingBase living, double x, double y, double z, int distance) {
        if (living.riddenByEntity != null) return;
        if (SAOOption.LESS_VISUALS.getValue() && !(living instanceof IMob || StaticPlayerHelper.getHealth(mc, living, SAOMod.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(living)))
            return;

        if (living.worldObj.isRemote) {
            double d3 = living.getDistanceSqToEntity(renderManager.livingPlayer);

            if (d3 <= (double) (distance * distance)) {
                final float sizeMult = living.isChild() && living instanceof EntityMob ? 0.5F : 1.0F;

                float f = 1.6F;
                float f1 = 0.016666668F * f;

                Tessellator tessellator = Tessellator.getInstance();

                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.0F, (float) y + sizeMult * living.height + sizeMult * 1.1F, (float) z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-(f1 * sizeMult), -(f1 * sizeMult), (f1 * sizeMult));
                GL11.glDisable(GL11.GL_LIGHTING);

                SAOGL.glDepthTest(true);

                SAOGL.glAlpha(true);
                SAOGL.glBlend(true);
                SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);

                SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.entities : SAOResources.entitiesCustom);
                SAOEventHandler.getColor(living);
                worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                //System.out.print(state.name() + " assigned to " + living.getCommandSenderName() + " " + living.getUniqueID() + "\n");

                if (SAOOption.SPINNING_CRYSTALS.getValue()) {
                    double a = (living.worldObj.getTotalWorldTime() % 40) / 20.0D  * Math.PI;
                    double cos = Math.cos(a);//Math.PI / 3 * 2);
                    double sin = Math.sin(a);//Math.PI / 3 * 2);

                    if (a > Math.PI / 2 && a <= Math.PI * 3 / 2 ) {
                        worldrenderer.pos(9.0D * cos, -1, 9.0D * sin).tex(0.125F, 0.25F).endVertex();
                        worldrenderer.pos(9.0D * cos, 17, 9.0D * sin).tex(0.125F, 0.375F).endVertex();
                        worldrenderer.pos(-9.0D * cos, 17, -9.0D * sin).tex(0F, 0.375F).endVertex();
                        worldrenderer.pos(-9.0D * cos, -1, -9.0D * sin).tex(0F, 0.25F).endVertex();
                    } else {
                        worldrenderer.pos(-9.0D * cos, -1, -9.0D * sin).tex(0F, 0.25F).endVertex();
                        worldrenderer.pos(-9.0D * cos, 17, -9.0D * sin).tex(0F, 0.375F).endVertex();
                        worldrenderer.pos(9.0D * cos, 17, 9.0D * sin).tex(0.125F, 0.375F).endVertex();
                        worldrenderer.pos(9.0D * cos, -1, 9.0D * sin).tex(0.125F, 0.25F).endVertex();
                    }

                    tessellator.draw();
                    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                    if (a < Math.PI) {
                        worldrenderer.pos(-9.0D * sin, -1, 9.0D * cos).tex(0.125F, 0.25F).endVertex();
                        worldrenderer.pos(-9.0D * sin, 17, 9.0D * cos).tex(0.125F, 0.375F).endVertex();
                        worldrenderer.pos(9.0D * sin, 17, -9.0D * cos).tex(0F, 0.375F).endVertex();
                        worldrenderer.pos(9.0D * sin, -1, -9.0D * cos).tex(0F, 0.25F).endVertex();
                    } else {
                        worldrenderer.pos(9.0D * sin, -1, -9.0D * cos).tex(0F, 0.25F).endVertex();
                        worldrenderer.pos(9.0D * sin, 17, -9.0D * cos).tex(0F, 0.375F).endVertex();
                        worldrenderer.pos(-9.0D * sin, 17, 9.0D * cos).tex(0.125F, 0.375F).endVertex();
                        worldrenderer.pos(-9.0D * sin, -1, 9.0D * cos).tex(0.125F, 0.25F).endVertex();
                    }

                    tessellator.draw();
                } else {
                    worldrenderer.pos(-9, -1, 0.0D).tex(0F, 0.25F).endVertex();
                    worldrenderer.pos(-9, 17, 0.0D).tex(0F, 0.375F).endVertex();
                    worldrenderer.pos(9, 17, 0.0D).tex(0.125F, 0.375F).endVertex();
                    worldrenderer.pos(9, -1, 0.0D).tex(0.125F, 0.25F).endVertex();
                    tessellator.draw();
                }

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
        }
    }

    private static void doRenderHealthBar(RenderManager renderManager, Minecraft mc, EntityLivingBase living, double x, double y, double z) {
        if (living.riddenByEntity != null && living.riddenByEntity == mc.thePlayer) return;
        if (SAOOption.LESS_VISUALS.getValue() && !(living instanceof IMob || StaticPlayerHelper.getHealth(mc, living, SAOMod.UNKNOWN_TIME_DELAY) != StaticPlayerHelper.getMaxHealth(living)))
            return;
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.getValue() ? SAOResources.entities : SAOResources.entitiesCustom);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        SAOGL.glDepthTest(true);
        SAOGL.glCullFace(false);
        SAOGL.glBlend(true);

        SAOGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final int hitPoints = (int) (getHealthFactor(mc, living, SAOMod.UNKNOWN_TIME_DELAY) * HEALTH_COUNT);
        useColor(mc, living, SAOMod.UNKNOWN_TIME_DELAY);

        worldrenderer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

        final float sizeMult = living.isChild() && living instanceof EntityMob? 0.5F: 1.0F;

        for (int i = 0; i <= hitPoints; i++) {
            final double value = (double) (i + HEALTH_COUNT - hitPoints) / HEALTH_COUNT;
            final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

            final double x0 = x + sizeMult * living.width * HEALTH_RANGE * Math.cos(rad);
            final double y0 = y + sizeMult * living.height * HEALTH_OFFSET;
            final double z0 = z + sizeMult * living.width * HEALTH_RANGE * Math.sin(rad);

            final double uv_value = value - (double) (HEALTH_COUNT - hitPoints) / HEALTH_COUNT;

            worldrenderer.pos(x0, y0 + HEALTH_HEIGHT, z0).tex((1.0 - uv_value), 0).endVertex();
            worldrenderer.pos(x0, y0, z0).tex((1.0 - uv_value), 0.125).endVertex();
        }

        tessellator.draw();

        SAOGL.glColor(1, 1, 1, 1);
        worldrenderer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

        for (int i = 0; i <= HEALTH_COUNT; i++) {
            final double value = (double) i / HEALTH_COUNT;
            final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

            final double x0 = x + sizeMult * living.width * HEALTH_RANGE * Math.cos(rad);
            final double y0 = y + sizeMult * living.height * HEALTH_OFFSET;
            final double z0 = z + sizeMult * living.width * HEALTH_RANGE * Math.sin(rad);

            worldrenderer.pos(x0, y0 + HEALTH_HEIGHT, z0).tex((1.0 - value), 0.125).endVertex();
            worldrenderer.pos(x0, y0, z0).tex((1.0 - value), 0.25).endVertex();
        }

        tessellator.draw();

        SAOGL.glCullFace(true);
    }

    public static void doSpawnDeathParticles(Minecraft mc, Entity living) {
        final World world = living.worldObj;

        if (living.worldObj.isRemote) {
            final float[][] colors = {
                    {1F / 0xFF * 0x9A, 1F / 0xFF * 0xFE, 1F / 0xFF * 0x2E},
                    {1F / 0xFF * 0x01, 1F / 0xFF * 0xFF, 1F / 0xFF * 0xFF},
                    {1F / 0xFF * 0x08, 1F / 0xFF * 0x08, 1F / 0xFF * 0x8A}
            };

            final float size = living.width * living.height;
            final int pieces = (int) Math.max(Math.min((size * 64), 128), 8);

            for (int i = 0; i < pieces; i++) {
                final float[] color = colors[i % 3];

                final double x0 = living.width * (Math.random() * 2 - 1) * 0.75;
                final double y0 = living.height * (Math.random());
                final double z0 = living.width * (Math.random() * 2 - 1) * 0.75;

                mc.effectRenderer.addEffect(new SAOEntityPiecesFX(
                        world,
                        living.posX + x0, living.posY + y0, living.posZ + z0,
                        color[0], color[1], color[2]
                ));
            }
        }
    }

    private static void useColor(Minecraft mc, Entity living, float time) {
        if (living instanceof EntityLivingBase) {
            SAOHealthStep.getStep(mc, (EntityLivingBase) living, time).glColor();
        } else {
            SAOHealthStep.GOOD.glColor();
        }
    }

    private static float getHealthFactor(Minecraft mc, Entity living, float time) {
        final float normalFactor = StaticPlayerHelper.getHealth(mc, living, time) / StaticPlayerHelper.getMaxHealth(living);
        final float delta = 1.0F - normalFactor;

        return normalFactor + (delta * delta / 2) * normalFactor;
    }

}