package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlas;
import xfacthd.atlasviewer.client.screen.widget.AtlasLoadTable;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.util.ClientUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class AtlasInfoScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.atlasinfo");
    private static final Component MSG_HW_DEPEND = Component.translatable("msg.atlasviewer.atlas_hw_dependent");
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.atlas_name");
    private static final Component LABEL_WIDTH = Component.translatable("label.atlasviewer.atlas_width");
    private static final Component LABEL_HEIGHT = Component.translatable("label.atlasviewer.atlas_height");
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final Component LABEL_MAX_SIZE = Component.translatable(
            "label.atlasviewer.atlas_max_size",
            Component.literal("\u26A0").withStyle(s -> s.withColor(0xFF7700))
    );
    private static final Component LABEL_MIP_LEVELS = Component.translatable("label.atlasviewer.atlas_mip_levels");
    private static final Component LABEL_SPRITES = Component.translatable("label.atlasviewer.atlas_sprite_count");
    private static final Component LABEL_PERCENT_FILLED = Component.translatable("label.atlasviewer.atlas_percent_filled");
    private static final Component[] LABELS = { LABEL_NAME, LABEL_WIDTH, LABEL_HEIGHT, LABEL_MAX_SIZE, LABEL_MIP_LEVELS, LABEL_SPRITES, LABEL_PERCENT_FILLED };
    private static final int WIDTH = 400;
    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = 12;
    private static final int TITLE_Y = PADDING * 2;
    private static final int TEXT_X = PADDING * 2;
    private static final int FIRST_LINE_Y = TITLE_Y + (PADDING * 4);
    private static final int TABLE_WIDTH = WIDTH - (PADDING * 4);
    private static final int HEIGHT = FIRST_LINE_Y + (LINE_HEIGHT * 8) + AtlasLoadTable.TABLE_HEIGHT + (PADDING * 2);
    private static final int CLOSE_SIZE = 12;

    private final AtlasInfo atlasInfo;
    private int xLeft;
    private int yTop;
    private int labelLen = 0;
    private int valueX;
    private int tableTitleY;

    public AtlasInfoScreen(AtlasInfo atlasInfo)
    {
        super(TITLE);
        this.atlasInfo = atlasInfo;
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);

        for (Component label : LABELS)
        {
            labelLen = Math.max(labelLen, font.width(label));
        }
        valueX = TEXT_X + labelLen + PADDING;

        tableTitleY = yTop + FIRST_LINE_Y + (LINE_HEIGHT * 7);
        addRenderableWidget(new AtlasLoadTable(xLeft + TEXT_X, tableTitleY + LINE_HEIGHT, TABLE_WIDTH, atlasInfo));

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), xLeft, yTop, 0, WIDTH, HEIGHT, AtlasScreen.BACKGROUND);

        graphics.drawString(font, title, xLeft + TEXT_X, yTop + (PADDING * 2), 0x404040, false);

        graphics.drawString(font, LABEL_NAME, xLeft + TEXT_X, yTop + FIRST_LINE_Y, 0x404040, false);
        graphics.drawString(font, LABEL_WIDTH, xLeft + TEXT_X, yTop + FIRST_LINE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, LABEL_HEIGHT, xLeft + TEXT_X, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        graphics.drawString(font, LABEL_MAX_SIZE, xLeft + TEXT_X, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        graphics.drawString(font, LABEL_MIP_LEVELS, xLeft + TEXT_X, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 4), 0x404040, false);
        graphics.drawString(font, LABEL_SPRITES, xLeft + TEXT_X, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 5), 0x404040, false);
        graphics.drawString(font, LABEL_PERCENT_FILLED, xLeft + TEXT_X, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 6), 0x404040, false);

        graphics.drawString(font, atlasInfo.name, xLeft + valueX, yTop + FIRST_LINE_Y, 0x404040, false);
        graphics.drawString(font, atlasInfo.width + "px", xLeft + valueX, yTop + FIRST_LINE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, atlasInfo.height + "px", xLeft + valueX, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        String maxSize = atlasInfo.maxSize + "px x " + atlasInfo.maxSize + "px";
        graphics.drawString(font, maxSize, xLeft + valueX, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        String mipLevels = Integer.toString(atlasInfo.mipLevels);
        graphics.drawString(font, mipLevels, xLeft + valueX, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 4), 0x404040, false);
        String spriteCount = Integer.toString(atlasInfo.spriteCount);
        graphics.drawString(font, spriteCount, xLeft + valueX, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 5), 0x404040, false);
        String percentFilled = "%.1f %%".formatted(atlasInfo.percentFilled * 100F);
        graphics.drawString(font, percentFilled, xLeft + valueX, yTop + FIRST_LINE_Y + (LINE_HEIGHT * 6), 0x404040, false);

        int nsCount = atlasInfo.fillStats.size();
        Component tableHeader = Component.translatable("label.atlasviewre.atlas_percent_filled_by_ns", nsCount);
        graphics.drawString(font, tableHeader, xLeft + TEXT_X, tableTitleY, 0x404040, false);

        int len = font.width(LABEL_MAX_SIZE);
        int minY = yTop + FIRST_LINE_Y + (LINE_HEIGHT * 3);
        if (mouseX >= xLeft + TEXT_X && mouseX < xLeft + TEXT_X + len && mouseY >= minY && mouseY <= minY + font.lineHeight)
        {
            setTooltipForNextRenderPass(MSG_HW_DEPEND);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }



    public static AtlasInfo computeInfo(TextureAtlas atlas, Collection<TextureAtlasSprite> sprites)
    {
        Map<String, Integer> areaByNamespace = sprites.stream()
                .map(TextureAtlasSprite::contents)
                .map(c -> ObjectIntPair.of(c.name().getNamespace(), c.width() * c.height()))
                .collect(Collectors.groupingBy(ObjectIntPair::first, Collectors.summingInt(ObjectIntPair::rightInt)));

        Object2IntMap<String> countByNamespace = new Object2IntLinkedOpenHashMap<>();
        sprites.stream()
                .map(TextureAtlasSprite::contents)
                .map(SpriteContents::name)
                .map(ResourceLocation::getNamespace)
                .forEach(s -> countByNamespace.computeInt(s, (ns, count) -> (count != null ? count : 0) + 1));

        int width = ((AccessorTextureAtlas) atlas).getWidth();
        int height = ((AccessorTextureAtlas) atlas).getHeight();
        int area = width * height;
        int areaFilled = areaByNamespace.values().stream().mapToInt(Integer::intValue).sum();
        float filled = (float) areaFilled / (float) area;

        List<FillStat> fillStats = new ArrayList<>();
        areaByNamespace.forEach((namespace, value) ->
        {
            float namespaceArea = value;
            float percentOfTotal = namespaceArea / (float) area;
            float percentOfFilled = namespaceArea / (float) areaFilled;
            fillStats.add(new FillStat(
                    namespace, countByNamespace.getInt(namespace), percentOfTotal, percentOfFilled
            ));
        });

        return new AtlasInfo(
                atlas.location().toString(),
                atlas.maxSupportedTextureSize(),
                width,
                height,
                ((AccessorTextureAtlas) atlas).getMipLevel(),
                sprites.size(),
                filled,
                fillStats
        );
    }

    public record AtlasInfo(
            String name,
            int maxSize,
            int width,
            int height,
            int mipLevels,
            int spriteCount,
            float percentFilled,
            List<FillStat> fillStats
    ) { }

    public record FillStat(String namespace, int count, float percentOfTotal, float percentOfFilled) { }
}
