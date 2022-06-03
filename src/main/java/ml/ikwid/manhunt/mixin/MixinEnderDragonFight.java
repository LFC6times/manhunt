package ml.ikwid.manhunt.mixin;

import ml.ikwid.manhunt.game.RunnersWin;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class)
public class MixinEnderDragonFight {
	@Inject(method = "dragonKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/ServerBossBar;setPercent(F)V"))
	private void runnersWin(EnderDragonEntity dragon, CallbackInfo ci) {
		RunnersWin.win();
	}
}
