package com.bozhong.document.junit;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;  
import com.jacob.com.DispatchEvents;  
import com.jacob.com.Variant;  
import java.io.File;  
import java.util.logging.Level;  
import java.util.logging.Logger;  
  
public class DocChangePdfForJco {  
  
    public static Converter newConverter(String name) {  
        if (name.equals("wps")) {  
            return new Wps();  
        } else if (name.equals("pdfcreator")) {  
            return new PdfCreator();  
        }  
        return null;  
    }  
  
    public synchronized static boolean convert(String word, String pdf) {  
        return newConverter("wps").convert(word, pdf);
    }  
  
    public abstract static interface Converter {  
  
        public boolean convert(String word, String pdf);  
    }  
  
    public static class Wps implements Converter {  
  
        public synchronized boolean convert(String word, String pdf) {  
            File pdfFile = new File(pdf);  
            File wordFile = new File(word);  
            ActiveXComponent wps = null;  
            try {  
                wps = new ActiveXComponent("KWPS.Application");
                ActiveXComponent doc = wps.invokeGetComponent("Documents").invokeGetComponent("Open", new Variant(wordFile.getAbsolutePath()));  
                doc.invoke("ExportPdf", new Variant(pdfFile.getAbsolutePath()));
                doc.invoke("Close");  
                doc.safeRelease();  
            } catch (Exception ex) {  
                Logger.getLogger(DocChangePdfForJco.class.getName()).log(Level.SEVERE, null, ex);  
                return false;  
            } catch (Error ex) {  
                Logger.getLogger(DocChangePdfForJco.class.getName()).log(Level.SEVERE, null, ex);  
                return false;  
            } finally {  
                if (wps != null) {  
                    wps.invoke("Terminate");  
                    wps.safeRelease();  
                }  
            }  
            return true;  
        }  
    }  
  
    public static class PdfCreator implements Converter {  
  
        public static final int STATUS_IN_PROGRESS = 2;  
        public static final int STATUS_WITH_ERRORS = 1;  
        public static final int STATUS_READY = 0;  
        private ActiveXComponent pdfCreator;  
        private DispatchEvents dispatcher;  
        private volatile int status;  
        private Variant defaultPrinter;  
  
        private void init() {  
            pdfCreator = new ActiveXComponent("PDFCreator.clsPDFCreator");  
            dispatcher = new DispatchEvents(pdfCreator, this);  
            pdfCreator.setProperty("cVisible", new Variant(false));  
            pdfCreator.invoke("cStart", new Variant[]{new Variant("/NoProcessingAtStartup"), new Variant(true)});  
            setCOption("UseAutosave", 1);  
            setCOption("UseAutosaveDirectory", 1);  
            setCOption("AutosaveFormat", 0);  
            defaultPrinter = pdfCreator.getProperty("cDefaultPrinter");  
            status = STATUS_IN_PROGRESS;  
            pdfCreator.setProperty("cDefaultprinter", "PDFCreator");  
            pdfCreator.invoke("cClearCache");  
            pdfCreator.setProperty("cPrinterStop", false);  
        }  
  
        private void setCOption(String property, Object value) {  
            Dispatch.invoke(pdfCreator, "cOption", Dispatch.Put, new Object[]{property, value}, new int[2]);  
        }  
  
        private void close() {  
            if (pdfCreator != null) {  
                pdfCreator.setProperty("cDefaultprinter", defaultPrinter);  
                pdfCreator.invoke("cClearCache");  
                pdfCreator.setProperty("cPrinterStop", true);  
                pdfCreator.invoke("cClose");  
                pdfCreator.safeRelease();  
                pdfCreator = null;  
            }  
            if (dispatcher != null) {  
                dispatcher.safeRelease();  
                dispatcher = null;  
            }  
        }  
  
        public synchronized boolean convert(String word, String pdf) {  
            File pdfFile = new File(pdf);  
            File wordFile = new File(word);  
            try {  
                init();  
                setCOption("AutosaveDirectory", pdfFile.getParentFile().getAbsolutePath());  
                setCOption("AutosaveFilename", pdfFile.getName());  
                pdfCreator.invoke("cPrintfile", wordFile.getAbsolutePath());  
                int seconds = 0;  
                while (isInProcess()) {  
                    Thread.sleep(1000);  
                    seconds++;  
                    if (seconds > 20) { // timeout  
                        break;  
                    }  
                }  
                if (seconds > 20 || isWithError()) return false;  
            } catch (InterruptedException ex) {  
                Logger.getLogger(DocChangePdfForJco.class.getName()).log(Level.SEVERE, null, ex);  
                return false;  
            } catch (Exception ex) {  
                Logger.getLogger(DocChangePdfForJco.class.getName()).log(Level.SEVERE, null, ex);  
                return false;  
            } catch (Error ex) {  
                Logger.getLogger(DocChangePdfForJco.class.getName()).log(Level.SEVERE, null, ex);  
                return false;  
            } finally {  
                close();  
            }  
            return true;  
        }  
  
        private boolean isInProcess() {  
            return status == STATUS_IN_PROGRESS;  
        }  
  
        private boolean isWithError() {  
            return status == STATUS_WITH_ERRORS;  
        }  
  
        // eReady event  
        public void eReady(Variant[] args) {  
            status = STATUS_READY;  
        }  
  
        // eError event  
        public void eError(Variant[] args) {  
            status = STATUS_WITH_ERRORS;  
        }  
    }  
  
    public static void main(String[] args) {  
        convert("D:\\openofficestorage\\app.doc","D:\\openofficestorage\\app.pdf");
    }  
}  