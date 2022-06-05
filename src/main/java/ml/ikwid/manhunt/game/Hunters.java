package ml.ikwid.manhunt.game;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import ml.ikwid.manhunt.command.RunnerCommand;

import static ml.ikwid.manhunt.Manhunt.LOGGER;
import static ml.ikwid.manhunt.Manhunt.playerManager;

public class Hunters {
	public static final HashMap<UUID, Boolean> updatedTrackedRunner = new HashMap<>();
	public static final HashMap<UUID, Integer> handleRightClick = new HashMap<>();

	private static final LiteralText msg = new LiteralText(Formatting.RED + "Hunters win!" + Formatting.RESET);

	public static void win() {
		LOGGER.info("hunters won");
		for(ServerPlayerEntity player : playerManager.getPlayerList()) {
			player.sendMessage(msg, true);
			player.sendMessage(msg, false);
		}
	}

	private record TrackedLocation(UUID uuid, BlockPos blockPos, RegistryKey<World> dimension) {
		public boolean equals(Object o) {
			if (o instanceof TrackedLocation other) {
				return other.uuid().equals(this.uuid()) && other.blockPos().equals(this.blockPos()) && other.dimension().equals(this.dimension());
			}
			return false;
		}
	}

	private static final HashMap<UUID, TrackedLocation> hunterTracked = new HashMap<>();

	private static final HashSet<UUID> needsUpdateCompass = new HashSet<>();

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
		int index = RunnerCommand.getRunners().indexOf(getTrackedRunner(hunter));
		index++;
		if (index >= RunnerCommand.getRunners().size()) {
			index = 0;
		}
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

	public static void clear() {
		hunterTracked.clear();
		needsUpdateCompass.clear();
	}
}
