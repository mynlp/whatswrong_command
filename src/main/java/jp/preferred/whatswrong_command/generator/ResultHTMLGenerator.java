/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.generator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.googlecode.whatswrong.NLPInstance;

public class ResultHTMLGenerator {
	private HTMLBuilder builder;

	public ResultHTMLGenerator(HTMLBuilder builder) {
		if (builder == null) {
			throw new NullPointerException();
		}
		this.builder = builder;
	}

	public void generate(List<NLPInstance> instances) throws IOException {
		Iterator<NLPInstance> iterator = instances.iterator();

		builder.initDirectory();
		for (int i = 0; iterator.hasNext(); i++) {
			NLPInstance instance = iterator.next();
			builder.appendInstance(i, instance, HTMLBuilder.makeText(instance));
		}
		builder.generateHTML();
	}
}
