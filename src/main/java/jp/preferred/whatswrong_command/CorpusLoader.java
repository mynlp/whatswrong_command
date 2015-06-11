/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.preferred.whatswrong_command.format.CorpusFormatFactory;
import jp.preferred.whatswrong_command.format.LispSExprFormatFactory;
import jp.preferred.whatswrong_command.format.TabFormatFactory;

import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.io.CoNLL2000;
import com.googlecode.whatswrong.io.CoNLL2002;
import com.googlecode.whatswrong.io.CoNLL2003;
import com.googlecode.whatswrong.io.CoNLL2004;
import com.googlecode.whatswrong.io.CoNLL2005;
import com.googlecode.whatswrong.io.CoNLL2006;
import com.googlecode.whatswrong.io.CoNLL2008;
import com.googlecode.whatswrong.io.CoNLL2009;
import com.googlecode.whatswrong.io.CorpusFormat;

public class CorpusLoader {
	enum CorpusType {
		CONLL2000, CONLL2002, CONLL2003, CONLL2004, CONLL2005, CONLL2006, CONLL2008, CONLL2009, SEXPR,
	}

	private static CorpusFormatFactory createCorpusFormatFactory(CorpusType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		switch (type) {
		case CONLL2000:
			return new TabFormatFactory(CoNLL2000.name);
		case CONLL2002:
			return new TabFormatFactory(CoNLL2002.name);
		case CONLL2003:
			return new TabFormatFactory(CoNLL2003.name);
		case CONLL2004:
			return new TabFormatFactory(CoNLL2004.name);
		case CONLL2005:
			return new TabFormatFactory(CoNLL2005.name);
		case CONLL2006:
			return new TabFormatFactory(CoNLL2006.name);
		case CONLL2008:
			return new TabFormatFactory(CoNLL2008.name);
		case CONLL2009:
			return new TabFormatFactory(CoNLL2009.name);
		case SEXPR:
			return new LispSExprFormatFactory();
		}
		throw new RuntimeException("Unknown data type");
	}

	public static List<NLPInstance> readCorpus(File file, CorpusType type)
			throws IOException {
		CorpusFormat format = createCorpusFormatFactory(type).create();
		return format.load(file, 0, Integer.MAX_VALUE);
	}
}
