package net.irisshaders.iris.mixin;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemBlockRenderTypes.class)
public class MixinItemBlockRenderTypes {
	@Inject(method = "getRenderLayers", at = @At("HEAD"), cancellable = true, remap = false)
	private static void iris$setCustomRenderType(BlockState arg, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
		Map<Block, ChunkRenderTypeSet> idMap = WorldRenderingSettings.INSTANCE.getBlockTypeIds();
		if (idMap != null) {
			ChunkRenderTypeSet type = idMap.get(arg.getBlock());
			if (type != null) {
				cir.setReturnValue(type);
			}
		}
	}
}
