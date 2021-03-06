package me.desht.pneumaticcraft.client.gui.widget;

import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.NumberUtils;

public class WidgetTextFieldNumber extends WidgetTextField {

    public int minValue = Integer.MIN_VALUE;
    public int maxValue = Integer.MAX_VALUE;
    private int decimals;

    public WidgetTextFieldNumber(FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(fontRenderer, x, y, width, height);
        setValue(0);
    }

    public WidgetTextFieldNumber setDecimals(int decimals) {
        this.decimals = decimals;
        return this;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button) {
        boolean wasFocused = isFocused();
        super.onMouseClicked(mouseX, mouseY, button);
        if (isFocused()) {
            if (!wasFocused) { //setText("");
                setCursorPositionEnd();
                setSelectionPos(0);
            }
        } else {
            setValue(getDoubleValue());
        }
    }

    public WidgetTextFieldNumber setValue(double value) {
        setText(PneumaticCraftUtils.roundNumberTo(value, decimals));
        return this;
    }

    public int getValue() {
        return MathHelper.clamp(NumberUtils.toInt(getText()), minValue, maxValue);
    }

    public double getDoubleValue() {
        return PneumaticCraftUtils.roundNumberToDouble(MathHelper.clamp(NumberUtils.toDouble(getText()), minValue, maxValue), decimals);
    }
}
