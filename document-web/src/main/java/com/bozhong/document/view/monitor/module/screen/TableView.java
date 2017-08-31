package com.bozhong.document.view.monitor.module.screen;

import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/4/28 0028.
 */
@Controller
public class TableView implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("menu", TableView.class.getSimpleName());
        System.out.println("列表化展示");
    }
}
