package com.utd.distributed.async.networks.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.utd.distributed.async.networks.exceptions.FloodMaxInputParserException;
import com.utd.distributed.async.networks.exceptions.InvalidInputException;
import com.utd.distributed.async.networks.model.Channel;
import com.utd.distributed.async.networks.model.Node;

public class FloodMaxInputParser {

	private static Logger logger = LogManager.getLogger(FloodMaxInputParser.class);

	private String fileLocation;
	private List<Node> nodes = new LinkedList<Node>();

	public FloodMaxInputParser(String fileLocation) {
		super();
		this.fileLocation = fileLocation;
	}

	public List<Node> parseInputFile() throws FloodMaxInputParserException {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			logger.info("Reading the input data from the file: {}", fileLocation);
			fileReader = new FileReader(new File(fileLocation));
			bufferedReader = new BufferedReader(fileReader);
			int n = Integer.parseInt(bufferedReader.readLine());

			createNodes(bufferedReader, n);
			createChannels(bufferedReader, n);

		} catch (Exception e) {
			throw new FloodMaxInputParserException(e);
		} finally {
			try {
				if (fileReader != null)
					fileReader.close();

				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
				throw new FloodMaxInputParserException(e);
			}
		}
		return nodes;
	}

	private void createChannels(BufferedReader bufferedReader, int n) throws IOException, InvalidInputException {
		Map<String, Channel> channelMap = new HashMap<String, Channel>();
		for (int i = 0; i < n; i++) {
			String line = bufferedReader.readLine();
			StringTokenizer tokenizer = new StringTokenizer(line);

			if (tokenizer.countTokens() != n)
				throw new InvalidInputException("Number of channels specified for nodeNumber:" + (i)
						+ " is not equal to the number of nodes" + n + ". Verify the input data!");

			List<Channel> channels = new LinkedList<Channel>();
			for (int j = 0; tokenizer.hasMoreTokens(); j++) {
				String value = tokenizer.nextToken();
				// A node will be connected to itself. It is unnecessary to
				// create a channel with itself.
				if (i == j)
					continue;
				Integer connectedValue = Integer.parseInt(value);
				if (connectedValue == 1) {
					String connectedNode = String.valueOf(j);
					if (channelMap.containsKey(i + connectedNode)) {
						channels.add(channelMap.get(i + connectedNode));
					} else if (channelMap.containsKey(connectedNode + i)) {
						channels.add(channelMap.get(connectedNode + i));
					} else {
						Channel channel = new Channel(nodes.get(i), nodes.get(j));
						channels.add(channel);
						channelMap.put(connectedNode + i, channel);
					}
				}
			}
			nodes.get(i).setChannels(channels);
		}
	}

	private void createNodes(BufferedReader bufferedReader, int n) throws IOException, InvalidInputException {
		String line = bufferedReader.readLine();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			String nodeId = tokenizer.nextToken();
			Node node = new Node(Integer.parseInt(nodeId));
			nodes.add(node);
		}

		if (nodes.size() != n) {
			throw new InvalidInputException("Number of nodes created (" + nodes.size()
					+ ") is not matching with the specified input size:" + n + ". Verify the input data provided!");
		}
	}

}
