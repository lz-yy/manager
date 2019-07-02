package com.voucher.manage2.controller;


import com.voucher.manage2.service.PdfAddressRuturnService;
import com.voucher.manage2.service.Word2PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * wordתPDF�ĵ�
 * ����PDF�ĵ���ַ
 */
@Controller
@RequestMapping("/convert")
public class Word2PdfController {
    @Autowired
    private PdfAddressRuturnService  ruturnService;

    @Autowired
    private Word2PdfService word2PdfService;


    /**
     * wordתPDF����
     */
    @RequestMapping("/w2p")
    public void word2Pdf(){
        String word = "src\\main\\java\\com\\voucher\\docx\\01.docx";
        String name = "02".concat(".pdf");
        String pdf = "src\\main\\java\\com\\voucher\\docx\\"+name;
        word2PdfService.convert(word,pdf);
    }

    /**
     * ����PDF��ַ
     * @return
     */
    @RequestMapping("/return")
    public String returnAddress(){
        return ruturnService.ruturnAddress();
    }
}
