package com.cloud.baowang.service.vendor.LuckyPay;


/**
 * @author: fangfei
 * @createTime: 2025/01/15 8:32
 * @description:
 */
public enum BankEnum {
    MBB("MAYB", "MBB", "Maybank"),
    CIMB("CIMB", "CIMB", "CIMB Bank"),
    RHB("RHB", "RHB", "RHB Bank"),
    PBB("PBB", "PBB", "Public Bank"),
    HLB("HLB", "HLB", "Hong Leong Bank"),
    AFFIN("AFBB", "AFFIN", "Affin Bank"),
    ALLIANCE("ALB", "ALLIANCE", "Alliance Bank Malaysia"),
    AMBANK("AMBB", "AMBANK", "AmBank Group"),
    ISLAM("BIMB", "ISLAM", "Bank Islam"),
    BSN("BSN", "BSN", "BSN"),
    CITIM("CITI", "CITIM", "Citibank"),
    HSBCM("HSBCBM", "HSBCM", "HSBC Bank"),
    OCBCM("OCBCM", "OCBCM", "OCBC Bank"),
    UOBM("UOBB", "UOBM", "United Overseas Bank"),
    STCM("STCB", "STCM", "Standard Chartered Bank"),
    TNGQR("TNGO", "TNGQR", "TnG QR");

    private String sourceCode;
    private String bankCode;
    private String bankName;

    BankEnum(String sourceCode, String bankCode, String bankName) {
        this.sourceCode = sourceCode;
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public static String getBankCodeBySource(String sourceCode) {
        if (null == sourceCode) {
            return null;
        }
        BankEnum[] types = BankEnum.values();
        for (BankEnum type : types) {
            if (sourceCode.equals(type.getSourceCode())) {
                return type.getBankCode();
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
