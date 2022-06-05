package ml.ikwid.manhunt.mixin;

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

import ml.ikwid.manhunt.game.Hunters;

import static ml.ikwid.manhunt.Manhunt.LOGGER;
import static ml.ikwid.manhunt.game.Hunters.handleRightClick;

@Mixin(Item.class)
public abstract class MixinItem {
	@Shadow public abstract Item asItem();

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void checkCompassUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if(this.asItem() instanceof CompassItem) {
			LOGGER.info("right clicked");
			Hunters.setTrackedLocation(user.getUuid());
			handleRightClick.put(user.getUuid(), 0); // prepare to cancel 2 hand swings
			cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
		}
	}
}
