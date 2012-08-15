package com.shansun.sparrow.actor.internal;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-21
 */
public class ThreadWrapper extends Thread {

	/**
	 * ��ΪThread��target������˽�еģ����ⲻ�ɼ���������������runnable�ֶΣ��ṩ�����get����
	 */
	private Runnable	runnable;

	public Runnable getRunnable() {
		return runnable;
	}

	public ThreadWrapper() {
		super();
	}

	public ThreadWrapper(Runnable target, String name) {
		super(target, name);
		this.runnable = target;
	}

	public ThreadWrapper(Runnable target) {
		super(target);
		this.runnable = target;
	}

	public ThreadWrapper(String name) {
		super(name);
	}

	public ThreadWrapper(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		this.runnable = target;
	}

	public ThreadWrapper(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		this.runnable = target;
	}

	public ThreadWrapper(ThreadGroup group, Runnable target) {
		super(group, target);
		this.runnable = target;
	}

	public ThreadWrapper(ThreadGroup group, String name) {
		super(group, name);
	}
}
