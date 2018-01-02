package fr.icefirez.escapebot.commands;

import fr.icefirez.escapebot.EscapeBOT;
import fr.icefirez.escapebot.commands.Command.ExecutorType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

public class AllCommands {

	private final EscapeBOT escapeBOT;
	private final CommandMap commandMap;

	public AllCommands(EscapeBOT escapeBOT, CommandMap commandMap) {
		this.escapeBOT = escapeBOT;
		this.commandMap = commandMap;
	}

	@Command(name = "stop", type = ExecutorType.CONSOLE)
	private void stop() {
		escapeBOT.setRunning(false);
	}

	@SuppressWarnings("deprecation")
	@Command(name = "game", type = ExecutorType.USER)
	private void game(JDA jda, String[] args) {
		StringBuilder builder = new StringBuilder();
		for (String str : args) {
			if (builder.length() > 0)
				builder.append(" ");
			builder.append(str);
		}

		jda.getPresence().setGame(Game.of(builder.toString()));
	}

}
