package ml.ikwid.manhunt.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import ml.ikwid.manhunt.game.Hunters;

import static ml.ikwid.manhunt.Manhunt.LOGGER;
import static ml.ikwid.manhunt.Manhunt.playerManager;
import static ml.ikwid.manhunt.game.Hunters.updatedTrackedRunner;

public class HunterCommand {
	private static final HashSet<UUID> hunters = new HashSet<>();

	public static int add(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
		if(RunnerCommand.isRunner(player)) {
			ctx.getSource().sendError(Text.of(player.getName().asString() + " is already a runner"));
			return 1;
		}
		hunters.add(player.getUuid());
		updatedTrackedRunner.put(player.getUuid(), false);

		LOGGER.info("Added hunter " + player.getName().asString() + " as hunter");
		player.sendMessage(Text.of("You are now a hunter"), false);
		return 0;
	}

	public static int remove(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
		remove(player);

		return 0;
	}

	public static void remove(ServerPlayerEntity player) {
		hunters.remove(player.getUuid());
		Hunters.clear();
		updatedTrackedRunner.clear();

		removeCompass(player);
	}

	public static int clear(CommandContext<ServerCommandSource> ctx) {
		for(ServerPlayerEntity player : getHunterEntities()) {
			remove(player);
		}

		return 0;
	}

	private static void removeCompass(ServerPlayerEntity player) {
		PlayerInventory inventory = player.getInventory();

		for(int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if(stack.getItem() instanceof CompassItem) {
				inventory.removeStack(i);
			}
		}
	}

	public static boolean isHunter(ServerPlayerEntity player) {
		return hunters.contains(player.getUuid());
	}

	public static ArrayList<UUID> getHunters() {
		return new ArrayList<>(hunters.stream().toList());
	}

	public static ArrayList<ServerPlayerEntity> getHunterEntities() {
		ArrayList<ServerPlayerEntity> players = new ArrayList<>();
		for(UUID uuid : getHunters()) {
			players.add(playerManager.getPlayer(uuid));
		}
		return players;
	}
}
