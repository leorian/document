package com.bozhong.document.view.monitor.module.screen;

import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
@Controller
public class Test implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("testParam", eWebContext.get("testParam"));
    }
}
