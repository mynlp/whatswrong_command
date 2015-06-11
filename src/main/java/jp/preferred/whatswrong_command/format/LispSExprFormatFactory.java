/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.format;

import com.googlecode.whatswrong.io.CorpusFormat;
import com.googlecode.whatswrong.io.LispSExprFormat;

public class LispSExprFormatFactory implements CorpusFormatFactory {

	public CorpusFormat create() {
		return new LispSExprFormat();
	}
}
