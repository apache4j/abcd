package com.cloud.baowang.service.vendor.TSPay;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class TSSignUtils {

    public static String createSign(Map<String, Object> params, String privateKeyStr) throws Exception {
        String signString = getSignString(params);

        // Format private key with PEM headers
        privateKeyStr = "-----BEGIN PRIVATE KEY-----\n" + formatKey(privateKeyStr) + "\n-----END PRIVATE KEY-----";

        // Parse private key
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyStr));
        Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKey privateKey = converter.getPrivateKey((PrivateKeyInfo) object);

        // Create signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signString.getBytes());
        byte[] signed = signature.sign();

        return Base64.getEncoder().encodeToString(signed);
    }

    public static boolean verifySign(Map<String, Object> params, String pubKeyStr) throws Exception {
        String sign = params.get("sign").toString();
        String signString = getSignString(params);

        // Format public key with PEM headers
        pubKeyStr = "-----BEGIN PUBLIC KEY-----\n" + formatKey(pubKeyStr) + "\n-----END PUBLIC KEY-----";


        // Parse public key
        PEMParser pemParser = new PEMParser(new StringReader(pubKeyStr));
        Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PublicKey publicKey = converter.getPublicKey((SubjectPublicKeyInfo) object);

        // Verify signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(signString.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(sign);
        return signature.verify(signatureBytes);
    }

    public static String getSignString(Map<String, Object> params) {
        // TreeMap automatically sorts by key
        Map<String, Object> sortedParams = new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().toString().isEmpty() && !entry.getKey().equals("sign")) {
                if (!sb.isEmpty()) {
                    sb.append("&");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return sb.toString();
    }

    private static String formatKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\n", "").replaceAll(" ", "");
    }

    public static void main(String[] args) {
        try {
            // 示例
            Map<String, Object> params = new java.util.HashMap<>(Map.of(
                    "param1", "value1",
                    "param2", "value2"
            ));
            String privateKey = """
                    -----BEGIN PRIVATE KEY-----
                    MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDtyP2dqeJAnKEy
                    BwOdeWLsDBDQ+nAv6peEauk9qVhjkV/g/GoOmQ4iwP3PvteSO/8SFZYY/+Rn2+Fo
                    zQhCTPBAy1UrtSWm99WJ7Z/Bp0oIeJphao5eKRZb5DWXMhN1Be2UxixcJwK5trJH
                    J4icKUUZO/kFQVAc1wemH/kbCiYIhHxRo77pj5qCWmvdhdI5bG30K6Tj/p6qPc+q
                    okWVkU57PdpwRXDrA+3tvfTPka8Aio1nrdfhNAQit/5UVUJvOFRRIVyRAA9DWx/l
                    MBuTBhRSmHXD7FiLurrWLUdTfjrqNW+Z+aIG8iaEyVyOvQQPD5F2ZZilY0Jn/aX4
                    5UIAOYwVAgMBAAECggEAOsiA1hy8159BBFaSkLgE+fjDoX4ABQB4I56weXNna/nw
                    23RbHa+9vF68gEKcFUUqugRHHngINZq4f0mMnKNbk5EQxaTbIYtMKXRqlZuvJCf4
                    dlO6zVFX+zCQBQg2oivsf2Z8ae2k8VdWfFusHHrhX6shVSi/ztljxTBLS2F3ZIwu
                    DwOofTMhpUampXkAkL/T2hLHb8+8nR2KCpLv/smNJxQKNJRVlPd2yjfwFPNcjhQD
                    1P8V2vR49J1GCxfbRM5RyjGDW5rDHmeJYFGDPkgdtbYrhVSxtH3tNSVj0N+jdkvW
                    cs7Huw6+9vgkSfbwQQeQG26JSCedo+wjkfKwaNgKEwKBgQD87U8ebeRSm/WuBWke
                    IM1gN54ZDxpyaCXj1+SsEZlaLigLWaJz0n97lVIZrrlcZxpH7irHL7InOeKpGken
                    rPg/13tyzOaRZHgv4d/wH+9raiTOqFxX0+4AIXXL90BHHAEmtVvOCe1gj3G1Ab5z
                    ODmQnTg2k9jRg/LsLK+cZjWPkwKBgQDwrJXMTzYGJ0iX42JAQ081psTnplrGw0Ol
                    4tuk6ZF8WIGbWJz3RrgCWlZoxTEyALqHlfyX1LLnG5EV/D/EWjUKnXrg2DBMAgiK
                    HVLIqDKqzo5KInDBeLRIMeQIjc97c0PED0B9rYxAMQlzhDCMfpMNYvOurTrOoTD6
                    6Yo/an6utwKBgQD7EB0/WVoRXlqrGRfVHj5/SfYXbdSUyCkEV+PXociVqcd6LMmD
                    Kun8pKZQdA3vWTDPQe0Yt5GiOmVx8nI6UQpDZQRXWmls3UwoS5Bc7XZdK4nJ0XmI
                    fQh8/LnPc2lMrNG/uLqedWA6P2lhgY+pE6Cgmk+YbX4Fy3KxWVIgb5yvHQKBgC82
                    K8yAXWCnpB3nhQaGLaugo0+t2nDTLRoFfoDDML7rvcDziRcY0E/l2L2EsTnV2sFu
                    Mra2CsI7LiLZ8MhbybvjWI9y2UaPv40YY3zpQccv7cmtJ+FMBFGFN+5VozXcTpbP
                    Rx9gQ/rzo579d2iYC9C8cID7imWuSSVvWqdGBMPRAoGBANh441MXSWg0VaG/HXYf
                    +gjmE4JxxrZGDsl6CYMVhX4V3mYfEFSRIajxXh80ZRtZYFh3mt7DUo3qugRKawy5
                    kMYZlCZ6s2/dTh1teVyjFFA12KxpZAulWjg0p0mVQZGbv4nonREHFIjwCxrT6y8B
                    0+TU3YT0+W0j5vBly//7U+O1
                    -----END PRIVATE KEY-----
                    """;

            String publicKey = """
                    -----BEGIN PUBLIC KEY-----
                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7cj9naniQJyhMgcDnXli
                    7AwQ0PpwL+qXhGrpPalYY5Ff4PxqDpkOIsD9z77Xkjv/EhWWGP/kZ9vhaM0IQkzw
                    QMtVK7UlpvfVie2fwadKCHiaYWqOXikWW+Q1lzITdQXtlMYsXCcCubayRyeInClF
                    GTv5BUFQHNcHph/5GwomCIR8UaO+6Y+aglpr3YXSOWxt9Cuk4/6eqj3PqqJFlZFO
                    ez3acEVw6wPt7b30z5GvAIqNZ63X4TQEIrf+VFVCbzhUUSFckQAPQ1sf5TAbkwYU
                    Uph1w+xYi7q61i1HU3466jVvmfmiBvImhMlcjr0EDw+RdmWYpWNCZ/2l+OVCADmM
                    FQIDAQAB
                    -----END PUBLIC KEY-----
                    """;

            // 创建签名
            String sign = createSign(params, privateKey);
            System.out.println("Generated Sign: " + sign);

            // 验证签名
            params.put("sign", sign); // 添加签名到请求参数
            boolean isValid = verifySign(params, publicKey);
            System.out.println("Signature is valid: " + isValid);

        } catch (Exception ignored) {

        }

    }
}
