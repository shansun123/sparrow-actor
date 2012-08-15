package com.shansun.sparrow.actor.sqlite;

import com.shansun.sparrow.actor.api.MessageQueue;
import com.shansun.sparrow.actor.internal.MessageWrapper;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public class SqliteMessageQueue implements MessageQueue<MessageWrapper> {

	@Override
	public MessageWrapper poll() {
		return null;
	}

	@Override
	public boolean add(MessageWrapper msg) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

}
