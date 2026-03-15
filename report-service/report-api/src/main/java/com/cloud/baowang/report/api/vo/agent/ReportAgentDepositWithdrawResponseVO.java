package com.cloud.baowang.report.api.vo.agent;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理充提报表 resp
 */
@Data
@Schema(description =  "代理充提报表 resp")
@ExcelIgnoreUnannotated
@I18nClass
public class ReportAgentDepositWithdrawResponseVO {

    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(description = "统计日期")
    private Long dayMillis;

    @Schema(description = "报表统计日期 天或者月 yyyy-MM-dd")
    @ExcelProperty("统计日期")
    private String reportDate;

    @Schema(description = "站点Code")
    private String siteCode;

    @Schema(description = "上级代理Id")
    private String agentId;

    @Schema(description = "上级代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;

    @Schema(description = "直属上级代理ID")
    private String parentId;

    @Schema(description = "直属上级代理账号")
    @ExcelProperty("直属上级")
    private String parentAccount;

    @Schema(description = "层次")
    private String path;

    @Schema(description =  "代理层级")
    private Integer level;

    @Schema(description =  "代理层级名称")
    @ExcelProperty("代理层级")
    private String levelName;

    @Schema(title = "账号类型")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_TYPE)
    private Integer agentType;
    @Schema(title = "账号类型")
    @ExcelProperty("账号类型")
    private String agentTypeText;

