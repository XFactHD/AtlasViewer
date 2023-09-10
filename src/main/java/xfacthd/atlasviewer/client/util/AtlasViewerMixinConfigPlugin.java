package xfacthd.atlasviewer.client.util;

import net.minecraftforge.coremod.api.ASMAPI;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class AtlasViewerMixinConfigPlugin implements IMixinConfigPlugin
{
    private static final String MIXIN_DIRLISTER_VANILLA = "xfacthd.atlasviewer.client.mixin.spritesources.MixinDirectoryListerVanilla";
    private static final String MIXIN_DIRLISTER_OCULUS = "xfacthd.atlasviewer.client.mixin.spritesources.MixinDirectoryListerOculus";
    private static final String HANDLER_NAME = "atlasviewer$resourceAttachSpriteSourceSourcePack";
    private static final String HANDLER_DESC = "(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)V";
    private static final String LAMBDA_NAME = "lambda$run";
    private static final String LAMBDA_DESC = "(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)V";

    private boolean oculusLoaded = false;

    @Override
    public void onLoad(String mixinPackage)
    {
        oculusLoaded = LoadingModList.get().getModFileById("oculus") != null;
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String target, String mixin)
    {
        if (mixin.equals(MIXIN_DIRLISTER_VANILLA))
        {
            return !oculusLoaded;
        }
        if (mixin.equals(MIXIN_DIRLISTER_OCULUS))
        {
            return oculusLoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
        if (mixinClassName.equals(MIXIN_DIRLISTER_OCULUS) && oculusLoaded)
        {
            MethodNode handlerNode = findMethod(targetClass, HANDLER_NAME, HANDLER_DESC);
            MethodNode lambdaNode = findMethod(targetClass, LAMBDA_NAME, LAMBDA_DESC);

            boolean success = ASMAPI.insertInsnList(
                    lambdaNode,
                    ASMAPI.MethodType.INTERFACE,
                    "net/minecraft/client/renderer/texture/atlas/SpriteSource$Output",
                    ASMAPI.mapMethod("m_261028_"),
                    "(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)V",
                    ASMAPI.listOf(
                            new VarInsnNode(
                                    Opcodes.ALOAD,
                                    findParamIdx(lambdaNode, "this", "L" + targetClassName.replace('.', '/') + ";")
                            ),
                            new VarInsnNode(
                                    Opcodes.ALOAD,
                                    findParamIdx(lambdaNode, null, "Lnet/minecraft/resources/ResourceLocation;")
                            ),
                            new VarInsnNode(
                                    Opcodes.ALOAD,
                                    findParamIdx(lambdaNode, null, "Lnet/minecraft/server/packs/resources/Resource;")
                            ),
                            new MethodInsnNode(
                                    Opcodes.INVOKESPECIAL,
                                    targetClassName.replace('.', '/'),
                                    handlerNode.name,
                                    handlerNode.desc
                            )
                    ),
                    ASMAPI.InsertMode.INSERT_BEFORE
            );
            if (!success) throw new IllegalStateException("Failed to insert call to handler method into " + targetClassName);
        }
    }

    private static MethodNode findMethod(ClassNode clazz, String name, String desc)
    {
        return clazz.methods.stream()
                .filter(mth -> mth.name.contains(name) && mth.desc.equals(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to find method '*" + name + "*" + desc));
    }

    private static int findParamIdx(MethodNode mth, String name, String desc)
    {
        for (int i = 0; i < mth.localVariables.size(); i++)
        {
            LocalVariableNode var = mth.localVariables.get(i);
            if ((name == null || var.name.equals(name)) && var.desc.equals(desc) && var.index <= mth.parameters.size())
            {
                return var.index;
            }
        }
        throw new IllegalStateException("Parameter not found (" + name + " | " + desc + ")");
    }
}
