package com.bozhong.document.view.monitor.module.screen;

import com.bozhong.document.common.WebSettingParam;
import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;

/**
 * Created by xiezg@317hu.com on 2017/5/4 0004.
 */
@Controller
public class NoticePage implements ScreenInter {

    @Override
    public void excute(EWebContext eWebContext) {
        eWebContext.put("menu", NoticePage.class.getSimpleName());
        eWebContext.put("spring", WebSettingParam.SPRING);
    }
}
