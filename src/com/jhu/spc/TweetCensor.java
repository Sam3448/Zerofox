package com.jhu.spc;

import java.io.File;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TweetCensor {
	
	private static final int FAVORITE_COUNT = 0;
	private static final int RETWEET_COUNT = 1;
	private static final int HASHTAG_COUNT = 2;
	private static final int MEDIA_COUNT = 3;
	private static final int MENTION_COUNT = 4;
	private static final int URL_COUNT = 5;
	private static final int PLACE = 6;
	private static final String trainModel = "model.txt";
	
	private DataAnalyse da = new DataAnalyse(7);
	private Filter filter = new Filter();
	
	public static void main(String[] args){
		new TweetCensor().scan();
	}
	
	public void scan(){
		UserStreamListener listener = new UserStreamListener(){
	        public void onStatus(Status status) {
	        	double[] vector = new double[7];
				vector[FAVORITE_COUNT] = 1.0 - Math.pow(Math.E, -status.getFavoriteCount());
				vector[RETWEET_COUNT] = 1.0 - Math.pow(Math.E, -status.getRetweetCount());
				vector[HASHTAG_COUNT] = 1.0/(1 + status.getHashtagEntities().length);
				vector[MEDIA_COUNT] = 1.0/(1 + status.getMediaEntities().length);
				vector[MENTION_COUNT] = 1.0/(1 + status.getUserMentionEntities().length);
				vector[URL_COUNT] = 1.0/(1 + status.getURLEntities().length);
				/*
				if(status.getPlace()!=null){
					GeoLocation[] tmp = status.getPlace().getBoundingBoxCoordinates()[0];
					double lo = 0, la = 0; 
					for(GeoLocation geo : tmp){
						lo += geo.getLongitude();
						la += geo.getLatitude();
					}
					lo = lo/tmp.length;
					la = la/tmp.length;
					double dist = (la-TweetFetch.center.getLatitude())*(la-TweetFetch.center.getLatitude())
							+ (lo-TweetFetch.center.getLongitude())*(lo-TweetFetch.center.getLongitude());
					if(dist > 1){
						vector[PLACE] = 1.0/dist; 
					}else{
						vector[PLACE] = 1.0;
					}
				}else{
					vector[PLACE] = 1.0;
				}
				*/
				vector[PLACE] = 1.0;
				
				try {
					int label = (int)da.Evaluate(vector, new File(trainModel));
					String[] s = {status.getCreatedAt().toString(), status.getText(), "" + label};
					filter.textfilter(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			@Override
			public void onScrubGeo(long arg0, long arg1) {}
			@Override
			public void onStallWarning(StallWarning arg0) {}
			@Override
			public void onBlock(User arg0, User arg1) {}
			@Override
			public void onDeletionNotice(long arg0, long arg1) {}
			@Override
			public void onDirectMessage(DirectMessage arg0) {}
			@Override
			public void onFavorite(User arg0, User arg1, Status arg2) {}
			@Override
			public void onFavoritedRetweet(User arg0, User arg1, Status arg2) {}
			@Override
			public void onFollow(User arg0, User arg1) {}
			@Override
			public void onFriendList(long[] arg0) {}
			@Override
			public void onQuotedTweet(User arg0, User arg1, Status arg2) {}
			@Override
			public void onRetweetedRetweet(User arg0, User arg1, Status arg2) {}
			@Override
			public void onUnblock(User arg0, User arg1) {}
			@Override
			public void onUnfavorite(User arg0, User arg1, Status arg2) {}
			@Override
			public void onUnfollow(User arg0, User arg1) {}
			@Override
			public void onUserDeletion(long arg0) {}
			@Override
			public void onUserListCreation(User arg0, UserList arg1) {}
			@Override
			public void onUserListDeletion(User arg0, UserList arg1) {}
			@Override
			public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListSubscription(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {}
			@Override
			public void onUserListUpdate(User arg0, UserList arg1) {}
			@Override
			public void onUserProfileUpdate(User arg0) {}
			@Override
			public void onUserSuspension(long arg0) {}
	    };
	    
	    ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey("PdyPmS0jRPHFjAo4VnTq2C7A1");
		builder.setOAuthConsumerSecret("kB2E9U1lkN5q0zh7GwlNrS81mZK4pFBLuNSde5yG82K6WgARo6");
		builder.setOAuthAccessToken("795700592186445825-fgUj4W8BIeBKCc9VBtEDvZnqX0ghrf9");
		builder.setOAuthAccessTokenSecret("JRnBPvbkiV4TaMEzbbWTRjxTupBAxkbO9FoCdQsfCK1ac");
		Configuration configuration = builder.build();
	    TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.user();
	}
	
}
