/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.whatswrong.EdgeFilter;
import com.googlecode.whatswrong.EdgeTokenFilter;
import com.googlecode.whatswrong.EdgeTypeFilter;
import com.googlecode.whatswrong.FilterPipeline;
import com.googlecode.whatswrong.NLPInstanceFilter;
import com.googlecode.whatswrong.TokenFilter;

public class FilterConfiguration {
	@JsonProperty("edge_type")
	public List<String> edgeType;
	@JsonProperty("edge_type_postfix")
	public List<String> edgeTypePostfix;
	@JsonProperty("edge_token")
	public List<String> edgeToken;
	public boolean collaps;
	@JsonProperty("token_string")
	public List<String> tokenString;
	@JsonProperty("token_forbidden_property")
	public List<String> tokenForbiddenProperty;

	public FilterPipeline makeFilter() {
		List<NLPInstanceFilter> filters = new ArrayList<NLPInstanceFilter>();
		NLPInstanceFilter edgeFilter = this.makeEdgeFilter();
		if (edgeFilter != null) {
			filters.add(edgeFilter);
		}
		NLPInstanceFilter edgeTokenFilter = this.makeEdgeTokenFilter();
		if (edgeTokenFilter != null) {
			filters.add(edgeTokenFilter);
		}
		NLPInstanceFilter tokenFilter = this.makeTokenFilter();
		if (tokenFilter != null) {
			filters.add(tokenFilter);
		}

		return new FilterPipeline(filters.toArray(new NLPInstanceFilter[0]));
	}

	private EdgeFilter makeEdgeFilter() {
		if (this.edgeType == null && this.edgeTypePostfix == null) {
			return null;
		}

		EdgeTypeFilter filter = new EdgeTypeFilter();
		if (this.edgeType != null) {
			for (String prefix : this.edgeType) {
				filter.addAllowedPrefixType(prefix);
			}
		}
		if (this.edgeTypePostfix != null) {
			for (String postfix : this.edgeTypePostfix) {
				filter.addAllowedPostfixType(postfix);
			}
		}
		return filter;
	}

	private EdgeTokenFilter makeEdgeTokenFilter() {
		if (this.edgeToken == null) {
			return null;
		}
		EdgeTokenFilter filter = new EdgeTokenFilter(
				this.edgeToken.toArray(new String[0]));
		filter.setCollaps(this.collaps);
		return filter;
	}

	private TokenFilter makeTokenFilter() {
		if (this.tokenForbiddenProperty == null) {
			return null;
		}

		TokenFilter filter = new TokenFilter();
		if (this.tokenForbiddenProperty != null) {
			for (String forbidden : this.tokenForbiddenProperty) {
				filter.addForbiddenProperty(forbidden);
			}
		}
		return filter;
	}
}
