package com.utd.distributed.async.networks.model;

import java.util.List;

public class Node {

	private int nodeId;
	private List<Channel> channels;

	public Node(int nodeId) {
		super();
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder.append("[NodeId:").append(nodeId).append(" Channels:").append(channels).append("]").toString();
	}

}
