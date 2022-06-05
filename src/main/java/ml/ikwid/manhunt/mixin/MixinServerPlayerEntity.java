package ml.ikwid.manhunt.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ml.ikwid.manhunt.command.RunnerCommand;
import ml.ikwid.manhunt.game.Hunters;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	@Inject(method = "onDeath", at = @At("HEAD"))
	private void checkIfRunnerDeath(DamageSource source, CallbackInfo ci) {
		if(RunnerCommand.isRunner(((ServerPlayerEntity)(Object)this))) {
			RunnerCommand.remove(((ServerPlayerEntity)(Object)this).getUuid());
			if(RunnerCommand.getRunners().size() == 0) {
				Hunters.win();
			}
		}
	}
}
