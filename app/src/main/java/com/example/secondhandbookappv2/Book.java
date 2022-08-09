package com.example.secondhandbookappv2;

public class Book {
    public String bookName;
    public String bookDate;
    public String bookCategory;
    public String bookPrice;

    public Book(){

    }

    public Book(String bookName, String bookDate, String bookCategory, String bookPrice) {
        this.bookName = bookName;
        this.bookDate = bookDate;
        this.bookCategory = bookCategory;
        this.bookPrice = bookPrice;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }
}

