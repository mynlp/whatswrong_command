/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import jp.preferred.whatswrong_command.config.Configuration;
import jp.preferred.whatswrong_command.generator.DiffHTMLGenerator;
import jp.preferred.whatswrong_command.generator.HTMLBuilder;
import jp.preferred.whatswrong_command.generator.ImageGenerator;
import jp.preferred.whatswrong_command.generator.ResultHTMLGenerator;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.whatswrong.NLPCanvasRenderer;
import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.NLPInstanceFilter;
import com.googlecode.whatswrong.SingleSentenceRenderer;

public class Main {
	private Logger logger = Logger.getAnonymousLogger();

	@Option(name = "--config", usage = "Config file path (use default config if not specified)")
	private File configFile;
	@Option(name = "--type", usage = "Select data type", required = true)
	private CorpusLoader.CorpusType dataType;
	@Option(name = "--output", usage = "Path to output directory")
	private File outputDirectory = new File("output");
	@Argument(index = 0, metaVar = "DATA", required = true, usage = "Path to data to show")
	private File data;
	@Argument(index = 1, metaVar = "GOLD", required = false, usage = "Path to gold data to compare")
	private File gold;

	protected HTMLBuilder makeBuilder() {
		Configuration config = loadConfig();

		NLPInstanceFilter filter = config.filter.makeFilter();
		NLPCanvasRenderer renderer = new SingleSentenceRenderer();
		config.format.initRenderer(renderer);

		HTMLBuilder builder = new HTMLBuilder(outputDirectory);
		builder.setFilter(filter);
		builder.setImageGenerator(new ImageGenerator());
		builder.setRenderer(renderer);
		return builder;
	}

	protected Configuration loadConfig() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
				true);

		Configuration config;
		try (InputStream input = openConfig()) {
			config = mapper.readValue(input, Configuration.class);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
			return null;
		}

		if (config.filter == null) {
			logger.severe("\"filter\" is not specified in the config");
			System.exit(1);
		}
		if (config.format == null) {
			logger.severe("\"format\" is not specified in the config");
			System.exit(1);
		}
		return config;
	}

	private InputStream openConfig() throws IOException {
		if (this.configFile != null) {
			return new FileInputStream(this.configFile);
		} else {
			URL defaultConfig = Main.class.getClassLoader().getResource(
					"default_config.json");
			return defaultConfig.openStream();
		}
	}

	private void showUsage(CmdLineParser parser) {
		System.err.println("Usage:");
		System.err.print(" whatsnew");
		parser.printSingleLineUsage(System.err);
		System.err.println();

		System.err.println("Opiotns:");
		parser.printUsage(System.err);
	}

	private void runResult() {
		HTMLBuilder builder = this.makeBuilder();
		List<NLPInstance> guessCorpus;
		try {
			guessCorpus = CorpusLoader.readCorpus(data, dataType);
		} catch (IOException e) {
			logger.severe("Cannot load data: " + data.getAbsolutePath());
			logger.severe(e.getMessage());
			System.exit(1);
			return;
		}
		ResultHTMLGenerator generator = new ResultHTMLGenerator(builder);
		try {
			generator.generate(guessCorpus);
		} catch (IOException e) {
			logger.severe("Failed to create result files");
			logger.severe(e.getMessage());
			System.exit(1);
		}
	}

	private void runDiff() {
		HTMLBuilder builder = this.makeBuilder();
		List<NLPInstance> guessCorpus;
		try {
			guessCorpus = CorpusLoader.readCorpus(data, dataType);
		} catch (IOException e) {
			logger.severe("Cannot load data: " + data.getAbsolutePath());
			logger.severe(e.getMessage());
			System.exit(1);
			return;
		}

		List<NLPInstance> goldCorpus;
		try {
			goldCorpus = CorpusLoader.readCorpus(gold, dataType);
		} catch (IOException e) {
			logger.severe("Cannot load gold data: " + gold.getAbsolutePath());
			logger.severe(e.getMessage());
			System.exit(1);
			return;
		}
		DiffHTMLGenerator generator = new DiffHTMLGenerator(builder);
		try {
			generator.generate(goldCorpus, guessCorpus);
		} catch (IOException e) {
			logger.severe("Failed to create result files");
			logger.severe(e.getMessage());
			System.exit(1);
			return;
		}
	}

	public void run(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			showUsage(parser);
			System.exit(1);
		}

		if (gold == null) {
			this.runResult();
		} else {
			this.runDiff();
		}
	}

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		new Main().run(args);
	}
}
