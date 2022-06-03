package ml.ikwid.manhunt.game;

import ml.ikwid.manhunt.Manhunt;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class HuntersWin {
	private static final LiteralText msg = new LiteralText(Formatting.RED + "Hunters win!" + Formatting.RESET);

	public static void win() {
		Manhunt.LOGGER.info("hunters won");
		for(ServerPlayerEntity player : Manhunt.playerManager.getPlayerList()) {
			player.sendMessage(msg, true);
		}
	}
}
