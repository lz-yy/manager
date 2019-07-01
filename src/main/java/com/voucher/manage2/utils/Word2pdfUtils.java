package com.voucher.manage2.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

import java.io.File;

public class Word2pdfUtils {
    public static void wToPdfChange(String wordFile,String pdfFile){//wordFile word ��·��  //pdfFile pdf ��·��

        ActiveXComponent app = null;
        System.out.println("��ʼת��...");
        // ��ʼʱ��
        // long start = System.currentTimeMillis();
        try {
            // ��word
            app = new ActiveXComponent("Word.Application");
            // ���word�����д򿪵��ĵ�
            Dispatch documents = app.getProperty("Documents").toDispatch();
            System.out.println("���ļ�: " + wordFile);
            // ���ĵ�
            Dispatch document = Dispatch.call(documents, "Open", wordFile, false, true).toDispatch();
            // ����ļ����ڵĻ������Ḳ�ǣ���ֱ�ӱ�������������Ҫ�ж��ļ��Ƿ����
            File target = new File(pdfFile);
            if (target.exists()) {
                target.delete();
            }
            System.out.println("���Ϊ: " + pdfFile);
            Dispatch.call(document, "SaveAs", pdfFile, 17);
            // �ر��ĵ�
            Dispatch.call(document, "Close", false);
        }catch(Exception e) {
            System.out.println("ת��ʧ��"+e.getMessage());
        }finally {
            // �ر�office
            app.invoke("Quit", 0);
        }
    }
    public static void main(String[] args) {
        String word = "src\\main\\java\\com\\voucher\\docx\\01.docx";
        String name = "02".concat(".pdf");
        String pdf = "src\\main\\java\\com\\voucher\\docx\\"+name;
    wToPdfChange(word, pdf);
}
}
