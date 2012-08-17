package com.shansun.sparrow.actor.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;
import com.shansun.sparrow.actor.api.MessageQueue;
import com.shansun.sparrow.actor.api.MessageRedcapCallback;
import com.shansun.sparrow.actor.api.RejectedMessageHandler;
import com.shansun.sparrow.actor.constant.Constants;
import com.shansun.sparrow.actor.internal.MessageWrapper.SpreadMode;
import com.shansun.sparrow.actor.spi.AbstractActor;
import com.shansun.sparrow.actor.spi.ActorManager;
import com.shansun.sparrow.actor.statistic.CountStatistic;
import com.shansun.sparrow.actor.statistic.Statistics;
import com.shansun.sparrow.actor.util.RejectedMessageHandlers;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class DefaultActorManager implements ActorManager {

	/** �Ƿ��Ѿ���ɳ�ʼ�� */
	volatile boolean						initialized				= false;

	/** ������ */
	Object									lock					= new Object();

	/** �߳����ʶ */
	static int								groupCount				= 0;

	/** ���õ�ǰActorManager����������̴߳�����Ϣ */
	public int								threadCount				= Constants.DEFAULT_ACTOR_THREAD_COUNT;

	/** ʵ��ʹ���̼߳��� */
	int										realUsedThreadCount		= 0;

	/** ÿ���߳����н��������Ϣ���������У� �����Ϊ0�����ʾû������ */
	public long								maximumMessageSize		= 0;

	/**  */
	public RejectedMessageHandler			rejectedMessageHandler	= RejectedMessageHandlers.discardPolicy();

	/** �߳��� */
	ThreadGroup								threadGroup;

	/** ����������� */
	Random									random					= new Random();

	/** ִ���߳� */
	ThreadWrapper[]							threads;

	/** Actor���� */
	ConcurrentHashMap<String, Actor>		actors					= new ConcurrentHashMap<String, Actor>();

	public Class<? extends MessageQueue<?>>	messageQueueType		= MemMessageQueue.class;

	/** ����Ϣû���κ�Actor����ʱ����NoneMessageActor����ִ�� */
	public AbstractActor					noneMessageActor		= new NoneMessageActor();
	{
		noneMessageActor.setManager(this);
	}

	/** ActorManager״̬ */
	boolean									running					= false, terminated = false;

	public Logger							logger;

	// //////////////////////////////ͳ�Ƽ�����////////////////////////////////////////////////////////////

	CountStatistic							totalStat				= Statistics.getCountStat("TOTAL-ACTOR");
	CountStatistic							rejectStat				= Statistics.getCountStat("REJECT-ACTOR");
	Map<Integer, CountStatistic>			actorStat				= new HashMap<Integer, CountStatistic>();

	// ///////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize() {
		this.initialize(null);
	}

	@Override
	public void initialize(Map<String, Object> options) {
		synchronized (lock) {
			if (initialized) {
				logger.warn("DefaultActorManager has been initialized. Won't initialize again!");
				return;
			}

			initialized = running = true;

			terminated = false;

			// ������Ҫ����������߳�
			int count = getThreadCount(options);

			threadGroup = new ThreadGroup("ActorManager-ThreadGroup-" + groupCount++);

			threads = new ThreadWrapper[count];

			// �����߳�
			for (int i = 0; i < count; i++) {
				createThread(i);
			}

			// �����߳�
			for (Thread t : threads) {
				t.start();
			}

			// TODO ͳ�Ƽ����߳�
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					for (ThreadWrapper th : threads) {
						MessageRedcapRunnable runnable = (MessageRedcapRunnable) th.getRunnable();
						int size = runnable.getQueue().size();
						String msg = "[" + th.getName() + "] message in queue: " + size;
						logger.warn(msg);
					}
				}
			}, 10 * 60 * 1000, 10 * 60 * 1000);
		}
	}

	/**
	 * ������Ϣ�����߳�
	 * 
	 * @param i
	 */
	private Thread createThread(int i) {
		ThreadWrapper t = null;

		if (threads[i] != null) {
			throw new IllegalStateException("already exists: Actor-" + i);
		}

		MessageRedcapRunnable runnable = new MessageRedcapRunnable(maximumMessageSize, new MessageRedcapCallback() {

			@Override
			public void execute(MessageWrapper message) {
				Message realMessage = message.getMessage();

				boolean processed = false;

				// TODO ��Mode��Target������ͳ��

				switch (message.getMode()) {
					case BROAD_CAST: {
						for (Actor actor : actors.values()) {
							((AbstractActor) actor).process(realMessage);
							processed = true;
						}

						break;
					}
					case PEER_2_MULTI: {
						List<String> targets = message.getTargets();

						if (targets == null || targets.size() == 0)
							break;

						for (String name : actors.keySet()) {
							if (targets.contains(name)) {
								((AbstractActor) actors.get(name)).process(realMessage);
								processed = true;
							}
						}

						break;
					}
					case PEER_2_PEER: {
						String target = message.getTargetName();

						if (target == null)
							break;

						Actor actor = actors.get(target);

						if (actor == null)
							break;

						((AbstractActor) actor).process(realMessage);

						processed = true;

						break;
					}
				}

				if (!processed) {
					noneMessageActor.process(realMessage);
				}
			}
		});

		actorStat.put(i, Statistics.getCountStat("Actor-" + i));

		t = new ThreadWrapper(threadGroup, runnable, "Actor-" + i);
		t.setDaemon(true);
		t.setPriority(getThreadPriority());

		threads[i] = t;

		realUsedThreadCount++;

		return t;
	}

	/**
	 * ��ȡ�߳����ȼ�
	 * 
	 * @return
	 */
	private int getThreadPriority() {
		return Math.max(Thread.MIN_PRIORITY, Thread.currentThread().getPriority() - 1);
	}

	/**
	 * ע�⣬������ܻ��׳��쳣�����ָ���Ĳ������Ͳ��Ϸ�ʱ��
	 * 
	 * @param options
	 * @return
	 */
	private int getThreadCount(Map<String, Object> options) {
		Object xcount = options != null ? options.get(Constants.OPTION_KEY_THREAD_COUNT) : null;

		if (xcount == null) {
			return threadCount;
		} else if (xcount instanceof Integer) {
			return (Integer) xcount;
		} else if (xcount instanceof Long) {
			return ((Long) xcount).intValue();
		} else if (xcount instanceof Double) {
			return ((Double) xcount).intValue();
		} else if (xcount instanceof String) {
			return Integer.parseInt((String) xcount);
		} else {
			throw new IllegalArgumentException("thread count is not correctly setted!");
		}
	}

	@Override
	public void terminateAndWait() {
		// TODO
	}

	@Override
	public void terminate() {
		terminated = !(running = false);

		// �������̶߳�ֹͣ���������̻߳��ᴦ���û�д��������Ϣ
		for (ThreadWrapper th : threads) {
			Runnable runnable = th.getRunnable();

			if (runnable instanceof MessageRedcapRunnable) {
				((MessageRedcapRunnable) runnable).setRunning(false);
			} else {
				th.interrupt();
			}
		}

		for (Actor actor : actors.values()) {
			actor.deactivate();
		}
	}

	@Override
	public Actor createActor(Class<? extends Actor> clazz) {
		return createActor(clazz, null);
	}

	@Override
	public Actor createActor(Class<? extends Actor> clazz, Map<String, Object> options) {
		if (!AbstractActor.class.isAssignableFrom(clazz)) {
			throw new UnsupportedOperationException("actor must be typeof AbstractActor");
		}

		Actor actor = null;

		try {
			actor = clazz.newInstance();
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		synchronized (actors) {
			if (!actors.containsKey(actor.getName())) {
				try {
					((AbstractActor) actor).setManager(this);
					actors.put(actor.getName(), actor);
				} catch (Exception e) {
					throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException("mapped exception: " + e, e);
				}
			} else {
				throw new IllegalArgumentException("name already in use: " + actor.getName());
			}
		}

		return actor;
	}

	@Override
	public boolean startActor(Actor actor) {
		checkActor(actor);

		String name = actor.getName();

		synchronized (actors) {
			if (actors.containsKey(name)) {
				throw new IllegalStateException("actor [" + name + "] already started");
			}

			actor.activate();

			actors.put(name, actor);
		}

		return true;
	}

	@Override
	public boolean detachActor(String name) {
		synchronized (actors) {
			if (actors.containsKey(name)) {
				Actor actor = actors.get(name);

				if (((AbstractActor) actor).getManager() != this) {
					throw new IllegalStateException("actor not owned by this manager");
				}

				actor.deactivate();
				actors.remove(name);

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean detachActor(Actor actor) {
		checkActor(actor);

		detachActor(actor.getName());

		return false;
	}

	private void checkActor(Actor actor) {
		if (!(actor instanceof AbstractActor)) {
			throw new UnsupportedOperationException("actor must be typeof AbstractActor");
		}

		if (((AbstractActor) actor).getManager() != this) {
			throw new IllegalStateException("actor not owned by this manager");
		}
	}

	@Override
	public Actor createAndStartActor(Class<? extends Actor> clazz) {
		return createAndStartActor(clazz, null);
	}

	@Override
	public Actor createAndStartActor(Class<? extends Actor> clazz, Map<String, Object> options) {
		Actor actor = createActor(clazz, options);

		startActor(actor);

		return actor;
	}

	@Override
	public int broadcast(Message message, Actor from) {
		checkActorManagerState();

		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMessage(message);
		if (from == null) {
			wrapper.setSourceName("unknown-actor");
		} else {
			wrapper.setSourceName(from.getName());
		}
		wrapper.setMode(SpreadMode.BROAD_CAST);

		addMessageToQueue(wrapper);

		return 1;
	}

	@Override
	public int send(Message message, Actor sourceActor, String targetName) {
		checkActorManagerState();

		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMessage(message);
		if (sourceActor == null) {
			wrapper.setSourceName("unknown-actor");
		} else {
			wrapper.setSourceName(sourceActor.getName());
		}
		wrapper.setTargetName(targetName);
		wrapper.setMode(SpreadMode.PEER_2_PEER);

		addMessageToQueue(wrapper);

		return 1;
	}

	private void checkActorManagerState() {
		if (terminated || !running) {
			throw new IllegalStateException("ActorManager is terminated or not running!");
		}
	}

	@Override
	public int send(Message message, Actor sourceActor, String[] targetNames) {
		checkActorManagerState();

		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMessage(message);
		if (sourceActor == null) {
			wrapper.setSourceName("unknown-actor");
		} else {
			wrapper.setSourceName(sourceActor.getName());
		}
		wrapper.setTargets(Lists.newArrayList(targetNames));
		wrapper.setMode(SpreadMode.PEER_2_MULTI);

		addMessageToQueue(wrapper);

		return 1;
	}

	@Override
	public int send(Message message, Actor sourceActor, Collection<String> targetNames) {
		String[] list = (String[]) targetNames.toArray();

		return send(message, sourceActor, list);
	}

	/**
	 * ������Ϣ������
	 * 
	 * @param message
	 */
	protected void addMessageToQueue(MessageWrapper message) {
		int hash = message.hashCode() % realUsedThreadCount;

		if (threads.length < hash) {
			hash %= threads.length;
		}

		ThreadWrapper threadWrapper = threads[hash];

		Runnable runnable = threadWrapper.getRunnable();

		Preconditions.checkArgument(runnable instanceof MessageRedcapRunnable, "thread's target is not typeof MessageRedcapRunnable");

		boolean succ = ((MessageRedcapRunnable) runnable).addMessage(message);

		if (!succ && rejectedMessageHandler != null) {
			rejectedMessageHandler.reject(message, threadWrapper);
			rejectStat.incr();
		}

		totalStat.incr();
		actorStat.get(hash).incr();
	}

	/**
	 * ����Random�Ƿ�ֲ����� 0 -> 100055 1 -> 100196 2 -> 99803 3 -> 100186 4 -> 100040
	 * 5 -> 99969 6 -> 100198 7 -> 99653 8 -> 99955 9 -> 99945
	 */
	public static void main(String[] args) {
		Random random = new Random();

		Multiset<Integer> stat = HashMultiset.<Integer> create();

		for (int i = 0; i < 1000000; i++) {
			int nextInt = random.nextInt(10);
			stat.add(nextInt);
		}

		for (int v : stat.elementSet()) {
			System.err.println(v + " -> " + stat.count(v));
		}
	}

	public ThreadWrapper[] getThreads() {
		return threads;
	}

}
