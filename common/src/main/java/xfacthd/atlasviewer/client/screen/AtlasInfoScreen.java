package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlas;
import xfacthd.atlasviewer.client.screen.stacking.IStackedScreen;
import xfacthd.atlasviewer.client.screen.widget.AtlasLoadTable;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.IMipAwareTextureAtlas;
import xfacthd.atlasviewer.platform.Services;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class AtlasInfoScreen extends Screen implements IStackedScreen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.atlasinfo");
    private static final Component MSG_HW_DEPEND = Component.translatable("msg.atlasviewer.atlas_hw_dependent");
    private static final Component MSG_SPRITES_BY_MAX_MIP = Component.translatable("msg.atlasviewer.atlas_sprites_by_max_mip");
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final Component CHAR_ARROW = Component.literal("\u21B3").withStyle(s ->
            s.withFont(new ResourceLocation("atlasviewer:arrow"))
    );
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.atlas_name");
    private static final Component LABEL_SIZE = Component.translatable("label.atlasviewer.atlas_size");
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final Component LABEL_MAX_SIZE = Component.translatable(
            "label.atlasviewer.atlas_max_size",
            Component.literal("\u26A0").withStyle(s -> s.withColor(0xFF7700))
    );
    private static final Component LABEL_MIP_LEVELS = Component.translatable("label.atlasviewer.atlas_mip_levels");
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
    private final Component spriteCountText;
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
        for (Label label : LABELS)
        {
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
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), xLeft, yTop, 0, WIDTH, imageHeight, AtlasScreen.BACKGROUND);

        graphics.drawString(font, title, xLeft + TEXT_X, yTop + (PADDING * 2), 0x404040, false);

        int y = yTop + FIRST_LINE_Y;
        y = drawLine(graphics, LABEL_NAME, Component.literal(atlasInfo.name), y);
        y = drawLine(graphics, LABEL_SIZE, atlasSizeText, y);
        y = drawLine(graphics, LABEL_MAX_SIZE, atlasMaxSizeText, y);
        if (atlasInfo.mipped)
        {
            y = drawLine(graphics, LABEL_MIP_LEVELS, atlasMipLevelText, y);
        }
        y = drawLine(graphics, LABEL_SPRITES, spriteCountText, y);
        if (atlasInfo.mipped)
        {
            y = drawLine(graphics, LABEL_SPRITES_BY_MAX_MIP, countsByMip, y);
        }
        y = drawLine(graphics, LABEL_PERCENT_FILLED, percentFilledText, y);

        graphics.drawString(font, tableHeader, xLeft + TEXT_X, tableTitleY, 0x404040, false);

        int len = font.width(LABEL_MAX_SIZE);
        int minY = yTop + FIRST_LINE_Y + (LINE_HEIGHT * 2);
        if (mouseX >= xLeft + TEXT_X && mouseX < xLeft + TEXT_X + len && mouseY >= minY && mouseY <= minY + font.lineHeight)
        {
            setTooltipForNextRenderPass(MSG_HW_DEPEND);
        }
        if (atlasInfo.mipped)
        {
            len = font.width(LABEL_SPRITES_BY_MAX_MIP);
            minY = yTop + FIRST_LINE_Y + (LINE_HEIGHT * 5);
            if (mouseX >= xLeft + TEXT_X && mouseX < xLeft + TEXT_X + len && mouseY >= minY && mouseY <= minY + font.lineHeight)
            {
                setTooltipForNextRenderPass(MSG_SPRITES_BY_MAX_MIP);
            }
        }
    }

    private int drawLine(GuiGraphics graphics, Component label, Component value, int y)
    {
        graphics.drawString(font, label, xLeft + TEXT_X, y, 0x404040, false);
        graphics.drawString(font, value, xLeft + valueX, y, 0x404040, false);
        return y + LINE_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && (mouseX < xLeft || mouseY < yTop || mouseX > (xLeft + WIDTH) || mouseY > (yTop + imageHeight)))
        {
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose()
    {
        Services.PLATFORM.popScreenLayer();
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

        int[] spritesByMaxMip = new int[5];
        sprites.forEach(sprite ->
        {
            SpriteContents contents = sprite.contents();
            int lowestOne = Math.min(Integer.lowestOneBit(contents.width()), Integer.lowestOneBit(contents.height()));
            int maxLevel = Math.min(Mth.log2(lowestOne), 4);
            spritesByMaxMip[maxLevel]++;
        });

        int width = ((AccessorTextureAtlas) atlas).atlasviewer$getWidth();
        int height = ((AccessorTextureAtlas) atlas).atlasviewer$getHeight();
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
                ((IMipAwareTextureAtlas) atlas).atlasviewer$isMipMapEnabled(),
                atlas.maxSupportedTextureSize(),
                width,
                height,
                ((AccessorTextureAtlas) atlas).atlasviewer$getMipLevel(),
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
            int spriteCount,
            int[] spriteCountByMaxMipLevel,
            float percentFilled,
            List<FillStat> fillStats
    ) { }

    public record FillStat(String namespace, int count, float percentOfTotal, float percentOfFilled) { }

    private record Label(Component text, Predicate<AtlasInfoScreen> active)
    {
        public Label(Component text)
        {
            this(text, screen -> true);
        }
    }
}
