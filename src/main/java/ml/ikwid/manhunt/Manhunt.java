package ml.ikwid.manhunt;

import ml.ikwid.manhunt.command.RegisterCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Manhunt implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("manhunt");
	public static PlayerManager playerManager;

	public static int ticks = 0;

	public static final HashMap<Integer, CopyOnWriteArrayList<Runnable>> tasks = new HashMap<>(20);

	public static final HashMap<UUID, Boolean> updatedTrackedRunner = new HashMap<>();
	public static final HashMap<UUID, Integer> handleRightClick = new HashMap<>();

	private static Runnable resetCheck = null;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> RegisterCommands.register(dispatcher));

		ServerLifecycleEvents.SERVER_STARTED.register(server -> playerManager = server.getPlayerManager());

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			ticks++;
			if(ticks % 20 == 0) {
				ticks = 0;
			}
			if(tasks.containsKey(ticks)) {
				runTasks();
			}
		});

		LOGGER.info("du du du du");

		resetCheck = () -> {
			updatedTrackedRunner.replaceAll((u, v) -> false);
			setTimeout(2, resetCheck);
		};

		setTimeout(2, resetCheck);
	}

	public static void setTimeout(int ticks, Runnable task) {
		int targetTicks = Manhunt.ticks + ticks;
		if(targetTicks >= 20) {
			targetTicks -= 20;
		}
		if(!tasks.containsKey(targetTicks)) {
			CopyOnWriteArrayList<Runnable> list = new CopyOnWriteArrayList<>();
			list.add(task);
			tasks.put(targetTicks, list);
		} else {
			tasks.get(targetTicks).add(task);
		}
	}

	public static void runTasks() {
		for(Runnable task : tasks.get(Manhunt.ticks)) {
			task.run();
			tasks.get(Manhunt.ticks).remove(task);
		}
	}
}
