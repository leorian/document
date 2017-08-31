package com.bozhong.document.view.monitor.module.screen;

import com.bozhong.common.util.StringUtil;
import com.bozhong.document.entity.AppDO;
import com.yx.eweb.main.EWebContext;
import com.yx.eweb.main.ScreenInter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by xiezg@317hu.com on 2017/5/9 0009.
 */

@Controller
public class AppStatistics implements ScreenInter {
    @Override
    public void excute(EWebContext eWebContext) {
        String appId = eWebContext.getRequest().getParameter("appId");
        eWebContext.put("menu", AppStatistics.class.getSimpleName());
        List<AppDO> appDOList = (List<AppDO>) eWebContext.getRequest().getAttribute("appDOList");
        if (!CollectionUtils.isEmpty(appDOList) && StringUtil.isNotBlank(appId)) {
            for (AppDO appDO : appDOList) {
                if (appDO.getAppId().equals(appId)) {
                    appDO.setMainSelectType(true);
                    eWebContext.put("appDO", appDO);
                    break;
                }
            }
        }

        eWebContext.getRequest().setAttribute("appDOList", appDOList);
        System.out.println("单应用监控页面！");
    }
}
