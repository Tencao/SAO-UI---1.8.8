package com.saomc.events;

import com.saomc.screens.death.DeathScreen;
import com.saomc.screens.ingame.IngameGUI;
import com.saomc.screens.menu.IngameMenuGUI;
import com.saomc.SoundCore;
import com.saomc.renders.StaticRenderer;
import com.saomc.screens.menu.MainMenuGUI;
import com.saomc.screens.menu.StartupGUI;
import com.saomc.screens.window.ScreenGUI;
import com.saomc.util.OptionCore;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static com.saomc.events.EventCore.mc;

@SideOnly(Side.CLIENT)
public class RenderHandler {

    static final List<EntityLivingBase> deadHandlers = new ArrayList<>();
    private static boolean menuGUI = true;

    static void checkingameGUI() {
        if (mc.ingameGUI != null && !(mc.ingameGUI instanceof IngameGUI))
            mc.ingameGUI = new IngameGUI(mc);
    }

    static void deathHandlers() {
        deadHandlers.forEach(ent -> {
            if (ent != null) {
                final boolean deadStart = (ent.deathTime == 1);
                final boolean deadExactly = (ent.deathTime >= 18);
                if (deadStart) {
                    ent.deathTime++;
                    SoundCore.playAtEntity(ent, SoundCore.PARTICLES_DEATH);
                }

                if (deadExactly) {
                    StaticRenderer.doSpawnDeathParticles(mc, ent);
                    ent.setDead();
                }
            }
        });
        deadHandlers.removeIf(ent -> ent.isDead);
    }

    static void guiInstance(GuiOpenEvent e) {
        if (!(mc.currentScreen instanceof ScreenGUI)) {
            if (mc.currentScreen != e.gui) {
                if ((e.gui instanceof GuiIngameMenu) || ((e.gui instanceof GuiInventory) && (!OptionCore.DEFAULT_INVENTORY.getValue()))) {
                    final boolean inv = (e.gui instanceof GuiInventory);

                    if (mc.playerController.isInCreativeMode() && inv)
                        e.gui = new GuiContainerCreative(mc.thePlayer);
                    else {
                        e.gui = new IngameMenuGUI((inv ? (GuiInventory) mc.currentScreen : null));
                    }
                }
                if ((e.gui instanceof GuiGameOver) && (!OptionCore.DEFAULT_DEATH_SCREEN.getValue())) {
                    if (mc.ingameGUI instanceof IngameGUI) {
                        e.gui = new DeathScreen();
                    }
                }
            }
            else e.setCanceled(true);
        }
    }

    static void renderPlayer(RenderPlayerEvent.Post e) {
        if (!OptionCore.UI_ONLY.getValue()) {
            if (e.entityPlayer != null) {
                StaticRenderer.render(e.renderer.getRenderManager(), e.entityPlayer, e.entityPlayer.posX, e.entityPlayer.posY, e.entityPlayer.posZ);
            }
        }
    }

    static void renderEntity(RenderLivingEvent.Post e) {
        if (!OptionCore.UI_ONLY.getValue()) {
            if (e.entity != mc.thePlayer) {
                StaticRenderer.render(e.renderer.getRenderManager(), e.entity, e.x, e.y, e.z);
            }
        }
    }

    static void mainMenuGUI(GuiOpenEvent e){
        if (menuGUI)
            if (e.gui instanceof GuiMainMenu)
                if (StartupGUI.shouldShow()) {
                    e.gui = new StartupGUI();
                    menuGUI = false;
                }
                else if (e.gui instanceof GuiMainMenu)
                    e.gui = new MainMenuGUI();
    }

}
