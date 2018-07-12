package com.upcera.service;

import com.thoughtworks.xstream.XStream;
import com.upcera.constant.Constants;
import com.upcera.entity.Body;
import com.upcera.entity.Response;
import com.upcera.entity.Route;
import com.upcera.entity.RouteResponse;
import com.upcera.util.Excels;
import com.upcera.util.WayBills;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 12:33.
 */
public class WayBillService {
    private static Logger logger = LoggerFactory.getLogger(WayBillService.class);
    private static final String OK = "OK";
    private static final String FILE_PATH = "D:/";
    private static final String FILE_NAME = "waybill.xls";
    private static final String ACCEPT_TIME = "accept_time";
    private static final String acceptTime = "acceptTime";
    private static final String ACCEPT_ADDRESS = "accept_address";
    private static final String acceptAddress = "acceptAddress";
       /*544117429464
        544139138266
        544139043954
        544148231544*/

    public void getWayBills() {
        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream);
        xstream.autodetectAnnotations(true);
        xstream.alias("Response", Response.class);
        xstream.allowTypes(new Class[]{Response.class, Route.class});

        List<Response> list = new ArrayList<>();
        List<String> billNos = getWayBillNo();

        for (String billNo : billNos) {
            String wayBillXml = WayBills.getWayBillXml(billNo);
            System.out.println("xml \n." + wayBillXml);
            if (wayBillXml == null || wayBillXml == "" || !isContainRoute(wayBillXml)) {
                System.out.println("运单：" + billNo + " 无法获取轨迹.");
                continue;
            }
            wayBillXml = wayBillXml.replaceAll(ACCEPT_TIME, acceptTime).replaceAll(ACCEPT_ADDRESS, acceptAddress);
            Response mailconfig = (Response) xstream.fromXML(wayBillXml);

            list.add(mailconfig);
        }
        List<Map<String, String>> data = reSetData(list);
        //输出excel
        try {
            String[] title = new String[]{"A", "B"};
            Workbook workbook = Excels.writeXls(title, data, "22");
            FileOutputStream os = new FileOutputStream("./" + "tt" + ".xls");
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


        return lst;
    }

    private List<String> getWayBillNo() {
        List<Map<String, Object>> maps = Excels.readExcels(FILE_PATH, FILE_NAME, 1, 0, 0);
        List<String> list = new ArrayList<>();
        maps.forEach(e -> list.add(String.valueOf(e.get(Constants.PRE_STR + "0"))));
        return list;
    }
}
