package com.bozhong.document.view.monitor.module.screen;

import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/5/10 0010.
 */
@Controller
public class AppPie implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("menu", AppPie.class.getSimpleName());
        System.out.println("应用监控饼图");
    }
}
