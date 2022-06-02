package ml.ikwid.manhunt.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RegisterCommands {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager
				.literal("hunter")
					.then(CommandManager.literal("add")
						.then(CommandManager.argument("player", EntityArgumentType.player())
							.executes(HunterCommand::add))));

		dispatcher.register(
			CommandManager
				.literal("hunter")
					.then(CommandManager.literal("remove")
						.then(CommandManager.argument("player", EntityArgumentType.player())
							.executes(HunterCommand::remove))));

		dispatcher.register(
			CommandManager
				.literal("hunter")
					.then(CommandManager.literal("clear")
						.executes(HunterCommand::clear)));

		dispatcher.register(
			CommandManager
				.literal("runner")
					.then(CommandManager.literal("add")
						.then(CommandManager.argument("player", EntityArgumentType.player())
							.executes(RunnerCommand::add))));

		dispatcher.register(
			CommandManager
				.literal("runner")
					.then(CommandManager.literal("remove")
						.then(CommandManager.argument("player", EntityArgumentType.player())
							.executes(RunnerCommand::remove))));

		dispatcher.register(
			CommandManager
				.literal("runner")
					.then(CommandManager.literal("clear")
						.executes(RunnerCommand::clear)));

		dispatcher.register(
			CommandManager
				.literal("manhunt")
					.then(CommandManager.literal("start")
						.then(CommandManager.argument("headstart", IntegerArgumentType.integer())
							.executes(StartCommand::start))));
	}
}
