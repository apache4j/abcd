//package com.cloud.baowang.play.wallet.service;
//
//import com.cloud.baowang.play.wallet.vo.req.tf.TfTransferReq;
//import com.cloud.baowang.play.wallet.vo.req.tf.TfValidReq;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfTransferResp;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfValidResp;
//import com.cloud.baowang.play.wallet.vo.res.tf.TfWalletResp;
//import jakarta.servlet.http.HttpServletResponse;
//
//public interface TfService {
//
//    TfValidResp validate(TfValidReq req);
//
//
//    TfWalletResp wallet(String loginName, HttpServletResponse response);
//
//    TfTransferResp transfer(TfTransferReq req, HttpServletResponse response);
//
//    TfTransferResp rollback(TfTransferReq req, HttpServletResponse response);
//}
