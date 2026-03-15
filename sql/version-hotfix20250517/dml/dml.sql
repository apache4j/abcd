-- 平台增加韩语
INSERT INTO `language_manager` (`id`, `site_code`, `name`, `code`, `show_code`, `icon`, `sort`, `status`, `operate_time`, `operator`, `created_time`, `updated_time`, `creator`, `updater`) VALUES
    (6, '0', '한국인', 'ko-KR', 'KR', 'baowang/df85a7d4268a4312af2863919d743781.png', 4, 1, 1745225687646, 'PMtest01', 4, NULL, NULL, NULL);


-- 6种语言增加82区号
SET @MaxId = (SELECT CAST(MAX(id) AS UNSIGNED) FROM area_country_name);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'zh-CN', 'CN', '韩国', 1736855735102, 1736855735007, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'zh-TW', 'TW', '韓國', 1736855735007, 1736855735007, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'en-US', 'US', 'South Korea', 1736855735007, 1736855735007, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'pt-BR', 'BR', 'Coréia do Sul', 1736855735007, 1736855735007, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'vi-VN', 'VN', 'Hàn Quốc', 1736855735007, 1736855735007, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '82', 'ko-KR', 'KR', '대한민국', 20250501062410, 20250501062410, NULL, NULL);


-- 增加支持的国家区号韩语: 86,1, 351, 84, 63, 60 ,855 ,92, 91
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '86', 'ko-KR', 'KR', '중국', 20250501062445, 20250501062445, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '1', 'ko-KR', 'KR', '미국', 20250501062603, 20250501062603, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '351', 'ko-KR', 'KR', '포르투갈', 20250501062637, 20250501062637, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '84', 'ko-KR', 'KR', '베트남', 20250501062620, 20250501062620, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '63', 'ko-KR', 'KR', '필리핀', 20250501062637, 20250501062637, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '60', 'ko-KR', 'KR', '말레이시아', 20250501062637, 20250501062637, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '855', 'ko-KR', 'KR', '캄보디아', 20250501062637, 20250501062637, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '92', 'ko-KR', 'KR', '파키스탄', 20250501062637, 20250501062637, NULL, NULL);
INSERT INTO  area_country_name (id, area_code, language, country_code, country_name, created_time, updated_time, creator, updater) VALUES (@MaxId := @MaxId + 1, '91', 'ko-KR', 'KR', '인도', 20250501062637, 20250501062637, NULL, NULL);



-- 总控加韩语
SET @lastId = (SELECT CAST(MAX(id) AS UNSIGNED) FROM area_admin_manage);
INSERT INTO area_admin_manage(id, area_id, area_code, country_name, country_code, max_length, min_length, status, icon, created_time, updated_time, creator, updater) VALUES(@lastId, 83223, '82', '', 'KR', 111, 1, 1, 'baowang/xxx.png', 1725420241236, 1746773317657, NULL, 'sql');

-- 生成所有站点的脚本. 再跟(SET @latestId) 一起跑脚本.
--SELECT
--  CONCAT(
--    'INSERT INTO area_site_manage (id, site_code, area_id, area_code, country_name, country_code, max_length, min_length, status, icon, created_time, updated_time, creator, updater) VALUES (''',
--    '(@latestId := @latestId + 1), ''',
--    site_code, ''', 83222, ''82'', ''韩国'', ''KR'', 11, 9, 1, ',
--    '''baowang/xxx.png'', 1725420241236, 1734924691444, ''sql'', ''sql'');'
--  ) AS insert_sql
--FROM site_info;
--
--SET @latestId = (SELECT CAST(MAX(id) AS UNSIGNED) FROM area_site_manage);



