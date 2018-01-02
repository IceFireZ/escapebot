package fr.icefirez.escapebot;

import fr.icefirez.escapebot.commands.CommandMap;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class EscapeEvents implements EventListener {
	private final CommandMap cM;

	public EscapeEvents(CommandMap cM) {
		this.cM = cM;
	}

	@Override
	public void onEvent(Event e) {
		if (e instanceof MessageReceivedEvent)
			onMessage((MessageReceivedEvent) e);
	}

	private void onMessage(MessageReceivedEvent e) {
		if (e.getAuthor().equals(e.getJDA().getSelfUser()))
			return;

		String message = e.getMessage().getContent();
		if (message.startsWith(cM.getTag())) {
			message = message.replaceFirst(cM.getTag(), "");
			if (cM.commandUser(e.getAuthor(), message, e.getMessage())) {
				if (e.getTextChannel() != null
						&& e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
					e.getMessage().delete().queue();
				}
			}
		}

	}

}
