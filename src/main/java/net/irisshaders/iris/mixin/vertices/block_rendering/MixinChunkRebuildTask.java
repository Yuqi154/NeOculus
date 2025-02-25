package net.irisshaders.iris.mixin.vertices.block_rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

/**
 * Captures and tracks the current block being rendered.
 * <p>
 * Uses a priority of 999 so that we apply before Indigo's mixins.
 */
@Mixin(value = SectionRenderDispatcher.RenderSection.RebuildTask.class, priority = 999)
public class MixinChunkRebuildTask {
	@Unique
	private static final String oculus$RENDER = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask;compile(FFFLnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask$CompileResults;";
	// Resolve the ID map on the main thread to avoid thread safety issues
	@Unique
	private final Object2IntMap<BlockState> blockStateIds = getBlockStateIds();
	@Unique
	private BlockSensitiveBufferBuilder lastBufferBuilder;

	@Unique
	private Object2IntMap<BlockState> getBlockStateIds() {
		return WorldRenderingSettings.INSTANCE.getBlockStateIds();
	}

	@Unique
	private short resolveBlockId(BlockState state) {
		if (blockStateIds == null) {
			return -1;
		}

		return (short) blockStateIds.getOrDefault(state, -1);
	}

	@Inject(method = oculus$RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void iris$onRenderLiquid(float x, float y, float z, SectionBufferBuilderPack sectionBufferBuilderPack, CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir, SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults sectionrenderdispatcher$rendersection$rebuildtask$compileresults, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, RenderChunkRegion renderchunkregion, PoseStack posestack, Set set, RandomSource randomsource, BlockRenderDispatcher blockrenderdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, FluidState fluidstate, RenderType rendertype, BufferBuilder bufferbuilder) {
		if (bufferbuilder instanceof BlockSensitiveBufferBuilder) {
			lastBufferBuilder = ((BlockSensitiveBufferBuilder) bufferbuilder);
			// All fluids have a ShadersMod render type of 1, to match behavior of Minecraft 1.7 and earlier.
			lastBufferBuilder.beginBlock(resolveBlockId(fluidstate.createLegacyBlock()), ExtendedDataHelper.FLUID_RENDER_TYPE, blockpos2.getX() & 0xF, blockpos2.getY() & 0xF, blockpos2.getZ() & 0xF);
		}
	}

	@Inject(method = oculus$RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V", shift = At.Shift.AFTER))
	private void iris$finishRenderingLiquid(float cameraX, float cameraY, float cameraZ, SectionBufferBuilderPack buffers, CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir) {
		if (lastBufferBuilder != null) {
			lastBufferBuilder.endBlock();
			lastBufferBuilder = null;
		}
	}

	@Inject(method = oculus$RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void iris$onRenderBlock(float x, float y, float z, SectionBufferBuilderPack sectionBufferBuilderPack, CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir, SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults sectionrenderdispatcher$rendersection$rebuildtask$compileresults, int i, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, RenderChunkRegion renderchunkregion, PoseStack posestack, Set set, RandomSource randomsource, BlockRenderDispatcher blockrenderdispatcher, Iterator var15, BlockPos blockpos2, BlockState blockstate, FluidState fluidstate, BakedModel model, ModelData modelData, Iterator var21, RenderType rendertype2, BufferBuilder bufferbuilder2) {
		if (bufferbuilder2 instanceof BlockSensitiveBufferBuilder) {
			lastBufferBuilder = ((BlockSensitiveBufferBuilder) bufferbuilder2);
			// TODO: Resolve render types for normal blocks?
			lastBufferBuilder.beginBlock(resolveBlockId(blockstate), ExtendedDataHelper.BLOCK_RENDER_TYPE, blockpos2.getX() & 0xF, blockpos2.getY() & 0xF, blockpos2.getZ() & 0xF);
		}
	}

	@Inject(method = oculus$RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", shift = At.Shift.AFTER))
	private void iris$finishRenderingBlock(float cameraX, float cameraY, float cameraZ, SectionBufferBuilderPack buffers, CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir) {
		if (lastBufferBuilder != null) {
			lastBufferBuilder.endBlock();
			lastBufferBuilder = null;
		}
	}
}
