package cn.rsd;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
/**
 * @author 焦云亮
 * @date 2018/10/10
 * @modifyUser
 * @modifyDate
 */

public class Client1 {
    public static void main(String[] args) throws IOException {
        //客户端请求与本机在20006端口建立TCP连接
        Socket client = new Socket("39.106.2.250", 5570);
        client.setSoTimeout(10000);
        //获取键盘输入
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        InputStream in = new FileInputStream("D:\\tmp\\1539234290588");
        OutputStream  out = client.getOutputStream();
        byte[] buffer = new byte[1024];

        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        //获取Socket的输入流，用来接收从服务端发送过来的数据
        BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
        boolean flag = true;
        while(flag){
            try{
                //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
                String echo = buf.readLine();
                System.out.println(echo);
            }catch(SocketTimeoutException e){
                System.out.println("Time out, No response");
            }
            flag = false;
        }
        input.close();
        if(client != null){
            //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭
            client.close();	//只关闭socket，其关联的输入输出流也会被关闭
        }
    }
}