package com.upcera.util;

import java.io.*;
import java.util.Properties;

public class Propertys {
    private static final String FILE_PATH = "./config.properties";
    private static Properties prop;

    public static String SZ_REQ_URL;
    public static String SZ_CLIENT_CODE;
    public static String SZ_CHECK_WORD;

    public static String SY_REQ_URL;
    public static String SY_CLIENT_CODE;
    public static String SY_CHECK_WORD;

    static {
        try {
            prop = new Properties();
            // 通过输入缓冲流进行读取配置文件
            InputStream InputStream = new BufferedInputStream(new FileInputStream(new File(FILE_PATH)));
            // 加载输入流
            prop.load(InputStream);

            SZ_REQ_URL = prop.getProperty("sz_req_url");
            SZ_CLIENT_CODE = prop.getProperty("sz_client_code");
            SZ_CHECK_WORD = prop.getProperty("sz_check_word");

            SY_REQ_URL = prop.getProperty("sy_req_url");
            SY_CLIENT_CODE = prop.getProperty("sy_client_code");
            SY_CHECK_WORD = prop.getProperty("sy_check_word");

            System.out.println("===load Properties:===");
            System.out.println("sz_req_url:" + SZ_REQ_URL);
            System.out.println("sz_client_code:" + SZ_CLIENT_CODE);
            System.out.println("sz_check_word:" + SZ_CHECK_WORD);

            System.out.println("sy_req_url:" + SY_REQ_URL);
            System.out.println("sy_client_code:" + SY_CLIENT_CODE);
            System.out.println("sy_check_word:" + SY_CHECK_WORD);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
