package ml.ikwid.manhunt.mixin;

import ml.ikwid.manhunt.command.HunterCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
	@Shadow public abstract Item asItem();

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void checkCompassUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if(this.asItem() instanceof CompassItem) {
			HunterCommand.setTrackedLocation(user.getUuid());
			cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
		}
	}
}
