/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.config;

import java.awt.Color;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.whatswrong.NLPCanvasRenderer;

public class FormatConfiguration {
	@JsonProperty("anti_aliasing")
	public Boolean antiAliasing;
	public Boolean curved;
	@JsonProperty("height_factor")
	public Integer heightFactor;
	public Integer margin;
	@JsonProperty("edge_type_color")
	public Map<String, String> edgeTypeColor;

	public void initRenderer(NLPCanvasRenderer renderer) {
		if (antiAliasing != null) {
			renderer.setAntiAliasing(antiAliasing);
		}
		if (curved != null) {
			renderer.setCurved(curved);
		}
		if (heightFactor != null) {
			renderer.setHeightFactor(heightFactor);
		}
		if (margin != null) {
			renderer.setMargin(margin);
		}

		if (edgeTypeColor != null) {
			for (Map.Entry<String, String> edgeColor : edgeTypeColor.entrySet()) {
				Color color;
				try {
					color = Color.decode(edgeColor.getValue());
				} catch (NumberFormatException e) {
					// TODO
					throw e;
				}
				renderer.setEdgeTypeColor(edgeColor.getKey(), color);
			}
		}
	}
}
