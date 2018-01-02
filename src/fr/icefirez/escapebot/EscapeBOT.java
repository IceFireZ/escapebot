package fr.icefirez.escapebot;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import fr.icefirez.escapebot.commands.CommandMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

public class EscapeBOT implements Runnable {
	static Status status;
	private final JDA jda;
	private final CommandMap commandMap = new CommandMap(this);
	private final Scanner scanner = new Scanner(System.in);
	private boolean running;

	public EscapeBOT() throws LoginException, IllegalArgumentException, RateLimitedException {
		jda = new JDABuilder(AccountType.BOT).setToken("process.env.TOKEN")
				.buildAsync();
		jda.addEventListener(new EscapeEvents(commandMap));
		System.out.println("Bot connecté");
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public JDA getJda() {
		return jda;
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			if (scanner.hasNextLine())
				commandMap.commandConsole(scanner.nextLine());
		}

		scanner.close();
		System.out.println("Bot deconnecté.");
		jda.shutdown();
		commandMap.save();
		System.exit(0);
	}

	public static void main(String args[]) {

		try {
			EscapeBOT escapeBOT = new EscapeBOT();
			new Thread(escapeBOT, "bot").start();
		} catch (LoginException | IllegalArgumentException | RateLimitedException e) {
			e.printStackTrace();
		}

	}

	private void setupTwitter() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey("fgWO1JzIaSgI6D2dY4zihiFAT")
				.setOAuthConsumerSecret("NHcYSKe2C3X8xFcnoe6p022vvDt13Omsp8wc11SBFCXBLAPR4x")
				.setOAuthAccessToken("926024838430576640-LocIwfiN5cfqm86VlB3bmPtvhGQ1ehM")
				.setOAuthAccessTokenSecret("0hKc89uimQ51XCk3MEL9Hr2CXayZKG3PfqFcLOrSeQB7R");
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(listener);
		twitterStream.user();
	}

	private final UserStreamListener listener = new UserStreamListener() {
		@Override
		public void onStatus(Status status) {
			getJda().getGuildById("397793664530776065").getTextChannelById("397810427406581760")
					.sendMessage(status.getText()).queue();
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
		}

		@Override
		public void onDeletionNotice(long directMessageId, long userId) {
			System.out.println("Got a direct message deletion notice id:" + directMessageId);
		}

		@Override
		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
		}

		@Override
		public void onScrubGeo(long userId, long upToStatusId) {
			System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
		}

		@Override
		public void onStallWarning(StallWarning warning) {
			System.out.println("Got stall warning:" + warning);
		}

		@Override
		public void onFriendList(long[] friendIds) {
			System.out.print("onFriendList");
			for (long friendId : friendIds) {
				System.out.print(" " + friendId);
			}
			System.out.println();
		}

		@Override
		public void onFavorite(User source, User target, Status favoritedStatus) {
			System.out.println("onFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
					+ " @" + favoritedStatus.getUser().getScreenName() + " - " + favoritedStatus.getText());
		}

		@Override
		public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
			System.out.println("onUnFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
					+ " @" + unfavoritedStatus.getUser().getScreenName() + " - " + unfavoritedStatus.getText());
		}

		@Override
		public void onFollow(User source, User followedUser) {
			System.out
					.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onUnfollow(User source, User followedUser) {
			System.out
					.println("onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
		}

		@Override
		public void onDirectMessage(DirectMessage directMessage) {
			System.out.println("onDirectMessage text:" + directMessage.getText());
		}

		@Override
		public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
			System.out.println("onUserListMemberAddition added member:@" + addedMember.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
			System.out.println("onUserListMemberDeleted deleted member:@" + deletedMember.getScreenName()
					+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
			System.out.println("onUserListSubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
			System.out.println("onUserListUnsubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
					+ listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListCreation(User listOwner, UserList list) {
			System.out
					.println("onUserListCreated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListUpdate(User listOwner, UserList list) {
			System.out
					.println("onUserListUpdated  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserListDeletion(User listOwner, UserList list) {
			System.out.println(
					"onUserListDestroyed  listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
		}

		@Override
		public void onUserProfileUpdate(User updatedUser) {
			System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
		}

		@Override
		public void onUserDeletion(long deletedUser) {
			System.out.println("onUserDeletion user:@" + deletedUser);
		}

		@Override
		public void onUserSuspension(long suspendedUser) {
			System.out.println("onUserSuspension user:@" + suspendedUser);
		}

		@Override
		public void onBlock(User source, User blockedUser) {
			System.out.println("onBlock source:@" + source.getScreenName() + " target:@" + blockedUser.getScreenName());
		}

		@Override
		public void onUnblock(User source, User unblockedUser) {
			System.out.println(
					"onUnblock source:@" + source.getScreenName() + " target:@" + unblockedUser.getScreenName());
		}

		@Override
		public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
			System.out.println(
					"onRetweetedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
							+ retweetedStatus.getUser().getScreenName() + " - " + retweetedStatus.getText());
		}

		@Override
		public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
			System.out.println(
					"onFavroitedRetweet source:@" + source.getScreenName() + " target:@" + target.getScreenName()
							+ favoritedRetweet.getUser().getScreenName() + " - " + favoritedRetweet.getText());
		}

		@Override
		public void onQuotedTweet(User source, User target, Status quotingTweet) {
			System.out.println("onQuotedTweet" + source.getScreenName() + " target:@" + target.getScreenName()
					+ quotingTweet.getUser().getScreenName() + " - " + quotingTweet.getText());
		}

		@Override
		public void onException(Exception ex) {
			ex.printStackTrace();
			System.out.println("onException:" + ex.getMessage());
		}
	};

}
