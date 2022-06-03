package ml.ikwid.manhunt.game;

import ml.ikwid.manhunt.Manhunt;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class RunnersWin {
	public static boolean dragonKilled = false;
	private static final LiteralText msg = new LiteralText(Formatting.GREEN + "Runners win!" + Formatting.RESET);

	public static void win() {
		Manhunt.LOGGER.info("runners won");
		for(ServerPlayerEntity player : Manhunt.playerManager.getPlayerList()) {
			player.sendMessage(msg, true);
		}
	}
}
