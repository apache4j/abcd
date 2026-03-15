package com.cloud.baowang.service.vendor.XPay;


/**
 * @author: fangfei
 * @createTime: 2025/01/15 8:32
 * @description:
 */
public enum XpayBankEnum {
    ABB("AFBB", "ABB", "Affin Bank"),
    ABMB("ALB", "ABMB", "Alliance Bank Malaysia"),
    AGRO("AGRO", "AGRO", "Agrobank@"),
    AMBG("AMBB", "AMBG", "AmBank Group"),
    ARB("ALRA", "ARB", "Al Rajhi  Corporation (Malaysia) Berhad"),
    BBB("BBB", "BBB", "Maybank@"),
    BIGPAY("BIGPAY", "BIGPAY", "BigPay@"),
    BIMB("BIMB", "BIMB", "BANK ISLAM MALAYSIA BERHAD"),
    BKRM("BKR", "BKRM", "Bank Kerjasama Rakyat Malaysia Berhad"),
    BMMB("BMMB", "BMMB", "@Bank Muamalat Malaysia Berhad"),
    BNPP("BNPP", "BNPP", "BNP Paribas Malaysia Berhad"),
    BOCM("BOCM", "BOCM", "@Bank Of China (Malaysia) Berhad"),
    BOFA("BOFA", "BOFA", "@Bank Of America (Malaysia) Berhad"),
    BSN("BSN", "BSN", "Bank Simpanan Nasional Berhad"),
    CIMB("CIMB", "CIMB", "CIMB Bank"),
    CITIM("CITI", "CITIM", "Citibank"),
    DBB("DBB", "DBB", "Deutsche Bank (Malaysia) Berhad"),
    FCSD("FCSD", "FCSD", "Finexus Cards Sdn Bhd"),
    HLB("HLB", "HLB", "Hong Leong Bank"),
    HSBC("HSBCBM", "HSBC", "HSBC Bank"),
    ICBC("ICBC", "ICBC", "Industrial And Commercial Bank Of China"),
    JPMC("JPMC", "JPMC", "JP Morgan Chase Bank Berhad"),
    KFH("KFH", "KFH", "Kuwait Finance House (Malaysia) Berhad"),
    MBB("MAYB", "MBB", "Maybank"),
    MBSB("MBSB", "MBSB", "MBSB Bank Berhad"),
    MCBM("MCBM", "MCBM", "Mizuho Bank (Malaysia) Berhad"),
    MUFG("MUFG", "MUFG", "MUFG Bank (Malaysia) Berhad"),
    OCBC("OCBCM", "OCBC", "OCBC Bank"),
    PBB("PBB", "PBB", "Public Bank"),
    PCBC("PCBC", "PCBC", "China Construction Bank (Malaysia) Berhad"),
    RHB("RHB", "RHB", "RHB Bank"),
    SCB("STCB", "SCB", "Standard Chartered Bank"),
    SMBC("SMBC", "SMBC", "Sumitomo Mitsui Banking Corporation (Malaysia) Berhad"),
    TNGD("TNGO", "TNGD", "TnG QR"),
    UOB("UOBB", "UOB", "United Overseas Bank");

    private String sourceCode;
    private String bankCode;
    private String bankName;

    XpayBankEnum(String sourceCode, String bankCode, String bankName) {
        this.sourceCode = sourceCode;
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public static String getBankCodeBySource(String sourceCode) {
        if (null == sourceCode) {
            return null;
        }
        XpayBankEnum[] types = XpayBankEnum.values();
        for (XpayBankEnum type : types) {
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
