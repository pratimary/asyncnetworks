package com.utd.distributed.async.networks;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.utd.distributed.async.networks.model.Node;
import com.utd.distributed.async.networks.util.FloodMaxInputParser;

public class Controller {

	private static final String INPUT_FILE_KEY = "inputFile";

	private static Logger logger = LogManager.getLogger(Controller.class);

	public static void main(String[] args) {

		String inputFileLoc = System.getProperty(INPUT_FILE_KEY);

		if (inputFileLoc == null)
			inputFileLoc = "conf/connectivity.txt";

		FloodMaxInputParser parser = new FloodMaxInputParser(inputFileLoc);
		try {
			List<Node> nodes = parser.parseInputFile();
			logger.info(nodes);
		} catch (Exception e) {
			logger.error("Failed to parse", e);
		}

	}

}
