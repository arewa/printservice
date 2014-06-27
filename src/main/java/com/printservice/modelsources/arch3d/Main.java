package com.printservice.modelsources.arch3d;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class Main {

	public static final String ARCHIVE3D_ROOT = "http://archive3d.net/";
	
	private static final String CATEGORIES_XPATH_EXPR = "//div[@class='cats-main']//a";

	public static void main(String[] args) throws Exception {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		TagNode rootNode = htmlCleaner.clean(new URL(ARCHIVE3D_ROOT));

		Object[] els = rootNode.evaluateXPath(CATEGORIES_XPATH_EXPR);
		
		List<Category> categories = Collections.synchronizedList(new LinkedList<Category>());
		
		for (Object e : els) {
			if (e instanceof TagNode) {
				TagNode t = (TagNode) e;
				Category category = new Category(String.valueOf(t.getText()), t.getAttributeByName("href"));
				categories.add(category);
			}
		}
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		final long start = System.nanoTime();
		
		for (Category cat : categories) {
			CategoryParser catParser = new CategoryParser(cat);
			executor.execute(catParser);
			break;
		}
		
		executor.shutdown();
		
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		final long end = System.nanoTime();

		final double timeSpent = (end - start) / 1.0e9;
		
		for (Category cat : categories) {
			System.out.println(cat);
			break;
		}
		
		System.out.println("Time spent " + timeSpent + "s");
	}
}
