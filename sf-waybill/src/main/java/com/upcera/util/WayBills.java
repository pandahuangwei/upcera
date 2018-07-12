package com.upcera.util;

import com.sf.csim.express.service.CallExpressServiceTools;

public class WayBills {

    private static CallExpressServiceTools client = CallExpressServiceTools.getInstance();


    public static String getSZWayBillXml(String trackingNumber) {
        String myReqXML = getReqXml(Propertys.SZ_CLIENT_CODE, trackingNumber);
        String rs = client.callSfExpressServiceByCSIM(Propertys.SZ_REQ_URL, myReqXML, Propertys.SZ_CLIENT_CODE, Propertys.SZ_CHECK_WORD);
        return rs;
    }

    public static String getSYWayBillXml(String trackingNumber) {
        String myReqXML = getReqXml(Propertys.SY_CLIENT_CODE, trackingNumber);
        String rs = client.callSfExpressServiceByCSIM(Propertys.SY_REQ_URL, myReqXML, Propertys.SY_CLIENT_CODE, Propertys.SY_CHECK_WORD);
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
