# account-service

账务系统模块-统一记录所有会员、代理、平台所有资金变化

## 账务对外提供服务接口

| 接口                 |       名称 |       描述        |
|:-------------------|---------:|:---------------:|
| AccountActivityApi | 帐变模块活动相关 |     活动奖励、返水     |
| AccountAgentApi    | 帐变模块代理相关 | 代理存提款、佣金钱包、额度钱包 |
| AccountUserApi     | 帐变模块会员相关 |   会员存提款、人工加减额   |
| AccountPlayApi     | 帐变模块游戏相关 |   投注、派彩、注单结算    |

## 账务内部关系

* AccountActivityApi ===》AccountActivityApiImpl==》UserCoinApi==》AccountTransfer
