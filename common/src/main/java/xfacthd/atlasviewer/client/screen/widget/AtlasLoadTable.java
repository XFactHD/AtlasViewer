package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.client.screen.AtlasInfoScreen;

import java.util.Comparator;
import java.util.List;

public final class AtlasLoadTable extends AbstractWidget
{
    private static final ResourceLocation ARROW_UP = new ResourceLocation("minecraft", "transferable_list/move_up");
    private static final ResourceLocation ARROW_DOWN = new ResourceLocation("minecraft", "transferable_list/move_down");
    // The arrow sprites have whitespace around the content, coordinates need to be offset accordingly
    private static final float ARROW_SCALE = .5F;
    private static final float ARROW_UP_OFF_X = 18F * ARROW_SCALE;
    private static final float ARROW_UP_OFF_Y = 5F * ARROW_SCALE;
    private static final float ARROW_DOWN_OFF_X = 18F * ARROW_SCALE;
    private static final float ARROW_DOWN_OFF_Y = 20F * ARROW_SCALE;
    private static final Component HEADER_NAMESPACE = Component.translatable("header.atlasviewer.atlasload.namespace");
    private static final Component HEADER_COUNT = Component.translatable("header.atlasviewer.atlasload.count");
    private static final Component HEADER_OF_TOTAL = Component.translatable("header.atlasviewer.atlasload.of_total");
    private static final Component HEADER_OF_FILLED = Component.translatable("header.atlasviewer.atlasload.of_filled");
    private static final Component[] HEADERS = new Component[] { HEADER_NAMESPACE, HEADER_COUNT, HEADER_OF_TOTAL, HEADER_OF_FILLED };
    private static final Comparator<AtlasInfoScreen.FillStat> DEFAULT_STAT_COMPARATOR = Comparator.comparing(AtlasInfoScreen.FillStat::namespace);
    public static final int TABLE_HEIGHT = 5 * (9 + 2) + 1;
    private static final int SCROLL_BAR_WIDTH = 4;
    private static final int SORT_ARROW_WIDTH = 8;

    private final AtlasInfoScreen.AtlasInfo atlasInfo;
    private final int nsCount;
    private final int contentWidth;
    private final int[] colWidth = new int[4];
    private final int[] colTextX = new int[4];
    private final int[] colArrowX = new int[4];
    private final boolean scrollBar;
    private final int entryHeight;
    private final int scrollBarX;
    private final int scrollBarHeight;
    private final float scrollFactor;
    private final Font font = Minecraft.getInstance().font;
    private int scrollOffset = 0;
    private SortTarget sorting = SortTarget.NAMESPACE;
    private boolean reverse = false;

