package com.shansun.sparrow.actor.api;


/**
 * ��Ϣ��
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface Message {

	/** �Ѿ������Ĵ��� */
	public int	redoCount	= 0;

	/**
	 * ��ȡ��Ϣ��Դ
	 * 
	 * @return
	 */
	Actor getSource();

	/**
	 * ��ȡ��Ϣ����
	 * 
	 * @return
	 */
	String getSubject();

	/**
	 * ��Ϣ����
	 * 
	 * @return
	 */
	Object getData();
}
