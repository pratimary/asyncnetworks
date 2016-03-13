package com.utd.distributed.async.networks.model;

import java.util.List;

public class Node {

	private int nodeId;
	private List<Channel> channels;

	public Node(int nodeId, List<Channel> channels) {
		super();
		this.nodeId = nodeId;
		this.channels = channels;
	}

	public int getNodeId() {
		return nodeId;
	}

	public List<Channel> getChannels() {
		return channels;
	}

}
