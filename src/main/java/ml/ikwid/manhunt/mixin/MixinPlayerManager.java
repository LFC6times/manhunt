package ml.ikwid.manhunt.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

import ml.ikwid.manhunt.Manhunt;
import ml.ikwid.manhunt.command.HunterCommand;
import ml.ikwid.manhunt.command.RunnerCommand;

import static ml.ikwid.manhunt.Manhunt.playerManager;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(method = "respawnPlayer", at = @At("TAIL"))
	private void giveCompass(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
		if(!alive && HunterCommand.isHunter(player)) {
			UUID uuid = player.getUuid();
			Manhunt.setTimeout(5, () -> Objects.requireNonNull(playerManager.getPlayer(uuid)).giveItemStack(new ItemStack(Items.COMPASS)));
		} else if(!alive && RunnerCommand.isRunner(player)) {
			UUID uuid = player.getUuid();
			Manhunt.setTimeout(5, () -> Objects.requireNonNull(playerManager.getPlayer(uuid)).changeGameMode(GameMode.SPECTATOR));
		}
	}
}
