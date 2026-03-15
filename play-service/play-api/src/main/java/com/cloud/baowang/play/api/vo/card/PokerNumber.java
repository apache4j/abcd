package com.cloud.baowang.play.api.vo.card;

import com.alibaba.fastjson2.annotation.JSONCreator;

/**
 * 枚举类型：每张牌大小
 */
public enum PokerNumber {

    ACE("A", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13),
    RED("R", 53),
    BLACK("B", 54);

    private final String symbol;
    private final int power;

    private PokerNumber(String symbol, int power) {
        this.symbol = symbol;
        this.power = power;
    }

    @Override
    public String toString() {
        return "{symbol:" + symbol + ";power:" + power + "}";
    }

    public int getPower() {
        return power;
    }

    public String getSymbol() {
        return symbol;
    }
    @JSONCreator
    public static PokerNumber fromString(String name) {
        return PokerNumber.valueOf(name);
    }

    public static PokerNumber getCardNumber(int i) {
        PokerNumber cardNumner = null;
        switch (i) {
            case 0:
                cardNumner = PokerNumber.ACE;
                break;
            case 1:
                cardNumner = PokerNumber.TWO;
                break;
            case 2:
                cardNumner = PokerNumber.THREE;
                break;
            case 3:
                cardNumner = PokerNumber.FOUR;
                break;
            case 4:
                cardNumner = PokerNumber.FIVE;
                break;
            case 5:
                cardNumner = PokerNumber.SIX;
                break;
            case 6:
                cardNumner = PokerNumber.SEVEN;
                break;
            case 7:
                cardNumner = PokerNumber.EIGHT;
                break;
            case 8:
                cardNumner = PokerNumber.NINE;
                break;
            case 9:
                cardNumner = PokerNumber.TEN;
                break;
            case 10:
                cardNumner = PokerNumber.JACK;
                break;
            case 11:
                cardNumner = PokerNumber.QUEEN;
                break;
            case 12:
                cardNumner = PokerNumber.KING;
                break;
            case 52:
                cardNumner = PokerNumber.RED;
                break;
            case 53:
                cardNumner = PokerNumber.BLACK;
                break;
            default:
                System.out.println("wrong number!");
        }
        return cardNumner;
    }

}// enum  CARD_NUMBER