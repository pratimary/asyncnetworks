package com.utd.distributed.async.networks.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Channel {

	private Node node1;
	private Node node2;
	private List<Message> messages = new LinkedList<Message>();
	private Logger logger = LogManager.getLogger(Channel.class);

	public Channel(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public Node getNode1() {
		return node1;
	}

	public Node getNode2() {
		return node2;
	}

	public Message getMessage(int round) {
		synchronized (messages) {
			if (messages.size() != 0 && messages.get(0).getDeliveryTime() <= round) {
				return messages.remove(0);
			}
			logger.debug("No message to be exchanged between nodeId:{} and nodeId:{} for round:{}", node1.getNodeId(),
					node2.getNodeId(), round);
			return null;
		}
	}

	public void putMessage(Message message) {
		synchronized (messages) {
			messages.add(message);
			logger.debug(
					"Successfully placed message in the channel between nodeId:{} and nodeId:{} with deliveryTime:{}",
					node1.getNodeId(), node2.getNodeId(), message.getDeliveryTime());
		}
	}

}
