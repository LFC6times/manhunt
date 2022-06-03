package ml.ikwid.manhunt.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.ikwid.manhunt.Manhunt;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static ml.ikwid.manhunt.Manhunt.LOGGER;
import static ml.ikwid.manhunt.Manhunt.playerManager;

public class HunterCommand {
	private record TrackedLocation(UUID uuid, BlockPos blockPos, RegistryKey<World> dimension) {
		public boolean equals(Object o) {
			if (o instanceof TrackedLocation other) {
				return other.uuid().equals(this.uuid()) && other.blockPos().equals(this.blockPos()) && other.dimension().equals(this.dimension());
			}
			return false;
		}
	}

	private static final HashMap<UUID, Integer> hunters = new HashMap<>();
	private static final HashMap<UUID, TrackedLocation> hunterTracked = new HashMap<>();

	private static final HashSet<UUID> needsUpdateCompass = new HashSet<>();

	public static int add(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
		if(RunnerCommand.isRunner(player)) {
			ctx.getSource().sendError(Text.of(player.getName().asString() + " is already a runner"));
			return 1;
		}
		hunters.put(player.getUuid(), 0);
		Manhunt.updatedTrackedRunner.put(player.getUuid(), false);

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
		hunterTracked.remove(player.getUuid());
		needsUpdateCompass.clear();
		Manhunt.updatedTrackedRunner.clear();

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
		return hunters.containsKey(player.getUuid());
	}

	public static ArrayList<UUID> getHunters() {
		return new ArrayList<>(hunters.keySet());
	}

	public static ArrayList<ServerPlayerEntity> getHunterEntities() {
		ArrayList<ServerPlayerEntity> players = new ArrayList<>();
		for(UUID uuid : getHunters()) {
			players.add(playerManager.getPlayer(uuid));
		}
		return players;
	}

	public static void setTrackedLocation(UUID hunter, UUID runner, BlockPos blockPos, RegistryKey<World> dimension) {
		hunterTracked.put(hunter, new TrackedLocation(runner, blockPos, dimension));
		addNeedsUpdateCompass(hunter);
	}

	public static void setTrackedLocation(UUID hunter, UUID runner) {
		setTrackedLocation(hunter, runner, Objects.requireNonNull(playerManager.getPlayer(runner)).getBlockPos(), Objects.requireNonNull(playerManager.getPlayer(runner)).world.getRegistryKey());
	}

	public static void setTrackedLocation(UUID hunter) {
		TrackedLocation trackedRunner = hunterTracked.get(hunter);
		if(trackedRunner == null) {
			LOGGER.info("No tracked runner for hunter " + hunter);
		} else {
			setTrackedLocation(hunter, trackedRunner.uuid());
		}
	}

	public static UUID getTrackedRunner(UUID hunter) {
		return hunterTracked.get(hunter).uuid();
	}

	public static BlockPos getTrackedLocation(UUID hunter) {
		return hunterTracked.get(hunter) == null ? Objects.requireNonNull(playerManager.getPlayer(hunter)).getBlockPos() : hunterTracked.get(hunter).blockPos();
	}

	public static RegistryKey<World> getTrackedDimension(UUID hunter) {
		return hunterTracked.get(hunter) == null ? Objects.requireNonNull(playerManager.getPlayer(hunter)).getWorld().getRegistryKey() : hunterTracked.get(hunter).dimension();
	}

	public static void incrementTrackedRunner(UUID hunter) {
		int index = hunters.get(hunter);
		index++;
		if (index >= RunnerCommand.getRunners().size()) {
			index = 0;
		}
		hunters.put(hunter, index);
		setTrackedLocation(hunter, RunnerCommand.getRunners().get(index));
		addNeedsUpdateCompass(hunter);
	}

	public static void addNeedsUpdateCompass(UUID hunter) {
		needsUpdateCompass.add(hunter);
	}

	public static void removeNeedsUpdateCompass(UUID hunter) {
		needsUpdateCompass.remove(hunter);
	}

	public static boolean needsUpdateCompass(UUID hunter) {
		return needsUpdateCompass.contains(hunter);
	}
}
