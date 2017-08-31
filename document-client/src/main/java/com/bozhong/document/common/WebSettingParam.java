package com.bozhong.document.common;

import com.bozhong.myswitch.core.AppSwitch;

/**
 * Created by xiezg@317hu.com on 2017/4/18 0018.
 */
public class WebSettingParam {

    @AppSwitch(type = "string", desc = "公司")
    public static String CORP = "杭州伯仲信息科技有限公司";
    @AppSwitch(type = "string", desc = "部门")
    public static String DEPARTMENT = "云平台部";
    @AppSwitch(type = "string", desc = "网站标题")
    public static String HTML_TITLE = "文档转换中心";

    @AppSwitch(type = "string", desc = "节假日图片")
    public static String SPRING = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1493888650&di=9e28deac7e81ef853d4519f2a2c1dc47&src=http://www.dabaoku.com/sucai/qita/guoqingjie/18.jpg\n" +
            "\n";

    @AppSwitch(type = "boolean", desc = "是否开启异步文档转换")
    public static boolean OPEN_ASYNC_CONVERSION = true;
    @AppSwitch(type = "boolean", desc = "是否开启同步文档转换")
    public static boolean OPEN_SYNC_CONVERSION = true;
    @AppSwitch(type = "boolean", desc = "是否开启同一文档请求频繁性控制")
    public static boolean CONVERSION_SAME_REQUEST_CONTROLLER = true;
    @AppSwitch(type = "int", desc = "同一文档请求频繁性控制时间间隔，单位（秒）")
    public static int CONVERSION_SAME_REQUEST_EXPIRE_TIME = 180;
    @AppSwitch(type = "int", desc = "单台机器最大执行总数")
    public static int MAX_EXECUTING_STATUS_COUNT = 10;
    @AppSwitch(type = "int", desc = "文档大小最大值限制，单位（M）")
    public static int MAX_DOC_FILE_LENGTH = 120;
    @AppSwitch(type = "string", desc = "转换工具")
    public static String CONVERTER_TOOLS = ConverterToolEnum.WIN_OFFICE.name();

}
