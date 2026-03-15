package script

import com.cloud.baowang.play.po.OrderRecordPO
import groovy.json.JsonSlurper


String parseGameOrder(String jsonText, OrderRecordPO record) {
    def venueType=record.getVenueType();
    println("收到的参数 record : $record ") //groovy内部打印
    println("收到的参数 venueType : $venueType ")

    logger.info("收到 record: $record")//跟随java日志打印

    if(venueType==1){
        return parseGame1Order(jsonText,record)
    }

    if(venueType==2){
        return parseGame2Order(jsonText,record)
    }

    return ""

}


String parseGame1Order(String jsonText, OrderRecordPO record) {

    logger.info("收到 game1: $record")//跟随java日志打印


    def slurper = new JsonSlurper()
    def root = slurper.parseText(jsonText)

    def sb = new StringBuilder()
// 一级字段
    sb.append("用户: ${root.userName}\n")
    sb.append("下注时间: ${root.betTime}\n")
    sb.append("输赢: ${root.winOrLoss}\n")
    sb.append("赢家: ${root.winner}\n")

// 解析 betDetail
    if (root.betDetail) {
        def betDetail = slurper.parseText(root.betDetail)
        sb.append("下注详情:\n")
        betDetail.each { k, v ->
            sb.append("  ${k} -> ${v}\n")
        }
    }

// 解析 result
    if (root.result) {
        def result = slurper.parseText(root.result)
        sb.append("结果: ${result.result}\n")
        if (result.poker) {
            sb.append("  Joker: ${result.poker.joker}\n")
            sb.append("  Andar: ${result.poker.andar}\n")
            sb.append("  Bahar: ${result.poker.bahar}\n")
        }
    }

// 解析 transfers
    if (root.transfers) {
        def transfers = slurper.parseText(root.transfers)
        sb.append("资金流转:\n")
        transfers.each {
            sb.append("  金额: ${it.amount}, 流水号: ${it.serial}\n")
        }
    }

    return sb.toString()

}



String parseGame2Order(String jsonText, OrderRecordPO record) {

    logger.info("收到 game2: $record")//跟随java日志打印


    def slurper = new JsonSlurper()
    def root = slurper.parseText(jsonText)

    def sb = new StringBuilder()
    sb.append("场馆类型: ${venueType}\n")
// 一级字段
    sb.append("用户: ${root.userName}\n")
    sb.append("下注时间: ${root.betTime}\n")
    sb.append("输赢: ${root.winOrLoss}\n")
    sb.append("赢家: ${root.winner}\n")

// 解析 betDetail
    if (root.betDetail) {
        def betDetail = slurper.parseText(root.betDetail)
        sb.append("下注详情:\n")
        betDetail.each { k, v ->
            sb.append("  ${k} -> ${v}\n")
        }
    }

// 解析 result
    if (root.result) {
        def result = slurper.parseText(root.result)
        sb.append("结果: ${result.result}\n")
        if (result.poker) {
            sb.append("  Joker: ${result.poker.joker}\n")
            sb.append("  Andar: ${result.poker.andar}\n")
            sb.append("  Bahar: ${result.poker.bahar}\n")
        }
    }

// 解析 transfers
    if (root.transfers) {
        def transfers = slurper.parseText(root.transfers)
        sb.append("资金流转:\n")
        transfers.each {
            sb.append("  金额: ${it.amount}, 流水号: ${it.serial}\n")
        }
    }

    return sb.toString()

}