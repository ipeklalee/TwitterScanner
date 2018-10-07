# About
A class implementation that follows messages on [Twitter’s Streaming API](https://developer.twitter.com/en/docs) and counts how often a given company (e.g. “Facebook”) is mentioned. Every hour, the relative change is stored.

_LIBRARIES:_ <br/>
1- [twitter4j Stream 4.0.7](http://twitter4j.org/en/index.html) - Twitter4J is an unofficial Java library for the Twitter API <br/>

_VERSION:_ <br/>
Java 8 or higher is required to run this application. [Java SE Development Kit 8 Downloads](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html") <br/>

# Unit Tests Included

**Rate Calculation Method** is tested with edge conditions as following:
- #tweets in the past hour is greater than #tweets in the current hour and vice versa
- #tweets in the past hour is equal to #tweets in the current hour
- #tweets in the past hour and #tweets in the current hour are both equal to 0
- #tweets in the past hour equals to 0 and #tweets in the current hour are both equals to a double value and vice versa

**Update on status Method** is tested with possible two conditions as following:
- When there is no change in the timestamp
- When there is a change in the timestamp




