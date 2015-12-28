package com.bluexin.saoui;

import org.lwjgl.opengl.GL11;

import com.bluexin.saoui.util.SAOColorState;
import com.bluexin.saoui.util.SAOGL;
import com.bluexin.saoui.util.SAOHealthStep;
import com.bluexin.saoui.util.SAOOption;
import com.bluexin.saoui.util.SAOResources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SAORenderBase extends Render<EntityLivingBase> {


	private static final int HEALTH_COUNT = 32;
    private static final double HEALTH_ANGLE = 0.35F;
    private static final double HEALTH_RANGE = 0.975F;
    private static final float HEALTH_OFFSET = 0.75F;
    @SuppressWarnings("unused")
	private static final float HEALTH_OFFSET_PLAYER = -0.125F;
    private static final double HEALTH_HEIGHT = 0.21F;

    @SuppressWarnings("unused")
    private static final double PIECES_X_OFFSET = 0.02;
    @SuppressWarnings("unused")
    private static final double PIECES_Y_OFFSET = -0.02;
    @SuppressWarnings("unused")
    private static final int PIECES_COUNT = 150;
    @SuppressWarnings("unused")
    private static final double PIECES_SPEED = 1.4;
    @SuppressWarnings("unused")
    private static final double PIECES_GRAVITY = 0.4;

    private final Render parent;
    private boolean smallArms;

    public SAORenderBase(Render render) {
        super(render.getRenderManager());
        parent = render;
    }

    @Override
    public boolean shouldRender(EntityLivingBase livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return parent.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
	public void doRender(EntityLivingBase entity, double x, double y, double z, float f0, float f1) {
        final Minecraft mc = Minecraft.getMinecraft();

        boolean dead = false, deadStart = false, deadExactly = false;

        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase living = (EntityLivingBase) entity;

            dead = SAOMod.getHealth(mc, living, SAOMod.UNKNOWN_TIME_DELAY) <= 0;
            deadStart = (living.deathTime == 1);
            deadExactly = (living.deathTime >= 18);

            if (deadStart) {
                living.deathTime++;
            }
        }
        
        parent.doRender(entity, x, y, z, f0, f1);

        if ((entity instanceof EntityLivingBase) && (!dead) && (!entity.isInvisibleToPlayer(mc.thePlayer))) {
            if (SAOOption.COLOR_CURSOR.value) {
                doRenderColorCursor(mc, entity, x, y, z, 64);
            }

            if ((SAOOption.HEALTH_BARS.value) && (!entity.equals(mc.thePlayer))) {
                doRenderHealthBar(mc, entity, x, y, z, f0, f1);
            }
        }

        if (SAOOption.PARTICLES.value) {
            if (deadStart && entity instanceof EntityLivingBase) {
                SAOSound.playAtEntity(entity, SAOSound.PARTICLES_DEATH);
            }

            if (deadExactly) {
                doSpawnDeathParticles(mc, entity);

                entity.setDead();
            }
        }
    }

	public void bindTexture(ResourceLocation location) {
        parent.bindTexture(location);
    }

    @Override
	public void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_) {
        parent.doRenderShadowAndFire(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, p_76979_8_, p_76979_9_);
    }

    @Override
	public FontRenderer getFontRendererFromRenderManager() {
        return parent.getFontRendererFromRenderManager();
    }

    @Override
	public RenderManager getRenderManager() {
        return parent.getRenderManager();
    }

    private void doRenderColorCursor(Minecraft mc, Entity entity, double x, double y, double z, int distance) {
        if (entity instanceof EntityArmorStand) return;
        if (entity.riddenByEntity != null) return;
        if (SAOOption.LESS_VISUALS.value && !(entity instanceof IMob || SAOMod.getHealth(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) != SAOMod.getMaxHealth(entity)) && !(entity instanceof EntityPlayer)) return;

        double d3 = entity.getDistanceSqToEntity(renderManager.livingPlayer);

        if (d3 <= (double) (distance * distance)) {
            final float sizeMult = ((EntityLivingBase) entity).isChild() && entity instanceof EntityMob? 0.5F: 1.0F;

            float f = 1.6F;
            float f1 = 0.016666668F * f;

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + sizeMult * entity.height + sizeMult * 1.1F, (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(-(f1 * sizeMult), -(f1 * sizeMult), (f1 * sizeMult));
            GlStateManager.disableLighting();

            SAOGL.glDepthTest(true);

            SAOGL.glBlend(true);
            SAOGL.tryBlendFuncSeparate(770, 771, 1, 0);


            SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value ? SAOResources.entities : SAOResources.entitiesCustom);

            Tessellator tessellator = Tessellator.getInstance();

            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            if (entity instanceof EntityLiving || entity instanceof EntityPlayer){
            	SAOColorState.getColorState(mc, entity, SAOMod.UNKNOWN_TIME_DELAY).glColor();}

            if (SAOOption.SPINNING_CRYSTALS.value) {
                double a = (entity.worldObj.getTotalWorldTime() % 40) / 20.0D  * Math.PI;
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


            SAOGL.glBlend(false);
            SAOGL.glColor(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    private void doRenderHealthBar(Minecraft mc, Entity entity, double x, double y, double z, float f0, float f1) {
        if (entity instanceof EntityArmorStand) return;
        if (entity.riddenByEntity != null && entity.riddenByEntity == mc.thePlayer) return;
        if (entity instanceof EntityPlayer && SAOMod.isCreative((AbstractClientPlayer) entity)) return;
        if (SAOOption.LESS_VISUALS.value && !(entity instanceof IMob || SAOMod.getHealth(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) != SAOMod.getMaxHealth(entity)) && !(entity instanceof EntityPlayer)) return;
        SAOGL.glBindTexture(SAOOption.ORIGINAL_UI.value ? SAOResources.entities : SAOResources.entitiesCustom);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        SAOGL.glDepthTest(true);
        SAOGL.glCullFace(false);
        SAOGL.glBlend(true);

        SAOGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final int hitPoints = (int) (getHealthFactor(mc, entity, SAOMod.UNKNOWN_TIME_DELAY) * HEALTH_COUNT);
        useColor(mc, entity, SAOMod.UNKNOWN_TIME_DELAY);

        worldrenderer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

        final float sizeMult = ((EntityLivingBase) entity).isChild() && entity instanceof EntityMob? 0.5F: 1.0F;

        for (int i = 0; i <= hitPoints; i++) {
            final double value = (double) (i + HEALTH_COUNT - hitPoints) / HEALTH_COUNT;
            final double rad = Math.toRadians(renderManager.playerViewY - 135) + (value - 0.5) * Math.PI * HEALTH_ANGLE;

            final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
            final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
            final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

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

            final double x0 = x + sizeMult * entity.width * HEALTH_RANGE * Math.cos(rad);
            final double y0 = y + sizeMult * entity.height * HEALTH_OFFSET;
            final double z0 = z + sizeMult * entity.width * HEALTH_RANGE * Math.sin(rad);

            worldrenderer.pos(x0, y0 + HEALTH_HEIGHT, z0).tex((1.0 - value), 0.125).endVertex();
            worldrenderer.pos(x0, y0, z0).tex((1.0 - value), 0.25).endVertex();
        }

        tessellator.draw();

        SAOGL.glCullFace(true);
    }

    private void doSpawnDeathParticles(Minecraft mc, Entity entity) {
        final World world = entity.worldObj;

        if (world != null) {
            final float[][] colors = {
                    {1F / 0xFF * 0x9A, 1F / 0xFF * 0xFE, 1F / 0xFF * 0x2E},
                    {1F / 0xFF * 0x01, 1F / 0xFF * 0xFF, 1F / 0xFF * 0xFF},
                    {1F / 0xFF * 0x08, 1F / 0xFF * 0x08, 1F / 0xFF * 0x8A}
            };

            final float size = entity.width * entity.height;
            final int pieces = (int) Math.max(Math.min((size * 64), 128), 8);

            for (int i = 0; i < pieces; i++) {
                final float[] color = colors[i % 3];

                final double x0 = entity.width * (Math.random() * 2 - 1) * 0.75;
                final double y0 = entity.height * (Math.random());
                final double z0 = entity.width * (Math.random() * 2 - 1) * 0.75;

                mc.effectRenderer.addEffect(new SAOEntityPiecesFX(
                        world,
                        entity.posX + x0, entity.posY + y0, entity.posZ + z0,
                        color[0], color[1], color[2]
                ));
            }
        }
    }

    private void useColor(Minecraft mc, Entity entity, float time) {
        if (entity instanceof EntityLivingBase) {
            SAOHealthStep.getStep(mc, (EntityLivingBase) entity, time).glColor((EntityLivingBase) entity);
        } else {
            SAOHealthStep.GOOD.glColor();
        }
    }

    private float getHealthFactor(Minecraft mc, Entity entity, float time) {
        final float normalFactor = SAOMod.getHealth(mc, entity, time) / SAOMod.getMaxHealth(entity);
        final float delta = 1.0F - normalFactor;

        return normalFactor + (delta * delta / 2) * normalFactor;
    }

    @Override
    public void renderName(EntityLivingBase entity, double x, double y, double z) {
        if (entity instanceof EntityLivingBase) super.renderName(entity, x, y, z);
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
        return null;
	}

}
