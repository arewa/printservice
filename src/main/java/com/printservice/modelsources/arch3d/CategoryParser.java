package com.printservice.modelsources.arch3d;

import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class CategoryParser implements Runnable {
	
	private static final String CATEGORY_PAGES_XPATH_EXPR = "//div[@class='pg']//a[last()]";
	
	private Category category;
	private HtmlCleaner htmlCleaner;

	public CategoryParser(Category category) {
		super();
		this.category = category;
		this.htmlCleaner = new HtmlCleaner();
	}

	public void run() {
		category.setPages(parsePagesCount());
		
		int poolSize = 20;
		
		BlockingQueue<Runnable> parsersThreadQueue = new ArrayBlockingQueue<Runnable>(poolSize);
		ExecutorService executor = new ThreadPoolExecutor(4, poolSize, 20, TimeUnit.SECONDS, parsersThreadQueue, new ThreadPoolExecutor.CallerRunsPolicy());
		
		for (int p = 1; p <= category.getPages(); p ++) {
			CategoryPageParser catPageParser = new CategoryPageParser(category, p);
			executor.execute(catPageParser);
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int parsePagesCount() {
		StringBuilder url = new StringBuilder().append(Main.ARCHIVE3D_ROOT).append(category.getHref());
		
		try {
			TagNode rootNode = htmlCleaner.clean(new URL(url.toString()));
			
			Object[] els = rootNode.evaluateXPath(CATEGORY_PAGES_XPATH_EXPR);
			
			if (els != null && els.length == 0) {
				return 1;
			}
			
			for (Object e : els) {
				if (e instanceof TagNode) {
					TagNode t = (TagNode) e;
					return Integer.valueOf(String.valueOf(t.getText()));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("Can't parse pages count for category " + category);
	}

}
