package com.voucher.manage2.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * ��docx�ļ��е��ı�������е����ݽ����滻 --ģ���֧�ֶ� {key} ��ǩ���滻
 *
 * @ClassName: WordTemplate
 * @Description: TODO(!!!ʹ��word2013 docx�ļ�)
 * @author Juveniless
 * <br>(1)wordģ��ע��ҳ�߾�����⣬�������⣺����ҳ�߾�Ĭ��Ϊ3cm�������ʱ����Ȼ����ͨ��
 * ��ק���ѱ��߿��϶�������������ҳ�߾�ֻ��1cm�����ӣ�����ʵ���ϴ�ʱҳ�߾໹��3cm�����ɵ�
 * word�����ҳ�߾໹�ǻᰴ��3cm�����ɡ�����취����word�ļ�����ú�ҳ�߾࣬�����Ҫ���
 * ����ҳ�߾��խ����Ҫ��word������ҳ�߾�խһ�㣬������ֱ���϶����߿���ʵ�֡�
 *
 */

public class WordTemplateUtils {

    private XWPFDocument document;

    public XWPFDocument getDocument() {
        return document;
    }

    public void setDocument(XWPFDocument document) {
        this.document = document;
    }

    /**
     * ��ʼ��ģ������
     *
     * @author Juveniless
     * @param inputStream
     * ģ��Ķ�ȡ��(docx�ļ�)
     * @throws IOException
     *
     */
    public WordTemplateUtils(InputStream inputStream) throws IOException {
        document = new XWPFDocument(inputStream);
    }

    /**
     * ������������д�뵽�������
     *
     * @param outputStream
     * @throws IOException
     */
    public void write(OutputStream outputStream) throws IOException {
        document.write(outputStream);
    }





