package ml.ikwid.manhunt.mixin;

import ml.ikwid.manhunt.game.RunnersWin;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonEntity.class)
public class MixinEnderDragonEntity {
	@Inject(method = "updatePostDeath", at = @At(value = "HEAD"))
	private void runnersWin(CallbackInfo ci) {
		if(!RunnersWin.dragonKilled) {
			RunnersWin.dragonKilled = true;
			RunnersWin.win();
		}
	}
}
