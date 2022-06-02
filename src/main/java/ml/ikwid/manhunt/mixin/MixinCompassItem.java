package ml.ikwid.manhunt.mixin;

import ml.ikwid.manhunt.command.HunterCommand;
import net.minecraft.entity.Entity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public abstract class MixinCompassItem {
	@Shadow protected abstract void writeNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt);

	/**
	 * @author 6Times
	 * @reason make sure it always has lodestone so it'll always point to hunters (and so hunters don't go villager trading)
	 */
	@Overwrite
	public static boolean hasLodestone(ItemStack stack) {
		return true;
	}

	@Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
	private void setLocation(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
		if(world.isClient() || !(entity instanceof ServerPlayerEntity) || !HunterCommand.needsUpdateCompass(entity.getUuid())) {
			ci.cancel();
		}
		this.writeNbt(HunterCommand.getTrackedDimension(entity.getUuid()), HunterCommand.getTrackedLocation(entity.getUuid()), stack.getOrCreateNbt());
		HunterCommand.removeNeedsUpdateCompass(entity.getUuid());

		ci.cancel();
	}
}