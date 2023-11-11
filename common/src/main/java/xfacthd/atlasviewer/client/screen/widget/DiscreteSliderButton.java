package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntConsumer;

public final class DiscreteSliderButton extends AbstractSliderButton
{
    private final String msgKey;
    private int maxStep;
    private int step;
    private final IntConsumer changeListener;

    public DiscreteSliderButton(
            int x, int y, int width, int height, String msgKey, int step, int maxStep, IntConsumer changeListener
    )
    {
        super(x, y, width, height, Component.translatable(msgKey, step), maxStep > 0 ? ((double) step / (double) maxStep) : 0D);
        this.msgKey = msgKey;
        this.maxStep = maxStep;
        this.step = step;
        this.changeListener = changeListener;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (!active)
        {
            int x = getX() + (int) (value * (double) (width - 8));
            graphics.fill(x + 1, getY() + 1, x + 7, getY() + 19, 0xAA404040);
        }
    }

    @Override
    protected ResourceLocation getHandleSprite()
    {
        return active ? super.getHandleSprite() : SLIDER_HANDLE_SPRITE;
    }

    @Override
    protected void updateMessage()
    {
        setMessage(Component.translatable(msgKey, step));
    }

    @Override
    protected void applyValue()
    {
        setStep((int) Math.round(value * maxStep), false);
    }

    public int getStep()
    {
        return step;
    }

    public void setStep(int step, boolean forceUpdateMessage)
    {
        int lastStep = this.step;
        this.step = step;
        value = maxStep > 0 ? ((double) step / maxStep) : 0;
        if (step != lastStep)
        {
            changeListener.accept(step);
            if (forceUpdateMessage)
            {
                updateMessage();
            }
        }
    }

    public int getMaxStep()
    {
        return maxStep;
    }

    public void setMaxStep(int maxStep)
    {
        if (maxStep != this.maxStep)
        {
            this.maxStep = maxStep;
            step = 0;
            value = 0;
            updateMessage();
        }
    }
}
