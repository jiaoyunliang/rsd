package cn.rsd.server;

import cn.rsd.dao.MeterDataReportMapper;
import cn.rsd.po.MeterDataReport;
import cn.rsd.util.DataConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 焦云亮
 * @date 2018/10/30
 * @modifyUser
 * @modifyDate
 */
@Component("myChannelHandlerAdapter")
@ChannelHandler.Sharable
public class MyChannelHandlerAdapter extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;

    @Autowired
    private MeterDataReportMapper meterDataReportMapper;

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buf.release();
        buf = null;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(1);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] m = (byte[]) msg;
        buf.writeBytes(m);
        try {

            MeterDataReport meterDataReport = new MeterDataReport();

            meterDataReport.setHexStr(DataConverter.bytesToHexString(m));

            // 命令码 5 包长 1 GPRS编号15
            byte[] mlm = new byte[5];
            buf.readBytes(mlm);
            byte[] bc = new byte[1];
            buf.readBytes(bc);
            byte[] gprs = new byte[15];
            buf.readBytes(gprs);
            // 一次读取一个byte
            byte[] bytes = new byte[1];
            buf.readBytes(bytes);
            buf.readBytes(bytes);
            String dataStr = printNumber1(meterDataReport.getHexStr());

            meterDataReport.setDataStr(dataStr);
            try {
                meterDataReport.setRunDate(DateTime.parse(dataStr.substring(118,137),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate());
                meterDataReport.setTableNumber(dataStr.substring(2, 10));
                meterDataReport.setAggregateHeat(new Double(dataStr.substring(32, 41)));
            }catch (Exception e){
                e.printStackTrace();
            }
            meterDataReport.setReportDate(new Date());
            this.meterDataReportMapper.insert(meterDataReport);

//            buf.readBytes(bytes);
//            sb.append("第二次-------------------");
//            buf.readBytes(bytes);
//
//            printNumber(buf);
//
//            buf.readBytes(bytes);
//            sb.append("第三次-------------------");
//            buf.readBytes(bytes);
//
//            printNumber(buf);

//
//            List<MeterDataReport> list = this.meterDataReportMapper.selectAll();
//
//            for(MeterDataReport meterDataReport1:list){
//                meterDataReport1.setDataStr(printNumber1(meterDataReport1.getHexStr()));
//
//                this.meterDataReportMapper.updateByPrimaryKey(meterDataReport1);
//            }
            ctx.close();

        } finally {

        }
    }

    public static void main(String[] args) {
        String str = "4354524C3A52323031383130303030303031363936FEFE682766904318001111812E1F90000000000008270100000800000000170000000035071900002C603600982200660100493514011118200800CB16";
//
//        System.out.println(printNumber1(str));

        System.out.println(Integer.toBinaryString(0));
        System.out.println(toBinary(0,8));
        System.out.println(Integer.toBinaryString(1));
        System.out.println(toBinary(1,8));
        System.out.println(Integer.toBinaryString(2));
        System.out.println(toBinary(2,8));
        System.out.println(Integer.toBinaryString(3));
        System.out.println(toBinary(3,8));
        System.out.println(Integer.toBinaryString(4));
        System.out.println(toBinary(4,8));
        System.out.println(Integer.toBinaryString(5));
        System.out.println(toBinary(5,8));
        System.out.println(Integer.toBinaryString(6));
        System.out.println(toBinary(6,8));
        System.out.println(Integer.toBinaryString(7));
        System.out.println(toBinary(7,8));
        System.out.println(Integer.toBinaryString(8));
        System.out.println(toBinary(8,8));

        String str1 = "表号01270818 商代码001111 数据长度2E 累计热量456465.47MWH 热功率000000.00KW 流速0000.0000m³/h 累计流量155747.84m³ 供水温度23.28℃ 回水温度23.43℃ 实时时间2015-10-11 04:25:50 状态正常 ";

        System.out.println(str1.substring(2,10));
        System.out.println(new Double(str1.substring(32,41)));
        System.out.println(DateTime.parse(str1.substring(118,137),DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDateTime().toString("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将一个int数字转换为二进制的字符串形式。
     *
     * @param num    需要转换的int类型数据
     * @param digits 要转换的二进制位数，位数不足则在前面补0
     * @return 二进制的字符串形式
     */
    public static String toBinary(int num, int digits) {
        String cover = Integer.toBinaryString(1 << digits).substring(1);
        String s = Integer.toBinaryString(num);
        return s.length() < digits ? cover.substring(s.length()) + s : s;
    }


    static String printNumber1(String buf) {
        StringBuffer sb = new StringBuffer();

        //sb.append("起始" + buf.substring(46, 48));
        //sb.append(" ");
        //sb.append("仪表类型" + buf.substring(48, 50));
        //sb.append(" ");
        sb.append("表号" + buf.substring(50, 58));
        sb.append(" ");
        sb.append("商代码" + buf.substring(58, 64));
        sb.append(" ");
        //sb.append("控制字" + buf.substring(64, 66));
        //sb.append(" ");
        sb.append("数据长度" + buf.substring(66, 68));
        sb.append(" ");
        //sb.append("数据标识" + buf.substring(68, 72));
        //sb.append(" ");
        //sb.append("序列号" + buf.substring(72, 74));
        //sb.append(" ");

        //sb.append("累计冷量" + buf.substring(74, 84));
        //sb.append("累计冷量" + concatPoint(reverse(splitStr(buf.substring(74, 82))),2)+unit(buf.substring(82,84)));

        //sb.append(" ");

        //sb.append("累计热量" + buf.substring(84, 94));

        sb.append("累计热量" +concatPoint(reverse(splitStr(buf.substring(84, 92))), 2, ".") + unit(buf.substring(92, 94)));

        sb.append(" ");

        sb.append("热功率" + concatPoint(reverse(splitStr(buf.substring(94, 102))), 2, ".") + unit(buf.substring(102, 104)));

        sb.append(" ");

        sb.append("流速" + concatPoint(reverse(splitStr(buf.substring(104, 112))), 1, ".") + unit(buf.substring(112, 114)));
        sb.append(" ");

        sb.append("累计流量" + concatPoint(reverse(splitStr(buf.substring(114, 122))), 2, ".") + unit(buf.substring(122, 124)));
        sb.append(" ");

        sb.append("供水温度" + concatPoint(reverse(splitStr(buf.substring(124, 128))), 0, ".") + unit(buf.substring(128, 130)));
        sb.append(" ");
        sb.append("回水温度" + concatPoint(reverse(splitStr(buf.substring(130, 134))), 0, ".") + unit(buf.substring(134, 136)));
        sb.append(" ");

        //sb.append("运行时间" + concatPoint(reverse(splitStr(buf.substring(136, 140))),-1)+unit(buf.substring(140,142)));
        //sb.append(" ");
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyyMMddHHmmss");

        sb.append("实时时间" + f.parseLocalDateTime(concatPoint(reverse(splitStr(buf.substring(142, 156))), -1, "")).toString("yyyy-MM-dd HH:mm:ss"));
        sb.append(" ");

        long stateBit = 0x0000010000000111L;

        long l1 = (Integer.parseInt(buf.substring(156, 158)) & 0x000000ffffffffL) << 32;
        long l2 = Integer.parseInt(buf.substring(158, 160)) & 0x00000000ffffffffL;

        long l = l1 | l2;

        System.out.println(l);

        System.out.println(stateBit);
        System.out.println(buf.substring(156, 160));
        System.out.println(Integer.toBinaryString(0) + "==" + Integer.toBinaryString(8));

        sb.append("状态" + unit(concatPoint(reverse(splitStr(buf.substring(156, 160))), -1, "")));


        sb.append(" ");
        //sb.append("校验" + buf.substring(160, 162));
        //sb.append(" ");
        //sb.append("结束位" + buf.substring(162, 164));
        //sb.append(" ");
        return sb.toString();
    }

    private static String unit(String str) {
        String text = "";
        switch (str) {
            case "08":
                text = "MWH";
                break;
            case "17":
                text = "KW";
                break;
            case "35":
                text = "m³/h";
                break;
            case "2C":
                text = "m³";
                break;
            case "00":
                text = "℃";
                break;
            case "0800":
                text = "正常";
                break;
            case "0008":
                text = "正常";
                break;
            default:
                break;
        }

        return text;
    }

    private static String[] reverse(String[] strings) {

        for (int start = 0, end = strings.length - 1; start < end; start++, end--) {
            String temp = strings[end];
            strings[end] = strings[start];
            strings[start] = temp;
        }

        return strings;
    }

    private static String concatPoint(String[] strings, int point, String hao) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i == point || point == -1) {
                sb.append(hao);
            }
        }

        return sb.toString();

    }

    private static String[] splitStr(String str) {

        int m = str.length() / 2;
        if (m * 2 < str.length()) {
            m++;
        }
        String[] strs = new String[m];
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            //每隔两个
            if (i % 2 == 0) {
                strs[j] = "" + str.charAt(i);
            } else {
                strs[j] = strs[j] + str.charAt(i);
                j++;
            }
        }

        return strs;
    }

    private String printNumber(ByteBuf buf) {
        StringBuffer sb = new StringBuffer();

        sb.append("起始" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        sb.append("仪表类型" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        sb.append("地址" + DataConverter.bytesToHexString(showAvailableBytes(buf, 4)));
        sb.append(" ");
        sb.append("商代码" + DataConverter.bytesToHexString(showAvailableBytes(buf, 3)));
        sb.append(" ");
        sb.append("控制字" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        sb.append("数据长度" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        sb.append("数据标识" + DataConverter.bytesToHexString(showAvailableBytes(buf, 2)));
        sb.append(" ");
        sb.append("序列号" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");

        sb.append("累计热量" + DataConverter.bytesToHexString(showAvailableBytes(buf, 5)));
        sb.append(" ");
        sb.append("当前热量" + DataConverter.bytesToHexString(showAvailableBytes(buf, 5)));
        sb.append(" ");
        sb.append("热功率" + DataConverter.bytesToHexString(showAvailableBytes(buf, 5)));
        sb.append(" ");
        sb.append("流速" + DataConverter.bytesToHexString(showAvailableBytes(buf, 5)));
        sb.append(" ");
        sb.append("累计流量" + DataConverter.bytesToHexString(showAvailableBytes(buf, 5)));
        sb.append(" ");
        sb.append("供水温度" + DataConverter.bytesToHexString(showAvailableBytes(buf, 3)));
        sb.append(" ");
        sb.append("回水温度" + DataConverter.bytesToHexString(showAvailableBytes(buf, 3)));
        sb.append(" ");
        sb.append("运行时间" + DataConverter.bytesToHexString(showAvailableBytes(buf, 3)));
        sb.append(" ");
        sb.append("实时时间" + DataConverter.bcd2Str1(showAvailableBytes(buf, 7)));
        sb.append(" ");
        sb.append("状态" + DataConverter.bytesToHexString(showAvailableBytes(buf, 2)));
        sb.append(" ");
        sb.append("校验" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        sb.append("结束位" + DataConverter.bytesToHexString(showAvailableBytes(buf, 1)));
        sb.append(" ");
        return sb.toString();
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static byte[] showAvailableBytes(ByteBuf buf, int len) {
        try {

            byte[] b = new byte[len];
            buf.readBytes(b);

            return b;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
