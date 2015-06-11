/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.format;

import java.util.Properties;

import com.googlecode.whatswrong.io.CorpusFormat;
import com.googlecode.whatswrong.io.TabFormat;

public class TabFormatFactory implements CorpusFormatFactory {
	private final String tabTypeName;

	public TabFormatFactory(String tabTypeName) {
		this.tabTypeName = tabTypeName;
	}

	public CorpusFormat create() {
		TabFormat format = new TabFormat();
		format.setMonitor(new IgnoreMonitor());
		Properties prop = new Properties();
		prop.setProperty(".tab.type", this.tabTypeName);
		format.loadProperties(prop, "");
		return format;
	}
}
