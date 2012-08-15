package com.shansun.sparrow.actor.internal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.shansun.sparrow.actor.api.MessageQueue;

/**
 * <b>�����ڴ����Ϣ���С�</b> <br>
 * ÿ�������̻߳��Ӧһ��˽�е���Ϣ���С� <br>
 * �ڷ�����Ϣ��ʱ�򣬻��������ĳһ��˽�ж����С�
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class MemMessageQueue implements MessageQueue<MessageWrapper> {

	/** ʹ�÷��������б�����Ϣ */
	private Queue<MessageWrapper>	msgQueue	= new ConcurrentLinkedQueue<MessageWrapper>();

	public static MemMessageQueue create() {
		return new MemMessageQueue();
	}

	@Override
	public MessageWrapper poll() {
		return msgQueue.poll();
	}

	@Override
	public boolean add(MessageWrapper msg) {
		return msgQueue.add(msg);
	}

	@Override
	public int size() {
		return msgQueue.size();
	}
}
