package com.shansun.sparrow.actor.api;

import com.shansun.sparrow.actor.internal.MessageWrapper;
import com.shansun.sparrow.actor.internal.ThreadWrapper;


/**
 * �ܾ���Ϣ�Ĳ���
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-24
 */
public interface RejectedMessageHandler {

	/**
	 * �ܾ�������Ϣ����������Ϣ��������������Actor�����쳣��
	 * 
	 * @param message
	 *            ���ܾ�����Ϣ
	 * @param thread
	 *            ����ܾ����߳�
	 */
	public void reject(MessageWrapper message, ThreadWrapper thread);
}
