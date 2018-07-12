package com.upcera.util;

import com.sf.csim.express.service.CallExpressServiceTools;

public class WayBills {

    private static final String REQ_URL = "http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService";
    private static final String CLIENT_CODE = "AECKQJS";//此处替换为您在丰桥平台获取的顾客编码
    private static final String CHECK_WORD = "fCfDsgQatwYbF8dW9slS2WfRdqGbNAKO";//此处替换为您在丰桥平台获取的校验码

    private static CallExpressServiceTools client = CallExpressServiceTools.getInstance();


    public static String getWayBillXml(String trackingNumber) {
        String myReqXML = getReqXml(CLIENT_CODE, trackingNumber);
        String rs = CallExpressServiceTools.getInstance().callSfExpressServiceByCSIM(REQ_URL, myReqXML, CLIENT_CODE, CHECK_WORD);
        return rs;
    }

    private static String getReqXml(String clientCode, String trackingNumber) {
        return new String("\uFEFF<Request service='RouteService' lang='zh-CN'>\n" +
                "<Head>" + clientCode + "</Head>\n" +
                "<Body>\n" +
                "<RouteRequest\n" +
                "tracking_type='1'\n" +
                "method_type='1'\n" +
                "tracking_number='" + trackingNumber + "'/>\n" +
                "</Body>\n" +
                "</Request>");
    }

}
