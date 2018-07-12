package com.upcera;

import com.upcera.service.WayBillService;
import com.upcera.util.Propertys;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 15:05.
 */
public class Application {

    public static void main(String[] args) {
        Propertys prop = new Propertys();
        WayBillService service = new WayBillService();
        service.getWayBills();

        System.out.println("运单信息获取结束......");
    }
}
