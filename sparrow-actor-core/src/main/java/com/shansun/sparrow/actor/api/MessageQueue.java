package com.shansun.sparrow.actor.api;

/**
 * ��Ϣ����
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-8-15
 */
public interface MessageQueue<T extends Message> {

	/**
	 * �Ƴ������ض���ͷ������Ϣ���������Ϊ�գ��򷵻�null������������
	 */
	public T poll();

	/**
	 * ���һ����Ϣ������β����������true����������������򷵻�false������������
	 * 
	 * @param msg
	 * @return
	 */
	public boolean add(T msg);

	/**
	 * ��ȡ���г���
	 * 
	 * @return
	 */
	public int size();
}
