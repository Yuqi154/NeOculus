package net.irisshaders.iris.compat.sodium.mixin.shader_overrides;

import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ChunkBuilderMeshingTask.class)
public class MixinChunkBuilderMeshingTask {
    /**
     * @author embeddedt
     * @reason On Forge, render types are not intended to be driven by a central registry like in vanilla; instead, they
     * get queried from the block model during meshing. Only the default baked models defer to the vanilla registry;
     * specifying a render type in the model JSON or using a custom model will return its own value. Thus, we need
     * to redirect the access at a higher level.
     */
    @Redirect(method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getRenderTypes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;)Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;"),
            remap = false)
    private ChunkRenderTypeSet oculus$overrideRenderTypes(BakedModel instance, BlockState blockState, RandomSource randomSource, ModelData modelData) {
        Map<Block, ChunkRenderTypeSet> idMap = WorldRenderingSettings.INSTANCE.getBlockTypeIds();
        if (idMap != null) {
            ChunkRenderTypeSet type = idMap.get(blockState.getBlock());
            if (type != null) {
                return type;
            }
        }

        return instance.getRenderTypes(blockState, randomSource, modelData);
    }
}

