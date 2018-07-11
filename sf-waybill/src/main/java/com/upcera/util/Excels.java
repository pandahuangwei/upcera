package com.upcera.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-11 16:46.
 */
public class Excels {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Excels.class);
    public static final String PRE_STR = "var";

    public static List<Map<String, Object>> readExcels(String filepath, String filename, int startRow, int startCol, int sheetNum) {
        String extName = ""; // 扩展名格式：
        if (filename.lastIndexOf(".") >= 0) {
            extName = filename.substring(filename.lastIndexOf("."));
        }
        List<Map<String, Object>> rs = new ArrayList<>();
        if (".xls".equals(extName.toLowerCase())) {
            rs = readXls(filepath, filename, startRow, startCol, sheetNum);
        } else if (".xlsx".equals(extName.toLowerCase())) {
            rs = readXlsx(filepath, filename, startRow, startCol, sheetNum);
        }
        return rs;
    }

    /**
     * @param filepath //文件路径
     * @param filename //文件名
     * @param startrow //开始行号
     * @param startcol //开始列号
     * @param sheetnum //sheet
     * @return list
     */
    public static List<Map<String, Object>> readXls(String filepath, String filename, int startrow, int startcol, int sheetnum) {
        List<Map<String, Object>> varList = new ArrayList<>();

        try {
            File target = new File(filepath, filename);
            FileInputStream fi = new FileInputStream(target);
            HSSFWorkbook wb = new HSSFWorkbook(fi);
            HSSFSheet sheet = wb.getSheetAt(sheetnum);                    //sheet 从0开始
            int rowNum = sheet.getLastRowNum() + 1;                    //取得最后一行的行号

            for (int i = startrow; i < rowNum; i++) {                    //行循环开始

                Map<String, Object> map = new HashMap<>();
                HSSFRow row = sheet.getRow(i);                            //行
                int cellNum = row.getLastCellNum();                    //每行的最后一个单元格位置

                for (int j = startcol; j < cellNum; j++) {                //列循环开始

                    HSSFCell cell = row.getCell(Short.parseShort(j + ""));
                    String cellValue = null;
                    if (null != cell) {
                        switch (cell.getCellType()) {                    // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                            case 0:
                                cellValue = String.valueOf((int) cell.getNumericCellValue());
                                break;
                            case 1:
                                cellValue = cell.getStringCellValue();
                                break;
                            case 2:
                                cellValue = cell.getNumericCellValue() + "";
                                break;
                            case 3:
                                cellValue = "";
                                break;
                            case 4:
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case 5:
                                cellValue = String.valueOf(cell.getErrorCellValue());
                                break;
                        }
                    } else {
                        cellValue = "";
                    }

                    map.put(PRE_STR + j, cellValue);

                }
                varList.add(map);
            }

        } catch (Exception e) {
            logger.error("", e);
        }

        return filter(varList);
    }

    /**
     * 是操作Excel2007的版本，扩展名是.xlsx
     *
     * @param filepath 文件
     * @param startRow 开始行号
     * @param startCol 开始列号
     * @param sheetNum sheet
     * @return list
     */
    public static List<Map<String, Object>> readXlsx(String filepath, String filename, int startRow, int startCol, int sheetNum) {
        List<Map<String, Object>> varList = new ArrayList<>();

        try {
            File target = new File(filepath, filename);
            FileInputStream fi = new FileInputStream(target);
            XSSFWorkbook wb = new XSSFWorkbook(fi);
            XSSFSheet sheet = wb.getSheetAt(sheetNum);                  //sheet 从0开始
            int rowNum = sheet.getLastRowNum() + 1;                     //取得最后一行的行号

            for (int i = startRow; i < rowNum; i++) {                    //行循环开始
                Map<String, Object> map = new HashMap<>();
                XSSFRow row = sheet.getRow(i);                          //行
                int cellNum = row.getLastCellNum();                     //每行的最后一个单元格位置

                for (int j = startCol; j < cellNum; j++) {               //列循环开始

                    XSSFCell cell = row.getCell(Integer.parseInt(j + ""));
                    String cellValue = null;
                    if (null != cell) {
                        switch (cell.getCellType()) {                   // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                            case 0:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    cellValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
                                } else {
                                    cell.setCellType(1);
                                    cellValue = cell.getStringCellValue();
                                }
                                break;
                            case 1:
                                cellValue = cell.getStringCellValue();
                                break;
                            case 2:
                                cellValue = cell.getStringCellValue();
                                break;
                            case 3:
                                cellValue = "";
                                break;
                            case 4:
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case 5:
                                cellValue = String.valueOf(cell.getErrorCellValue());
                                break;
                        }
                    } else {
                        cellValue = "";
                    }
                    map.put(PRE_STR + j, cellValue);
                }
                varList.add(map);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return filter(varList);
    }

    private static List<Map<String, Object>> filter(List<Map<String, Object>> srcList) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (srcList == null || srcList.isEmpty()) {
            return list;
        }

        for (Map<String, Object> pd : list) {
            String var0 = String.valueOf(pd.get(PRE_STR + "0"));
            if (var0 == null) {
                continue;
            }
            list.add(pd);
        }
        return list;
    }
}
