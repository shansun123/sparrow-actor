package com.shansun.sparrow.actor.api;

import com.shansun.sparrow.actor.internal.MessageWrapper;

/**
 * ��Ϣ�����̻߳�ȡ����Ϣ�󣬽������߼�����callbackȥ��
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-21
 */
public interface MessageRedcapCallback {

	/**
	 * ������Ϣ
	 * 
	 * @param message
	 */
	void execute(MessageWrapper message);
}
