package com.education.zhxy.study.data.bean;

import java.io.Serializable;

public class Book implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String bookName;
	
	private String bookImages;
	
	private String bookAutho;
	
	private String bookIntro;
	
	private String bookConcern;
	
	private String bookDates;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookImages() {
		return bookImages;
	}

	public void setBookImages(String bookImages) {
		this.bookImages = bookImages;
	}

	public String getBookAutho() {
		return bookAutho;
	}

	public void setBookAutho(String bookAutho) {
		this.bookAutho = bookAutho;
	}

	public String getBookIntro() {
		return bookIntro;
	}

	public void setBookIntro(String bookIntro) {
		this.bookIntro = bookIntro;
	}

	public String getBookConcern() {
		return bookConcern;
	}

	public void setBookConcern(String bookConcern) {
		this.bookConcern = bookConcern;
	}

	public String getBookDates() {
		return bookDates;
	}

	public void setBookDates(String bookDates) {
		this.bookDates = bookDates;
	}
	
}
