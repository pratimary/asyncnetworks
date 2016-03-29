package com.utd.distributed.async.networks.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Node implements Runnable {

	private static Logger logger = LogManager.getLogger(Node.class);

	private int nodeId;
	private int highestIdSeenSoFar;
	private boolean isRoundComplete = true;
	private NodeStatus status;
	private int roundNum;
	private List<Channel> channels;
	private Map<Integer, Node> sourceVsParentNode = new HashMap<Integer, Node>();
	private Map<Integer, List<Node>> sourceVsWaitList = new HashMap<Integer, List<Node>>();

	public Node(int nodeId) {
		super();
		this.nodeId = nodeId;
		this.highestIdSeenSoFar = nodeId;
		this.status = NodeStatus.UNKNOWN;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (isRoundComplete() == false) {
					synchronized (this) {
						if (roundNum == 1) {
							sendMsgToAllNeighbors();
						}
						readMessageFromNeighbors();
						setRoundCompleteFlag(true);
					}
				} else {
					Thread.sleep(150);
				}
			}
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}

	private void sendMsgToAllNeighbors() {
		List<Node> waitList = new LinkedList<Node>();
		for (Channel channel : channels) {
			Node dest = channel.getOtherNode(this);
			Message msg = new Message(roundNum, nodeId, this, dest, nodeId, MsgType.EXPLORE);
			channel.putMessage(msg);
			waitList.add(dest);
		}
		sourceVsWaitList.put(nodeId, waitList);
		sourceVsParentNode.put(nodeId, null);
	}

	private void readMessageFromNeighbors() {
		for (Channel channel : channels) {
			Message message = channel.getMessage(roundNum, this);

			if (message == null)
				continue;

			Integer sourceNodeId = message.getSourceNodeId();

			if (message.getUid() > highestIdSeenSoFar) {
				highestIdSeenSoFar = message.getUid();
			}

			if (message.getMsgType() == MsgType.EXPLORE) {
				if (sourceVsParentNode.containsKey(sourceNodeId)) {
					Node dest = message.getMsgSource();
					sendRejectMsg(sourceNodeId, dest, channel);
				} else {
					sourceVsParentNode.put(sourceNodeId, message.getMsgSource());
					sendExploreMsgToNeighbors(sourceNodeId, highestIdSeenSoFar);
				}
			} else {
				List<Node> waitingList = sourceVsWaitList.get(sourceNodeId);
				Iterator<Node> iterator = waitingList.iterator();
				while (iterator.hasNext()) {
					Node next = iterator.next();
					if (next == message.getMsgSource()) {
						iterator.remove();
						break;
					}
				}
				if (waitingList.isEmpty()) {
					if (message.getSourceNodeId() == this.nodeId) {
						if ((highestIdSeenSoFar == this.nodeId)) {
							status = NodeStatus.LEADER;
						} else {
							status = NodeStatus.NON_LEADER;
						}
					} else {
						sendReplyToParent(sourceNodeId, message);
					}
				}
			}

		}

	}

	private void sendReplyToParent(Integer sourceNodeId, Message oldMsg) {
		Node parent = sourceVsParentNode.get(sourceNodeId);
		for (Channel channel : channels) {
			if (channel.isNodeInTheChannel(parent)) {
				Message msg = new Message(roundNum + 1, sourceNodeId, this, parent, highestIdSeenSoFar, MsgType.ACCEPT);
				channel.putMessage(msg);
				break;
			}
		}
	}

	private void sendExploreMsgToNeighbors(Integer sourceNodeId, int uid) {
		for (Channel channel : channels) {
			Node dest = channel.getOtherNode(this);
			Message msg = new Message(roundNum + 1, sourceNodeId, this, dest, highestIdSeenSoFar, MsgType.EXPLORE);
			channel.putMessage(msg);
			List<Node> waitingList = sourceVsWaitList.get(sourceNodeId);
			if (waitingList == null) {
				waitingList = new LinkedList<Node>();
				sourceVsWaitList.put(sourceNodeId, waitingList);
			}
			waitingList.add(dest);
		}

	}

	private void sendRejectMsg(Integer sourceNodeId, Node dest, Channel channel) {
		Message msg = new Message(roundNum + 1, sourceNodeId, this, dest, highestIdSeenSoFar, MsgType.REJECT);
		channel.putMessage(msg);
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

	public synchronized void setRoundCompleteFlag(boolean flag) {
		this.isRoundComplete = flag;
	}

	public boolean isRoundComplete() {
		return isRoundComplete;
	}

	public NodeStatus getStatus() {
		return status;
	}

	public void incrementRoundNumber() {
		roundNum++;
		return;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder.append("[NodeId:").append(nodeId).append(" roundNo:").append(roundNum).append(" Channels:")
				.append(channels).append(" highestIDSeen:").append(highestIdSeenSoFar).append(" status:")
				.append(status.toString()).append("]").toString();
	}

}
