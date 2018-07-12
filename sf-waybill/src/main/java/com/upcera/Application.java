package com.upcera;

import com.upcera.service.WayBillService;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 15:05.
 */
public class Application {

    public static void main(String[] args) {
        WayBillService service = new WayBillService();
        service.getWayBills();
    }
}
