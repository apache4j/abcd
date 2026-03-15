package com.cloud.baowang.play.game.cmd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class CmdBetInfoEnum {

    public static final Map<String, String> SBA_BET_TYPE_1X2 = Map.ofEntries(Map.entry("1", "FT1"),
            Map.entry("2", "FT.X"),
            Map.entry("3", "FT.2"));

    public static final Map<String, String> SBA_BET_TYPE_CS =  Map.ofEntries(
            Map.entry("1", "1:0"),
            Map.entry("2", "2:0"),
            Map.entry("3", "2:1"),
            Map.entry("4", "3:0"),
            Map.entry("5", "3:1"),
            Map.entry("6", "3:2"),
            Map.entry("7", "4:0(FT)"),
            Map.entry("8", "4:1(FT)"),
            Map.entry("9", "4:2(FT)"),
            Map.entry("10", "4:3(FT)"),
            Map.entry("11", "5:0 UP(Disable)"),
            Map.entry("12", "0:5 UP(Disable)"),
            Map.entry("13", "0:0"),
            Map.entry("14", "1:1"),
            Map.entry("15", "2:2"),
            Map.entry("16", "3:3"),
            Map.entry("17", "4:4(FT)"),
            Map.entry("18", "0:1"),
            Map.entry("19", "0:2"),
            Map.entry("20", "1:2"),
            Map.entry("21", "0:3"),
            Map.entry("22", "1:3"),
            Map.entry("23", "2:3"),
            Map.entry("24", "0:4(FT)"),
            Map.entry("25", "1:4(FT)"),
            Map.entry("26", "2:4(FT)"),
            Map.entry("27", "3:4(FT)"),
            Map.entry("-99", "AOS")
            );

    public static final Map<String, String>  SBA_BET_TYPE_FLG=Map.ofEntries(
            Map.entry("1", "First Goal"),
            Map.entry("2", "First Goal"),
            Map.entry("3", "Last Goal"),
            Map.entry("4", "Last Goal"),
            Map.entry("5", "No Goal"));

    public static final Map<String, String>  SBA_BET_TYPE_HDP =Map.ofEntries(
            Map.entry("1", "Home"),
            Map.entry("2", "Away"));
    public static final Map<String, String>  SBA_BET_TYPE_HFT =Map.ofEntries(
            Map.entry("1", "Home/Home"),Map.entry("2", "Home/Draw")
            ,Map.entry("3", "Home/Away"),Map.entry("4", "Draw/Home")
            ,Map.entry("5", "Draw/Draw"),Map.entry("6", "Draw/Away")
            ,Map.entry("7", "Away/Home"),Map.entry("8", "Away/Draw")
            ,Map.entry("9", "Away/Away"));

    public static final Map<String, String>  SBA_BET_TYPE_OE =Map.ofEntries(Map.entry("1", "Odd"),Map.entry("2", "Even"));
    public static final Map<String, String>  SBA_BET_TYPE_OU =Map.ofEntries(Map.entry("1", "Over"),Map.entry("2", "Under"));
    public static final Map<String, String>  SBA_BET_TYPE_OUT =Map.ofEntries(Map.entry("1", "Outright"));
    public static final Map<String, String>  SBA_BET_TYPE_PAR =Map.ofEntries(Map.entry("1", "Mixed Parlay"));
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
            Map.entry("1", "Home or Draw"),Map.entry("2", "Home or Away"),Map.entry("3", "Away or Draw"));

    public static final Map<String, String>  SBA_BET_TYPE_ETG =Map.ofEntries(
            Map.entry("1", "0 Goal"),Map.entry("2", "1 Goal"),Map.entry("3", "2 Goal")
            ,Map.entry("4", "3 Goal"),Map.entry("5", "4 Goal"),Map.entry("6", "5 Goal"),
            Map.entry("7", "6+"));

    public static final Map<String, String>  SBA_BET_TYPE_HTG =Map.ofEntries(Map.entry("1", "0 Goal"),
            Map.entry("2", "1 Goal"),
            Map.entry("3", "2 Goal"),Map.entry("4", "3+"));

    public static final Map<String, String>  SBA_BET_TYPE_ATG =Map.ofEntries(Map.entry("1", "0 Goal"),
            Map.entry("2", "1 Goal"),
            Map.entry("3", "2 Goal"),Map.entry("4", "3+"));

    public static final Map<String, String>  SBA_BET_TYPE_HP3 =Map.ofEntries(Map.entry("1", "Home"),Map.entry("2", "Draw"),Map.entry("3", "Away"));
    public static final Map<String, String>  SBA_BET_TYPE_CNS =Map.ofEntries(Map.entry("1", "Home YES"),Map.entry("2", "Home NO"),Map.entry("3", "Away YES"),Map.entry("4", "Away NO"));

}
