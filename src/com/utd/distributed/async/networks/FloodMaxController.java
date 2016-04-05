package com.utd.distributed.async.networks;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.utd.distributed.async.networks.model.Node;
import com.utd.distributed.async.networks.model.NodeStatus;
import com.utd.distributed.async.networks.util.FloodMaxInputParser;

public class FloodMaxController {

	private static final String INPUT_FILE_KEY = "inputFile";

	private static Logger logger = LogManager.getLogger(FloodMaxController.class);

	private static List<Node> nodes;

	private static List<Thread> threads;

	public static void main(String[] args) {

		String inputFileLoc = System.getProperty(INPUT_FILE_KEY);

		if (inputFileLoc == null)
			inputFileLoc = "conf/connectivity.txt";

		FloodMaxInputParser parser = new FloodMaxInputParser(inputFileLoc);
		try {
			nodes = parser.parseInputFile();
			logger.info(nodes);
			startFloodMaxAlgo();
			printFinalOutput();
		} catch (Exception e) {
			logger.error("Failed to parse", e);
		}

	}

	private static void printFinalOutput() {
		for (Node node : nodes) {
			logger.info(node);
		}
	}

	private static void startFloodMaxAlgo() throws InterruptedException {
		threads = createThreads();
		while (true) {
			if (isLeaderElected()) {
				terminateThreads();
				break;
			}
			requestNodesToStart();
			waitForNodesToComplete();
		}
	}

	private static void printDetails() {
		for (Node node : nodes) {
			logger.debug(node);
		}
	}

	private static void waitForNodesToComplete() throws InterruptedException {
		logger.debug("Waiting for threads to complete current round");
		for (Node node : nodes) {
			while (true) {
				if (node.isRoundComplete() == false)
					Thread.sleep(100);
				else
					break;
			}
		}
		logger.debug("All threads completed current round");
		printDetails();
	}

	private static void requestNodesToStart() {
		logger.debug("Incrementing round number.");
		for (Node node : nodes) {
			node.incrementRoundNumber();
			node.setRoundCompleteFlag(false);
		}

	}

	private static void terminateThreads() {
		for (Thread thread : threads) {
			thread.interrupt();
		}

	}

	private static boolean isLeaderElected() {
		for (Node node : nodes) {
			if (node.getStatus() == NodeStatus.UNKNOWN)
				return false;
		}
		return true;
	}

	private static List<Thread> createThreads() {
		List<Thread> threads = new LinkedList<Thread>();
		for (Node node : nodes) {
			Thread thread = new Thread(node, "Node:" + String.valueOf(node.getNodeId()));
			threads.add(thread);
			thread.start();
		}
		return threads;
	}

}
