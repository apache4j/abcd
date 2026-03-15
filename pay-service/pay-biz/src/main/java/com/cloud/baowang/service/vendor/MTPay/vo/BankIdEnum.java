package com.cloud.baowang.service.vendor.MTPay.vo;


/**
 * @author: fangfei
 * @createTime: 2025/01/15 9:36
 * @description:
 */
public enum BankIdEnum {
    //1 = Malaysia - Maybank
    //2 = Malaysia - CIMB
    //3 = Malaysia - RHB
    //4 = Malaysia - Public Bank
    //5 = Malaysia - Hong Leong Bank
    //6 = Malaysia - Alliance Bank
    //7 = Malaysia - Bank Islam
    //8 = Malaysia - Affin Bank
    //9 = Malaysia - AmBank
    //10 = Malaysia – BSN
    //11 = Malaysia – Touch N Go eWallet
    //12 = Malaysia – GX Bank

    MAYB("MAYB", "1", "Maybank"),
    CIMB("CIMB", "2", "CIMB Bank"),
    RHB("RHB", "3", "RHB Bank"),
    PBB("PBB", "4", "Public Bank"),
    HLB("HLB", "5", "Hong Leong Bank"),
    ALLIANCE("ALB", "6", "Alliance Bank Malaysia"),
    ISLAM("BIMB", "7", "Bank Islam"),
    AFFIN("AFBB", "8", "Affin Bank"),
    AMBANK("AMBB", "9", "AmBank Group"),
    BSN("BSN", "10", "BSN"),
    TNGQR("TNGO", "11", "TnG QR"),
    GXB("GXB", "12", "GX Bank");

    private String sourceCode;
    private String bankId;
    private String bankName;

    BankIdEnum(String sourceCode, String bankId, String bankName) {
        this.sourceCode = sourceCode;
        this.bankId = bankId;
        this.bankName = bankName;
    }

    public static String getBankIdBySource(String sourceCode) {
        if (null == sourceCode) {
            return null;
        }
        BankIdEnum[] types = BankIdEnum.values();
        for (BankIdEnum type : types) {
            if (sourceCode.equals(type.getSourceCode())) {
                return type.getBankId();
            }
        }
        return null;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
