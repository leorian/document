package com.bozhong.document.task;

import com.bozhong.common.util.StringUtil;
import com.bozhong.document.common.DocumentLogger;

public class Command {

    public void init() {
        exeCmd(System.getProperty("openOffice.starter"));
    }

    public static void exeCmd(String commandStr) {
        if (StringUtil.isBlank(commandStr)) {
            return;
        }

        Process process = null;
        try {
            if (commandStr.indexOf(" #@# ") > -1) {
                process = Runtime.getRuntime().exec(commandStr.split(" #@# "));
            } else {
                process = Runtime.getRuntime().exec(commandStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage(), e);
            System.exit(-1);
        } finally {
            if (process != null) {
                try {
                    Thread.sleep(3000l);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String commandStr = "soffice -headless -accept=\"socket,host=127.0.0.1" +
                ",port=8100;urp;\" -nofirststartwizard";
        Command.exeCmd(commandStr);
        System.out.println("启动完成！");
    }
}  