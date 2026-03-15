package com.cloud.baowang.service.vendor.PAPay.vo;

import com.cloud.baowang.service.vendor.MTPay.vo.CodeEnum;
import lombok.Getter;

/**
 * @author: fangfei
 * @createTime: 2024/10/30 10:49
 * @description:
 */
@Getter
public enum BankEnum {
    ACB("Asia Commercial Bank (Ngân hàng ACB)", "ACB"),
    ABBANK("An Binh Commercial Joint Stock Bank (Ngân hàng ABBANK)", "ABBANK"),
    AGRIBANK("Vietnam Bank for Agriculture and Rural Development (Ngân hàng Agribank)", "AGRIBANK"),
    BIDV("Bank for Investment and Development of Vietnam (Ngân hàng BIDV)", "BIDV"),
    BVB("Bao Viet Joint Stock Commercial Bank (Ngân hàng BVB)", "BVB"),
    BACABANK("Bac A Commercial Joint Stock Bank (Ngân hàng BACABANK)", "BACABANK"),
    CIMB("Commerce International Merchant Bank (Ngân hàng CIMB)", "CIMB"),
    DBS("DBS Bank Ltd. – Ho Chi Minh City Branch", "DBS"),
    DONGABANK("Eastern Asia Commercial Joint Stock Bank (Ngân hàng DongA)", "DONGABANK"),
    EXIMBANK("Vietnam Export Import Commercial Joint Stock Bank (Ngân hàng Eximbank)", "EXIMBANK"),
    GPB("Global Petro Commercial Joint Stock Bank (Ngân hàng GPBank)", "GPB"),
    HDB("Ho Chi Minh City Development Joint Stock Commercial Bank (Ngân hàng HDBank)", "HDB"),
    HLB("Hong Leong Bank Vietnam Limited (Ngân hàng HLB)", "HLB"),
    HSBC("HSBC Bank Viet Nam Limited", "HSBC"),
    IBK("Industrial Bank of Korea (Ngân hàng IBK)", "IBK"),
    IVB("Indovina Bank Limited (Ngân hàng IVB)", "IVB"),
    KIENLONBANK("Kien Long Commercial Joint Stock Bank (Ngân hàng Kienlongbank)", "KIENLONBANK"),
    KOOKMI("Kookmin Bank", "KOOKMI"),
    LPB("Lien Viet Post Joint Stock Commercial Bank (Ngân hàng LPB)", "LPB"),
    MSB("Maritime Commercial Joint Stock Bank (Ngân hàng MSB)", "MSB"),
    MBBANK("Military Commercial Joint Stock Bank (Ngân hàng MBBank)", "MBBANK"),
    NAMABANK("Nam A Commercial Joint Stock Bank (Ngân hàng NamABank)", "NAMABANK"),
    NCB("National Citizen Bank (Ngân hàng NCB)", "NCB"),
    CBBANK("VietNam Construction Bank (Ngân hàng CBBank)", "CBBANK"),
    NHB_HN("Nonghyup Bank (Ngân hàng NHB)", "NHB HN"),
    OCB("Orient Commercial Joint Stock Bank (Ngân hàng OCB)", "OCB"),
    OCEANBANK("Ocean Commercial One Member Limited Liability Bank (Ngân hàng OceanBank)", "OCEANBANK"),
    PBVN("Public Bank Vietnam (Ngân hàng PBVN)", "PBVN"),
    PGBANK("Petrolimex Group Commercial Joint Stock Bank (Ngân hàng PGBank)", "PGBANK"),
    PVCOMBANK("Vietnam Public Joint Stock Commercial Bank (Ngân hàng PVComBank)", "PVCOMBANK"),
    SACOMBANK("Sai Gon Thuong Tin Commercial Joint-stock Bank (Ngân hàng Sacombank)", "SACOMBANK"),
    SAIGONBANK("Saigon Bank for Industry and Trade (Ngân hàng Saigonbank)", "SAIGONBANK"),
    SCB("Sai Gon Commercial Joint Stock Bank (Ngân hàng SCB)", "SCB"),
    SEABANK("Southeast Asia Joint Stock Commercial Bank (Ngân hàng SeABank)", "SEABANK"),
    SHB("Saigon – Hanoi Commercial Joint Stock Bank (Ngân hàng SHB)", "SHB"),
    SHINHANBANK("Shinhan Bank", "SHINHANBANK"),
    TECHCOMBANK("Vietnam Technological and Commercial Joint Stock Bank (Ngân hàng Techcombank)", "TECHCOMBANK"),
    SCVN("Standard Chartered Bank Viet Nam Limited (Ngân hàng Standard Chartered)", "SCVN"),
    TPBBANK("Tien Phong Commercial Joint Stock Bank (Ngân hàng TPBank)", "TPBBANK"),
    VIETCOMBANK("Joint Stock Commercial Bank for Foreign Trade of Vietnam (Ngân hàng Vietcombank)", "VIETCOMBANK"),
    VIETINBANK("Vietnam Bank for Industry and Trade (Ngân hàng Vietinbank)", "VIETINBANK"),
    VPBBANK("Vietnam Prosperity Joint Stock Commercial Bank (Ngân hàng VPBank)", "VPBBANK"),
    VAB("Viet A Commercial Joint Stock Bank (Ngân hàng VAB)", "VAB"),
    VIB("Vietnam International Commercial Joint Stock Bank (Ngân hàng VIB)", "VIB"),
    VIETCAPITALBANK("Viet Capital Commercial Joint Stock Bank (Ngân hàng VietCapitalBank)", "VIETCAPITALBANK"),
    VIETBANK("Vietnam Thuong Tin Commercial Joint Stock Bank (Ngân hàng VietBank)", "VIETBANK"),
    VRB("Vietnam - Russia Joint Venture Bank (Ngân hàng VRB)", "VRB"),
    WOO("Woori Bank Vietnam Limited (Ngân hàng Woori Bank)", "WOO");


    private String vnBankCode;
    private String bankCode;

    BankEnum(String vnBankCode,String bankCode) {
        this.bankCode = bankCode;
        this.vnBankCode = vnBankCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getVnBankCode() {
        return vnBankCode;
    }

    public void setVnBankCode(String vnBankCode) {
        this.vnBankCode = vnBankCode;
    }

    public static String getVnBankCode(String bankCode) {
        if (bankCode == null) {
            return null;
        }
        BankEnum[] codes = BankEnum.values();
        for (BankEnum code : codes) {
            if (bankCode.equals(code.getBankCode())) {
                return code.getBankCode();
            }
        }
        return null;
    }
}
