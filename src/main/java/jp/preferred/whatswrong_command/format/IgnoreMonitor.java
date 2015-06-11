/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.format;

import com.googlecode.whatswrong.io.CorpusFormat;

public class IgnoreMonitor implements CorpusFormat.Monitor {
	@Override
	public void progressed(int index) {
		// do nothing
	}
}
