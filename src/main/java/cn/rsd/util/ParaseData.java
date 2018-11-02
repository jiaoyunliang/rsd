package cn.rsd.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/11/1
 * @modifyUser
 * @modifyDate
 */
public class ParaseData {
    static public String decodeHexString(String str) {

        str =HighLowHex(spaceHex(str));

        String value =new BigInteger(str, 16).toString();

        return value;

    }

    static private  String spaceHex(String str){

        char[] array= str.toCharArray();

        if(str.length()<=2) {return str;}

        StringBuffer buffer =new StringBuffer();

        for(int i=0;i<array.length;i++){

            int start =i+1;

            if(start%2==0){

                buffer.append(array[i]).append(" ");

            }else{

                buffer.append(array[i]);

            }

        }

        return buffer.toString();

    }

    static private String HighLowHex(String str){

        if(str.trim().length()<=2){ return str;}

        List<String> list = Arrays.asList( str.split(" "));

        Collections.reverse(list);

        StringBuffer stringBuffer = new StringBuffer();

        for(String string:list){

            stringBuffer.append(string);

        }

        return stringBuffer.toString();

    }

    public static void main(String[] args) {
        // 919
        System.out.println(decodeHexString( "0800"));

    }
}
