package cn.rsd;

import cn.rsd.po.BuyerOrder;
import cn.rsd.util.DataConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 焦云亮
 * @date 2018/10/11
 * @modifyUser
 * @modifyDate
 */
public class Test {
    public static void main(String[] args) {
        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setState(6);
        System.out.println(BuyerOrder.BuyerOrderStateEnum.UNPAID.ordinal());
      System.out.println(  buyerOrder.getState() == BuyerOrder.BuyerOrderStateEnum.UNPAID.ordinal());
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static void readFileByBytes(String fileName) {

        InputStream in = null;
        try {

            // 一次读多个字节
            in = new FileInputStream(fileName);

            // 命令码 5 包长 1 GPRS编号15

            byte[] mlm = new byte[5];

            in.read(mlm);

            //System.out.println(mlm);

            byte[] bc = new byte[1];

            in.read(bc);

            //System.out.println(bc);

            byte[] gprs = new byte[15];

            in.read(gprs);

            //System.out.println(gprs);

            // 一次读取一个byte
            byte[] bytes = new byte[1];
            String ret = "";
            //while (in.read(bytes) != -1) {
            in.read(bytes);

            in.read(bytes);

            System.out.println("起始" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            System.out.println("仪表类型" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            System.out.println("地址" + DataConverter.bytesToHexString(showAvailableBytes(in, 4)));

            System.out.println("商代码" + DataConverter.bytesToHexString(showAvailableBytes(in, 3)));

            System.out.println("控制字" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            System.out.println("数据长度" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            System.out.println("数据标识" + DataConverter.bytesToHexString(showAvailableBytes(in, 2)));

            System.out.println("序列号" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            //累计热量
            byte[] ljrl = new byte[4];
            in.read(ljrl);
            System.out.println("累计热量" + Integer.parseInt(DataConverter.bcd2Str1(ljrl)) / 100);

            in.read(new byte[1]);

            System.out.println("当前热量" + DataConverter.bytesToHexString(showAvailableBytes(in, 5)));

            System.out.println("热功率" + DataConverter.bytesToHexString(showAvailableBytes(in, 5)));

            System.out.println("流速" + DataConverter.bytesToHexString(showAvailableBytes(in, 5)));

            System.out.println("累计流量" + DataConverter.bytesToHexString(showAvailableBytes(in, 5)));

            System.out.println("供水温度" + DataConverter.bytesToHexString(showAvailableBytes(in,3)));

            System.out.println("回水温度" + DataConverter.bytesToHexString(showAvailableBytes(in, 3)));

            System.out.println("运行时间" + DataConverter.bytesToHexString(showAvailableBytes(in, 3)));

            System.out.println("实时时间" + DataConverter.bcd2Str1(showAvailableBytes(in, 7)));

            System.out.println("状态" + DataConverter.bytesToHexString(showAvailableBytes(in, 2)));

            System.out.println("校验" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));

            System.out.println("结束位" + DataConverter.bytesToHexString(showAvailableBytes(in, 1)));
            //}
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 秒转换为指定格式的日期
     * @param second
     * @param patten
     * @return
     */
    private static String secondToDate(long second,String patten) {
        Calendar calendar = Calendar.getInstance();
        //转换为毫秒
        calendar.setTimeInMillis(second * 1000);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static byte[] showAvailableBytes(InputStream in, int len) {
        try {

            byte[] b = new byte[len];
            in.read(b);

            return b;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Long cDate(String d){
        System.out.println(Long.parseLong(d));

        return  Long.parseLong(d)/100;
    }

}
