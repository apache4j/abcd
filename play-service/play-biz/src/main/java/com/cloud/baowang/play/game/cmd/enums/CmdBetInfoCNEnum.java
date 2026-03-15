package com.cloud.baowang.play.game.cmd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class CmdBetInfoCNEnum {

    public static final Map<String, String> SBA_BET_TYPE_1X2 = Map.ofEntries(Map.entry("1", "全场主"),
            Map.entry("2", "全场和"),
            Map.entry("3", "全场客"));

    public static final Map<String, String> SBA_BET_TYPE_CS =  Map.ofEntries(
            Map.entry("1", "1:0 "),
            Map.entry("2", "2:0"),
            Map.entry("3", "2:1"),
            Map.entry("4", "3:0"),
            Map.entry("5", "3:1"),
            Map.entry("6", "3:2"),
            Map.entry("7", "4:0(全场)"),
            Map.entry("8", "4:1(全场)"),
            Map.entry("9", "4:2(全场)"),
            Map.entry("10", "4:3(全场)"),
            Map.entry("11", "5:0 UP(Disable)"),
            Map.entry("12", "0:5 UP(Disable)"),
            Map.entry("13", "0:0"),
            Map.entry("14", "1:1"),
            Map.entry("15", "2:2"),
            Map.entry("16", "3:3"),
            Map.entry("17", "4:4(全场)"),
            Map.entry("18", "0:1"),
            Map.entry("19", "0:2"),
            Map.entry("20", "1:2"),
            Map.entry("21", "0:3"),
            Map.entry("22", "1:3"),
            Map.entry("23", "2:3"),
            Map.entry("24", "0:4(全场)"),
            Map.entry("25", "1:4(全场)"),
            Map.entry("26", "2:4(全场)"),
            Map.entry("27", "3:4(全场)"),
            Map.entry("-99", "其它比分")
            );

    public static final Map<String, String>  SBA_BET_TYPE_FLG=Map.ofEntries(
            Map.entry("1", "(最先进球)"),
            Map.entry("2", "(最先进球)"),
            Map.entry("3", "(最后进球)"),
            Map.entry("4", "(最后进球)"),
            Map.entry("5", "没进球"));

    public static final Map<String, String>  SBA_BET_TYPE_HDP =Map.ofEntries(
            Map.entry("1", "主队"),
            Map.entry("2", "客队"));

    public static final Map<String, String>  SBA_BET_TYPE_HFT =Map.ofEntries(
            Map.entry("1", "主主"),Map.entry("2", "主和")
            ,Map.entry("3", "主客"),Map.entry("4", "和主")
            ,Map.entry("5", "和和"),Map.entry("6", "和客")
            ,Map.entry("7", "客主"),Map.entry("8", "客和")
            ,Map.entry("9", "客客"));
    public static final Map<String, String>  SBA_BET_TYPE_OE =Map.ofEntries(Map.entry("1", "单"),Map.entry("2", "双"));
    public static final Map<String, String>  SBA_BET_TYPE_OU =Map.ofEntries(Map.entry("1", "大"),Map.entry("2", "小"));
    public static final Map<String, String>  SBA_BET_TYPE_OUT =Map.ofEntries(Map.entry("1", "优胜冠军"));
    public static final Map<String, String>  SBA_BET_TYPE_PAR =Map.ofEntries(Map.entry("1", "混合过关"));
    public static final Map<String, String>  SBA_BET_TYPE_TG =Map.ofEntries(
            Map.entry("1", "0-1"),
            Map.entry("2", "2-3"),
            Map.entry("3", "4-6"),
            Map.entry("4", "7+"));

    public static final Map<String, String>  SBA_BET_TYPE_TG1H =Map.ofEntries(
            Map.entry("1", "0-1"),
            Map.entry("2", "2-3"),
            Map.entry("3", "4+"));


    public static final Map<String, String>  SBA_BET_TYPE_DC =Map.ofEntries(
            Map.entry("1", "主 或 和局"),Map.entry("2", "主 或 客"),Map.entry("3", "客 或 和局"));

    public static final Map<String, String>  SBA_BET_TYPE_ETG =Map.ofEntries(
            Map.entry("1", "0个入球"),Map.entry("2", "1个入球"),Map.entry("3", "2个入球")
            ,Map.entry("4", "3个入球"),Map.entry("5", "4个入球"),Map.entry("6", "5个入球"),
            Map.entry("7", "6+"));

    public static final Map<String, String>  SBA_BET_TYPE_HTG =Map.ofEntries(Map.entry("1", "0个入球"),
            Map.entry("2", "1个入球"),
            Map.entry("3", "2个入球"),Map.entry("4", "3+"));

    public static final Map<String, String>  SBA_BET_TYPE_ATG =Map.ofEntries(Map.entry("1", "0个入球"),
            Map.entry("2", "1个入球"),
            Map.entry("3", "2个入球"),Map.entry("4", "3+"));

    public static final Map<String, String>  SBA_BET_TYPE_HP3 =Map.ofEntries(Map.entry("1", "主"),Map.entry("2", "和"),Map.entry("3", "客"));
    public static final Map<String, String>  SBA_BET_TYPE_CNS =Map.ofEntries(Map.entry("1", "主队 是"),Map.entry("2", "主队 否"),
            Map.entry("3", "客队 是"),Map.entry("4", "客队 否"));

}
