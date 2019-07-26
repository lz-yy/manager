package com.voucher.manage2.utils;

import cn.hutool.core.util.IdUtil;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.voucher.manage2.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class WrodToPDF {


    public static String doc2pdf(String inPath) {

        String pdfFileName = IdUtil.simpleUUID() + SystemConstant.PDF_SUFFIX;
        String pdfFile = SystemConstant.SAVE_CAHCE_FILE_PATH + File.separator + pdfFileName;
        //log.debug("开始转换为pdf,保存地址{}", pdfFile);


        FileOutputStream os = null;
        try {
            File file = new File(pdfFile); // 新建一个空白pdf文档
            os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pdfFileName;
    }


}
