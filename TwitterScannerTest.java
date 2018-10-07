package com.fractallabs.assignment;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import org.junit.BeforeClass;

import org.junit.Test;

public class TwitterScannerTest {

	static TwitterScanner twitterScanner;
	static ArrayList<Timestamp> sampleTimeStamps;
	
	@BeforeClass
    	public static void setUpBeforeClass() throws Exception 
	{
		twitterScanner = new TwitterScanner();
		sampleTimeStamps = new ArrayList<Timestamp>();
    	}

	
	//Check Rate calculation method with the edge conditions 
	@Test
	public void testCalculateRate() 
	{
		//calculateRate(double numTwPastHour, double numTwCurrHour)
		assertEquals(400.0, twitterScanner.calculateRate(20, 100), 0.1);
		assertEquals(-50.0,twitterScanner.calculateRate(100, 50), 0.1);
		assertEquals(0.0,twitterScanner.calculateRate(100, 100), 0.1);
		assertEquals(0.0,twitterScanner.calculateRate(0, 0), 0.1);
		assertEquals(-100.0,twitterScanner.calculateRate(10, 0), 0.1);
		assertEquals(100.0,twitterScanner.calculateRate(0, 10), 0.1);
		assertEquals(100.0,twitterScanner.calculateRate(0, 30), 0.1);
	} 

	//Check on status method with possible two conditions (change in timestamp, no change in timestamp)
	@Test
	public void testUpdateOnStatusNoChange() 
	{
		// Add 100 tweets
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2018);
		cal.set(Calendar.MONTH, 10);     
		cal.set(Calendar.DAY_OF_MONTH, 6);
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		for(int i = 0; i < 100; i++) {
			twitterScanner.updateOnStatus(new Timestamp(cal.getTimeInMillis()));
		}
		assertEquals((Long)100L, (Long)twitterScanner.numTweetPerCurrHour);
	}

	
	@Test
	public void testUpdateOnStatusUponChange() 
	{
		// Add 120 tweets
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2018);
		cal.set(Calendar.MONTH, 10);     
		cal.set(Calendar.DAY_OF_MONTH, 6);
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 1);
		for(int i = 0; i < 100; i++) {
			twitterScanner.updateOnStatus(new Timestamp(cal.getTimeInMillis()));
		}
		cal.set(Calendar.HOUR_OF_DAY, 22);
		for(int i = 0; i < 120; i++) {
			twitterScanner.updateOnStatus(new Timestamp(cal.getTimeInMillis()));
		}
		assertEquals(120, (long)twitterScanner.numTweetPerCurrHour, 0.1);
		assertEquals(100, (long)twitterScanner.numTweetPerPastHour, 0.1);
	}
}
