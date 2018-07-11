/**
 * @author Panda.HuangWei313.
 * @since 2018-07-11 18:09.
 */
import java.io.FileInputStream;
import java.io.InputStream;
import com.sf.csim.express.service.CallExpressServiceTools;

public class TestCallExpressService {
    public static void main(String[] args) {
        String reqXml = "";
        try{
            @SuppressWarnings("resource")
            // InputStream is = new FileInputStream("1.order.txt");//下订单
                    //     InputStream is = new FileInputStream("2.order_query.txt");//查订单
                    //     InputStream is = new FileInputStream("3.order_confirm.txt");//订单取消
                    //     InputStream is = new FileInputStream("4.order_filter.txt");//订单筛选
                    //	 InputStream is = new FileInputStream("5.route_queryByMailNo.txt");//路由查询-通过运单号
                    //     InputStream is = new FileInputStream("5.route_queryByOrderNo.txt");//路由查询-通过订单号
                    //     InputStream is = new FileInputStream("7.orderZD.txt");  //子单号申请
                    InputStream is = new FileInputStream("D:/1.order.txt");
            // InputStream is = new FileInputStream("D:/5.route_queryByMailNo.txt");
            byte[] bs = new byte[is.available()];
            is.read(bs);
            reqXml = new String(bs);

          /*  reqXml = new String("\uFEFF<Request service='RouteService' lang='zh-CN'>\n" +
                    "<Head>SLKJ2019</Head>\n" +
                    "<Body>\n" +
                    "<RouteRequest\n" +
                    "tracking_type='1'\n" +
                    "method_type='1'\n" +
                    "tracking_number='444000092338'/>\n" +
                    "</Body>\n" +
                    "</Request>");

            reqXml = getReqXml("AECKQJS","444000092338");*/
        }catch(Exception e){
        }
        // AECKQJS
        //您的应用 校验码 (checkWord) ： fCfDsgQatwYbF8dW9slS2WfRdqGbNAKO
        String reqURL="https://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService";
        String clientCode="AECKQJS";//此处替换为您在丰桥平台获取的顾客编码
        String checkword="fCfDsgQatwYbF8dW9slS2WfRdqGbNAKO";//此处替换为您在丰桥平台获取的校验码
        CallExpressServiceTools client=CallExpressServiceTools.getInstance();
        String myReqXML=reqXml.replace("SLKJ2019",clientCode);
        // String myReqXML= reqXml;
        System.out.println("请求报文："+myReqXML);
        String respXml= client.callSfExpressServiceByCSIM(reqURL, myReqXML, clientCode, checkword);

        if (respXml != null) {
            System.out.println("--------------------------------------");
            System.out.println("返回报文: "+ respXml);
            System.out.println("--------------------------------------");
        }
    }

    private static String getReqXml(String clientCode,String trackingNumber) {
        return new String("\uFEFF<Request service='RouteService' lang='zh-CN'>\n" +
                "<Head>"+clientCode+"</Head>\n" +
                "<Body>\n" +
                "<RouteRequest\n" +
                "tracking_type='1'\n" +
                "method_type='1'\n" +
                "tracking_number='"+trackingNumber+"'/>\n" +
                "</Body>\n" +
                "</Request>");
    }
}
