package com.voucher.manage2.service;

import com.voucher.manage.daoModel.ChartInfo;
import com.voucher.manage.daoModel.ChartRoom;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Map;

public interface ReplaceKeywordsService {

    /**
     * ��docx�ļ��е��ı�������е����ݽ����滻
     */
    public void printWord(ChartInfo chartInfo) throws IOException;

}
