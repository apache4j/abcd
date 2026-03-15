package com.cloud.baowang.common.core.utils;

import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
import com.google.protobuf.ByteString;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @Desciption: 地址合法性校验工具类
 * @Author: Ford
 * @Date: 2024/10/25 15:44
 * @Version: V1.0
 **/
public class AddressUtils {
    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;
    private static final BigInteger ALPHABET_SIZE = BigInteger.valueOf("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".length());

    /**
     * 校验地址合法性
     * @param inputAddress 为了方便和ETH地址区分 TRON需要让用户强制输入 T开头的地址
     * @param chainType 链类型 TRON:波场 ETH:以太坊
     * @return true:地址合法 false:地址非法
     */
    public static boolean isValidAddress(String inputAddress,String chainType){
        if(!StringUtils.hasText(inputAddress)){
            return false;
        }
        if("TRON".equals(chainType)){
            if(!inputAddress.startsWith("T")){
                return false;
            }
            return isValidTronAddress(inputAddress);
        }else {
            return isValidETHAddress(inputAddress);
        }
    }

    /**
     * 校验输入地址属于哪种网络
     * @param inputAddress 用户输入的地址
     * @return "TRC20" / "ERC20"，非法地址返回 null
     */
    public static String getAddressNetWork(String inputAddress) {
        if (inputAddress == null || inputAddress.trim().isEmpty()) {
            return null;
        }
        String address = inputAddress.trim();

        // 判断是否是 TRON 地址（以 T 开头）
        if (address.toUpperCase().startsWith("T")) {
            return isValidTronAddress(address) ? NetWorkTypeEnum.TRC20.getCode() : null;
        }

        // 判断是否是 ETH 地址
        return isValidETHAddress(address) ? NetWorkTypeEnum.ERC20.getCode() : null;
    }


    public static boolean isValidTronAddress(String inputAddress){
        try{
            parseAddress(inputAddress);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static boolean isValidETHAddress(String inputAddress){
        String cleanInput = cleanHexPrefix(inputAddress);
        try {
            toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return !StringUtils.isEmpty(input)
                && input.length() > 1
                && input.charAt(0) == '0'
                && input.charAt(1) == 'x';
    }

    public static BigInteger toBigIntNoPrefix(String hexValue) {
        return new BigInteger(hexValue, 16);
    }

    public static ByteString parseAddress(String address) {
        byte[] raw;
        if (address.startsWith("T")) {
            raw = base58ToBytes(address);
        } else if (address.startsWith("41")) {
            raw = org.springframework.security.crypto.codec.Hex.decode(address);
        } else if (address.startsWith("0x")) {
            raw = org.springframework.security.crypto.codec.Hex.decode(address.substring(2));
        } else {
            try {
                raw = Hex.decode(address);
            } catch (Exception var3) {
                throw new IllegalArgumentException("Invalid address: " + address);
            }
        }
        return ByteString.copyFrom(raw);
    }



    public static byte[] base58ToBytes(String s) {
        byte[] concat = base58ToRawBytes(s);
        byte[] data = Arrays.copyOf(concat, concat.length - 4);
        byte[] hash = Arrays.copyOfRange(concat, concat.length - 4, concat.length);
        SHA256.Digest digest = new SHA256.Digest();
        digest.update(data);
        byte[] hash0 = digest.digest();
        digest.reset();
        digest.update(hash0);
        byte[] rehash = Arrays.copyOf(digest.digest(), 4);
        if (!Arrays.equals(rehash, hash)) {
            throw new IllegalArgumentException("Checksum mismatch");
        } else {
            return data;
        }
    }


    static byte[] base58ToRawBytes(String s) {
        BigInteger num = BigInteger.ZERO;

        for(int i = 0; i < s.length(); ++i) {
            num = num.multiply(ALPHABET_SIZE);
            int digit = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".indexOf(s.charAt(i));
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character for Base58Check");
            }

            num = num.add(BigInteger.valueOf(digit));
        }

        byte[] b = num.toByteArray();
        if (b[0] == 0) {
            b = Arrays.copyOfRange(b, 1, b.length);
        }

        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            for(int i = 0; i < s.length() && s.charAt(i) == '1'; ++i) {
                buf.write(0);
            }

            buf.write(b);
            return buf.toByteArray();
        } catch (IOException var5) {
            throw new AssertionError(var5);
        }
    }

   /* public static void main(String[] args) {
        boolean validFlag2=AddressUtils.isValidAddress("TJ3jpTaKF6GMa47woUoL74XoQNwivApDr5","TRON");
        System.out.println(validFlag2);

        boolean validFlag3=AddressUtils.isValidAddress("0x90c9238e046d117641ce1327f2817a02074410a9","ETH");
        System.out.println(validFlag3);

        boolean validFlag4=AddressUtils.isValidAddress("0x90c9238e046d117641ce1327f2817a02074410a9","TRON");
        System.out.println(validFlag4);

        boolean validFlag5=AddressUtils.isValidAddress("TJ3jpTaKF6GMa47woUoL74XoQNwivApDr5","ETH");
        System.out.println(validFlag5);

    }*/
}
