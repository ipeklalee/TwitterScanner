package com.fractallabs.assignment;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;


public class TwitterScanner 
{
	
	//Initialization of global variables.
	private String companyName; 
 	TwitterStream twitterStream;
 	Long numTweetPerCurrHour = null;
 	Long numTweetPerPastHour = null;
 	Long value = null;
 	Long hour = null;
 	Long currentHour = null;
 	Calendar calH = null;
 	Calendar cal = null;
 	double rate = 0.0;
 	ArrayList<TSValue> TSValues = new ArrayList<TSValue>();
 	
 	
	public static class TSValue
	{
		private final Instant timestamp;
		private final double val;
		
		public TSValue(Instant timestamp, double val)
		{
			this.timestamp = timestamp;
			this.val = val;
		}
		
		public Instant getTimestamp()
		{
			return timestamp;
		}
		
		public double getVal()
		{
			return val;
		}
	}
	
	//empty Constructor for unit testing
	public TwitterScanner()
	{
		
	}
	
	public TwitterScanner (String companyname)
	{
		companyName = companyname;			
	}
	
	public void run()
	{
		//Twitter listener for streaming 
		StatusListener listener = new StatusListener() {
	        @Override
	        public void onException(Exception e) {
	            e.printStackTrace();
	        }
	        @Override
	        public void onDeletionNotice(StatusDeletionNotice arg) {
	        }
	        @Override
	        public void onScrubGeo(long userId, long upToStatusId) {
	        }
	        @Override
	        public void onStallWarning(StallWarning warning) {
	        }
	        @Override
	        public void onStatus(Status status) 
	        {    
	        		//query is ensured with if statement below
		    		if(!status.getText().contains(companyName))
		    		{
		    			return;
		    		}
		    		Timestamp creationTs=new Timestamp(status.getCreatedAt().getTime());
	        		updateOnStatus(creationTs);
	        }
	        
	        @Override
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	        }
	    };
    
	    twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);  
	    	twitterStream.sample();
	    	
	}
	
	//Rate method is called in below method to calculate the relative change
	public void updateOnStatus(Timestamp tmsp)
	{
		Timestamp ts=tmsp; 
		Date creationDate = ts;
		currentHour = creationDate.getTime();
		// Calendar objects are needed to hold old and current values in hour-min-sec format
		cal = Calendar.getInstance();
		cal.setTimeInMillis(currentHour);
		
		// to rate changes secondly use onesecondDifference for hourly use oneHour Difference
		//second is put for testing purposes
		int oneSecondDiff = 1000;
		int oneHourDiff = 3600000;
		
		// Initilaization
		if(numTweetPerCurrHour == null) {
			numTweetPerCurrHour = (long) 1;
			hour = currentHour;
			calH = Calendar.getInstance();
			calH.setTimeInMillis(hour);
			return;
		}
		
		// Dump
		if((currentHour - hour) >= oneHourDiff) { 
			
			if(numTweetPerPastHour != null)
			{
				System.out.println("Current timestamp: " + tmsp);
				System.out.println("number of tweets in this timestamp: " + numTweetPerCurrHour);
				System.out.println("number of tweets in previous timestamp: " + numTweetPerPastHour);
			
				rate = calculateRate(numTweetPerPastHour, numTweetPerCurrHour);
				
				System.out.print("Rate of change: ");
				System.out.printf("%.2f", rate);
				System.out.println();
				
				if(rate < 0)
					System.out.println("%" + -rate + " decrease observed between timestamps " + calH.get(Calendar.HOUR_OF_DAY) + "-" + calH.get(Calendar.MINUTE)+ "-" + calH.get(Calendar.SECOND) + " and " + cal.get(Calendar.HOUR_OF_DAY) + "/" + cal.get(Calendar.MINUTE)+ "/" + cal.get(Calendar.SECOND)  );
				if(rate > 0)
					System.out.println("%" + rate + " increase observed between timestamps " + + calH.get(Calendar.HOUR_OF_DAY) + "-" + calH.get(Calendar.MINUTE)+ "-" + calH.get(Calendar.SECOND) + " and " + cal.get(Calendar.HOUR_OF_DAY) + "/" + cal.get(Calendar.MINUTE)+ "/" + cal.get(Calendar.SECOND) );
				if(rate ==0)
					System.out.println(0 + " there is no change or the stream is initiated recently" );
			}
			else
			{
				rate = 0;
				System.out.println("Current timestamp: " + tmsp);
				System.out.println("number of tweets in this timestamp: " + numTweetPerCurrHour);
				System.out.println("number of tweets in previous timestamp: " + numTweetPerPastHour);
				System.out.print("Rate of change: ");
				System.out.printf("%.2f", rate);
				System.out.println();				
			}
			numTweetPerPastHour = numTweetPerCurrHour;
			TSValue timeStVal = new TSValue(ts.toInstant(), rate);
			storeValue(timeStVal);
			
			numTweetPerCurrHour = (long) 1;
			hour = currentHour;
			return;
		}
		
		// Update
		if((currentHour- hour) < oneSecondDiff)
		{
			numTweetPerCurrHour += 1;
		}
	}
	
	//Calculate Rate
	public double calculateRate(double numTwPastHour, double numTwCurrHour)
	{
		if (numTwPastHour == 0 && numTwCurrHour == 0)
		{
			return 0;
		}
		if (numTwPastHour == 0 && numTwCurrHour != 0)
		{
			return 100;
		}
		if (numTwPastHour != 0 && numTwCurrHour == 0)
		{
			return -100;
		}
	    double rateIn = ((numTwPastHour - numTwCurrHour)/numTwPastHour)*-100;
		return rateIn;
	}
	
	private void storeValue(TSValue value)
	{
		TSValues.add(value);
	}

	
	public static void main(String ... args) throws TwitterException
	{
		TwitterScanner scanner = new TwitterScanner("el");
		scanner.run();

	}

}
