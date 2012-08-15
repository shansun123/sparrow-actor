package com.shansun.sparrow.actor.spi;

import java.util.Collection;
import java.util.Map;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public interface ActorManager {

	Actor createActor(Class<? extends Actor> clazz);

	Actor createActor(Class<? extends Actor> clazz, Map<String, Object> options);

	boolean startActor(Actor actor);

	boolean detachActor(String name);

	boolean detachActor(Actor actor);

	Actor createAndStartActor(Class<? extends Actor> clazz);

	Actor createAndStartActor(Class<? extends Actor> clazz, Map<String, Object> options);

	/**
	 * ������Ϣ��ָ��Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            ��Ϣ��ԴActor�����Ϊnull������Զ�����ΪUnknownActor.
	 * @param targetName
	 * @return
	 */
	int send(Message message, Actor sourceActor, String targetName);

	/**
	 * ������Ϣ��ָ��Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            ��Ϣ��ԴActor�����Ϊnull������Զ�����ΪUnknownActor.
	 * @param targetNames
	 * @return
	 */
	int send(Message message, Actor sourceActor, String[] targetNames);

	/**
	 * ������Ϣ��ָ��Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            ��Ϣ��ԴActor�����Ϊnull������Զ�����ΪUnknownActor.
	 * @param targetNames
	 * @return
	 */
	int send(Message message, Actor sourceActor, Collection<String> targetNames);

	/**
	 * �㲥��Ϣ������Actor
	 * 
	 * @param message
	 * @param sourceActor
	 *            ��Ϣ��ԴActor�����Ϊnull������Զ�����ΪUnknownActor.
	 * @return
	 */
	int broadcast(Message message, Actor sourceActor);

	/**
	 * ��ʼ��
	 */
	void initialize();

	/**
	 * �������ĳ�ʼ��
	 * @param options
	 */
	void initialize(Map<String, Object> options);

	/**
	 * ֹͣActor����
	 */
	void terminateAndWait();

	/**
	 * ֹͣActor����
	 */
	void terminate();
}
