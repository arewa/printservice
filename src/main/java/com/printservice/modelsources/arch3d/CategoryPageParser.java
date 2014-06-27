package com.printservice.modelsources.arch3d;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class CategoryPageParser implements Runnable {

	private static final String MODEL_URL_XPATH_EXPR = "//div[@class='b1']/a[1]";
	private Category category;
	private int page;
	private HtmlCleaner htmlCleaner;

	public CategoryPageParser(Category category, int page) {
		super();
		this.category = category;
		this.page = (page - 1) * 24 + 1;
		this.htmlCleaner = new HtmlCleaner();
	}

	public void run() {
		StringBuilder url = new StringBuilder().append(Main.ARCHIVE3D_ROOT).append(category.getHref()).append("&page=").append(page);
		
		try {
			TagNode rootNode = htmlCleaner.clean(new URL(url.toString()));
			
			Object[] els = rootNode.evaluateXPath(MODEL_URL_XPATH_EXPR);
			
			List<Model> models = Collections.synchronizedList(new LinkedList<Model>());
			
			for (Object e : els) {
				if (e instanceof TagNode) {
					TagNode t = (TagNode) e;
					Model model = new Model();
					model.setHref(t.getAttributeByName("href").replace("&amp;", "&"));
					models.add(model);
				}
			}
			
			ExecutorService executor = Executors.newCachedThreadPool();
			
			for (Model model : models) {
				ModelParser modelParser = new ModelParser(model);
				executor.execute(modelParser);
			}
			
			executor.shutdown();
			
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Model model : models) {
				System.out.println(model);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
