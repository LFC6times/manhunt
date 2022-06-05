package ml.ikwid.manhunt.game;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import ml.ikwid.manhunt.Manhunt;

import static ml.ikwid.manhunt.Manhunt.LOGGER;

public class Runners {
	public static boolean dragonKilled = false;
	private static final LiteralText msg = new LiteralText(Formatting.GREEN + "Runners win!" + Formatting.RESET);

	public static void win() {
		if(!dragonKilled) {
			LOGGER.info("runners won");
			for (ServerPlayerEntity player : Manhunt.playerManager.getPlayerList()) {
				player.sendMessage(msg, true);
				player.sendMessage(msg, false);
			}
		}
	}
}
