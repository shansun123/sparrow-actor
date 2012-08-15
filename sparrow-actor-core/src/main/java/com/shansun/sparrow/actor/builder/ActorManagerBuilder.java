package com.shansun.sparrow.actor.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.shansun.sparrow.actor.api.MessageQueue;
import com.shansun.sparrow.actor.api.RejectedMessageHandler;
import com.shansun.sparrow.actor.constant.Constants;
import com.shansun.sparrow.actor.internal.DefaultActorManager;
import com.shansun.sparrow.actor.spi.AbstractActor;
import com.shansun.sparrow.actor.spi.ActorManager;

/**
 * ActorManagerµÄÉú³ÉÆ÷
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-23
 */
public class ActorManagerBuilder {

	private int									threadCount			= Constants.DEFAULT_ACTOR_THREAD_COUNT;

	private AbstractActor						noneMessageActor	= null;

	private long								maximumMessageSize	= 0;

	private RejectedMessageHandler				rejectedMessageHanlder;

	private Logger								logger				= LoggerFactory.getLogger(DefaultActorManager.class);

	private Class<? extends MessageQueue<?>>	messageQueueType;

	public static ActorManagerBuilder newBuilder() {
		return new ActorManagerBuilder();
	}

	public ActorManagerBuilder withThreadCount(int count) {
		Preconditions.checkArgument(count > 0);
		this.threadCount = count;
		return this;
	}

	public ActorManagerBuilder withNoneMessageActor(AbstractActor actor) {
		Preconditions.checkNotNull(actor);
		this.noneMessageActor = actor;
		return this;
	}

	public ActorManagerBuilder withMaximumMessageSize(long maximum) {
		this.maximumMessageSize = maximum;
		return this;
	}

	public ActorManagerBuilder withRejectedMessageHandler(RejectedMessageHandler handler) {
		Preconditions.checkNotNull(handler);
		this.rejectedMessageHanlder = handler;
		return this;
	}

	public ActorManagerBuilder withLogger(Logger logger) {
		Preconditions.checkNotNull(logger);
		this.logger = logger;
		return this;
	}

	public ActorManagerBuilder useMessageQueue(Class<? extends MessageQueue<?>> queueType) {
		Preconditions.checkNotNull(queueType);
		this.messageQueueType = queueType;
		return this;
	}

	public ActorManager build() {
		DefaultActorManager manager = new DefaultActorManager();

		if (noneMessageActor != null) {
			manager.noneMessageActor = noneMessageActor;
		}

		manager.threadCount = threadCount;

		manager.maximumMessageSize = maximumMessageSize;

		manager.rejectedMessageHandler = rejectedMessageHanlder;

		manager.logger = logger;

		manager.messageQueueType = messageQueueType;

		return manager;
	}
}