    public AtlasLoadTable(int x, int y, int width, AtlasInfoScreen.AtlasInfo atlasInfo)
    {
        super(x, y, width, TABLE_HEIGHT, Component.empty());
        this.atlasInfo = atlasInfo;
        this.nsCount = atlasInfo.fillStats().size();
        this.scrollBar = atlasInfo.fillStats().size() > 4;
        this.contentWidth = width - 2 - (scrollBar ? (SCROLL_BAR_WIDTH + 1) : 0);
        this.entryHeight = font.lineHeight + 2;
        this.scrollBarX = scrollBar ? (x + width - 1 - SCROLL_BAR_WIDTH) : 0;
        this.scrollBarHeight = scrollBar ? ((int) Math.max((height - 1 - entryHeight) * (4F / (float) nsCount), 3)) : 0;
        this.scrollFactor = scrollBar ? (((entryHeight * 4) - scrollBarHeight - 1) / (float) (nsCount - 4)) : 0F;

        colWidth[1] = font.width(HEADER_COUNT) + SORT_ARROW_WIDTH + 3;
        colWidth[2] = font.width(HEADER_OF_TOTAL) + SORT_ARROW_WIDTH + 3;
        colWidth[3] = font.width(HEADER_OF_FILLED) + SORT_ARROW_WIDTH + 3;
        colWidth[0] = contentWidth - (colWidth[1] + colWidth[2] + colWidth[3]) - 3;

        int tx = x + 3;
        for (int i = 0; i < 4; i++)
        {
            colTextX[i] = tx;
            colArrowX[i] = tx + font.width(HEADERS[i]) + 1;
            tx += colWidth[i] + 1;
        }

        sorting.sort(atlasInfo.fillStats(), false);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.renderOutline(getX(), getY(), width, height, 0xFF000000);

        int x = getX() + 1;
        int y = getY() + 1;
        for (int i = 0; i < 4; i++)
        {
            int w = colWidth[i];
            int color = (i % 2 == 0) ? 0xFF8A8A8A : 0xFFA0A0A0;
            graphics.fill(x, y, x + w, y + height - 2, color);
            if (i != 3)
            {
                graphics.vLine(x + w, getY(), getY() + height - 1, 0xFF000000);
            }
            x += w + 1;
        }
        x = getX() + 1;
        y++;
        graphics.hLine(x, x + width - 2, y + font.lineHeight, 0xFF000000);

        graphics.drawString(font, HEADER_NAMESPACE, colTextX[0], y, 0x303030, false);
        graphics.drawString(font, HEADER_COUNT, colTextX[1], y, 0x303030, false);
        graphics.drawString(font, HEADER_OF_TOTAL, colTextX[2], y, 0x303030, false);
        graphics.drawString(font, HEADER_OF_FILLED, colTextX[3], y, 0x303030, false);

        x = getX() + 3;
        y += entryHeight;
        int maxIdx = Mth.clamp(nsCount - scrollOffset, 0, 4);
        for (int i = 0; i < maxIdx; i++)
        {
            int idx = i + scrollOffset;

            int tx = colTextX[0];
            AtlasInfoScreen.FillStat stat = atlasInfo.fillStats().get(idx);
            graphics.drawString(font, stat.namespace(), tx, y, 0x303030, false);

            String count = Integer.toString(stat.count());
            tx = colTextX[1];
            int tw = font.width(count);
            graphics.drawString(font, count, tx + colWidth[1] - tw - 3, y, 0x303030, false);

            String percentOfTotal = "%.1f %%".formatted(stat.percentOfTotal() * 100F);
            tx = colTextX[2];
            tw = font.width(percentOfTotal);
            graphics.drawString(font, percentOfTotal, tx + colWidth[2] - tw - 3, y, 0x303030, false);

            String percentOfFilled = "%.1f %%".formatted(stat.percentOfFilled() * 100F);
            tx = colTextX[3];
            tw = font.width(percentOfFilled);
            graphics.drawString(font, percentOfFilled, tx + colWidth[3] - tw - 3, y, 0x303030, false);

            graphics.hLine(x - 3, x + contentWidth - 2, y + font.lineHeight, 0xFF000000);
            y += entryHeight;
        }

        if (scrollBar)
        {
            graphics.vLine(getX() + 1 + contentWidth, getY(), getY() + height - 1, 0xFF000000);

            int by = getY() + 1 + entryHeight + Math.round(scrollFactor * scrollOffset);
            by = Math.min(by, getY() + height - scrollBarHeight - 1);
            graphics.fill(scrollBarX,     by,     scrollBarX + SCROLL_BAR_WIDTH,     by + scrollBarHeight,     0xFF666666);
            graphics.fill(scrollBarX + 1, by + 1, scrollBarX + SCROLL_BAR_WIDTH - 1, by + scrollBarHeight - 1, 0xFFAAAAAA);
        }

        ResourceLocation tex = reverse ? ARROW_UP : ARROW_DOWN;
        float xOff = reverse ? ARROW_UP_OFF_X : ARROW_DOWN_OFF_X;
        float yOff = reverse ? ARROW_UP_OFF_Y : ARROW_DOWN_OFF_Y;
        graphics.pose().pushPose();
        graphics.pose().translate(colArrowX[sorting.ordinal()] - xOff, getY() + 4 - yOff, 0);
        graphics.pose().scale(ARROW_SCALE, ARROW_SCALE, 1F);
        graphics.blitSprite(tex, 0, 0, 0, 32, 32);
        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn)
    {
        if (btn == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseY >= getY() + 1 && mouseY <= getY() + font.lineHeight + 2)
        {
            SortTarget newTarget = null;
            int x = getX() + 1;
            for (int i = 0; i < 4; i++)
            {
                int w = colWidth[i];
                if (mouseX >= x && mouseX <= x + w)
                {
                    newTarget = SortTarget.VALUES[i];
                    break;
                }
                x += w + 1;
            }

            if (newTarget != null)
            {
                if (newTarget == sorting)
                {
                    reverse = !reverse;
                }
                else
                {
                    sorting = newTarget;
                    reverse = false;
                }
                sorting.sort(atlasInfo.fillStats(), reverse);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if (scrollBar && mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height)
        {
            if (deltaY < 0 && scrollOffset < atlasInfo.fillStats().size() - 4)
            {
                scrollOffset++;
            }
            else if (deltaY > 0 && scrollOffset > 0)
            {
                scrollOffset--;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) { }



    private enum SortTarget
    {
        NAMESPACE(DEFAULT_STAT_COMPARATOR),
        COUNT(Comparator.comparingInt(AtlasInfoScreen.FillStat::count)),
        PERCENT_OF_TOTAL(Comparator.comparingDouble(AtlasInfoScreen.FillStat::percentOfTotal)),
        PERCENT_OF_FILLED(Comparator.comparingDouble(AtlasInfoScreen.FillStat::percentOfFilled));

        private static final SortTarget[] VALUES = values();
        private final Comparator<AtlasInfoScreen.FillStat> statComparator;
        private final Comparator<AtlasInfoScreen.FillStat> statComparatorReverse;

        SortTarget(Comparator<AtlasInfoScreen.FillStat> statComparator)
        {
            if (statComparator != DEFAULT_STAT_COMPARATOR)
            {
                statComparator = statComparator.thenComparing(DEFAULT_STAT_COMPARATOR);
            }
            this.statComparator = statComparator;
            this.statComparatorReverse = statComparator.reversed();
        }

        public void sort(List<AtlasInfoScreen.FillStat> stats, boolean reverse)
        {
            stats.sort(reverse ? statComparatorReverse : statComparator);
        }
    }
}
