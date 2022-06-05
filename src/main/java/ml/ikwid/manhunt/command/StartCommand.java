package ml.ikwid.manhunt.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.Objects;
import java.util.UUID;

import ml.ikwid.manhunt.game.Hunters;

public class StartCommand {

	public static int start(CommandContext<ServerCommandSource> ctx) {
		int time = IntegerArgumentType.getInteger(ctx, "headstart");
		int ticks = time * 20;

		if(RunnerCommand.getRunners().size() == 0 || HunterCommand.getHunters().size() == 0) {
			ctx.getSource().sendError(new LiteralText("No hunters / runners to start the game with!"));
			return 1;
		}

		LiteralText msg = new LiteralText(Formatting.RED + "Hunters will be released in " + time + " seconds!" + Formatting.RESET);

		StatusEffectInstance slowness = new StatusEffectInstance(StatusEffects.SLOWNESS, ticks, 7);
		StatusEffectInstance miningFatigue = new StatusEffectInstance(StatusEffects.MINING_FATIGUE, ticks, 4);
		StatusEffectInstance weakness = new StatusEffectInstance(StatusEffects.WEAKNESS, ticks, 4);
		StatusEffectInstance jump = new StatusEffectInstance(StatusEffects.JUMP_BOOST, ticks, 128); // anti jump
		StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, ticks, 4);
		StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, ticks, 5);

		for(ServerPlayerEntity player : HunterCommand.getHunterEntities()) {
			Hunters.setTrackedLocation(player.getUuid(), RunnerCommand.getRunners().get(0));

			player.addStatusEffect(slowness);
			player.addStatusEffect(miningFatigue);
			player.addStatusEffect(weakness);
			player.addStatusEffect(jump);
			player.addStatusEffect(blindness);
			player.addStatusEffect(resistance);

			player.sendMessage(msg, true);
			player.giveItemStack(new ItemStack(Items.COMPASS));
		}

		PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();

		for(UUID player : RunnerCommand.getRunners()) {
			Objects.requireNonNull(playerManager.getPlayer(player)).sendMessage(msg, true);
		}
		return 0;
	}
}
