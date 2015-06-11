/*
 * Copyright (c) 2015, Preferred Infrastructure, Inc.
 */

package jp.preferred.whatswrong_command.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.googlecode.whatswrong.NLPCanvasRenderer;
import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.NLPInstanceFilter;
import com.googlecode.whatswrong.Token;
import com.googlecode.whatswrong.TokenProperty;

public class HTMLBuilder {
	private static final String IMAGE_FILE_PREFIX = "image_";
	public static final String RESULT_HTML_FILE_NAME = "result.html";
	public static final String IMAGE_DIRECTORY_NAME = "image";

	private NLPInstanceFilter filter = null;
	private NLPCanvasRenderer renderer;
	private ImageGenerator imageGenerator;
	private String imageFormatName = "PNG";
	private boolean overwrite = true;

	private File outputDirectory;
	private List<Result> results = new ArrayList<Result>();

	public HTMLBuilder(File outputDirectory) {
		if (outputDirectory == null) {
			throw new NullPointerException();
		}
		this.outputDirectory = outputDirectory;
	}

	public void setFilter(NLPInstanceFilter filter) {
		this.filter = filter;
	}

	public void setRenderer(NLPCanvasRenderer renderer) {
		this.renderer = renderer;
	}

	public void setImageGenerator(ImageGenerator imageGenerator) {
		this.imageGenerator = imageGenerator;
	}

	private File getImageDirectory() {
		return new File(outputDirectory, IMAGE_DIRECTORY_NAME);
	}

	public static class Result {
		public int number;
		public String text;
		public String image;

		public int getNumber() {
			return number;
		}

		public String getImage() {
			return image;
		}

		public String getText() {
			return text;
		}
	}

	public void appendInstance(int index, NLPInstance instance, String text)
			throws IOException {
		if (this.filter != null) {
			instance = this.filter.filter(instance);
		}

		String imageName = IMAGE_FILE_PREFIX + index;
		String imageFileName = getImageFileName(imageName);
		File imageFile = new File(getImageDirectory(), imageFileName);
		BufferedImage image = imageGenerator.generateImage(renderer, instance);
		ImageIO.write(image, imageFormatName, imageFile);

		Result result = new Result();
		result.number = index;
		result.image = new File(IMAGE_DIRECTORY_NAME, imageFileName).getPath();
		result.text = text;
		this.results.add(result);
	}

	public void generateHTML() throws IOException {
		VelocityContext context = new VelocityContext();
		Calendar now = Calendar.getInstance();

		context.put("results", this.results);
		context.put("created", new SimpleDateFormat().format(now.getTime()));

		String html = render(context);

		File htmlFile = new File(outputDirectory, RESULT_HTML_FILE_NAME);

		try (FileWriter fileWriter = new FileWriter(htmlFile)) {
			fileWriter.write(html);
		}
	}

	private String render(VelocityContext context) {
		VelocityEngine velocity = initVelocity();
		Template template = velocity.getTemplate("result.html.vm");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		writer.flush();
		return writer.toString();
	}

	private VelocityEngine initVelocity() {
		VelocityEngine velocity = new VelocityEngine();
		velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocity.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		velocity.init();
		return velocity;
	}

	public void initDirectory() throws IOException {
		File imageDirectory = this.getImageDirectory();
		if (!this.overwrite) {
			if (imageDirectory.exists()) {
				throw new IOException("Image directory already exists: "
						+ imageDirectory.getAbsolutePath());
			}

		} else {
			if (imageDirectory.exists()) {
				if (imageDirectory.isDirectory()) {
					for (String child : imageDirectory.list()) {
						new File(imageDirectory, child).delete();
					}
				}
				imageDirectory.delete();
			}
		}

		if (!imageDirectory.mkdirs()) {
			throw new IOException("Cannot make image directory: "
					+ imageDirectory.getAbsolutePath());
		}
	}

	private String getImageFileName(String imageName) {
		String suffix = guessImageSuffix(this.imageFormatName);
		String path;
		if (suffix != null) {
			path = imageName + "." + suffix;
		} else {
			path = imageName;
		}
		return path;
	}

	private String guessImageSuffix(String formatName) {
		Iterator<ImageWriter> iWriter = ImageIO
				.getImageWritersByFormatName(formatName);
		if (!iWriter.hasNext()) {
			return null;
		}
		ImageWriter writer = iWriter.next();
		String[] suffixes = writer.getOriginatingProvider().getFileSuffixes();
		if (suffixes == null || suffixes.length == 0) {
			return null;
		}
		return suffixes[0];
	}

	public static String makeText(NLPInstance gold) {
		StringBuilder buffer = new StringBuilder();
		for (Token token : gold.getTokens()) {
			TokenProperty property = token.getSortedProperties().get(0);
			if (buffer.length() > 0) {
				buffer.append(' ');
			}
			buffer.append(token.getProperty(property));
		}
		return buffer.toString();
	}

}
