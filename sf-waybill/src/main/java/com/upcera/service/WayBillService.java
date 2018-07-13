package com.upcera.service;

import com.thoughtworks.xstream.XStream;
import com.upcera.constant.Constants;
import com.upcera.entity.Response;
import com.upcera.entity.Route;
import com.upcera.util.Excels;
import com.upcera.util.WayBills;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 12:33.
 */
public class WayBillService {
    private static Logger logger = LoggerFactory.getLogger(WayBillService.class);
    private static final String OK = "OK";
    private static final String BILL_TYPE_SZ = "SZ";
    private static final String BILL_TYPE_SY = "SY";

    private static final String FILE_PATH = "./";
    private static final String SZ_FILE_NAME = "sz-waybill.xls";
    private static final String SY_FILE_NAME = "sy-waybill.xls";

    private static final String ACCEPT_TIME = "accept_time";
    private static final String acceptTime = "acceptTime";
    private static final String ACCEPT_ADDRESS = "accept_address";
    private static final String acceptAddress = "acceptAddress";

    private static XStream xstream;

    static {
        xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.autodetectAnnotations(true);
        xstream.alias("Response", Response.class);
        xstream.allowTypes(new Class[]{Response.class, Route.class});
    }

    /*544117429464
     544139138266
     544139043954
     544148231544*/
    public void getWayBills() {
        List<String> billNos = getWayBillNo(SZ_FILE_NAME);
        if (billNos != null && !billNos.isEmpty()) {
            getWayBills(billNos, BILL_TYPE_SZ);
        }

        billNos = getWayBillNo(SY_FILE_NAME);
        if (billNos != null && !billNos.isEmpty()) {
            getWayBills(billNos, BILL_TYPE_SY);
        }
    }


    public void getWayBills(List<String> billNos, String billType) {
        List<Response> list = new ArrayList<>();
        for (String billNo : billNos) {
            String wayBillXml;
            if (BILL_TYPE_SZ.equals(billType)) {
                wayBillXml = WayBills.getSZWayBillXml(billNo);
            } else {
                wayBillXml = WayBills.getSYWayBillXml(billNo);
            }

            if (wayBillXml == null || wayBillXml == "" || !isContainRoute(wayBillXml)) {
                System.out.println("运单：" + billNo + " 获取轨迹失败....");
                continue;
            }
            wayBillXml = wayBillXml.replaceAll(ACCEPT_TIME, acceptTime).replaceAll(ACCEPT_ADDRESS, acceptAddress);
            Response mailconfig = (Response) xstream.fromXML(wayBillXml);
            System.out.println("运单：" + billNo + " 获取轨迹成功.");
            list.add(mailconfig);
        }
        if(list.isEmpty()) {
            return;
        }
        List<Map<String, String>> data = reSetData(list);
        //输出excel
        try {
            System.out.println("=====================================");
            System.out.println("==本次应获取运单的轨迹数= " + billNos.size());
            System.out.println("==实际获取运单的轨迹数= " + list.size());
            System.out.println("=====================================");

            String outFile = "./" + billType + "-" + getTimeFormat() + ".xls";
            System.out.println("开始生成Excel:" + outFile);
            String[] title = new String[]{"运单号", "运单轨迹"};
            Workbook workbook = Excels.writeXls(title, data, "运单轨迹");
            FileOutputStream os = new FileOutputStream(outFile);
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isContainRoute(String wayBillXml) {
        return wayBillXml.contains("remark");
    }

    private List<Map<String, String>> reSetData(List<Response> list) {
        List<Map<String, String>> lst = new ArrayList<>(list.size());
        for (Response r : list) {
            Map<String, String> map = new HashMap<>();
            String mailNo = r.getBody().getRouteResponse().getMailno();
            List<Route> routes = r.getBody().getRouteResponse().getRoutes();
            map.put(Constants.PRE_STR + "1", mailNo);
            map.put(Constants.PRE_STR + "2", toString(routes));
            lst.add(map);
        }

        return lst;
    }

    private List<String> getWayBillNo(String fileName) {
        List<Map<String, Object>> maps = Excels.readExcels(FILE_PATH, fileName, 1, 0, 0);
        List<String> list = new ArrayList<>();
        maps.forEach(e -> list.add(String.valueOf(e.get(Constants.PRE_STR + "0"))));
        return list;
    }

    private String toString(List<Route> list) {
        StringBuffer sb = new StringBuffer(512);
        for (Route r : list) {
            sb.append(r.getFormat()).append("\n");
        }
        return sb.toString();
    }

    private String getTimeFormat() {
        int i = (int) (1 + Math.random() * (10 - 1 + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String str = sdf.format(new Date());
        return str + i;
    }
}
