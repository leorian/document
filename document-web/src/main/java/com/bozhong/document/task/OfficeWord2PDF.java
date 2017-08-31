package com.bozhong.document.task;

import com.bozhong.document.common.DocFileTypeEnum;
import com.bozhong.document.common.DocumentErrorEnum;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.util.DocumentException;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.io.File;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiezg@317hu.com on 2017/7/28 0028.
 */
public class OfficeWord2PDF {

    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;
    private static final int msoTrue = -1;
    private static final int msofalse = 0;
    private static final Lock lock_word = new ReentrantLock();
    private static final Lock lock_ppt = new ReentrantLock();
    private static final Lock lock_xls = new ReentrantLock();

    /**
     * 文件格式转换
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    public static int convert2PDF(String inputFile, String pdfFile) {
        String suffix = getFileSuffix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            //文件不存在
            throw new DocumentException(DocumentErrorEnum.E10012.getError(),
                    DocumentErrorEnum.E10012.getMsg());
        }

        if (suffix.equals(DocFileTypeEnum.PDF.getExt())) {
            //pdf格式文件无需转换
            throw new DocumentException(DocumentErrorEnum.E10013.getError(),
                    DocumentErrorEnum.E10013.getMsg());
        }

        if (suffix.equals(DocFileTypeEnum.DOC.getExt()) ||
                suffix.equals(DocFileTypeEnum.DOCX.getExt()) ||
                suffix.equals("txt")) {
            lock_word.lock();
            try {
                return OfficeWord2PDF.word2PDF(inputFile, pdfFile);
            } catch (Throwable e) {
                e.printStackTrace();
                DocumentLogger.getSysLogger().error(e.getMessage());
                throw e;
            } finally {
                lock_word.unlock();
            }
        } else if (suffix.equals(DocFileTypeEnum.PPT.getExt()) ||
                suffix.equals(DocFileTypeEnum.PPTX.getExt())) {
            lock_ppt.lock();
            try {
                return OfficeWord2PDF.ppt2PDF(inputFile, pdfFile);
            } catch (Throwable e) {
                e.printStackTrace();
                DocumentLogger.getSysLogger().error(e.getMessage());
                throw e;
            } finally {
                lock_ppt.unlock();
            }
        } else if (suffix.equals(DocFileTypeEnum.XLS.getExt()) ||
                suffix.equals(DocFileTypeEnum.XLSX.getExt())) {
            lock_xls.lock();
            try {
                return OfficeWord2PDF.excel2PDF(inputFile, pdfFile);
            } catch (Throwable e) {
                e.printStackTrace();
                DocumentLogger.getSysLogger().error(e.getMessage());
                throw e;
            } finally {
                lock_xls.unlock();
            }
        } else {
            //文件格式不支持
            throw new DocumentException(DocumentErrorEnum.E10007.getError(),
                    DocumentErrorEnum.E10007.getMsg());
        }

    }


    /**
     * word文档转换pdf格式
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    public static int word2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(false);
            // 打开Word应用程序
            ActiveXComponent app = new ActiveXComponent("Word.Application");
            System.out.println("开始转化Word为PDF...");
            long date = new Date().getTime();
            // 设置Word不可见
            app.setProperty("Visible", new Variant(false));
            // 禁用宏
            app.setProperty("AutomationSecurity", new Variant(3));
            // 获得Word中所有打开的文档，返回documents对象
            Dispatch docs = app.getProperty("Documents").toDispatch();
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();
            /***
             *
             * 调用Document对象的SaveAs方法，将文档保存为pdf格式
             *
             * Dispatch.call(doc, "SaveAs", pdfFile, wdFormatPDF
             * word保存为pdf格式宏，值为17 )
             *
             */
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF);// word保存为pdf格式宏，值为17
            System.out.println(doc);
            // 关闭文档
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);

            Dispatch.call(doc, "Close", false);
            // 关闭Word应用程序
            app.invoke("Quit", 0);
            if (doc != null) {
                doc.safeRelease();
            }

            if (docs != null) {
                docs.safeRelease();
            }

            if (app != null) {
                app.safeRelease();
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage());
            return -1;
        } finally {
            ComThread.Release();
        }
    }


    /**
     * Excel转换PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    public static int excel2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(false);
            ActiveXComponent ax = new ActiveXComponent("Excel.Application");
            System.out.println("开始转化Excel为PDF...");
            long date = new Date().getTime();
            ax.setProperty("Visible", false);
            ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();

            Dispatch excel = Dispatch
                    .invoke(excels, "Open", Dispatch.Method,
                            new Object[]{inputFile, new Variant(false), new Variant(false)}, new int[9])
                    .toDispatch();
            // 转换格式
            Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{new Variant(0), // PDF格式=0
                    pdfFile, new Variant(xlTypePDF) // 0=标准 (生成的PDF图片不会变模糊) 1=最小文件
                    // (生成的PDF图片糊的一塌糊涂)
            }, new int[1]);

            // 这里放弃使用SaveAs
            /*
             * Dispatch.invoke(excel,"SaveAs",Dispatch.Method,new Object[]{
             * outFile, new Variant(57), new Variant(false), new Variant(57),
             * new Variant(57), new Variant(false), new Variant(true), new
             * Variant(57), new Variant(true), new Variant(true), new
             * Variant(true) },new int[1]);
             */
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            Dispatch.call(excel, "Close", new Variant(false));

            if (ax != null) {
                ax.invoke("Quit", new Variant[]{});
                ax = null;
            }

            if (excel != null) {
                excel.safeRelease();
            }

            if (excels != null) {
                excels.safeRelease();
            }

            if (ax != null) {
                ax.safeRelease();
            }

            return time;
        } catch (Exception e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage());
            return -1;
        } finally {
            ComThread.Release();
        }
    }

    /**
     * ppt转换PDF格式
     *
     * @param inputFile
     * @param pdfFile
     * @return
     */
    public static int ppt2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA(false);
            ActiveXComponent app = new ActiveXComponent("PowerPoint.Application");
//            app.setProperty("Visible", false);
            System.out.println("开始转化PPT为PDF...");
            long date = new Date().getTime();
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true, // ReadOnly
                    //    false, // Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
            ).toDispatch();
            Dispatch.invoke(ppt, "SaveAs", Dispatch.Method, new Object[]{
                    pdfFile, new Variant(ppSaveAsPDF)}, new int[1]);
            System.out.println("PPT");
            Dispatch.call(ppt, "Close");
            long date2 = new Date().getTime();
            int time = (int) ((date2 - date) / 1000);
            app.invoke("Quit");

            if (ppt != null) {
                ppt.safeRelease();
            }

            if (ppts != null) {
                ppts.safeRelease();
            }

            if (app != null) {
                app.safeRelease();
            }

            return time;
        } catch (Exception e) {
            e.printStackTrace();
            DocumentLogger.getSysLogger().error(e.getMessage());
            return -1;
        } finally {
            ComThread.Release();
        }
    }


    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }
}
