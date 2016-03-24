package com.bluexin.saoui.screens.buttons;

import com.bluexin.saoui.screens.menu.Categories;
import com.bluexin.saoui.util.IconCore;
import com.bluexin.saoui.util.OptionCore;
import com.bluexin.saoui.screens.ParentElement;

import java.util.stream.Stream;

/**
 * Part of SAOUI
 *
 * @author Bluexin
 */
public class OptionButton extends ButtonGUI {
    private final OptionCore option;

    public OptionButton(ParentElement gui, int xPos, int yPos, OptionCore option) {
        super(gui, option.isCategory ? Categories.OPT_CAT : Categories.OPTION, xPos, yPos, option.toString(), IconCore.OPTION);
        this.highlight = option.getValue();
        this.option = option;
    }

    public OptionCore getOption() {
        return this.option;
    }

    public void action() {
        if (this.option.isRestricted()){
            if (!option.getValue()) {
                OptionCore category = option.getCategory();
                Stream.of(OptionCore.values()).filter(opt -> opt.category == category).filter(opt -> opt.getValue()).forEachOrdered(saoOption -> saoOption.flip());
                this.highlight = this.option.flip();
            }
        }
        else this.highlight = this.option.flip();
    }
}
