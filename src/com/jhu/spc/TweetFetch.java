package com.jhu.spc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TweetFetch {
	
	private Twitter twitter;
	public static GeoLocation center;
	
	private static final int FAVORITE_COUNT = 0;
	private static final int RETWEET_COUNT = 1;
	private static final int HASHTAG_COUNT = 2;
	private static final int MEDIA_COUNT = 3;
	private static final int MENTION_COUNT = 4;
	private static final int URL_COUNT = 5;
	private static final int PLACE = 6;
	
	private static final String data = "output.txt";
	private static final String trainModel = "model.txt";
	
	public static void main(String[] args){
		TweetFetch tf = new TweetFetch();
		tf.init();
		//tf.fetch();
		//tf.getGeoCenter(file1);
		//tf.processFile(file1, file2);
		DataGenerator.generate(data);
		DataAnalyse da = new DataAnalyse(7);
		try {
			da.Train(new File(data), new File(trainModel));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey("PdyPmS0jRPHFjAo4VnTq2C7A1");
		builder.setOAuthConsumerSecret("kB2E9U1lkN5q0zh7GwlNrS81mZK4pFBLuNSde5yG82K6WgARo6");
		builder.setOAuthAccessToken("795700592186445825-fgUj4W8BIeBKCc9VBtEDvZnqX0ghrf9");
		builder.setOAuthAccessTokenSecret("JRnBPvbkiV4TaMEzbbWTRjxTupBAxkbO9FoCdQsfCK1ac");
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
	}
	
	public void fetch(){
		StringBuilder sb = new StringBuilder();
		try {
			List<Status> l = twitter.getUserTimeline();
			GeoLocation[] tmp;
			for(Status s : l){
				sb.append("" + s.getFavoriteCount() + " ");
				sb.append("" + s.getRetweetCount() + " ");
				sb.append("" + s.getHashtagEntities().length + " ");
				sb.append("" + s.getMediaEntities().length + " ");
				sb.append("" + s.getUserMentionEntities().length + " ");
				sb.append("" + s.getURLEntities().length + " ");
				if(s.getPlace()!=null && s.getPlace().getBoundingBoxCoordinates() != null){
					tmp = s.getPlace().getBoundingBoxCoordinates()[0];
					double lo = 0, la = 0; 
					for(GeoLocation geo : tmp){
						lo += geo.getLongitude();
						la += geo.getLatitude();
					}
					sb.append("" + la/4 + "," + lo/4 + "\n");
				}else
					sb.append("null\n");
				System.out.println(s.getFavoriteCount());
			}
			output(sb.toString(), data);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	public void processFile(String src, String dest){
		try {
			File file = new File(src);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			StringBuilder sb = new StringBuilder();
			int count = 1;
			while((line = br.readLine())!=null){
				String[] status = line.split(" ");
				double[] vector = new double[7];
				//vector[FAVORITE_COUNT] = 1.0 - Math.pow(Math.E, -Integer.parseInt(status[FAVORITE_COUNT]));
				//vector[RETWEET_COUNT] = 1.0 - Math.pow(Math.E, -Integer.parseInt(status[RETWEET_COUNT]));
				vector[FAVORITE_COUNT] = 1.0;
				vector[RETWEET_COUNT] = 1.0;
				vector[HASHTAG_COUNT] = 1.0/(1 + Integer.parseInt(status[HASHTAG_COUNT]));
				vector[MEDIA_COUNT] = 1.0/(1 + Integer.parseInt(status[MEDIA_COUNT]));
				vector[MENTION_COUNT] = 1.0/(1 + Integer.parseInt(status[MENTION_COUNT]));
				vector[URL_COUNT] = 1.0/(1 + Integer.parseInt(status[URL_COUNT]));
				if(!status[PLACE].equals("null")){
					String[] loc = status[PLACE].split(",");
					double la = Double.parseDouble(loc[0]);
					double lo = Double.parseDouble(loc[1]);
					double dist = (la-center.getLatitude())*(la-center.getLatitude()) + (lo-center.getLongitude())*(lo-center.getLongitude());
					if(dist > 1){
						vector[PLACE] = 1.0/dist; 
					}else{
						vector[PLACE] = 1.0;
					}
				}else{
					vector[PLACE] = 1.0;
				}
				
				double tmp = 0;
				for(int i=0;i<vector.length;i++){
					tmp += vector[i]*vector[i];
				}
				tmp = Math.sqrt(tmp);
				
				for(int i=0;i<1;i++){
					sb = new StringBuilder();
					sb.append("" + count + " ");
					sb.append("" + vector[FAVORITE_COUNT]/tmp + " ");
					sb.append("" + vector[RETWEET_COUNT]/tmp + " ");
					sb.append("" + vector[HASHTAG_COUNT]/tmp + " ");
					sb.append("" + vector[MEDIA_COUNT]/tmp + " ");
					sb.append("" + vector[MENTION_COUNT]/tmp + " ");
					sb.append("" + vector[URL_COUNT]/tmp + " ");
					sb.append("" + vector[PLACE]/tmp + " ");
					sb.append(status[PLACE+1]);
					System.out.println(sb.toString());
					count ++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getGeoCenter(String src){
		try {
			File file = new File(src);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			double latitude = 0, longitude = 0;
			int count = 0;
			while((line = br.readLine())!=null){
				String[] status = line.split(" ");
				if(!status[PLACE].equals("null") && status[PLACE+1].equals("0")){
					String[] loc = status[PLACE].split(",");
					latitude += Double.parseDouble(loc[0]);
					longitude += Double.parseDouble(loc[1]);
					count ++;
				}
			}
			center = new GeoLocation(latitude/count, longitude/count);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void output(String s, String path){
		try {
			File f = new File(path);
			FileWriter fw = new FileWriter(f);
			fw.write(s);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
