/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.generator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.googlecode.whatswrong.Edge;
import com.googlecode.whatswrong.NLPDiff;
import com.googlecode.whatswrong.NLPInstance;

public class DiffHTMLGenerator {
	private boolean onlyShowDifference = true;
	private HTMLBuilder builder;

	public DiffHTMLGenerator(HTMLBuilder builder) {
		if (builder == null) {
			throw new NullPointerException();
		}
		this.builder = builder;
	}

	private boolean containsDiff(NLPInstance diff) {
		for (Edge edge : diff.getEdges()) {
			String postfix = edge.getTypePostfix();
			if (postfix.equals("FP") || postfix.equals("FN")) {
				return true;
			}
		}
		return false;
	}

	public void generate(List<NLPInstance> goldInstances,
			List<NLPInstance> guessInstances) throws IOException {
		NLPDiff nlpDiff = new NLPDiff();
		Iterator<NLPInstance> iGold = goldInstances.iterator();
		Iterator<NLPInstance> iGuess = guessInstances.iterator();

		builder.initDirectory();
		for (int i = 0; iGold.hasNext() && iGuess.hasNext(); i++) {
			NLPInstance gold = iGold.next();
			NLPInstance guess = iGuess.next();
			NLPInstance diff = nlpDiff.diff(gold, guess);

			if (this.onlyShowDifference && !containsDiff(diff)) {
				continue;
			}
			builder.appendInstance(i, diff, HTMLBuilder.makeText(gold));
		}
		builder.generateHTML();
	}

}
