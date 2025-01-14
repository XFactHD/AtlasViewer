package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.*;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.api.SourceAwareness;
import xfacthd.atlasviewer.client.screen.stacking.IStackedScreen;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.screen.widget.DiscreteSliderButton;
import xfacthd.atlasviewer.client.util.*;
import xfacthd.atlasviewer.platform.Services;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SpriteInfoScreen extends Screen implements IStackedScreen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.spriteinfo");
    private static final Component CHAR_INFO = Component.literal("i").withStyle(ChatFormatting.BLUE);
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.spriteinfo.name");
    private static final Component LABEL_SIZE = Component.translatable("label.atlasviewer.spriteinfo.size");
    private static final Component LABEL_SOURCEPACK = Component.translatable("label.atlasviewer.spriteinfo.sourcepack");
    private static final Component LABEL_READERPACK = Component.translatable(
            "label.atlasviewer.spriteinfo.readerpack", CHAR_INFO
    );
    private static final Component LABEL_READERTYPE = Component.translatable("label.atlasviewer.spriteinfo.readertype");
    private static final Component LABEL_MAX_MIP_LEVEL = Component.translatable(
            "label.atlasviewer.spriteinfo.max_mip_level", CHAR_INFO
    );
    private static final Component LABEL_ANIMATED = Component.translatable("label.atlasviewer.spriteinfo.animated");
    private static final Component LABEL_FRAMECOUNT = Component.translatable("label.atlasviewer.spriteinfo.frames");
    private static final Component LABEL_INTERPOLATED = Component.translatable("label.atlasviewer.spriteinfo.interpolated");
    private static final Component LABEL_FRAMETIME = Component.translatable("label.atlasviewer.spriteinfo.frametime");
    private static final Component LABEL_GUI_SPRITE_TYPE = Component.translatable("label.atlasviewer.spriteinfo.gui_type");
    private static final Component LABEL_GUI_SPRITE_SIZE = Component.translatable("label.atlasviewer.spriteinfo.gui_size");
    private static final Component LABEL_GUI_SPRITE_NINESLICE_BORDER = Component.translatable("label.atlasviewer.spriteinfo.gui_nineslice_border");
    private static final List<Label> LABELS = List.of(
            new Label(LABEL_NAME),
            new Label(LABEL_SIZE),
            new Label(LABEL_SOURCEPACK),
            new Label(LABEL_READERPACK),
            new Label(LABEL_READERTYPE),
            new Label(LABEL_MAX_MIP_LEVEL, screen -> screen.mipped),
            new Label(LABEL_ANIMATED),
            new Label(LABEL_FRAMECOUNT, screen -> screen.animated),
            new Label(LABEL_INTERPOLATED, screen -> screen.animated),
            new Label(LABEL_FRAMETIME, screen -> screen.animated),
            new Label(LABEL_GUI_SPRITE_TYPE, screen -> screen.guiSprite),
            new Label(LABEL_GUI_SPRITE_SIZE, screen -> screen.guiSprite && screen.guiScaling.type() != GuiSpriteScaling.Type.STRETCH),
            new Label(LABEL_GUI_SPRITE_NINESLICE_BORDER, screen -> screen.guiSprite && screen.guiScaling.type() == GuiSpriteScaling.Type.NINE_SLICE)
    );
    private static final Component VALUE_TRUE = Component.translatable("value.atlasviewer.true").withStyle(Style.EMPTY.withColor(0x00D000));
    private static final Component VALUE_FALSE = Component.translatable("value.atlasviewer.false").withStyle(Style.EMPTY.withColor(0xD00000));
    private static final Component VALUE_FRAMETIME_MIXED = Component.translatable("value.atlasviewer.frametime_mixed");
    private static final Component VALUE_UNKNOWN_PACK = Component.translatable("value.atlasviewer.unknown_pack").withStyle(s -> s.withColor(0xD00000));
    private static final Component TITLE_EXPORT = Component.translatable("btn.atlasviewer.export_sprite");
    private static final Component TITLE_EXPORT_MIPPED = Component.translatable("btn.atlasviewer.export_mipped_sprite");
    private static final Component MSG_EXPORT_DETAILS = Component.translatable("msg.atlasviewer.export_sprite.detail");
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_sprite_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_sprite_error");
    private static final Component TOOLTIP_READERPACK = Component.translatable("tooltip.atlasviewer.reader_pack");
    private static final Component TOOLTIP_MAX_MIP_LEVEL = Component.translatable("tooltip.atlasviewer.sprite.max_mip_level");
    private static final Component TOOLTIP_MIPMAP_DISABLED = Component.translatable("tooltip.atlasviewer.sprite.mipmap_disabled");
    private static final Component TOOLTIP_MIPMAP_FULL = Component.translatable("tooltip.atlasviewer.sprite.mipmap_full");
    private static final Component FULL_TYPE_PLACEHOLDER = Component.translatable(
            "value.atlasviewer.source_tooltip.hold_to_show",
            InputConstants.getKey(GLFW.GLFW_KEY_LEFT_SHIFT, -1).getDisplayName()
    ).withStyle(ChatFormatting.GOLD);
    private static final ClientTooltipPositioner PACK_LIST_POSITIONER = new FixedTooltipPositioner();
    private static final int WIDTH = 400;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 12;
    private static final int EXPORT_WIDTH = 120;
    private static final int EXPORT_HEIGHT = 20;
    private static final int MIP_LEVEL_WIDTH = 120;
    private static final int MIP_LEVEL_HEIGHT = 20;
    private static final int CLOSE_SIZE = 12;
    private static final int SPRITE_SIZE = 128;
    private static final int FOOTER_HEIGHT = MIP_LEVEL_HEIGHT + PADDING * 4;
    private static final int MIN_HEIGHT = SPRITE_Y + SPRITE_SIZE + FOOTER_HEIGHT;
    private static final int LINE_NAME = 0;
    private static final int LINE_SOURCEPACK = 2;
    private static final int LINE_READERPACK = 3;
    private static final int LINE_READERTYPE = 4;
    private static final int LINE_MAX_MIP_LEVEL = 5;

    private final TextureAtlas atlas;
    private final TextureAtlasSprite sprite;
    private final SpriteContents contents;
    private final boolean mipped;
    private final boolean animated;
    private final boolean guiSprite;
    private final List<String> sourceNames;
    private final String primarySource;
    private final SpriteContents.AnimatedTexture animation;
    private final int animFrameTime;
    private final GuiSpriteScaling guiScaling;
    private int imageHeight;
    private int xLeft;
    private int yTop;
    private int valueX;
    private Button btnExport;
    private Button btnExportMipped;
    private TextLine spriteName;
    private Component spriteSizeText;
    private TextLine primarySourceName;
    private SourcePackList sourceNameTooltip;
    private SpriteSourceInfo sourceInfo;
    private Component maxMipLevel;
    private Component maxMipLevelTooltip;
    private Component animatedText;
    private Component framesText;
    private Component interpText;
    private Component frameTimeText;
    private Component guiSpriteTypeText;
    private Component guiSpriteSizeText;
    private Component guiSpriteNinesliceBorderText;
    private int currentMipLevel;

    public SpriteInfoScreen(TextureAtlas atlas, TextureAtlasSprite sprite, int currentMipLevel)
    {
        super(TITLE);
        this.atlas = atlas;
        this.sprite = sprite;
        this.contents = sprite.contents();
        this.mipped = atlas.atlasviewer$isMipMapEnabled();
        this.guiSprite = atlas == Minecraft.getInstance().getGuiSprites().atlasviewer$getAtlas();
        this.sourceNames = collectSourcePackNames();
        this.primarySource = sourceNames.isEmpty() ? null : sourceNames.getFirst();
        this.animation = contents.atlasviewer$getAnimatedTexture();
        this.animated = animation != null;
        this.animFrameTime = getAnimationFrameTime();
        this.guiScaling = guiSprite ? Minecraft.getInstance().getGuiSprites().getSpriteScaling(sprite) : null;
        this.currentMipLevel = currentMipLevel;
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
        valueX = LABEL_X + labelLen + PADDING;

        imageHeight = Math.max(MIN_HEIGHT, SPRITE_Y + labelHeight + FOOTER_HEIGHT);
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (imageHeight / 2);

        DiscreteSliderButton mipLevelSlider = addRenderableWidget(new DiscreteSliderButton(
                xLeft + (PADDING * 2), yTop + imageHeight - (PADDING * 2) - MIP_LEVEL_HEIGHT,
                MIP_LEVEL_WIDTH, MIP_LEVEL_HEIGHT,
                "btn.atlasviewer.mip_level",
                currentMipLevel,
                atlas.atlasviewer$getMipLevel(),
                this::selectMipLevel
        ));
        addRenderableWidget(btnExport = Button.builder(TITLE_EXPORT, this::exportSprite)
                .pos(xLeft + WIDTH - (PADDING * 2) - EXPORT_WIDTH, yTop + imageHeight - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );
        addRenderableWidget(btnExportMipped = Button.builder(TITLE_EXPORT_MIPPED, this::exportSpriteMipped)
                .pos(xLeft + WIDTH - (PADDING * 4) - (EXPORT_WIDTH * 2), yTop + imageHeight - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );

        mipLevelSlider.active = atlas.atlasviewer$getMipLevel() > 0;
        btnExportMipped.active = currentMipLevel > 0;

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));

        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        spriteName = TextLine.of(contents.name().toString(), font, maxValueLen);
        spriteSizeText = Component.translatable("value.atlasviewer.size", contents.width(), contents.height());
        primarySourceName = primarySource != null && !primarySource.isEmpty() ? TextLine.of(primarySource, font, maxValueLen) : new TextLine(VALUE_UNKNOWN_PACK);
        sourceNameTooltip = makeTooltipList();
        sourceInfo = makeSourceInfo();
        if (mipped)
        {
            maxMipLevel = calculateMaxMipLevel();
        }
        if (animated)
        {
            animatedText = VALUE_TRUE;
            framesText = Component.literal(String.valueOf(contents.atlasviewer$callGetFrameCount()));
            boolean interp = animation.atlasviewer$getInterpolateFrames();
            interpText = interp ? VALUE_TRUE : VALUE_FALSE;
            if (animFrameTime == -1)
            {
                frameTimeText = VALUE_FRAMETIME_MIXED;
            }
            else
            {
                frameTimeText = Component.literal(animFrameTime + (animFrameTime == 1 ? " tick" : " ticks"));
            }
        }
        else
        {
            animatedText = VALUE_FALSE;
        }
        if (guiSprite)
        {
            guiSpriteTypeText = Component.translatable("value.atlasviewer.spriteinfo.gui_sprite_type." + guiScaling.type().getSerializedName());
            switch (guiScaling)
            {
                case GuiSpriteScaling.Tile tile ->
                        guiSpriteSizeText = Component.translatable("value.atlasviewer.size", tile.width(), tile.height());
                case GuiSpriteScaling.NineSlice nineSlice ->
                {
                    guiSpriteSizeText = Component.translatable("value.atlasviewer.size", nineSlice.width(), nineSlice.height());
                    GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
                    if (border.top() == border.bottom() && border.top() == border.left() && border.top() == border.right())
                    {
                        guiSpriteNinesliceBorderText = Component.literal(border.top() + "px");
                    }
                    else
                    {
                        guiSpriteNinesliceBorderText = Component.translatable(
                                "value.atlasviewer.spriteinfo.gui_nineslice_border",
                                border.top(), border.bottom(), border.left(), border.right()
                        );
                    }
                }
                default -> { }
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), xLeft, yTop, 0, WIDTH, imageHeight, AtlasScreen.BACKGROUND);

        graphics.drawString(font, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040, false);

        int y = yTop + SPRITE_Y;
        y = drawLine(graphics, LABEL_NAME, spriteName.text(), y);
        y = drawLine(graphics, LABEL_SIZE, spriteSizeText, y);
        y = drawLine(graphics, LABEL_SOURCEPACK, primarySourceName.text(), y);
        y = drawLine(graphics, LABEL_READERPACK, sourceInfo.sourcePack, y);
        y = drawLine(graphics, LABEL_READERTYPE, sourceInfo.sourceType, y);
        if (mipped)
        {
            y = drawLine(graphics, LABEL_MAX_MIP_LEVEL, maxMipLevel, y);
        }
        y = drawLine(graphics, LABEL_ANIMATED, animatedText, y);
        if (animated)
        {
            y = drawLine(graphics, LABEL_FRAMECOUNT, framesText, y);
            y = drawLine(graphics, LABEL_INTERPOLATED, interpText, y);
            y = drawLine(graphics, LABEL_FRAMETIME, frameTimeText, y);
        }
        if (guiSprite)
        {
            y = drawLine(graphics, LABEL_GUI_SPRITE_TYPE, guiSpriteTypeText, y);
            if (guiSpriteSizeText != null)
            {
                y = drawLine(graphics, LABEL_GUI_SPRITE_SIZE, guiSpriteSizeText, y);
            }
            if (guiSpriteNinesliceBorderText != null)
            {
                y = drawLine(graphics, LABEL_GUI_SPRITE_NINESLICE_BORDER, guiSpriteNinesliceBorderText, y);
            }
        }

        float scale = (float) SPRITE_SIZE / Math.max(contents.width(), contents.height());

        RenderSystem.setShaderTexture(0, AtlasScreen.CHECKER_LOC);
        ClientUtils.drawNineSliceTexture(
                graphics.pose(),
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                AtlasScreen.CHECKER
        );

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();
        AtlasScreen.setAtlasMipLevel(atlas, currentMipLevel);
        graphics.blit(
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                sprite
        );
        AtlasScreen.setAtlasMipLevel(atlas, 0);
        RenderSystem.disableBlend();

        if (btnExport.isHovered())
        {
            setTooltipForNextRenderPass(MSG_EXPORT_DETAILS);
        }
        else if (btnExportMipped.active && btnExportMipped.isHovered())
        {
            setTooltipForNextRenderPass(Component.translatable("msg.atlasviewer.export_mipped_atlas.detail", currentMipLevel));
        }
    }

    private int drawLine(GuiGraphics graphics, Component label, Component value, int y)
    {
        graphics.drawString(font, label, xLeft + LABEL_X, y, 0x404040, false);
        graphics.drawString(font, value, xLeft + valueX, y, 0x404040, false);
        return y + LINE_HEIGHT;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int lx = xLeft + LABEL_X;
        int lyPack = yTop + SPRITE_Y + (LINE_HEIGHT * LINE_READERPACK);
        int lyMip = yTop + SPRITE_Y + (LINE_HEIGHT * LINE_MAX_MIP_LEVEL);
        if (mouseX >= lx && mouseX < lx + font.width(LABEL_READERPACK) && mouseY >= lyPack && mouseY < lyPack + font.lineHeight)
        {
            setTooltipForNextRenderPass(TOOLTIP_READERPACK);
        }
        else if (mipped && mouseX >= lx && mouseX < lx + font.width(LABEL_MAX_MIP_LEVEL) && mouseY >= lyMip && mouseY < lyMip + font.lineHeight)
        {
            setTooltipForNextRenderPass(TOOLTIP_MAX_MIP_LEVEL);
        }
        else if (spriteName.capped() && isHoveringLine(mouseX, mouseY, LINE_NAME, spriteName.text()))
        {
            graphics.renderTooltip(font, spriteName.fullText(), mouseX, mouseY);
        }
        else if (sourceInfo.sourcePackTooltip != null && isHoveringLine(mouseX, mouseY, LINE_READERPACK, sourceInfo.sourcePack))
        {
            if (sourceInfo.hasSourcePack)
            {
                renderFixedTooltip(graphics, LINE_READERPACK, sourceInfo.sourcePackTooltip);
            }
            else
            {
                graphics.renderTooltip(font, sourceInfo.sourcePackTooltip, mouseX, mouseY);
            }
        }
        else if (sourceInfo.sourceTypeTooltip != null && isHoveringLine(mouseX, mouseY, LINE_READERTYPE, sourceInfo.sourceType))
        {
            List<FormattedCharSequence> lines = sourceInfo.sourceTypeTooltip;
            if (sourceInfo.hasConcreteSourceType && !hasShiftDown())
            {
                lines = Objects.requireNonNull(sourceInfo.sourceTypeTooltipNoFullType);
            }
            graphics.renderTooltip(font, lines, mouseX, mouseY);
        }
        else if ((sourceNames.size() > 1 || primarySourceName.capped()) && isHoveringLine(mouseX, mouseY, LINE_SOURCEPACK, primarySourceName.text()))
        {
            renderFixedTooltip(graphics, LINE_SOURCEPACK, sourceNameTooltip.entries, sourceNameTooltip.maxLen);
        }
        else if (mipped && isHoveringLine(mouseX, mouseY, LINE_MAX_MIP_LEVEL, maxMipLevel))
        {
            graphics.renderTooltip(font, maxMipLevelTooltip, mouseX, mouseY);
        }
    }

    private boolean isHoveringLine(int mouseX, int mouseY, int lineIdx, Component text)
    {
        int yTextTop = yTop + SPRITE_Y + (LINE_HEIGHT * lineIdx);
        int xTextRight = xLeft + valueX + font.width(text);
        return mouseX >= xLeft + valueX && mouseX <= xTextRight && mouseY >= yTextTop && mouseY <= yTextTop + font.lineHeight;
    }

    @SuppressWarnings("SameParameterValue")
    private void renderFixedTooltip(GuiGraphics graphics, int lineIdx, Component text)
    {
        var tooltip = List.of(ClientTooltipComponent.create(text.getVisualOrderText()));
        renderFixedTooltip(graphics, lineIdx, tooltip, font.width(text));
    }

    private void renderFixedTooltip(
            GuiGraphics graphics, int lineIdx, List<ClientTooltipComponent> components, int maxLen
    )
    {
        int x = Math.min(xLeft + valueX, width - maxLen - PADDING);
        int y = yTop + SPRITE_Y + (LINE_HEIGHT * lineIdx);
        graphics.renderTooltipInternal(font, components, x, y, PACK_LIST_POSITIONER);
    }

    private List<String> collectSourcePackNames()
    {
        ResourceLocation name = contents.name();
        ResourceLocation loc = Objects.requireNonNullElseGet(
                contents.atlasviewer$getOriginalPath(),
                () -> ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "textures/" + name.getPath() + ".png")
        );
        List<Resource> resources = Minecraft.getInstance().getResourceManager().getResourceStack(loc);
        List<String> sources = resources.stream()
                .map(Resource::source)
                .map(PackResources::packId)
                .collect(Collectors.toCollection(ArrayList::new));
        if (sources.isEmpty())
        {
            String capturedPackId = contents.atlasviewer$getTextureSourcePack();
            if (capturedPackId != null)
            {
                sources.add(capturedPackId);
            }
        }
        Collections.reverse(sources);
        return sources;
    }

    private SourcePackList makeTooltipList()
    {
        if (sourceNames.isEmpty())
        {
            return new SourcePackList(List.of(), 0);
        }

        List<Component> lines = sourceNames.stream()
                .map(Component::literal)
                .map(Component.class::cast)
                .toList();

        int maxWidth = lines.stream().mapToInt(font::width).max().orElseThrow();
        TooltipSeparator seperator = new TooltipSeparator(maxWidth, 0xFFFFFFFF, false);

        MutableBoolean first = new MutableBoolean(true);
        List<ClientTooltipComponent> components = lines.stream()
                .flatMap(line ->
                {
                    List<ClientTooltipComponent> lineComponents = font.split(line, maxWidth)
                            .stream()
                            .map(ClientTooltipComponent::create)
                            .collect(Collectors.toCollection(ArrayList::new));

                    if (first.booleanValue() && lineComponents.size() == 1)
                    {
                        lineComponents.add(new TooltipSeparator(maxWidth, 0xFFFFFFFF, true));
                    }
                    else
                    {
                        lineComponents.add(seperator);
                    }
                    first.setFalse();
                    return lineComponents.stream();
                })
                .collect(Collectors.toCollection(ArrayList::new));
        components.removeLast();
        return new SourcePackList(components, maxWidth);
    }

    private SpriteSourceInfo makeSourceInfo()
    {
        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        Component sourcePack;
        Component sourcePackTooltip;
        boolean hasSourcePack = false;
        SourceAwareness awareness = contents.atlasviewer$getSourceAwareness();
        if (awareness == SourceAwareness.SOURCE_KNOWN)
        {
            String packId = contents.atlasviewer$getSpriteSourceSourcePack();
            if (packId != null && !packId.isEmpty())
            {
                TextLine packIdPair = TextLine.of(packId, font, maxValueLen);
                sourcePack = packIdPair.text();
                sourcePackTooltip = !sourcePack.equals(packIdPair.fullText()) ? packIdPair.fullText() : null;
                hasSourcePack = true;
            }
            else
            {
                sourcePack = VALUE_UNKNOWN_PACK;
                sourcePackTooltip = null;
            }
        }
        else
        {
            sourcePack = awareness.getDescription();
            sourcePackTooltip = awareness.getTooltip();
        }

        Component sourceType;
        List<FormattedCharSequence> sourceTypeTooltip;
        List<FormattedCharSequence> sourceTypeTooltipNoFullType = null;
        boolean hasConcreteSourceType = false;
        SpriteSource source = contents.atlasviewer$getSpriteSource();
        if (source != null)
        {
            Class<?> sourceTypeClazz = source.getClass();
            String typeDesc = SpriteSourceManager.getSpecialDescription(sourceTypeClazz);
            if (typeDesc != null)
            {
                TextLine typeNamePair = TextLine.of(typeDesc, font, maxValueLen);
                sourceType = typeNamePair.text();
                if (!sourceType.equals(typeNamePair.fullText()))
                {
                    sourceTypeTooltip = List.of(typeNamePair.fullText().getVisualOrderText());
                }
                else
                {
                    sourceTypeTooltip = null;
                }
            }
            else
            {
                String typeName = Services.PLATFORM.getSpriteSourceName(source);
                String shortTypeName = typeName.substring(typeName.lastIndexOf('.') + 1);
                sourceType = TextLine.of(shortTypeName, font, maxValueLen).text();
                hasConcreteSourceType = true;

                List<Tuple<Component, Component>> tooltipLines = SpriteSourceManager.buildSourceTooltip(source, typeName);

                sourceTypeTooltip = formatTooltip(tooltipLines);
                tooltipLines.getLast().setB(FULL_TYPE_PLACEHOLDER);
                sourceTypeTooltipNoFullType = formatTooltip(tooltipLines);
            }
        }
        else
        {
            sourceType = awareness.getDescription();
            sourceTypeTooltip = List.of(awareness.getTooltip().getVisualOrderText());
        }

        return new SpriteSourceInfo(
                sourcePack,
                sourcePackTooltip,
                hasSourcePack,
                sourceType,
                sourceTypeTooltip,
                sourceTypeTooltipNoFullType,
                hasConcreteSourceType
        );
    }

    private static List<FormattedCharSequence> formatTooltip(List<Tuple<Component, Component>> tooltipLines)
    {
        return tooltipLines.stream()
                .peek(pair ->
                {
                    //noinspection ConstantConditions
                    if (pair.getA() != null)
                    {
                        pair.setA(pair.getA().copy().withStyle(ChatFormatting.ITALIC));
                    }
                })
                .map(pair ->
                {
                    MutableComponent line = Component.empty();
                    //noinspection ConstantConditions
                    if (pair.getA() != null)
                    {
                        line = line.append(pair.getA()).append(": ");
                    }
                    return line.append(pair.getB());
                })
                .map(Component::getVisualOrderText)
                .toList();
    }

    private Component calculateMaxMipLevel()
    {
        int lowestOne = Math.min(Integer.lowestOneBit(contents.width()), Integer.lowestOneBit(contents.height()));
        int maxLevel = Math.min(Mth.log2(lowestOne), 4);
        return switch (maxLevel)
        {
            case 0 ->
            {
                maxMipLevelTooltip = TOOLTIP_MIPMAP_DISABLED;
                yield Component.literal("0").withStyle(s -> s.withColor(0xD00000));
            }
            case 4 ->
            {
                maxMipLevelTooltip = TOOLTIP_MIPMAP_FULL;
                yield Component.literal("4").withStyle(s -> s.withColor(0x00D000));
            }
            default ->
            {
                maxMipLevelTooltip = Component.translatable("tooltip.atlasviewer.sprite.mipmap_limited", maxLevel);
                yield Component.literal(String.valueOf(maxLevel)).withStyle(s -> s.withColor(0xCC6600));
            }
        };
    }

    private int getAnimationFrameTime()
    {
        if (animation == null)
        {
            return 0;
        }

        List<SpriteContents.FrameInfo> frames = animation.atlasviewer$getFrames();

        int first = frames.getFirst().atlasviewer$getTime();
        for (SpriteContents.FrameInfo frame : frames)
        {
            if (frame.atlasviewer$getTime() != first)
            {
                // No point in displaying a frame time when it's different for some frames
                return -1;
            }
        }

        return first;
    }

    private void selectMipLevel(int level)
    {
        currentMipLevel = level;
        btnExportMipped.active = level > 0;
    }

    private void exportSprite(@SuppressWarnings("unused") Button btn)
    {
        exportSprite(0);
    }

    private void exportSpriteMipped(@SuppressWarnings("unused") Button btn)
    {
        exportSprite(currentMipLevel);
    }

    private void exportSprite(int mipLevel)
    {
        try
        {
            NativeImage image = contents.atlasviewer$getByMipLevel()[mipLevel];
            AtlasScreen.exportNativeImage(image, contents.name(), "sprite", false, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting sprite", e);
            Services.PLATFORM.pushScreenLayer(MessageScreen.error(List.of(
                    MSG_EXPORT_ERROR,
                    Component.literal(e.getMessage())
            )));
        }
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



    private record Label(Component text, Predicate<SpriteInfoScreen> active)
    {
        public Label(Component text)
        {
            this(text, screen -> true);
        }
    }

    private record SourcePackList(List<ClientTooltipComponent> entries, int maxLen) { }

    private record SpriteSourceInfo(
            Component sourcePack,
            @Nullable Component sourcePackTooltip,
            boolean hasSourcePack,
            Component sourceType,
            @Nullable List<FormattedCharSequence> sourceTypeTooltip,
            @Nullable List<FormattedCharSequence> sourceTypeTooltipNoFullType,
            boolean hasConcreteSourceType
    ) { }
}
