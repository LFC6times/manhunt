package ml.ikwid.manhunt.mixin;

import ml.ikwid.manhunt.Manhunt;
import ml.ikwid.manhunt.command.HunterCommand;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
	@Inject(method = "onHandSwing", at = @At("HEAD"))
	private void checkCompassUse(HandSwingC2SPacket packet, CallbackInfo ci) {
		UUID uuid = ((ServerPlayNetworkHandler)(Object)this).getPlayer().getUuid();
		if(HunterCommand.isHunter(((ServerPlayNetworkHandler)(Object)this).getPlayer()) && !Manhunt.updatedTrackedRunner.get(uuid)) {
			HunterCommand.incrementTrackedRunner(((ServerPlayNetworkHandler) (Object) this).getPlayer().getUuid());
			Manhunt.updatedTrackedRunner.put(uuid, true);
			Manhunt.LOGGER.info("Updated tracked runner for " + ((ServerPlayNetworkHandler)(Object)this).getPlayer().getName().asString() + " to " + Objects.requireNonNull(Manhunt.playerManager.getPlayer(HunterCommand.getTrackedRunner(uuid))).getName().asString());
		}
	}
}
