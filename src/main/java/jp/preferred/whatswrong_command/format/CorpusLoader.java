/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.format;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.preferred.whatswrong_command.format.TabFormatFactory;

import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.io.CoNLL2006;
import com.googlecode.whatswrong.io.CorpusFormat;

public class CorpusLoader {
	private static CorpusFormat createCorpusFormat(String name) {
		if (name.equals(CoNLL2006.name)) {
			return new TabFormatFactory(CoNLL2006.name).create();
		}
		return null;
	}

	public static List<NLPInstance> readCorpus(File file) throws IOException {
		CorpusFormat format = createCorpusFormat(CoNLL2006.name);
		return format.load(file, 0, Integer.MAX_VALUE);
	}
}
