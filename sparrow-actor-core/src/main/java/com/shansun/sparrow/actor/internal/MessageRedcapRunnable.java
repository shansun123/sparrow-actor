package com.shansun.sparrow.actor.internal;

import org.slf4j.LoggerFactory;

import com.shansun.sparrow.actor.api.MessageQueue;
import com.shansun.sparrow.actor.api.MessageRedcapCallback;
import com.shansun.sparrow.actor.statistic.CountStatistic;
import com.shansun.sparrow.actor.statistic.Statistics;

/**
 * ��Ϣ�İ��˹����������Ϣȡ����������ָ����Actorȥ����
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class MessageRedcapRunnable implements Runnable {

	private boolean						running;

	final MessageQueue<MessageWrapper>	queue;

	final MessageRedcapCallback			callback;

	long								maximumSize	= 0;

	Thread								thisThread	= Thread.currentThread();

	volatile Object						lock		= new Object();

	CountStatistic						stat;

	public MessageRedcapRunnable(MessageRedcapCallback callback) {
		this(0, callback);
	}

	public MessageRedcapRunnable(MessageRedcapCallback callback, MessageQueue<MessageWrapper> msgQueue) {
		this(0, callback, null);
	}

	public MessageRedcapRunnable(long maximumSize, MessageRedcapCallback callback) {
		this(maximumSize, callback, null);
	}

	public MessageRedcapRunnable(long maximumSize, MessageRedcapCallback callback, MessageQueue<MessageWrapper> msgQueue) {
		super();

		// ��ʼ��������ִ���߳��߼�
		setRunning(true);

		// ��ʼ�����߳�˽�е���Ϣ����
		queue = msgQueue == null ? new MemMessageQueue() : msgQueue;

		// ������ҵ���߼������ⲿȥʵ�֣�һ��ŵ�Manager��
		this.callback = callback;
	}

	/**
	 * �����Ϣ
	 * 
	 * @param wrapper
	 */
	public boolean addMessage(MessageWrapper wrapper) {
		if (maximumSize > 0 && maximumSize <= queue.size()) {
			return false;
		}

		queue.add(wrapper);

		synchronized (lock) {
			lock.notify();
		}

		return true;
	}

	@Override
	public void run() {
		stat = Statistics.getCountStat(Thread.currentThread().getName() + "-Processed");

		thisThread = Thread.currentThread();

		synchronized (lock) {
			while (isRunning()) {
				MessageWrapper message = (MessageWrapper) queue.poll();

				if (message == null) {
					try {
						LoggerFactory.getLogger(getClass()).info("[" + Thread.currentThread().getName() + "] is idle, there is no message.");

						lock.wait();
					} catch (InterruptedException e) {
						LoggerFactory.getLogger(getClass()).warn("Thread sleep was interrupted", e);
					}

					continue;
				}

				else {
					try {
						// ������Ϣ
						callback.execute(message);

						LoggerFactory.getLogger(getClass()).info("processing message [" + message + "]");
					} catch (Throwable e) {
						LoggerFactory.getLogger(getClass()).warn("processing message encounter error", e);
					} finally {
						stat.incr();
					}
				}
			}
		}

		processLeftMessage();
	}

	/**
	 * �����ʣ�����Ϣ
	 */
	private void processLeftMessage() {
		MessageWrapper msg = null;
		while ((msg = queue.poll()) != null) {
			callback.execute(msg);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public MessageQueue<MessageWrapper> getQueue() {
		return queue;
	}

	public MessageRedcapCallback getCallback() {
		return callback;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
