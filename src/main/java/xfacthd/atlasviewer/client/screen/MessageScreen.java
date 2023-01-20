package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import xfacthd.atlasviewer.client.util.ClientUtils;

import java.util.*;

public class MessageScreen extends Screen
{
    private static final Component INFO_TITLE = Component.translatable("atlasviewer.message.info.title");
    private static final Component ERROR_TITLE = Component.translatable("atlasviewer.message.error.title");
    private static final Component TITLE_BTN_OK = Component.translatable("atlasviewer.message.btn.ok");
    private static final int WIDTH = 176;
    private static final int BASE_HEIGHT = 64;
    private static final int TEXT_WIDTH = WIDTH - 12;
    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 6;

    private final List<Component> messages;
    private final List<List<FormattedCharSequence>> textBlocks = new ArrayList<>();
    private int leftPos;
    private int topPos;
    private int imageHeight;

    public static MessageScreen info(List<Component> message) { return new MessageScreen(INFO_TITLE, message); }

    public static MessageScreen error(List<Component> message) { return new MessageScreen(ERROR_TITLE, message); }

    public MessageScreen(Component title, List<Component> messages)
    {
        super(title);
        this.messages = messages;
    }

    @Override
    protected void init()
    {
        textBlocks.clear();

        imageHeight = BASE_HEIGHT;
        for (Component msg : messages)
        {
            imageHeight += ClientUtils.getWrappedHeight(font, msg, TEXT_WIDTH);
            imageHeight += font.lineHeight;

            textBlocks.add(font.split(msg, TEXT_WIDTH));
        }
        imageHeight -= font.lineHeight;

        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - imageHeight) / 2;

        addRenderableWidget(Button.builder(TITLE_BTN_OK, btn -> onClose())
                .pos(leftPos + (WIDTH / 2) - 30, topPos + imageHeight - 30)
                .size(60, 20)
                .build()
        );
    }

    @Override
    public void render(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(pstack);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(this, pstack, leftPos, topPos, WIDTH, imageHeight, AtlasScreen.BACKGROUND);
        font.draw(pstack, title, leftPos + TITLE_X, topPos + TITLE_Y, 0x404040);

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks)
        {
            for (FormattedCharSequence line : block)
            {
                font.draw(pstack, line, leftPos + TITLE_X, y, 0);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }

        Style style = findTextLine(mouseX, mouseY);
        if (style != null)
        {
            renderComponentHoverEffect(pstack, style, mouseX, mouseY);
        }

        super.render(pstack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        Style style = findTextLine((int) mouseX, (int) mouseY);
        if (style != null && handleComponentClicked(style))
        {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Style findTextLine(int mouseX, int mouseY)
    {
        int localX = mouseX - leftPos - TITLE_X;
        if (localX < 0) { return null; }

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks)
        {
            int height = block.size() * font.lineHeight;
            if (mouseY >= y && mouseY <= y + height)
            {
                int idx = (mouseY - y) / font.lineHeight;
                if (idx >= block.size()) { return null; }
                return font.getSplitter().componentStyleAtWidth(block.get(idx), localX);
            }

            y += height + font.lineHeight;
        }
        return null;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}