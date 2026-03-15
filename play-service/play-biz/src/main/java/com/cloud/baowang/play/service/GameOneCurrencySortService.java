package com.cloud.baowang.play.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.po.GameOneCurrencySortPO;
import com.cloud.baowang.play.repositories.GameOneCurrencySortRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Service
@Slf4j
public class GameOneCurrencySortService extends ServiceImpl<GameOneCurrencySortRepository, GameOneCurrencySortPO> {
}
