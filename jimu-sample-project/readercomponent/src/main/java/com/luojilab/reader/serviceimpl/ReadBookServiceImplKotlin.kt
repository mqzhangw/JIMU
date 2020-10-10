package com.luojilab.reader.serviceimpl

import androidx.fragment.app.Fragment
import com.luojilab.componentservice.readerbook.ReadBookService
import com.luojilab.reader.ReaderFragment

/**
 * Created by mrzhang on 2018/2/9.
 */
class ReadBookServiceImplKotlin : ReadBookService {
    override fun getReadBookFragment(): Fragment {
        return ReaderFragment()
    }
}
