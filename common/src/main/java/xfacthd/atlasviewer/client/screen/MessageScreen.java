package xfacthd.atlasviewer.client.screen;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;
import xfacthd.atlasviewer.client.screen.stacking.IStackedScreen;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.platform.Services;

import java.util.ArrayList;
import java.util.List;

public final class MessageScreen extends AtlasViewerScreen implements IStackedScreen {
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

    public static MessageScreen info(List<Component> message) {
        return new MessageScreen(INFO_TITLE, message);
    }

    public static MessageScreen error(List<Component> message) {
        return new MessageScreen(ERROR_TITLE, message);
    }

    public MessageScreen(Component title, List<Component> messages) {
        super(title);
        this.messages = messages;
    }

    @Override
    protected void init() {
        textBlocks.clear();

        imageHeight = BASE_HEIGHT;
        for (Component msg : messages) {
            imageHeight += ClientUtils.getWrappedHeight(font, msg, TEXT_WIDTH);
            imageHeight += font.lineHeight;

            textBlocks.add(font.split(msg, TEXT_WIDTH));
        }
        imageHeight -= font.lineHeight;

        leftPos = (this.width - WIDTH) / 2;
        topPos = (this.height - imageHeight) / 2;

        addRenderableWidget(Button.builder(TITLE_BTN_OK, _ -> onClose())
                .pos(leftPos + (WIDTH / 2) - 30, topPos + imageHeight - 30)
                .size(60, 20)
                .build()
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractBlurredBackground(graphics);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AtlasScreen.BACKGROUND_LOC, leftPos, topPos, WIDTH, imageHeight);

        graphics.text(font, title, leftPos + TITLE_X, topPos + TITLE_Y, 0xFF404040, false);

        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks) {
            for (FormattedCharSequence line : block) {
                graphics.text(font, line, leftPos + TITLE_X, y, 0xFF000000, false);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        Style style = findTextLine(mouseX, mouseY);
        if (style != null) {
            graphics.componentHoverEffect(font, style, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        Style style = findTextLine((int) event.x(), (int) event.y());
        if (style != null && style.getClickEvent() != null) {
            defaultHandleClickEvent(style.getClickEvent(), minecraft, this);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private @Nullable Style findTextLine(int mouseX, int mouseY) {
        int x = leftPos + TITLE_X;
        if (mouseX < x) {
            return null;
        }

        ActiveTextCollector.ClickableStyleFinder styleFinder = new ActiveTextCollector.ClickableStyleFinder(font, mouseX, mouseY);
        int y = topPos + TITLE_Y + font.lineHeight * 2;
        for (List<FormattedCharSequence> block : textBlocks) {
            for (FormattedCharSequence line : block) {
                styleFinder.accept(x, y, line);
                y += font.lineHeight;
            }
            y += font.lineHeight;
        }
        return styleFinder.result();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Services.PLATFORM.popScreenLayer();
    }
}