    @Schema(description = "代理类别")

    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(title = "代理类别")
    @ExcelProperty("代理类别")
    private String agentCategoryText;


    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    @ExcelProperty("风控层级")
    private String riskLevel;

    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "代理标签")
    @ExcelProperty("代理标签")
    private String agentLabel;

    @Schema(description = "注册时间")
    private Long registerTime;

    @ExcelProperty("注册时间")
    private String registerTimeText;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currencyCode;

    @Schema(description = "代理总存款")
    @ExcelProperty("代理总存款")
    private BigDecimal totalDepositAmount;

    @Schema(description = "代理存款总次数")
    @ExcelProperty("代理存款总次数")
    private Long totalDepositNum;

    @Schema(description = "代理总提款")
    @ExcelProperty("代理总提款")
    private BigDecimal totalWithdrawAmount;


    @Schema(description = "代理提款总次数")
    @ExcelProperty("代理提款总次数")
    private Long totalWithdrawNum;

    @Schema(description = "代理存提差")
    @ExcelProperty("代理存提差")
    private BigDecimal diffDepositWithdraw;


    @Schema(description = "代存会员总额")
    @ExcelProperty("代存会员总额")
    private BigDecimal agentSubordinatesAmount;

    @Schema(description = "代存会员人数")
    @ExcelProperty("代存会员人数")
    private Long agentSubordinatesUser;

    @Schema(description = "代存会员次数")
    @ExcelProperty("代存会员次数")
    private Long agentSubordinatesCount;


    @Schema(description = "代理转账总额")
    @ExcelProperty("代理转账总额")
    private BigDecimal agentTransferAmount;

    @Schema(description = "代理转账人数")
    @ExcelProperty("代理转账人数")
    private Long agentTransferUser;

    @Schema(description = "代理转账次数")
    @ExcelProperty("代理转账次数")
    private Long agentTransferCount;

    public void addTotalDepositAmount(BigDecimal totalDepositAmount) {
        this.totalDepositAmount=this.totalDepositAmount==null?BigDecimal.ZERO:this.totalDepositAmount;
        totalDepositAmount=totalDepositAmount==null?BigDecimal.ZERO:totalDepositAmount;
        this.totalDepositAmount=this.totalDepositAmount.add(totalDepositAmount);
    }

    public void addTotalDepositNum(Long totalDepositNum) {
        this.totalDepositNum= this.totalDepositNum==null?0L: this.totalDepositNum;
        totalDepositNum= totalDepositNum==null?0L: totalDepositNum;
        this.totalDepositNum=this.totalDepositNum+totalDepositNum;
    }

    public void addTotalWithdrawAmount(BigDecimal totalWithdrawAmount) {
        this.totalWithdrawAmount=this.totalWithdrawAmount==null?BigDecimal.ZERO:this.totalWithdrawAmount;
        totalWithdrawAmount=totalWithdrawAmount==null?BigDecimal.ZERO:totalWithdrawAmount;
        this.totalWithdrawAmount=this.totalWithdrawAmount.add(totalWithdrawAmount);
    }

    public void addTotalWithdrawNum(Long totalWithdrawNum) {
        this.totalWithdrawNum= this.totalWithdrawNum==null?0L: this.totalWithdrawNum;
        totalWithdrawNum=totalWithdrawNum==null?0L:totalWithdrawNum;
        this.totalWithdrawNum=this.totalWithdrawNum+totalWithdrawNum;
    }

    public void addDiffDepositWithdraw(BigDecimal diffDepositWithdraw) {
        this.diffDepositWithdraw=this.diffDepositWithdraw==null?BigDecimal.ZERO:this.diffDepositWithdraw;
        diffDepositWithdraw=diffDepositWithdraw==null?BigDecimal.ZERO:diffDepositWithdraw;
        this.diffDepositWithdraw=this.diffDepositWithdraw.add(diffDepositWithdraw);
    }

    public void addAgentTransferAmount(BigDecimal agentTransferAmount) {
        this.agentTransferAmount=this.agentTransferAmount==null?BigDecimal.ZERO:this.agentTransferAmount;
        agentTransferAmount=agentTransferAmount==null?BigDecimal.ZERO:agentTransferAmount;
        this.agentTransferAmount=this.agentTransferAmount.add(agentTransferAmount);
    }

    public void addAgentTransferUser(Long agentTransferUser) {
        this.agentTransferUser= this.agentTransferUser==null?0L: this.agentTransferUser;
        agentTransferUser= agentTransferUser==null?0L: agentTransferUser;
        this.agentTransferUser=this.agentTransferUser+agentTransferUser;
    }

    public void addAgentTransferCount(Long agentTransferCount) {
        this.agentTransferCount= this.agentTransferCount==null?0L: this.agentTransferCount;
        agentTransferCount= agentTransferCount==null?0L: agentTransferCount;
        this.agentTransferCount=this.agentTransferCount+agentTransferCount;
    }

    public void addAgentSubordinatesAmount(BigDecimal agentSubordinatesAmount) {
        this.agentSubordinatesAmount=this.agentSubordinatesAmount==null?BigDecimal.ZERO:this.agentSubordinatesAmount;
        agentSubordinatesAmount=agentSubordinatesAmount==null?BigDecimal.ZERO:agentSubordinatesAmount;
        this.agentSubordinatesAmount=this.agentSubordinatesAmount.add(agentSubordinatesAmount);
    }

    public void addAgentSubordinatesCount(Long agentSubordinatesCount) {
        this.agentSubordinatesCount= this.agentSubordinatesCount==null?0L: this.agentSubordinatesCount;
        agentSubordinatesCount= agentSubordinatesCount==null?0L: agentSubordinatesCount;
        this.agentSubordinatesCount=this.agentSubordinatesCount+agentSubordinatesCount;
    }

    public void addAgentSubordinatesUser(Long agentSubordinatesUser) {
        this.agentSubordinatesUser= this.agentSubordinatesUser==null?0L: this.agentSubordinatesUser;
        agentSubordinatesUser= agentSubordinatesUser==null?0L: agentSubordinatesUser;
        this.agentSubordinatesUser=this.agentSubordinatesUser+agentSubordinatesUser;
    }

    public String getReportDate(){
        if(this.getDayMillis()!=null){
            return DateUtils.formatDateByZoneId(this.getDayMillis(), DateUtils.DATE_FORMAT_1,CurrReqUtils.getTimezone());
        }
        return "";
    }

    public String getRegisterTimeText(){
        if(this.getRegisterTime()!=null){
            return DateUtils.formatDateByZoneId(this.getRegisterTime(), CurrReqUtils.getTimezone());
        }
        return "";
    }
}
