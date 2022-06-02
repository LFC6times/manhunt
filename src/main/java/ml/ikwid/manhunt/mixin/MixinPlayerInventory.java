package ml.ikwid.manhunt.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {
	@Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
	private boolean checkIfCompass(ItemStack stack) {
		return stack.getItem() instanceof CompassItem || stack.isEmpty();
	}
}