    /**
     * ����dataMap��word�ļ��еı�ǩ�����滻; <br><br>
     * ��������***��Ҫע��dataMap�����ݸ�ʽ***�������� <br><br>
     * ������Ҫ�滻����ͨ��ǩ���ݱ�ǩ������Ҫѭ����-----������dataMap�д洢һ��keyΪparametersMap��map��
     * ���洢��Щ����Ҫѭ�����ɵ����ݣ����磺��ͷ��Ϣ�����ڣ��Ʊ��˵ȡ� <br><br>
     * ������Ҫѭ�����ɵı������------key�Զ��壬valueΪ --ArrayList&lt;Map&lt;String, String>>
     * @author Juveniless
     * @param dataMap
     *
     */
    public void replaceDocument(Map<String, Object> dataMap) {

        if (!dataMap.containsKey("parametersMap")) {
            System.out.println("����Դ����--����Դ(parametersMap)ȱʧ");
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> parametersMap = (Map<String, Object>) dataMap
                .get("parametersMap");

        List<IBodyElement> bodyElements = document.getBodyElements();// ���ж��󣨶���+���
        int templateBodySize = bodyElements.size();// ���ģ���ļ�������+����ܸ���

        int curT = 0;// ��ǰ���������������
        int curP = 0;// ��ǰ����������������
        for (int a = 0; a < templateBodySize; a++) {
            IBodyElement body = bodyElements.get(a);
            if (BodyElementType.TABLE.equals(body.getElementType())) {// ������
                XWPFTable table = body.getBody().getTableArray(curT);

                List<XWPFTable> tables = body.getBody().getTables();
                table = tables.get(curT);
                if (table != null) {

                    // ������
                    List<XWPFTableCell> tableCells = table.getRows().get(0).getTableCells();// ��ȡ��ģ�����һ�У������жϱ������
                    String tableText = table.getText();// ����е������ı�

                    if (tableText.indexOf("##{foreach") > -1) {
                        // ���ҵ�##{foreach��ǩ���ñ����Ҫ����ѭ��
                        if (tableCells.size() != 2
                                || tableCells.get(0).getText().indexOf("##{foreach") < 0
                                || tableCells.get(0).getText().trim().length() == 0) {
                            System.out
                                    .println("�ĵ��е�"
                                            + (curT + 1)
                                            + "�����ģ�����,ģ�����һ����Ҫ����2����Ԫ��"
                                            + "��һ����Ԫ��洢�������(##{foreachTable}## ���� ##{foreachTableRow}##)���ڶ�����Ԫ��������Դ��");
                            return;
                        }

                        String tableType = tableCells.get(0).getText();
                        String dataSource = tableCells.get(1).getText();
                        System.out.println("��ȡ������Դ��"+dataSource);
                        if (!dataMap.containsKey(dataSource)) {
                            System.out.println("�ĵ��е�" + (curT + 1) + "�����ģ������Դȱʧ");
                            return;
                        }
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> tableDataList = (List<Map<String, Object>>) dataMap
                                .get(dataSource);
                        if ("##{foreachTable}##".equals(tableType)) {
                            // System.out.println("ѭ�����ɱ��");
                            addTableInDocFooter(table, tableDataList, parametersMap, 1);

                        } else if ("##{foreachTableRow}##".equals(tableType)) {
                            // System.out.println("ѭ�����ɱ���ڲ�����");
                            addTableInDocFooter(table, tableDataList, parametersMap, 2);
                        }

                    } else if (tableText.indexOf("{") > -1) {
                        // û�в��ҵ�##{foreach��ǩ�����ҵ�����ͨ�滻���ݵ�{}��ǩ���ñ��ֻ��Ҫ���滻
                        addTableInDocFooter(table, null, parametersMap, 3);
                    } else {
                        // û�в��ҵ��κα�ǩ���ñ����һ����̬��񣬽���Ҫ����һ�����ɡ�
                        addTableInDocFooter(table, null, null, 0);
                    }
                    curT++;

                }
            } else if (BodyElementType.PARAGRAPH.equals(body.getElementType())) {// �������
                // System.out.println("��ȡ������");
                XWPFParagraph ph = body.getBody().getParagraphArray(curP);
                if (ph != null) {
                    // htmlText = htmlText+readParagraphX(ph);
                    addParagraphInDocFooter(ph, null, parametersMap, 0);

                    curP++;
                }
            }

        }
        // �������ģ�壬ɾ���ı��е�ģ������
        for (int a = 0; a < templateBodySize; a++) {
            document.removeBodyElement(0);
        }

    }








    /**
     * ���� ģ���� �� ����list ��word�ĵ�ĩβ���ɱ��
     * @author Juveniless
     * @param templateTable ģ����
     * @param list   ѭ�����ݼ�
     * @param parametersMap  ��ѭ�����ݼ�
     * @param flag   (0Ϊ��̬���1Ϊ�������ѭ����2Ϊ����ڲ���ѭ����3Ϊ���ѭ�������滻��ǩ����)
     *
     */
    public void addTableInDocFooter(XWPFTable templateTable, List<Map<String, Object>> list,
                                    Map<String, Object> parametersMap, int flag) {

        if (flag == 1) {// �������ѭ��
            for (Map<String, Object> map : list) {
                List<XWPFTableRow> templateTableRows = templateTable.getRows();// ��ȡģ����������
                XWPFTable newCreateTable = document.createTable();// �����±��,Ĭ��һ��һ��
                for (int i = 1; i < templateTableRows.size(); i++) {
                    XWPFTableRow newCreateRow = newCreateTable.createRow();
                    CopyTableRow(newCreateRow, templateTableRows.get(i));// ����ģ�����ı�����ʽ������
                }
                newCreateTable.removeRow(0);// �Ƴ�������ĵ�һ��
                document.createParagraph();// ��ӻس�����
                replaceTable(newCreateTable, map);//�滻��ǩ
            }

        } else if (flag == 2) {// ������ڲ���ѭ��
            XWPFTable newCreateTable = document.createTable();// �����±��,Ĭ��һ��һ��
            List<XWPFTableRow> TempTableRows = templateTable.getRows();// ��ȡģ����������
            int tagRowsIndex = 0;// ��ǩ��indexs
            for (int i = 0, size = TempTableRows.size(); i < size; i++) {
                String rowText = TempTableRows.get(i).getCell(0).getText();// ��ȡ������еĵ�һ����Ԫ��
                if (rowText.indexOf("##{foreachRows}##") > -1) {
                    tagRowsIndex = i;
                    break;
                }
            }

            /* ����ģ���кͱ�ǩ��֮ǰ���� */
            for (int i = 1; i < tagRowsIndex; i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, TempTableRows.get(i));// ������
                replaceTableRow(newCreateRow, parametersMap);// ����ѭ����ǩ���滻
            }

            /* ѭ������ģ���� */
            XWPFTableRow tempRow = TempTableRows.get(tagRowsIndex + 1);// ��ȡ��ģ����
            for (int i = 0; i < list.size(); i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, tempRow);// ����ģ����
                replaceTableRow(newCreateRow, list.get(i));// �����ǩ�滻
            }

            /* ����ģ���кͱ�ǩ��֮����� */
            for (int i = tagRowsIndex + 2; i < TempTableRows.size(); i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, TempTableRows.get(i));// ������
                replaceTableRow(newCreateRow, parametersMap);// ����ѭ����ǩ���滻
            }
            newCreateTable.removeRow(0);// �Ƴ�������ĵ�һ��
            document.createParagraph();// ��ӻس�����

        } else if (flag == 3) {
            //���ѭ�������滻��ǩ
            List<XWPFTableRow> templateTableRows = templateTable.getRows();// ��ȡģ����������
            XWPFTable newCreateTable = document.createTable();// �����±��,Ĭ��һ��һ��
            for (int i = 0; i < templateTableRows.size(); i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// ����ģ�����ı�����ʽ������
            }
            newCreateTable.removeRow(0);// �Ƴ�������ĵ�һ��
            document.createParagraph();// ��ӻس�����
            replaceTable(newCreateTable, parametersMap);

        } else if (flag == 0) {
            List<XWPFTableRow> templateTableRows = templateTable.getRows();// ��ȡģ����������
            XWPFTable newCreateTable = document.createTable();// �����±��,Ĭ��һ��һ��
            for (int i = 0; i < templateTableRows.size(); i++) {
                XWPFTableRow newCreateRow = newCreateTable.createRow();
                CopyTableRow(newCreateRow, templateTableRows.get(i));// ����ģ�����ı�����ʽ������
            }
            newCreateTable.removeRow(0);// �Ƴ�������ĵ�һ��
            document.createParagraph();// ��ӻس�����
        }

    }






    /**
     * ���� ģ����� �� ���� ���ĵ�ĩβ���ɶ���
     *
     * @author Juveniless
     * @param templateParagraph
     *            ģ�����
     * @param list
     *            ѭ�����ݼ�
     * @param parametersMap
     *            ��ѭ�����ݼ�
     * @param flag
     *            (0Ϊ��ѭ���滻��1Ϊѭ���滻)
     *
     */
    public void addParagraphInDocFooter(XWPFParagraph templateParagraph,
                                        List<Map<String, String>> list, Map<String, Object> parametersMap, int flag) {

        if (flag == 0) {
            XWPFParagraph createParagraph = document.createParagraph();
            // ���ö�����ʽ
            createParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
            // �Ƴ�ԭʼ����
            for (int pos = 0; pos < createParagraph.getRuns().size(); pos++) {
                createParagraph.removeRun(pos);
            }
            // ���Run��ǩ
            for (XWPFRun s : templateParagraph.getRuns()) {
                XWPFRun targetrun = createParagraph.createRun();
                CopyRun(targetrun, s);
            }

            replaceParagraph(createParagraph, parametersMap);

        } else if (flag == 1) {
            // ����ʵ��
        }

    }




    /**
     * ����map�滻����Ԫ���ڵ�{**}��ǩ
     * @author Juveniless
     * @param xWPFParagraph
     * @param parametersMap
     *
     */
    public void replaceParagraph(XWPFParagraph xWPFParagraph, Map<String, Object> parametersMap) {
        List<XWPFRun> runs = xWPFParagraph.getRuns();
        String xWPFParagraphText = xWPFParagraph.getText();
        String regEx = "\\{.+?\\}";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(xWPFParagraphText);//����ƥ���ַ���{****}

        if (matcher.find()) {
            // ���ҵ��б�ǩ��ִ���滻
            int beginRunIndex = xWPFParagraph.searchText("{", new PositionInParagraph()).getBeginRun();// ��ǩ��ʼrunλ��
            int endRunIndex = xWPFParagraph.searchText("}", new PositionInParagraph()).getEndRun();// ������ǩ
            StringBuffer key = new StringBuffer();

            if (beginRunIndex == endRunIndex) {
                // {**}��һ��run��ǩ��
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();

                int beginIndex = beginRunText.indexOf("{");
                int endIndex = beginRunText.indexOf("}");
                int length = beginRunText.length();

                if (beginIndex == 0 && endIndex == length - 1) {
                    // ��run��ǩֻ��{**}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // �����ı�
                    key.append(beginRunText.substring(1, endIndex));
                    insertNewRun.setText(getValueBykey(key.toString(),parametersMap));
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                } else {
                    // ��run��ǩΪ**{**}** ���� **{**} ����{**}**���滻key�󣬻���Ҫ����ԭʼkeyǰ����ı�
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // �����ı�
                    key.append(beginRunText.substring(beginRunText.indexOf("{")+1, beginRunText.indexOf("}")));
                    String textString=beginRunText.substring(0, beginIndex) + getValueBykey(key.toString(),parametersMap)
                            + beginRunText.substring(endIndex + 1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);
                }

            }else {
                // {**}���ֳɶ��run

                //�ȴ�����ʼrun��ǩ,ȡ�õ�һ��{key}ֵ
                XWPFRun beginRun = runs.get(beginRunIndex);
                String beginRunText = beginRun.text();
                int beginIndex = beginRunText.indexOf("{");
                if (beginRunText.length()>1  ) {
                    key.append(beginRunText.substring(beginIndex+1));
                }
                ArrayList<Integer> removeRunList = new ArrayList<Integer>();//��Ҫ�Ƴ���run
                //�����м��run
                for (int i = beginRunIndex + 1; i < endRunIndex; i++) {
                    XWPFRun run = runs.get(i);
                    String runText = run.text();
                    key.append(runText);
                    removeRunList.add(i);
                }

                // ��ȡendRun�е�keyֵ
                XWPFRun endRun = runs.get(endRunIndex);
                String endRunText = endRun.text();
                int endIndex = endRunText.indexOf("}");
                //run��**}����**}**
                if (endRunText.length()>1 && endIndex!=0) {
                    key.append(endRunText.substring(0,endIndex));
                }



                //*******************************************************************
                //ȡ��keyֵ���滻��ǩ

                //�ȴ���ʼ��ǩ
                if (beginRunText.length()==2 ) {
                    // run��ǩ���ı�{
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // �����ı�
                    insertNewRun.setText(getValueBykey(key.toString(),parametersMap));
                    xWPFParagraph.removeRun(beginRunIndex + 1);//�Ƴ�ԭʼ��run
                }else {
                    // ��run��ǩΪ**{**���� {** ���滻key�󣬻���Ҫ����ԭʼkeyǰ���ı�
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(beginRunIndex);
                    insertNewRun.getCTR().setRPr(beginRun.getCTR().getRPr());
                    // �����ı�
                    String textString=beginRunText.substring(0,beginRunText.indexOf("{"))+getValueBykey(key.toString(),parametersMap);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(beginRunIndex + 1);//�Ƴ�ԭʼ��run
                }

                //���������ǩ
                if (endRunText.length()==1 ) {
                    // run��ǩ���ı�ֻ��}
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // �����ı�
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(endRunIndex + 1);//�Ƴ�ԭʼ��run

                }else {
                    // ��run��ǩΪ**}**���� }** ����**}���滻key�󣬻���Ҫ����ԭʼkey����ı�
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(endRunIndex);
                    insertNewRun.getCTR().setRPr(endRun.getCTR().getRPr());
                    // �����ı�
                    String textString=endRunText.substring(endRunText.indexOf("}")+1);
                    insertNewRun.setText(textString);
                    xWPFParagraph.removeRun(endRunIndex + 1);//�Ƴ�ԭʼ��run
                }

                //�����м��run��ǩ
                for (int i = 0; i < removeRunList.size(); i++) {
                    XWPFRun xWPFRun = runs.get(removeRunList.get(i));//ԭʼrun
                    XWPFRun insertNewRun = xWPFParagraph.insertNewRun(removeRunList.get(i));
                    insertNewRun.getCTR().setRPr(xWPFRun.getCTR().getRPr());
                    insertNewRun.setText("");
                    xWPFParagraph.removeRun(removeRunList.get(i) + 1);//�Ƴ�ԭʼ��run
                }

            }// ����${**}���ֳɶ��run

            replaceParagraph( xWPFParagraph, parametersMap);

        }//if �б�ǩ

    }




    /**
     * ���Ʊ����XWPFTableRow��ʽ
     *
     * @param target
     *            ���޸ĸ�ʽ��XWPFTableRow
     * @param source
     *            ģ��XWPFTableRow
     */
    private void CopyTableRow(XWPFTableRow target, XWPFTableRow source) {

        int tempRowCellsize = source.getTableCells().size();// ģ���е�����
        for (int i = 0; i < tempRowCellsize - 1; i++) {
            target.addNewTableCell();// Ϊ����ӵ��������ģ�����Ӧ������ͬ�����ĵ�Ԫ��
        }
        // ������ʽ
        target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        // ���Ƶ�Ԫ��
        for (int i = 0; i < target.getTableCells().size(); i++) {
            copyTableCell(target.getCell(i), source.getCell(i));
        }
    }





    /**
     * ���Ƶ�Ԫ��XWPFTableCell��ʽ
     *
     * @author Juveniless
     * @param newTableCell
     *            �´����ĵĵ�Ԫ��
     * @param templateTableCell
     *            ģ�嵥Ԫ��
     *
     */
    private void copyTableCell(XWPFTableCell newTableCell, XWPFTableCell templateTableCell) {
        // ������
        newTableCell.getCTTc().setTcPr(templateTableCell.getCTTc().getTcPr());
        // ɾ��Ŀ�� targetCell �����ı�����
        for (int pos = 0; pos < newTableCell.getParagraphs().size(); pos++) {
            newTableCell.removeParagraph(pos);
        }
        // ������ı�����
        for (XWPFParagraph sp : templateTableCell.getParagraphs()) {
            XWPFParagraph targetP = newTableCell.addParagraph();
            copyParagraph(targetP, sp);
        }
    }

    /**
     * �����ı�����XWPFParagraph��ʽ
     *
     * @author Juveniless
     * @param newParagraph
     *            �´����ĵĶ���
     * @param templateParagraph
     *            ģ�����
     *
     */
    private void copyParagraph(XWPFParagraph newParagraph, XWPFParagraph templateParagraph) {
        // ���ö�����ʽ
        newParagraph.getCTP().setPPr(templateParagraph.getCTP().getPPr());
        // ���Run��ǩ
        for (int pos = 0; pos < newParagraph.getRuns().size(); pos++) {
            newParagraph.removeRun(pos);

        }
        for (XWPFRun s : templateParagraph.getRuns()) {
            XWPFRun targetrun = newParagraph.createRun();
            CopyRun(targetrun, s);
        }

    }

    /**
     * �����ı��ڵ�run
     * @author Juveniless
     * @param newRun
     *            �´����ĵ��ı��ڵ�
     * @param templateRun
     *            ģ���ı��ڵ�
     *
     */
    private void CopyRun(XWPFRun newRun, XWPFRun templateRun) {
        newRun.getCTR().setRPr(templateRun.getCTR().getRPr());
        // �����ı�
        newRun.setText(templateRun.text());


    }




    /**
     * ���ݲ���parametersMap�Ա���һ�н��б�ǩ���滻
     *
     * @author Juveniless
     * @param tableRow
     *            �����
     * @param parametersMap
     *            ����map
     *
     */
    public void replaceTableRow(XWPFTableRow tableRow, Map<String, Object> parametersMap) {

        List<XWPFTableCell> tableCells = tableRow.getTableCells();
        for (XWPFTableCell xWPFTableCell : tableCells) {
            List<XWPFParagraph> paragraphs = xWPFTableCell.getParagraphs();
            for (XWPFParagraph xwpfParagraph : paragraphs) {

                replaceParagraph(xwpfParagraph, parametersMap);
            }
        }

    }





    /**
     * ����map�滻����е�{key}��ǩ
     * @author Juveniless
     * @param xwpfTable
     * @param parametersMap
     *
     */
    public void replaceTable(XWPFTable xwpfTable,Map<String, Object> parametersMap){
        List<XWPFTableRow> rows = xwpfTable.getRows();
        for (XWPFTableRow xWPFTableRow : rows ) {
            List<XWPFTableCell> tableCells = xWPFTableRow.getTableCells();
            for (XWPFTableCell xWPFTableCell : tableCells ) {
                List<XWPFParagraph> paragraphs2 = xWPFTableCell.getParagraphs();
                for (XWPFParagraph xWPFParagraph : paragraphs2) {
                    replaceParagraph(xWPFParagraph, parametersMap);
                }
            }
        }

    }



    private String getValueBykey(String key, Map<String, Object> map) {
        String returnValue="";
        if (key != null) {
            try {
                returnValue=map.get(key)!=null ? map.get(key).toString() : "";
            } catch (Exception e) {
                System.out.println("key:"+key+"***"+e);
                returnValue="";
            }

        }
        return returnValue;
    }


}

