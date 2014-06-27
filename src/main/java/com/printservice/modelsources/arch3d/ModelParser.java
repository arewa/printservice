package com.printservice.modelsources.arch3d;

import java.net.URL;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class ModelParser implements Runnable {
	
	private static final String MODEL_TITLE_XPATH_EXPR = "//div[@id='info']/h2";
	private static final String MODEL_DESCRIPTION_XPATH_EXPR = "//div[@id='info']/p[6]";
	
	private Model model;
	private HtmlCleaner htmlCleaner;

	public ModelParser(Model model) {
		super();
		this.model = model;
		this.htmlCleaner = new HtmlCleaner();
	}

	public void run() {
		StringBuilder url = new StringBuilder().append(Main.ARCHIVE3D_ROOT).append(model.getHref());
		try {
			TagNode rootNode = htmlCleaner.clean(new URL(url.toString()));
			
			model.setTitle(Utils.getHtmlText(rootNode, MODEL_TITLE_XPATH_EXPR));
			model.setDescription(Utils.getHtmlText(rootNode, MODEL_DESCRIPTION_XPATH_EXPR).replace("Additional Info: ", ""));
			
		} catch (Exception e) {
			System.out.println(url);
			e.printStackTrace();
		} 
	}
	
}
