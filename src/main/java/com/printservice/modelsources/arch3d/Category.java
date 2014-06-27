package com.printservice.modelsources.arch3d;

public final class Category {
	private String title;
	private String href;
	private int pages;
	
	public Category(String title, String href) {
		super();
		this.title = title;
		this.href = href;
	}

	public String getTitle() {
		return title;
	}

	public String getHref() {
		return href;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		if (pages > 0) { 
			this.pages = pages;
		} else {
			this.pages = 1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		result = prime * result + pages;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		if (pages != other.pages)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Category [title='" + title + "', href='" + href + "', pages="
				+ pages + "]";
	}
}
