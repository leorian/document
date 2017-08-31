package com.bozhong.document.test;

import com.bozhong.document.common.DocFileTypeEnum;
import org.junit.Test;

/**
 * Created by xiezg@317hu.com on 2017/4/27 0027.
 */
public class DocFileTypeEnumTest {

    @Test
    public void TestDocFileTypeEnum() {
        System.out.println(DocFileTypeEnum.contentTypeExtMap.get("hello"));//null
        System.out.println(DocFileTypeEnum.contentTypeExtMap.get("application/msword"));//doc
    }
}
