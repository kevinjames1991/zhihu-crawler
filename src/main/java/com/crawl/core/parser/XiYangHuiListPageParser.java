package com.crawl.core.parser;

import com.crawl.zhihu.entity.Page;

import java.util.List;

public interface XiYangHuiListPageParser extends Parser {
    List parse(Page page);
}
