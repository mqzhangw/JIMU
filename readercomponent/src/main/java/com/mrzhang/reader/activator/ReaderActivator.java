package com.mrzhang.reader.activator;

import com.mrzhang.component.componentlib.activator.IActivator;
import com.mrzhang.component.componentlib.router.Router;
import com.mrzhang.componentservice.readerbook.ReadBookService;
import com.mrzhang.reader.serviceimpl.ReadBookServiceImpl;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class ReaderActivator implements IActivator {

    Router router = Router.getInstance();

    @Override
    public void onCreate() {
        router.addService(ReadBookService.class.getSimpleName(), new ReadBookServiceImpl());
    }

    @Override
    public void onStop() {
        router.removeService(ReadBookService.class.getSimpleName());
    }
}
