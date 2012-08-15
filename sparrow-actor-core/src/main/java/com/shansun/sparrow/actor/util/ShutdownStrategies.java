package com.shansun.sparrow.actor.util;

import com.shansun.sparrow.actor.api.ShutdownStrategy;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-25
 */
public class ShutdownStrategies {

	public static ShutdownStrategy discardStrategy() {
		return new ShutdownStrategy() {
			@Override
			public void onExit() {
				// DO NOTHING
			}
		};
	}

	public static ShutdownStrategy dumpStrategy() {
		return new ShutdownStrategy() {

			@Override
			public void onExit() {
				// TODO Dumpδ�������Ϣ��tmpĿ¼�£���ʱ���
			}
		};
	}

	public static ShutdownStrategy processLeftMessageStrategy() {
		return new ShutdownStrategy() {

			@Override
			public void onExit() {
				// TODO ���������δ��ɵ���Ϣ
			}
		};
	}
}