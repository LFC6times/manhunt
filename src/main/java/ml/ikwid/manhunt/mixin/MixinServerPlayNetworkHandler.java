package ml.ikwid.manhunt.mixin;

import net.minecraft.item.CompassItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

import ml.ikwid.manhunt.command.HunterCommand;
import ml.ikwid.manhunt.game.Hunters;

import static ml.ikwid.manhunt.Manhunt.playerManager;
import static ml.ikwid.manhunt.Manhunt.LOGGER;
import static ml.ikwid.manhunt.game.Hunters.handleRightClick;
import static ml.ikwid.manhunt.game.Hunters.updatedTrackedRunner;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
	@Inject(method = "onHandSwing", at = @At("HEAD"))
	private void checkCompassUse(HandSwingC2SPacket packet, CallbackInfo ci) {
		ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
		UUID uuid = player.getUuid();
		if(HunterCommand.isHunter(player) && player.getMainHandStack().getItem() instanceof CompassItem) {
			if(!updatedTrackedRunner.get(uuid)) {
				if(!(handleRightClick.get(uuid) == 2)) { // canceled twice
					Hunters.incrementTrackedRunner(((ServerPlayNetworkHandler) (Object) this).getPlayer().getUuid());
					updatedTrackedRunner.put(uuid, true);
					LOGGER.info("Updated tracked runner for " + ((ServerPlayNetworkHandler) (Object) this).getPlayer().getName().asString() + " to " + Objects.requireNonNull(playerManager.getPlayer(Hunters.getTrackedRunner(uuid))).getName().asString());
					Objects.requireNonNull(playerManager.getPlayer(uuid)).sendMessage(Text.of(Formatting.GREEN + "Updated tracked runner to " + Objects.requireNonNull(playerManager.getPlayer(Hunters.getTrackedRunner(uuid))).getName().asString() + Formatting.RESET), true);
				} else {
					LOGGER.info("cancelled by right click");
					int cancels = handleRightClick.get(uuid); // increment # of cancels
					handleRightClick.put(uuid, cancels + 1);
				}
			} else {
				Objects.requireNonNull(playerManager.getPlayer(uuid)).sendMessage(Text.of(Formatting.RED + "You are on cooldown." + Formatting.RESET), true);
			}
		}
	}
}
