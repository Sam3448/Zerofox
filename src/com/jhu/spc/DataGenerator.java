package com.jhu.spc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataGenerator {
	
	private static final int FAVORITE_COUNT = 0;
	private static final int RETWEET_COUNT = 1;
	private static final int HASHTAG_COUNT = 2;
	private static final int MEDIA_COUNT = 3;
	private static final int MENTION_COUNT = 4;
	private static final int URL_COUNT = 5;
	private static final int PLACE = 6;
	
	public static void generate(String file){
		int count = 0;
		StringBuilder sb = new StringBuilder();
		while (count<2000){
			double[] vector = new double[7];
			int flag = Math.random()>=0.5?0:1;
			vector[FAVORITE_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			vector[RETWEET_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			vector[HASHTAG_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			vector[MEDIA_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			vector[MENTION_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			vector[URL_COUNT] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			
			int geo = Math.random()>=0.5?0:1;
			if(geo==1){
				vector[PLACE] = flag==0?(Math.random()/3+0.66):(0.4-Math.random()/3);
			}else{
				vector[PLACE] = 1;
			}
			
			double tmp = 0;
			for(int i=0;i<vector.length;i++)
				tmp += vector[i]*vector[i];
			tmp = Math.sqrt(tmp);
			
			//sb = new StringBuilder();
			sb.append("" + (count+1) + " ");
			sb.append("" + vector[FAVORITE_COUNT] + " ");
			sb.append("" + vector[RETWEET_COUNT] + " ");
			sb.append("" + vector[HASHTAG_COUNT] + " ");
			sb.append("" + vector[MEDIA_COUNT] + " ");
			sb.append("" + vector[MENTION_COUNT] + " ");
			sb.append("" + vector[URL_COUNT] + " ");
			sb.append("" + vector[PLACE] + " ");
			sb.append(flag + "\n");
			//System.out.println(sb.toString());
			count ++;
		}
		output(sb.toString(), file);
	}
	
	public static void output(String s, String path){
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
