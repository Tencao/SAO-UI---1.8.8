package com.bluexin.saoui.screens.buttons;

import com.bluexin.saoui.screens.menu.Categories;
import com.bluexin.saoui.util.IconCore;
import com.bluexin.saoui.screens.ParentElement;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ButtonState extends ButtonGUI {

    private final StateHandler state;

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IconCore saoIcon, StateHandler handler) {
        super(gui, saoID, xPos, yPos, w, h, string, saoIcon);
        state = handler;
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IconCore saoIcon, StateHandler handler) {
        this(gui, saoID, xPos, yPos, w, 20, string, saoIcon, handler);
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore saoIcon, StateHandler handler) {
        this(gui, saoID, xPos, yPos, 100, string, saoIcon, handler);
    }

    @Override
    public void update(Minecraft mc) {
        if (state != null) enabled = state.isStateEnabled(mc, this);

        super.update(mc);
    }

}
