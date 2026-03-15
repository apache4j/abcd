package com.cloud.baowang.user.controller.lobby;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayLobbyGameApi;
import com.cloud.baowang.play.api.vo.lobby.LobbyGameCollectionRequestVO;
import com.cloud.baowang.play.api.vo.lobby.LobbyGameInfoVO;
import com.cloud.baowang.play.api.vo.lobby.Sin4LobbyGameRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "收藏游戏")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/game/api")
public class GameController {

    private final PlayLobbyGameApi playLobbyGameApi;


    @Operation(summary = "收藏游戏")
    @PostMapping("/collection")
    public ResponseVO<Boolean> collection(@Valid @RequestBody LobbyGameCollectionRequestVO requestVO){
        return playLobbyGameApi.collection(requestVO);
    }


    @Operation(summary = "查询收藏游戏列表")
    @PostMapping("/queryCollection")
    public ResponseVO<Page<LobbyGameInfoVO>> queryCollection(@RequestBody Sin4LobbyGameRequestVO requestVO){
        return playLobbyGameApi.queryCollection(requestVO);
    }

//    @Operation(summary = "最近玩的")
//    @PostMapping("/queryRecentlyPlayer")
//    public ResponseVO<List<LobbyGameInfoVO>> queryRecentlyPlayer(){
//        return gameLobbyService.queryRecentlyPlayer();
//    }


}
