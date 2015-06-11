/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.generator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.googlecode.whatswrong.NLPCanvasRenderer;
import com.googlecode.whatswrong.NLPInstance;

public class ImageGenerator {
	private float scale = 1.f;

	public void setScale(float scale) {
		this.scale = scale;
	}

	BufferedImage generateImage(NLPCanvasRenderer renderer, NLPInstance instance) {
		// Dummy image to calculate size
		BufferedImage image = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D dummy = (Graphics2D) image.getGraphics();
		Dimension dim = renderer.render(instance, dummy);
		dummy.dispose();
		dim.setSize(dim.getWidth() * this.scale, dim.getHeight() * this.scale);

		image = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.scale(scale, scale);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, dim.width, dim.height);
		renderer.render(instance, g);

		g.dispose();
		return image;
	}
}
