package com.utd.distributed.async.networks.model;

public class Message {

	private int roundNum;
	private Node source;
	private Node destination;
	private int delay;
	private int deliveryTime;

	public Message(int roundNum, Node source, Node destination) {
		super();
		this.roundNum = roundNum;
		this.source = source;
		this.destination = destination;
	}

	public int getRoundNum() {
		return roundNum;
	}

	public Node getSource() {
		return source;
	}

	public Node getDestination() {
		return destination;
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
}
