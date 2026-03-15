package com.cloud.baowang.play.api.vo.card;

import lombok.Data;


@Data
public class Card implements Comparable<Card> {


    private PokerSuit pokerPattern;
    private PokerNumber pokerNumber;

    public Card() {
    }

    public Card(PokerNumber pokerNumber, PokerSuit pokerPattern) {
        this.pokerPattern = pokerPattern;
        this.pokerNumber = pokerNumber;
    }


    public boolean equals(Object obj) {
//        if (!(obj instanceof Card)) {
//            return false;
//        }
        Card other = (Card) obj;
        return pokerNumber.getPower() == other.getPokerNumber().getPower(); //&& number==other.number
    }

    public int compareTo(Card card) {
        if (pokerNumber.getPower() > card.pokerNumber.getPower()) {
            return 1;
        }
        return -1;
    }

    public Card formatCard(int source) {
        int face = source-1;
        if (face == 52 || face == 53) {
            pokerPattern = PokerSuit.ALL;
        } else {
            switch (face / 13) {
                case 0:
                    pokerPattern = PokerSuit.SPADE;
                    break;
                case 1:
                    pokerPattern = PokerSuit.HEART;
                    break;
                case 2:
                    pokerPattern = PokerSuit.CLUB;
                    break;
                case 3:
                    pokerPattern = PokerSuit.DIAMOND;
                    break;
            }
        }

        if (face == 52) {
            pokerNumber = PokerNumber.RED;
        } else if (face == 53) {
            pokerNumber = PokerNumber.BLACK;
        } else {
            switch (face % 13) {
                case 0:
                    pokerNumber = PokerNumber.ACE;
                    break;
                case 1:
                    pokerNumber = PokerNumber.TWO;
                    break;
                case 2:
                    pokerNumber = PokerNumber.THREE;
                    break;
                case 3:
                    pokerNumber = PokerNumber.FOUR;
                    break;
                case 4:
                    pokerNumber = PokerNumber.FIVE;
                    break;
                case 5:
                    pokerNumber = PokerNumber.SIX;
                    break;
                case 6:
                    pokerNumber = PokerNumber.SEVEN;
                    break;
                case 7:
                    pokerNumber = PokerNumber.EIGHT;
                    break;
                case 8:
                    pokerNumber = PokerNumber.NINE;
                    break;
                case 9:
                    pokerNumber = PokerNumber.TEN;
                    break;
                case 10:
                    pokerNumber = PokerNumber.JACK;
                    break;
                case 11:
                    pokerNumber = PokerNumber.QUEEN;
                    break;
                case 12:
                    pokerNumber = PokerNumber.KING;
                    break;
                case 52:
                    pokerNumber = PokerNumber.RED;
                    break;
                case 53:
                    pokerNumber = PokerNumber.BLACK;
                    break;
            }
        }
        return new Card(pokerNumber, pokerPattern);
    }


    public  Card formatSexyCard(String cardStr) {
        String suitLetter = cardStr.substring(0, 1);
        PokerSuit suit = PokerSuit.fromLetter(suitLetter);

        // 牌号
        String numberStr = cardStr.substring(1);
        if (numberStr.startsWith("0")){
            numberStr = cardStr.substring(1);
        }
        int numberPower = Integer.parseInt(numberStr);
        PokerNumber number = null;

        for (PokerNumber n : PokerNumber.values()) {
            if (n.getPower()==numberPower) {
                number = n;
                break;
            }
        }

        if (number == null) {
            return null;
        }

        return new Card(number, suit);
    }

    public Card formatDBCard(int face) {
        if (face == 52 || face == 53) {
            pokerPattern = PokerSuit.ALL;
        } else {
            switch (face % 4) {
                case 0:
                    pokerPattern = PokerSuit.DIAMOND;
                    break;
                case 1:
                    pokerPattern = PokerSuit.CLUB;
                    break;
                case 2:
                    pokerPattern = PokerSuit.HEART;
                    break;
                case 3:
                    pokerPattern = PokerSuit.SPADE;
                    break;
            }
        }

        if (face == 52) {
            pokerNumber = PokerNumber.RED;
        } else if (face == 53) {
            pokerNumber = PokerNumber.BLACK;
        } else {
            int num  = face/4;
            switch (num) {
                case 0:
                    pokerNumber = PokerNumber.ACE;
                    break;
                case 1:
                    pokerNumber = PokerNumber.TWO;
                    break;
                case 2:
                    pokerNumber = PokerNumber.THREE;
                    break;
                case 3:
                    pokerNumber = PokerNumber.FOUR;
                    break;
                case 4:
                    pokerNumber = PokerNumber.FIVE;
                    break;
                case 5:
                    pokerNumber = PokerNumber.SIX;
                    break;
                case 6:
                    pokerNumber = PokerNumber.SEVEN;
                    break;
                case 7:
                    pokerNumber = PokerNumber.EIGHT;
                    break;
                case 8:
                    pokerNumber = PokerNumber.NINE;
                    break;
                case 9:
                    pokerNumber = PokerNumber.TEN;
                    break;
                case 10:
                    pokerNumber = PokerNumber.JACK;
                    break;
                case 11:
                    pokerNumber = PokerNumber.QUEEN;
                    break;
                case 12:
                    pokerNumber = PokerNumber.KING;
                    break;
            }
        }
        return new Card(pokerNumber, pokerPattern);
    }




    public static void main(String[] args) {
//        System.out.println("Card.main - "+new Card().formatSexyCard("D04"));
//        System.out.println(PokerSuit.DIAMOND);
//        System.out.println(new Card().formatCard(25));

        String source = "0:10:43;14:2:26";
        String[] strings = source.split(";");
        String[] c = strings[1].split(":");
        for (String s : c) {
            Card card = new Card();
            System.out.println("Card.main - "+card.formatDBCard(Integer.parseInt(s)));;
        }
    }


}

