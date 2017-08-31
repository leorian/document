package com.bozhong.document.view.monitor.module.screen;

import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/5/12 0012.
 */
@Controller
public class AppServer implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("menu", AppServer.class.getSimpleName());
        System.out.println("文档转换机器分布");
    }
}
