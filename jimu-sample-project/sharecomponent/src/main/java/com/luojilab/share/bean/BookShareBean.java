package com.luojilab.share.bean;

import com.luojilab.share.core.AbsShareBean;

/**
 * <p><b>Package:</b> com.luojilab.share.bean </p>
 * <p><b>Project:</b> jimu-sample-project </p>
 * <p><b>Classname:</b> BookShareBean </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/7/6.
 */
public class BookShareBean extends AbsShareBean {
    private String book;
    private String author;
    private String county;
    private String reason;


    public BookShareBean(int shareVia, String book, String author, String county, String reason) {
        super(shareVia);
        this.book = book;
        this.author = author;
        this.county = county;
        this.reason = reason;
    }

    @Override
    protected String getShareContent() {
        return toString();
    }

    @Override
    public String toString() {
        return "BookShareBean{" +
                "book='" + book + '\'' +
                ", author='" + author + '\'' +
                ", county='" + county + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
