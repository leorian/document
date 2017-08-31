package com.bozhong.document.view.monitor.module.screen;

import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/5/3 0003.
 */
@Controller
public class ManualView implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("menu", ManualView.class.getSimpleName());
        System.out.println("手动控制文档转换");
    }
}
