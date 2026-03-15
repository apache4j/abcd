import java.util.Base64;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/1 10:48
 * @Version: V1.0
 **/
public class Test {
    public static void main(String[] args) {
        byte[]  base=Base64.getDecoder().decode("eyJhbGciOiJIUzUxMiJ9.eyJhZG1pbl9yb2xlX2xpc3QiOlsiMjUiXSwidXNlck5hbWUiOiJmb3JkMDAwIiwiU0lURV9DT0RFIjoiZGV2IiwiYWRtaW5fa2V5IjoiNDQ4MDE3NWU1ZWNkNGU1YzkxOWRlNjNmYTAzYTFmZjYiLCJzdXBlckFkbWluIjpmYWxzZSwiYWRtaW5faWQiOiIxNzkyNzUyODU2ODY5NzYxMDI2In0.TdlEwBVsN_PqTEDSn2OA-06HIAyCbSNapoJhsQ6DdPBbpe4-MnJI_nON3IbMsfoNWA7UNAviB5S0wc7QgRti0A");
        System.err.println(new String(base));
    }
}
