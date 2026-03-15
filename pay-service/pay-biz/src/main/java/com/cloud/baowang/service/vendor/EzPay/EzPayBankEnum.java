package com.cloud.baowang.service.vendor.EzPay;


/**
 * @author: fangfei
 * @createTime: 2025/01/15 8:32
 * @description:
 */
public enum EzPayBankEnum {
    CTS("CTS", "City Savings Bank"),
    GW_PDX("GW_PDX", "PDAX"),
    GW_SSB("GW_SSB", "Sun Savings Bank, Inc."),
    PSCOP("PSCOP", "Producers Savings Bank Corporation"),
    GW_GBY("GW_GBY", "GrabPay"),
    RCBC("RCBC", "RCBC / DiskarTech"),
    RBG("RBG", "Asenso"),
    CTCBP("CTCBP", "CTBC Bank (Philippines) Corp"),
    GW_UNO("GW_UNO", "UNObank"),
    CHSVP("CHSVP", "China Bank Savings"),
    GW_TDBBank("GW_TDB", "Tonik Bank"),
    GW_APY("GW_APY","Alipay / Lazada Wallet"),
    QCDFP("QCDFP","Queen City Development Bank"),
    OMNIP("OMNIP","OMNIPAY"),
    SME("SME","CARD SME Bank"),
    QCRIP("QCRIP","QUEZON CAPITAL RURAL BANK"),
    CPHIP("CPHIP","PBCOM"),
    CELRP("CELRP","Cebuana Lhuillier Bank"),
    GW_BCH("GW_BCH","Bank of China"),
    SCBLP("SCBLP","Standard Chartered Bank - Manila Branch"),
    MBTCP("MBTCP","Metrobank"),


    GW_CMG("GW_CMG","Camalig Bank"),
    EAWRP("EAWRP","Komo / East West Rural Bank, Inc."),
    BMB("BMB","Bangko Mabuhay"),
    GW_NBK("GW_NBK","Netbank"),
    GW_MYA("GW_MYA","Maya Bank, Inc."),
    GW_TPI("GW_TPI","TraxionPay/DigiCOOP/COOPNET"),
    UBP("UBP","UNION BANK OF THE PHILIPPINES"),

    EPGEC("EPGEC","Easy Pay Global EMI Corp"),
    GW_IRI("GW_IRI","I-Remit / iCASH"),
    MAARP("MAARP","Malayan Bank Savings & Mortgaga Bank,Inc."),
    MARCP("MARCP","MarCoPay"),
    CHBKP("CHBKP","China Bank"),
    ALKBP("ALKBP","AllBank, Inc."),

    PRTOP("PRTOP","PARTNER RURAL BANK"),
    GW_SEA("GW_SEA","Seabank"),
    BF("BF","Banana Fintech / BananaPay"),

    GW_GOT("GW_GOT","GoTyme Bank"),
    STARP("STARP","STARPAY"),
    UCPBC("UCPBC","UCPB Savings Bank"),
    BDOBK("BDOBK","BDO Bank"),

    WEDVP("WEDVP","Wealth Development Bank Corp."),
    GW_PPS("GW_PPS","PalawanPay"),
    PNBC("PNBC","PNB"),
    BGB("BGB","BanKo, A Subsidiary of BPI"),


    GCASH("GCASH","GCach")
    ;


    private String bankCode;
    private String bankName;

    EzPayBankEnum(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
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
