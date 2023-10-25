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
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.*;
import net.minecraftforge.resource.DelegatingPackResources;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.api.*;
import xfacthd.atlasviewer.client.mixin.*;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.screen.widget.DiscreteSliderButton;
import xfacthd.atlasviewer.client.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpriteInfoScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.spriteinfo");
    private static final Component CHAR_INFO = Component.literal("i").withStyle(ChatFormatting.BLUE);
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.spritename");
    private static final Component LABEL_WIDTH = Component.translatable("label.atlasviewer.spritewidth");
    private static final Component LABEL_HEIGHT = Component.translatable("label.atlasviewer.spriteheight");
    private static final Component LABEL_SOURCEPACK = Component.translatable("label.atlasviewer.sprite_sourcepack");
    private static final Component LABEL_READERPACK = Component.translatable(
            "label.atlasviewer.sprite_readerpack", CHAR_INFO
    );
    private static final Component LABEL_READERTYPE = Component.translatable("label.atlasviewer.sprite_readertype");
    private static final Component LABEL_MAX_MIP_LEVEL = Component.translatable(
            "label.atlasviewer.sprite_max_mip_level", CHAR_INFO
    );
    private static final Component LABEL_ANIMATED = Component.translatable("label.atlasviewer.spriteanimated");
    private static final Component LABEL_FRAMECOUNT = Component.translatable("label.atlasviewer.spriteframes");
    private static final Component LABEL_INTERPOLATED = Component.translatable("label.atlasviewer.spriteinterpolated");
    private static final Component LABEL_FRAMETIME = Component.translatable("label.atlasviewer.spriteframetime");
    private static final Component[] LABELS = {
            LABEL_NAME,
            LABEL_WIDTH,
            LABEL_HEIGHT,
            LABEL_SOURCEPACK,
            LABEL_READERPACK,
            LABEL_READERTYPE,
            LABEL_MAX_MIP_LEVEL,
            LABEL_ANIMATED,
            LABEL_FRAMECOUNT,
            LABEL_INTERPOLATED,
            LABEL_FRAMETIME
    };
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
    private static final int HEIGHT = 193;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 12;
    private static final int EXPORT_WIDTH = 120;
    private static final int EXPORT_HEIGHT = 20;
    private static final int MIP_LEVEL_WIDTH = 120;
    private static final int MIP_LEVEL_HEIGHT = 20;
    private static final int CLOSE_SIZE = 12;

    private final TextureAtlas atlas;
    private final TextureAtlasSprite sprite;
    private final SpriteContents contents;
    private final List<String> sourceNames;
    private final String primarySource;
    private final SpriteContents.AnimatedTexture animation;
    private final int animFrameTime;
    private int xLeft;
    private int yTop;
    private int labelLen = 0;
    private int valueX;
    private Button btnExport;
    private Button btnExportMipped;
    private TextLine spriteName;
    private TextLine primarySourceName;
    private SourcePackList sourceNameTooltip;
    private SpriteSourceInfo sourceInfo;
    private Component maxMipLevel;
    private Component maxMipLevelTooltip;
    private Component animatedText;
    private String framesText;
    private Component interpText;
    private Component frameTimeText;
    private int currentMipLevel;

    public SpriteInfoScreen(TextureAtlas atlas, TextureAtlasSprite sprite, int currentMipLevel)
    {
        super(TITLE);
        this.atlas = atlas;
        this.sprite = sprite;
        this.contents = sprite.contents();
        this.sourceNames = collectSourcePackNames();
        this.primarySource = sourceNames.isEmpty() ? null : sourceNames.get(0);
        this.animation = ((AccessorSpriteContents) contents).getAnimatedTexture();
        this.animFrameTime = getAnimationFrameTime();
        this.currentMipLevel = currentMipLevel;
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);

        DiscreteSliderButton mipLevelSlider = addRenderableWidget(new DiscreteSliderButton(
                xLeft + (PADDING * 2), yTop + HEIGHT - (PADDING * 2) - MIP_LEVEL_HEIGHT,
                MIP_LEVEL_WIDTH, MIP_LEVEL_HEIGHT,
                "btn.atlasviewer.mip_level",
                currentMipLevel,
                ((AccessorTextureAtlas) atlas).getMipLevel(),
                this::selectMipLevel
        ));
        addRenderableWidget(btnExport = Button.builder(TITLE_EXPORT, this::exportSprite)
                .pos(xLeft + WIDTH - (PADDING * 2) - EXPORT_WIDTH, yTop + HEIGHT - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );
        addRenderableWidget(btnExportMipped = Button.builder(TITLE_EXPORT_MIPPED, this::exportSpriteMipped)
                .pos(xLeft + WIDTH - (PADDING * 4) - (EXPORT_WIDTH * 2), yTop + HEIGHT - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );

        mipLevelSlider.active = ((AccessorTextureAtlas) atlas).getMipLevel() > 0;
        btnExportMipped.active = currentMipLevel > 0;

        for (Component label : LABELS)
        {
            labelLen = Math.max(labelLen, font.width(label));
        }
        valueX = LABEL_X + labelLen + PADDING;

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));

        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        spriteName = TextLine.of(contents.name().toString(), font, maxValueLen);
        primarySourceName = primarySource != null && !primarySource.isEmpty() ? TextLine.of(primarySource, font, maxValueLen) : new TextLine(VALUE_UNKNOWN_PACK);
        sourceNameTooltip = makeTooltipList();
        sourceInfo = makeSourceInfo();
        maxMipLevel = calculateMaxMipLevel();
        if (animation != null)
        {
            animatedText = VALUE_TRUE;
            framesText = String.valueOf(((AccessorSpriteContents) contents).callGetFrameCount());
            boolean interp = ((AccessorAnimatedTexture) animation).getInterpolateFrames();
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
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), xLeft, yTop, 0, WIDTH, HEIGHT, AtlasScreen.BACKGROUND);

        graphics.drawString(font, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040, false);

        boolean animated = animation != null;

        graphics.drawString(font, LABEL_NAME, xLeft + LABEL_X, yTop + SPRITE_Y, 0x404040, false);
        graphics.drawString(font, LABEL_WIDTH, xLeft + LABEL_X, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, LABEL_HEIGHT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        graphics.drawString(font, LABEL_SOURCEPACK, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        graphics.drawString(font, LABEL_READERPACK, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040, false);
        graphics.drawString(font, LABEL_READERTYPE, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040, false);
        graphics.drawString(font, LABEL_MAX_MIP_LEVEL, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040, false);
        graphics.drawString(font, LABEL_ANIMATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040, false);
        if (animated)
        {
            graphics.drawString(font, LABEL_FRAMECOUNT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 8), 0x404040, false);
            graphics.drawString(font, LABEL_INTERPOLATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 9), 0x404040, false);
            graphics.drawString(font, LABEL_FRAMETIME, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 10), 0x404040, false);
        }

        graphics.drawString(font, spriteName.text(), xLeft + valueX, yTop + SPRITE_Y, 0x404040, false);
        graphics.drawString(font, contents.width() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, contents.height() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        graphics.drawString(font, primarySourceName.text(), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        graphics.drawString(font, sourceInfo.sourcePack, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040, false);
        graphics.drawString(font, sourceInfo.sourceType, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040, false);
        graphics.drawString(font, maxMipLevel, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0xFFFFFF, false);
        graphics.drawString(font, animatedText, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040, false);
        if (animated)
        {
            graphics.drawString(font, framesText, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 8), 0x404040, false);
            graphics.drawString(font, interpText, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 9), 0x404040, false);
            graphics.drawString(font, frameTimeText, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 10), 0x404040, false);
        }

        float scale = 128F / Math.max(contents.width(), contents.height());

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

        super.render(graphics, mouseX, mouseY, partialTick);

        int lx = xLeft + LABEL_X;
        int lyPack = yTop + SPRITE_Y + (LINE_HEIGHT * 4);
        int lyMip = yTop + SPRITE_Y + (LINE_HEIGHT * 6);
        if (mouseX >= lx && mouseX < lx + font.width(LABEL_READERPACK) && mouseY >= lyPack && mouseY < lyPack + font.lineHeight)
        {
            setTooltipForNextRenderPass(TOOLTIP_READERPACK);
        }
        else if (mouseX >= lx && mouseX < lx + font.width(LABEL_MAX_MIP_LEVEL) && mouseY >= lyMip && mouseY < lyMip + font.lineHeight)
        {
            setTooltipForNextRenderPass(TOOLTIP_MAX_MIP_LEVEL);
        }
        else if (spriteName.capped() && isHoveringLine(mouseX, mouseY, 0, spriteName.text()))
        {
            graphics.renderTooltip(font, spriteName.fullText(), mouseX, mouseY);
        }
        else if (sourceInfo.sourcePackTooltip != null && isHoveringLine(mouseX, mouseY, 4, sourceInfo.sourcePack))
        {
            if (sourceInfo.hasSourcePack)
            {
                renderFixedTooltip(graphics, 4, sourceInfo.sourcePackTooltip);
            }
            else
            {
                graphics.renderTooltip(font, sourceInfo.sourcePackTooltip, mouseX, mouseY);
            }
        }
        else if (sourceInfo.sourceTypeTooltip != null && isHoveringLine(mouseX, mouseY, 5, sourceInfo.sourceType))
        {
            List<FormattedCharSequence> lines = sourceInfo.sourceTypeTooltip;
            if (sourceInfo.hasConcreteSourceType && !hasShiftDown())
            {
                lines = Objects.requireNonNull(sourceInfo.sourceTypeTooltipNoFullType);
            }
            graphics.renderTooltip(font, lines, mouseX, mouseY);
        }
        else if ((sourceNames.size() > 1 || primarySourceName.capped()) && isHoveringLine(mouseX, mouseY, 3, primarySourceName.text()))
        {
            renderFixedTooltip(graphics, 3, sourceNameTooltip.entries, sourceNameTooltip.maxLen);
        }
        else if (isHoveringLine(mouseX, mouseY, 6, maxMipLevel))
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
                ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$getOriginalPath(),
                () -> new ResourceLocation(name.getNamespace(), "textures/" + name.getPath() + ".png")
        );
        List<Resource> resources = Minecraft.getInstance().getResourceManager().getResourceStack(loc);
        List<String> sources = resources.stream()
                .map(Resource::source)
                .flatMap(pack -> getPackIds(pack, loc))
                .collect(Collectors.toCollection(ArrayList::new));
        if (sources.isEmpty())
        {
            String capturedPackId = ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$getTextureSourcePack();
            if (capturedPackId != null)
            {
                sources.add(capturedPackId);
            }
        }
        Collections.reverse(sources);
        return sources;
    }

    private static Stream<String> getPackIds(PackResources pack, ResourceLocation loc)
    {
        if (pack instanceof DelegatingPackResources delPack)
        {
            return Objects.requireNonNull(delPack.getChildren())
                    .stream()
                    .filter(d -> d.getResource(PackType.CLIENT_RESOURCES, loc) != null)
                    .map(PackResources::packId)
                    .map(id -> "\"" + pack.packId() + "\" -> \"" + (id.isEmpty() ? " " : id) + "\"");
        }
        return Stream.of(pack.packId());
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
        components.remove(components.size() - 1);
        return new SourcePackList(components, maxWidth);
    }

    private SpriteSourceInfo makeSourceInfo()
    {
        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        Component sourcePack;
        Component sourcePackTooltip;
        boolean hasSourcePack = false;
        SourceAwareness awareness = ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$getSourceAwareness();
        if (awareness == SourceAwareness.SOURCE_KNOWN)
        {
            String packId = ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$getSpriteSourceSourcePack();
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
        SpriteSource source = ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$getSpriteSource();
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
                String typeName = sourceTypeClazz.getName();
                String shortTypeName = typeName.substring(typeName.lastIndexOf('.') + 1);
                sourceType = TextLine.of(shortTypeName, font, maxValueLen).text();
                hasConcreteSourceType = true;

                List<Tuple<Component, Component>> tooltipLines = SpriteSourceManager.buildSourceTooltip(source, typeName);

                sourceTypeTooltip = formatTooltip(tooltipLines);
                tooltipLines.get(tooltipLines.size() - 1).setB(FULL_TYPE_PLACEHOLDER);
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

        List<SpriteContents.FrameInfo> frames = ((AccessorAnimatedTexture) animation).getFrames();

        int first = ((AccessorFrameInfo) frames.get(0)).getTime();
        for (SpriteContents.FrameInfo frame : frames)
        {
            if (((AccessorFrameInfo) frame).getTime() != first)
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
            NativeImage image = contents.byMipLevel[mipLevel];
            AtlasScreen.exportNativeImage(image, contents.name(), "sprite", false, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting sprite", e);
            Minecraft.getInstance().pushGuiLayer(MessageScreen.error(List.of(
                    MSG_EXPORT_ERROR,
                    Component.literal(e.getMessage())
            )));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && (mouseX < xLeft || mouseY < yTop || mouseX > (xLeft + WIDTH) || mouseY > (yTop + HEIGHT)))
        {
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
