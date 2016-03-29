package com.utd.distributed.async.networks.model;

public class Message {

	private int roundNum;
	private int sourceNodeId;
	private Node msgSource;
	private Node msgDestination;
	private int delay;
	private int deliveryTime;
	private int uid;
	private MsgType msgType;

	/**
	 * @param roundNum
	 *            - Round number for the message
	 * @param sourceNodeId
	 *            - UID of the node for which the spanning tree is constructed
	 * @param msgSource
	 *            - Specifies the node, that is sending the message
	 * @param msgDestination
	 *            - Specifies the node for which the message is sent
	 * @param uid
	 *            - Highest Seen UID by the node, which is sending the message
	 * @param msgType
	 *            - ACCEPT / REJECT / EXPLORE
	 */
	public Message(int roundNum, int sourceNodeId, Node msgSource, Node msgDestination, int uid, MsgType msgType) {
		super();
		this.roundNum = roundNum;
		this.sourceNodeId = sourceNodeId;
		this.msgSource = msgSource;
		this.msgDestination = msgDestination;
		this.uid = uid;
		this.msgType = msgType;
	}

	public int getRoundNum() {
		return roundNum;
	}

	public int getDelay() {
		return delay;
	}

	public int getDeliveryTime() {
		return deliveryTime;
	}

	public void setDelay(int delay) {
		this.delay = delay;
		this.deliveryTime = roundNum + delay;
	}

	public int getUid() {
		return uid;
	}

	public Node getMsgSource() {
		return msgSource;
	}

	public Node getMsgDestination() {
		return msgDestination;
	}

	public int getSourceNodeId() {
		return sourceNodeId;
	}

	public MsgType getMsgType() {
		return msgType;
	}

}
