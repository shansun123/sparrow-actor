package com.shansun.sparrow.actor.internal;

import java.util.List;

import com.shansun.sparrow.actor.api.Actor;
import com.shansun.sparrow.actor.api.Message;

/**
 * ��Ϣ���װ
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-20
 */
public class MessageWrapper implements Message {

	/**
	 * ��Ϣ����
	 */
	private Message			message;

	/**
	 * ��ϢԴ
	 */
	private String			sourceName;

	/**
	 * ��ϢĿ��
	 */
	private String			targetName;

	/**
	 * ��ϢĿ���б�
	 */
	private List<String>	targets;

	/**
	 * ִ�з�ʽ��
	 */
	private SpreadMode		mode;

	/**
	 * ������ʽ
	 * 
	 * @author: lanbo <br>
	 * @version: 1.0 <br>
	 * @date: 2012-7-21
	 */
	public static enum SpreadMode {
		PEER_2_PEER, // ��Ե㷢��
		PEER_2_MULTI, // ��Զ��
		BROAD_CAST, // �㲥
	}

	@Override
	public String getSubject() {
		return message.getSubject();
	}

	@Override
	public Object getData() {
		return message.getData();
	}

	public Actor getSource() {
		return message.getSource();
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public SpreadMode getMode() {
		return mode;
	}

	public void setMode(SpreadMode mode) {
		this.mode = mode;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageWrapper other = (MessageWrapper) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
