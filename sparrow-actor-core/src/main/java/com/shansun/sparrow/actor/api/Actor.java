package com.shansun.sparrow.actor.api;

/**
 * <b>һ��Actor�ǿ��ظ�ִ�еĵ��ȵ�Ԫ. <b><br>
 * Actor���������Ϣ������ִ����������Ĳ�����<br>
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface Actor {

	/**
	 * ��ȡ��ǰActor�����ƣ����ֶβ���Ϊ�գ������޷��ҵ���Ӧ��Managerȥ������Ϣ��
	 * 
	 * @return
	 */
	String getName();

	/**
	 * ��ȡ��ǰActor����Ŀ¼
	 * 
	 * @return
	 */
	String getCategory();

	/**
	 * ���õ�ǰActorΪ��Ч����״̬�£�Actor��������������Ϣ������ִ��active()֮ǰ����Ϣ����������
	 */
	void activate();

	/**
	 * ���õ�ǰActorΪ��Ч����״̬�£�Actor���������н��յ�����Ϣ��
	 */
	void deactivate();
}
