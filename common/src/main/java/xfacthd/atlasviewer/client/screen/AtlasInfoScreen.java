package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.screen.stacking.IStackedScreen;
import xfacthd.atlasviewer.client.screen.widget.AtlasLoadTable;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.platform.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class AtlasInfoScreen extends AtlasViewerScreen implements IStackedScreen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.atlasinfo");
    private static final Component MSG_HW_DEPEND = Component.translatable("msg.atlasviewer.atlas_hw_dependent");
    private static final Component MSG_SPRITES_BY_MAX_MIP = Component.translatable("msg.atlasviewer.atlas_sprites_by_max_mip");
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final Component CHAR_ARROW = Component.literal("\u21B3").withStyle(s ->
            s.withFont(new FontDescription.Resource(AtlasViewer.rl("arrow")))
    );
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.atlas_name");
    private static final Component LABEL_SIZE = Component.translatable("label.atlasviewer.atlas_size");
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final Component LABEL_MAX_SIZE = Component.translatable(
            "label.atlasviewer.atlas_max_size",
            Component.literal("\u26A0").withStyle(s -> s.withColor(0xFF7700))
    );
    private static final Component LABEL_MIP_LEVELS = Component.translatable("label.atlasviewer.atlas_mip_levels");
    private static final Component LABEL_FILTER_MODE = Component.translatable("label.atlasviewer.atlas_filter_mode");
    private static final Component LABEL_ANISO_LEVELS = Component.translatable("label.atlasviewer.atlas_aniso_levels");
    private static final Component LABEL_SPRITES = Component.translatable("label.atlasviewer.atlas_sprite_count");
    private static final Component LABEL_SPRITES_BY_MAX_MIP = Component.translatable(
            "label.atlasviewer.atlas_sprite_count_by_max_mip",
            CHAR_ARROW,
            Component.literal("i").withStyle(ChatFormatting.BLUE)
    );
    private static final Component LABEL_PERCENT_FILLED = Component.translatable("label.atlasviewer.atlas_percent_filled");
    private static final Label[] LABELS = {
            new Label(LABEL_NAME),
            new Label(LABEL_SIZE),
            new Label(LABEL_MAX_SIZE),
            new Label(LABEL_MIP_LEVELS, screen -> screen.atlasInfo.mipped),
            new Label(LABEL_FILTER_MODE, screen -> screen.atlasInfo.mipped),
            new Label(LABEL_ANISO_LEVELS, screen -> screen.atlasInfo.isUsingAF()),
            new Label(LABEL_SPRITES),
            new Label(LABEL_SPRITES_BY_MAX_MIP, screen -> screen.atlasInfo.mipped),
            new Label(LABEL_PERCENT_FILLED)
    };
    private static final int WIDTH = 400;
    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = 12;
    private static final int TITLE_Y = PADDING * 2;
    private static final int TEXT_X = PADDING * 2;
    private static final int FIRST_LINE_Y = TITLE_Y + (PADDING * 4);
    private static final int TABLE_WIDTH = WIDTH - (PADDING * 4);
    private static final int CLOSE_SIZE = 12;

    private final AtlasInfo atlasInfo;
    private final Component atlasSizeText;
    private final Component atlasMaxSizeText;
    private final Component atlasMipLevelText;
    private final Component filterModeText;
    @Nullable
    private final Component anisoLevelsText;
    private final Component spriteCountText;
    @Nullable
    private final Component countsByMip;
    private final Component percentFilledText;
    private final Component tableHeader;
    private int imageHeight;
    private int xLeft;
    private int yTop;
    private int valueX;
    private int tableTitleY;

    public AtlasInfoScreen(AtlasInfo atlasInfo)
    {
        super(TITLE);
        this.atlasInfo = atlasInfo;
        this.atlasSizeText = Component.translatable("value.atlasviewer.size", atlasInfo.width, atlasInfo.height);
        this.atlasMaxSizeText = Component.translatable("value.atlasviewer.size", atlasInfo.maxSize, atlasInfo.maxSize);
        this.atlasMipLevelText = Component.literal(Integer.toString(atlasInfo.mipLevels));
        this.filterModeText = atlasInfo.filterMode.caption();
        this.anisoLevelsText = atlasInfo.isUsingAF() ? Component.literal(Integer.toString(atlasInfo.anisoLevels)) : null;
        this.spriteCountText = Component.literal(Integer.toString(atlasInfo.spriteCount));
        this.countsByMip = atlasInfo.mipped ? Component.translatable(
                "value.atlasviewer.atlas_sprites_by_max_mip",
                Arrays.stream(atlasInfo.spriteCountByMaxMipLevel)
                        .mapToObj(String::valueOf)
                        .map(v -> Component.literal(v).withStyle(Style.EMPTY.withColor(0x666666)))
                        .toArray()
        ) : null;
        this.percentFilledText = Component.literal("%.1f %%".formatted(atlasInfo.percentFilled * 100F));
        int nsCount = atlasInfo.fillStats.size();
        if (nsCount == 1)
        {
            this.tableHeader = Component.translatable("label.atlasviewer.atlas_percent_filled_by_ns_single", CHAR_ARROW);
        }
        else
        {
            this.tableHeader = Component.translatable("label.atlasviewer.atlas_percent_filled_by_ns", CHAR_ARROW, nsCount);
        }
    }

    @Override
    protected void init()
    {
        int labelLen = 0;
        int labelHeight = 0;
        //noinspection ForLoopReplaceableByForEach Using for-each breaks IDEA's static analysis
        for (int i = 0; i < LABELS.length; i++)
        {
            Label label = LABELS[i];
            if (!label.active.test(this)) continue;
            labelLen = Math.max(labelLen, font.width(label.text));
            labelHeight += LINE_HEIGHT;
        }
        valueX = TEXT_X + labelLen + PADDING;

        imageHeight = FIRST_LINE_Y + labelHeight + LINE_HEIGHT + AtlasLoadTable.TABLE_HEIGHT + PADDING * 2;
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (imageHeight / 2);

        tableTitleY = yTop + FIRST_LINE_Y + labelHeight;
        addRenderableWidget(new AtlasLoadTable(xLeft + TEXT_X, tableTitleY + LINE_HEIGHT, TABLE_WIDTH, atlasInfo));

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        extractBlurredBackground(graphics);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AtlasScreen.BACKGROUND_LOC, xLeft, yTop, WIDTH, imageHeight);

        graphics.text(font, title, xLeft + TEXT_X, yTop + (PADDING * 2), 0xFF404040, false);

        int y = yTop + FIRST_LINE_Y;
        y = drawLine(graphics, LABEL_NAME, Component.literal(atlasInfo.name), y);
        y = drawLine(graphics, LABEL_SIZE, atlasSizeText, y);
        int maxSizeY = y;
        y = drawLine(graphics, LABEL_MAX_SIZE, atlasMaxSizeText, y);
        if (atlasInfo.mipped)
        {
            y = drawLine(graphics, LABEL_MIP_LEVELS, atlasMipLevelText, y);
            y = drawLine(graphics, LABEL_FILTER_MODE, filterModeText, y);
            if (atlasInfo.isUsingAF())
            {
                y = drawLine(graphics, LABEL_ANISO_LEVELS, Objects.requireNonNull(anisoLevelsText), y);
            }
        }
        y = drawLine(graphics, LABEL_SPRITES, spriteCountText, y);
        int countByMipY = 0;
        if (atlasInfo.mipped)
        {
            countByMipY = y;
            y = drawLine(graphics, LABEL_SPRITES_BY_MAX_MIP, Objects.requireNonNull(countsByMip), y);
        }
        y = drawLine(graphics, LABEL_PERCENT_FILLED, percentFilledText, y);

        graphics.text(font, tableHeader, xLeft + TEXT_X, tableTitleY, 0xFF404040, false);

        int len = font.width(LABEL_MAX_SIZE);
        if (mouseX >= xLeft + TEXT_X && mouseX < xLeft + TEXT_X + len && mouseY >= maxSizeY && mouseY <= maxSizeY + font.lineHeight)
        {
            setTooltipForNextFrame(graphics, MSG_HW_DEPEND, mouseX, mouseY);
        }
        if (atlasInfo.mipped)
        {
            len = font.width(LABEL_SPRITES_BY_MAX_MIP);
            if (mouseX >= xLeft + TEXT_X && mouseX < xLeft + TEXT_X + len && mouseY >= countByMipY && mouseY <= countByMipY + font.lineHeight)
            {
                setTooltipForNextFrame(graphics, MSG_SPRITES_BY_MAX_MIP, mouseX, mouseY);
            }
        }
    }

    private int drawLine(GuiGraphicsExtractor graphics, Component label, Component value, int y)
    {
        graphics.text(font, label, xLeft + TEXT_X, y, 0xFF404040, false);
        graphics.text(font, value, xLeft + valueX, y, 0xFF404040, false);
        return y + LINE_HEIGHT;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && (event.x() < xLeft || event.y() < yTop || event.x() > (xLeft + WIDTH) || event.y() > (yTop + imageHeight)))
        {
            onClose();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void onClose()
    {
        Services.PLATFORM.popScreenLayer();
    }

    public static AtlasInfo computeInfo(AtlasManager.AtlasEntry atlasEntry, Collection<TextureAtlasSprite> sprites)
    {
        Map<String, Integer> areaByNamespace = sprites.stream()
                .map(TextureAtlasSprite::contents)
                .map(c -> ObjectIntPair.of(c.name().getNamespace(), c.width() * c.height()))
                .collect(Collectors.groupingBy(ObjectIntPair::first, Collectors.summingInt(ObjectIntPair::rightInt)));

        Object2IntMap<String> countByNamespace = new Object2IntLinkedOpenHashMap<>();
        sprites.stream()
                .map(TextureAtlasSprite::contents)
                .map(SpriteContents::name)
                .map(Identifier::getNamespace)
                .forEach(s -> countByNamespace.computeInt(s, (_, count) -> (count != null ? count : 0) + 1));

        int[] spritesByMaxMip = new int[5];
        sprites.forEach(sprite ->
        {
            SpriteContents contents = sprite.contents();
            int lowestOne = Math.min(Integer.lowestOneBit(contents.width()), Integer.lowestOneBit(contents.height()));
            int maxLevel = Math.min(Mth.log2(lowestOne), 4);
            spritesByMaxMip[maxLevel]++;
        });

        TextureAtlas atlas = atlasEntry.atlas();
        int width = atlas.getWidth();
        int height = atlas.getHeight();
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

        Options options = Minecraft.getInstance().options;
        int mipLevels = atlas.atlasviewer$getMipLevel();
        TextureFilteringMethod filterMode = mipLevels > 0 ? options.textureFiltering().get() : TextureFilteringMethod.NONE;
        int anisoLevels = filterMode == TextureFilteringMethod.ANISOTROPIC ? options.maxAnisotropyValue() : -1;

        return new AtlasInfo(
                atlasEntry.config().textureId().toString(),
                atlasEntry.config().createMipmaps(),
                atlas.maxSupportedTextureSize(),
                width,
                height,
                mipLevels,
                filterMode,
                anisoLevels,
                sprites.size(),
                spritesByMaxMip,
                filled,
                fillStats
        );
    }

    public record AtlasInfo(
            String name,
            boolean mipped,
            int maxSize,
            int width,
            int height,
            int mipLevels,
            TextureFilteringMethod filterMode,
            int anisoLevels,
            int spriteCount,
            int[] spriteCountByMaxMipLevel,
            float percentFilled,
            List<FillStat> fillStats
    )
    {
        public boolean isUsingAF()
        {
            return filterMode == TextureFilteringMethod.ANISOTROPIC;
        }
    }

    public record FillStat(String namespace, int count, float percentOfTotal, float percentOfFilled) { }

    private record Label(Component text, Predicate<AtlasInfoScreen> active)
    {
        public Label(Component text)
        {
            this(text, _ -> true);
        }
    }
}
