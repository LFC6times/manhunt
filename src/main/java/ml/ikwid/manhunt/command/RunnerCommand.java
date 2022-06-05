package ml.ikwid.manhunt.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.UUID;

import static ml.ikwid.manhunt.Manhunt.LOGGER;

public class RunnerCommand {
	private static final ArrayList<UUID> runners = new ArrayList<>();

	public static int add(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
		if(HunterCommand.isHunter(player)) {
			ctx.getSource().sendError(Text.of(player.getName().asString() + " is already a hunter"));
			return 1;
		}
		runners.add(player.getUuid());

		LOGGER.info("Added runner " + player.getName().asString() + " as runner");
		player.sendMessage(Text.of("You are now a runner"), false);
		return 0;
	}

	public static int remove(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
		runners.remove(player.getUuid());

		return 0;
	}

	public static int clear(CommandContext<ServerCommandSource> ctx) {
		runners.clear();

		return 0;
	}

	public static boolean isRunner(ServerPlayerEntity player) {
		return runners.contains(player.getUuid());
	}

	public static ArrayList<UUID> getRunners() {
		return new ArrayList<>(runners);
	}

	public static void remove(UUID uuid) {
		runners.remove(uuid);
	}
}
