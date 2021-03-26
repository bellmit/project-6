package com.miguan.xuanyuan.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by suhongju on 2020/8/11.
 */
public class AESUtils {
    // 密匙
    private static final String KEY = "f4k9f5w7f8g4er26";
    // 偏移量
    private static final String OFFSET = "5e8y6w45ju8w9jq8";
    // 编码
    private static final String ENCODING = "UTF-8";
    //算法
    private static final String ALGORITHM = "AES";
    // 默认的加密算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";


    /**
     * 加密
     * @param data
     * @return
     * @throws Exception
     * @return String
     * @author tyg
     * @date   2018年6月28日下午2:50:35
     */
    public static String encrypt(String data,String offset) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
    }

    public static String encrypt(String data) throws Exception {
        return encrypt(data, AESUtils.OFFSET);
    }

    /**
     * 解密
     * @param data
     * @return
     * @throws Exception
     * @return String
     * @author tyg
     * @date   2018年6月28日下午2:50:43
     */
    public static String decrypt(String data, String offset) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] buffer = new BASE64Decoder().decodeBuffer(data);
        byte[] encrypted = cipher.doFinal(buffer);
        return new String(encrypted, ENCODING);//此处使用BASE64做转码。
    }

    public static String decrypt(String data) throws Exception {
        return decrypt(data, AESUtils.OFFSET);
    }







    public static void main(String[] args) {
        //AES方式加密，密钥：ext-api@98du
        String enckey = null;
        try {
            //输入secretkey
            Map<String,Object> map = new HashMap<>();
            map.put("appKey","A018529B5wM6HIACB65C");
            map.put("secretKey","c5f6d8c1083bb8e463235cdec10ecb547038cdd0");
            map.put("positionKey","89c721d0bec5472580f49368468cad4d");
            map.put("mobileType",1);
            map.put("channelId","xxx1221");
            map.put("appVersion","1.0.46");
            map.put("abExp","12-2,2-3");
            SignUtil.createSignParam(map);
            System.out.print("map:"+map.toString());


            String secretkey = "85663914356f164f92f34a13fb619481";
            //输入appId
            String appId = "com.mg.xyvideo";
            //获取appId的MD5值前16位
            String md5AppId16 = DigestUtils.md5DigestAsHex(appId.getBytes()).substring(0,16);
            //加密
            enckey = AESUtils.encrypt(secretkey,md5AppId16);
            //解密
            String deckey = AESUtils.decrypt(enckey,md5AppId16);
            //判断secretkey和加密解密后的deckey是否一致
            if(StringUtils.isNotEmpty(deckey) && deckey.equals(secretkey)){
                System.out.println("uuid="+StringUtil.getUUID());
                System.out.println("deckey="+deckey);
                System.out.println("enckey="+enckey);
                System.out.println("secretkey和加密解密后的deckey一致!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
