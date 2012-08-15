package com.shansun.sparrow.actor.statistic;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-13
 */
public interface CountStatistic extends Statistic {

	long getCount();

	long incr();
	
	long clear();
}
