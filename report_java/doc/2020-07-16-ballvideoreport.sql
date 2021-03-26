-- 2020/7/16 新增广告规则
INSERT INTO `ballvideoreport`.`banner_rule`(`id`, `ad_space`, `ad_type`, `ad_style`, `search_key`, `key`, `status`, `jt`, `app_type`, `total_name`)
VALUES (58, '金币弹窗', '穿-信息流，广-动态信息流，快-信息流', '自渲染', '金币弹窗', '金币弹窗,&^翻倍', 1, 1, 1, '金币弹窗');
INSERT INTO `ballvideoreport`.`banner_rule`(`id`, `ad_space`, `ad_type`, `ad_style`, `search_key`, `key`, `status`, `jt`, `app_type`, `total_name`)
VALUES (59, '金币弹窗翻倍', '激励视频', '穿-模板渲染', '金币弹窗翻倍', '金币弹窗翻倍', 1, 1, 1, '金币弹窗翻倍');
INSERT INTO `ballvideoreport`.`banner_rule`(`id`, `ad_space`, `ad_type`, `ad_style`, `search_key`, `key`, `status`, `jt`, `app_type`, `total_name`)
VALUES (60, '补签激励', '激励视频', '穿-模板渲染', '补签激励', '补签激励', 1, 1, 1, '补签激励');
INSERT INTO `ballvideoreport`.`banner_rule`(`id`, `ad_space`, `ad_type`, `ad_style`, `search_key`, `key`, `status`, `jt`, `app_type`, `total_name`)
VALUES (61, '看视频赚金币', '激励视频', '穿-模板渲染', '看视频赚金币', '看视频赚金币', 1, 1, 1, '看视频赚金币');


DELETE FROM app_use_time WHERE create_time > 1590940799;

INSERT INTO app_use_time (app_type, data_type, use_day, use_time, use_time_str, create_time) VALUES
('1', '1', '2020-06-01', '1556', '0:25:56', '1591027199'),  ('1', '2', '2020-06-01', '1979', '0:32:59', '1591027199'),
('1', '3', '2020-06-01', '2025', '0:33:45', '1591027199'),  ('1', '6', '2020-06-01', '665', '0:11:5', '1591027199'),
('2', '1', '2020-06-01', '2171', '0:36:11', '1591027199'),  ('2', '4', '2020-06-01', '2171', '0:36:11', '1591027199'),
('1', '1', '2020-06-02', '1412', '0:23:32', '1591113599'),  ('1', '2', '2020-06-02', '1958', '0:32:38', '1591113599'),
('1', '3', '2020-06-02', '2022', '0:33:42', '1591113599'),  ('1', '6', '2020-06-02', '256', '0:4:16', '1591113599'),
('2', '1', '2020-06-02', '2178', '0:36:18', '1591113599'),  ('2', '4', '2020-06-02', '2178', '0:36:18', '1591113599'),
('1', '1', '2020-06-03', '1445', '0:24:5', '1591199999'),  ('1', '2', '2020-06-03', '1983', '0:33:3', '1591199999'),
('1', '3', '2020-06-03', '1996', '0:33:16', '1591199999'),  ('1', '6', '2020-06-03', '356', '0:5:56', '1591199999'),
('2', '1', '2020-06-03', '2144', '0:35:44', '1591199999'),  ('2', '4', '2020-06-03', '2144', '0:35:44', '1591199999'),
('1', '1', '2020-06-04', '1426', '0:23:46', '1591286399'),  ('1', '2', '2020-06-04', '1917', '0:31:57', '1591286399'),
('1', '3', '2020-06-04', '1929', '0:32:9', '1591286399'),  ('1', '6', '2020-06-04', '432', '0:7:12', '1591286399'),
('2', '1', '2020-06-04', '2137', '0:35:37', '1591286399'),  ('2', '4', '2020-06-04', '2137', '0:35:37', '1591286399'),
('1', '1', '2020-06-05', '1502', '0:25:2', '1591372799'),  ('1', '2', '2020-06-05', '1970', '0:32:50', '1591372799'),
('1', '3', '2020-06-05', '2045', '0:34:5', '1591372799'),  ('1', '6', '2020-06-05', '491', '0:8:11', '1591372799'),
('2', '1', '2020-06-05', '2092', '0:34:52', '1591372799'),  ('2', '4', '2020-06-05', '2092', '0:34:52', '1591372799'),
('1', '1', '2020-06-06', '1471', '0:24:31', '1591459199'),  ('1', '2', '2020-06-06', '1942', '0:32:22', '1591459199'),
('1', '3', '2020-06-06', '2006', '0:33:26', '1591459199'),  ('1', '6', '2020-06-06', '467', '0:7:47', '1591459199'),
('2', '1', '2020-06-06', '2032', '0:33:52', '1591459199'),  ('2', '4', '2020-06-06', '2032', '0:33:52', '1591459199'),
('1', '1', '2020-06-07', '1501', '0:25:1', '1591545599'),  ('1', '2', '2020-06-07', '1880', '0:31:20', '1591545599'),
('1', '3', '2020-06-07', '2035', '0:33:55', '1591545599'),  ('1', '6', '2020-06-07', '590', '0:9:50', '1591545599'),
('2', '1', '2020-06-07', '1986', '0:33:6', '1591545599'),  ('2', '4', '2020-06-07', '1986', '0:33:6', '1591545599'),
('1', '1', '2020-06-08', '1499', '0:24:59', '1591631999'),  ('1', '2', '2020-06-08', '1964', '0:32:44', '1591631999'),
('1', '3', '2020-06-08', '2033', '0:33:53', '1591631999'),  ('1', '6', '2020-06-08', '500', '0:8:20', '1591631999'),
('2', '1', '2020-06-08', '2091', '0:34:51', '1591631999'),  ('2', '4', '2020-06-08', '2091', '0:34:51', '1591631999'),
('1', '1', '2020-06-09', '1483', '0:24:43', '1591718399'),  ('1', '2', '2020-06-09', '1885', '0:31:25', '1591718399'),
('1', '3', '2020-06-09', '2046', '0:34:6', '1591718399'),  ('1', '6', '2020-06-09', '518', '0:8:38', '1591718399'),
('2', '1', '2020-06-09', '2050', '0:34:10', '1591718399'),  ('2', '4', '2020-06-09', '2050', '0:34:10', '1591718399'),
('1', '1', '2020-06-10', '1503', '0:25:3', '1591804799'),  ('1', '2', '2020-06-10', '1909', '0:31:49', '1591804799'),
('1', '3', '2020-06-10', '1945', '0:32:25', '1591804799'),  ('1', '6', '2020-06-10', '656', '0:10:56', '1591804799'),
('2', '1', '2020-06-10', '2137', '0:35:37', '1591804799'),  ('2', '4', '2020-06-10', '2137', '0:35:37', '1591804799'),
('1', '1', '2020-06-11', '1420', '0:23:40', '1591891199'),  ('1', '2', '2020-06-11', '1873', '0:31:13', '1591891199'),
('1', '3', '2020-06-11', '1895', '0:31:35', '1591891199'),  ('1', '6', '2020-06-11', '493', '0:8:13', '1591891199'),
('2', '1', '2020-06-11', '2261', '0:37:41', '1591891199'),  ('2', '4', '2020-06-11', '2261', '0:37:41', '1591891199'),
('1', '1', '2020-06-12', '1470', '0:24:30', '1591977599'),  ('1', '2', '2020-06-12', '1878', '0:31:18', '1591977599'),
('1', '3', '2020-06-12', '1892', '0:31:32', '1591977599'),  ('1', '6', '2020-06-12', '641', '0:10:41', '1591977599'),
('2', '1', '2020-06-12', '2244', '0:37:24', '1591977599'),  ('2', '4', '2020-06-12', '2244', '0:37:24', '1591977599'),
('1', '1', '2020-06-13', '1432', '0:23:52', '1592063999'),  ('1', '2', '2020-06-13', '1827', '0:30:27', '1592063999'),
('1', '3', '2020-06-13', '1886', '0:31:26', '1592063999'),  ('1', '6', '2020-06-13', '585', '0:9:45', '1592063999'),
('2', '1', '2020-06-13', '2242', '0:37:22', '1592063999'),  ('2', '4', '2020-06-13', '2242', '0:37:22', '1592063999'),
('1', '1', '2020-06-14', '1427', '0:23:47', '1592150399'),  ('1', '2', '2020-06-14', '1849', '0:30:49', '1592150399'),
('1', '3', '2020-06-14', '1914', '0:31:54', '1592150399'),  ('1', '6', '2020-06-14', '519', '0:8:39', '1592150399'),
('2', '1', '2020-06-14', '2254', '0:37:34', '1592150399'),  ('2', '4', '2020-06-14', '2254', '0:37:34', '1592150399'),
('1', '1', '2020-06-15', '1456', '0:24:16', '1592236799'),  ('1', '2', '2020-06-15', '1864', '0:31:4', '1592236799'),
('1', '3', '2020-06-15', '1881', '0:31:21', '1592236799'),  ('1', '6', '2020-06-15', '625', '0:10:25', '1592236799'),
('2', '1', '2020-06-15', '2308', '0:38:28', '1592236799'),  ('2', '4', '2020-06-15', '2308', '0:38:28', '1592236799'),
('1', '1', '2020-06-16', '1389', '0:23:9', '1592323199'),  ('1', '2', '2020-06-16', '1797', '0:29:57', '1592323199'),
('1', '3', '2020-06-16', '1844', '0:30:44', '1592323199'),  ('1', '6', '2020-06-16', '526', '0:8:46', '1592323199'),
('2', '1', '2020-06-16', '2217', '0:36:57', '1592323199'),  ('2', '4', '2020-06-16', '2217', '0:36:57', '1592323199'),
('1', '1', '2020-06-17', '1404', '0:23:24', '1592409599'),  ('1', '2', '2020-06-17', '1843', '0:30:43', '1592409599'),
('1', '3', '2020-06-17', '1906', '0:31:46', '1592409599'),  ('1', '6', '2020-06-17', '464', '0:7:44', '1592409599'),
('2', '1', '2020-06-17', '2309', '0:38:29', '1592409599'),  ('2', '4', '2020-06-17', '2309', '0:38:29', '1592409599'),
('1', '1', '2020-06-18', '1358', '0:22:38', '1592495999'),  ('1', '2', '2020-06-18', '1791', '0:29:51', '1592495999'),
('1', '3', '2020-06-18', '1886', '0:31:26', '1592495999'),  ('1', '6', '2020-06-18', '397', '0:6:37', '1592495999'),
('2', '1', '2020-06-18', '2298', '0:38:18', '1592495999'),  ('2', '4', '2020-06-18', '2298', '0:38:18', '1592495999'),
('1', '1', '2020-06-19', '1373', '0:22:53', '1592582399'),  ('1', '2', '2020-06-19', '1823', '0:30:23', '1592582399'),
('1', '3', '2020-06-19', '1805', '0:30:5', '1592582399'),  ('1', '6', '2020-06-19', '491', '0:8:11', '1592582399'),
('2', '1', '2020-06-19', '2251', '0:37:31', '1592582399'),  ('2', '4', '2020-06-19', '2251', '0:37:31', '1592582399'),
('1', '1', '2020-06-20', '1512', '0:25:12', '1592668799'),  ('1', '2', '2020-06-20', '1924', '0:32:4', '1592668799'),
('1', '3', '2020-06-20', '1897', '0:31:37', '1592668799'),  ('1', '6', '2020-06-20', '716', '0:11:56', '1592668799'),
('2', '1', '2020-06-20', '2242', '0:37:22', '1592668799'),  ('2', '4', '2020-06-20', '2242', '0:37:22', '1592668799'),
('1', '1', '2020-06-21', '1429', '0:23:49', '1592755199'),  ('1', '2', '2020-06-21', '1933', '0:32:13', '1592755199'),
('1', '3', '2020-06-21', '1903', '0:31:43', '1592755199'),  ('1', '6', '2020-06-21', '452', '0:7:32', '1592755199'),
('2', '1', '2020-06-21', '2287', '0:38:7', '1592755199'),  ('2', '4', '2020-06-21', '2287', '0:38:7', '1592755199'),
('1', '1', '2020-06-22', '1451', '0:24:11', '1592841599'),  ('1', '2', '2020-06-22', '1924', '0:32:4', '1592841599'),
('1', '3', '2020-06-22', '1905', '0:31:45', '1592841599'),  ('1', '6', '2020-06-22', '526', '0:8:46', '1592841599'),
('2', '1', '2020-06-22', '2274', '0:37:54', '1592841599'),  ('2', '4', '2020-06-22', '2274', '0:37:54', '1592841599'),
('1', '1', '2020-06-23', '1443', '0:24:3', '1592927999'),  ('1', '2', '2020-06-23', '1885', '0:31:25', '1592927999'),
('1', '3', '2020-06-23', '1923', '0:32:3', '1592927999'),  ('1', '6', '2020-06-23', '522', '0:8:42', '1592927999'),
('2', '1', '2020-06-23', '2399', '0:39:59', '1592927999'),  ('2', '4', '2020-06-23', '2399', '0:39:59', '1592927999'),
('1', '1', '2020-06-24', '1339', '0:22:19', '1593014399'),  ('1', '2', '2020-06-24', '1834', '0:30:34', '1593014399'),
('1', '3', '2020-06-24', '1800', '0:30:0', '1593014399'),  ('1', '6', '2020-06-24', '383', '0:6:23', '1593014399'),
('2', '1', '2020-06-24', '2438', '0:40:38', '1593014399'),  ('2', '4', '2020-06-24', '2438', '0:40:38', '1593014399'),
('1', '1', '2020-06-25', '1340', '0:22:20', '1593100799'),  ('1', '2', '2020-06-25', '1894', '0:31:34', '1593100799'),
('1', '3', '2020-06-25', '1904', '0:31:44', '1593100799'),  ('1', '6', '2020-06-25', '223', '0:3:43', '1593100799'),
('2', '1', '2020-06-25', '2463', '0:41:3', '1593100799'),  ('2', '4', '2020-06-25', '2463', '0:41:3', '1593100799'),
('1', '1', '2020-06-26', '1358', '0:22:38', '1593187199'),  ('1', '2', '2020-06-26', '1838', '0:30:38', '1593187199'),
('1', '3', '2020-06-26', '1842', '0:30:42', '1593187199'),  ('1', '6', '2020-06-26', '394', '0:6:34', '1593187199'),
('2', '1', '2020-06-26', '2399', '0:39:59', '1593187199'),  ('2', '4', '2020-06-26', '2399', '0:39:59', '1593187199'),
('1', '1', '2020-06-27', '1386', '0:23:6', '1593273599'),  ('1', '2', '2020-06-27', '1848', '0:30:48', '1593273599'),
('1', '3', '2020-06-27', '1816', '0:30:16', '1593273599'),  ('1', '6', '2020-06-27', '494', '0:8:14', '1593273599'),
('2', '1', '2020-06-27', '2355', '0:39:15', '1593273599'),  ('2', '4', '2020-06-27', '2355', '0:39:15', '1593273599'),
('1', '1', '2020-06-28', '1348', '0:22:28', '1593359999'),  ('1', '2', '2020-06-28', '1867', '0:31:7', '1593359999'),
('1', '3', '2020-06-28', '1836', '0:30:36', '1593359999'),  ('1', '6', '2020-06-28', '341', '0:5:41', '1593359999'),
('2', '1', '2020-06-28', '2802', '0:46:42', '1593359999'),  ('2', '4', '2020-06-28', '2802', '0:46:42', '1593359999'),
('1', '1', '2020-06-29', '1446', '0:24:6', '1593446399'),  ('1', '2', '2020-06-29', '1845', '0:30:45', '1593446399'),
('1', '3', '2020-06-29', '1773', '0:29:33', '1593446399'),  ('1', '6', '2020-06-29', '722', '0:12:2', '1593446399'),
('2', '1', '2020-06-29', '3216', '0:53:36', '1593446399'),  ('2', '4', '2020-06-29', '3216', '0:53:36', '1593446399'),
('1', '1', '2020-06-30', '1493', '0:24:53', '1593532799'),  ('1', '2', '2020-06-30', '1788', '0:29:48', '1593532799'),
('1', '3', '2020-06-30', '1708', '0:28:28', '1593532799'),  ('1', '6', '2020-06-30', '983', '0:16:23', '1593532799'),
('2', '1', '2020-06-30', '2710', '0:45:10', '1593532799'),  ('2', '4', '2020-06-30', '2710', '0:45:10', '1593532799'),
('1', '1', '2020-07-01', '1366', '0:22:46', '1593619199'),  ('1', '2', '2020-07-01', '1833', '0:30:33', '1593619199'),
('1', '3', '2020-07-01', '1719', '0:28:39', '1593619199'),  ('1', '6', '2020-07-01', '546', '0:9:6', '1593619199'),
('2', '1', '2020-07-01', '2497', '0:41:37', '1593619199'),  ('2', '4', '2020-07-01', '2497', '0:41:37', '1593619199'),
('1', '1', '2020-07-02', '1415', '0:23:35', '1593705599'),  ('1', '2', '2020-07-02', '1866', '0:31:6', '1593705599'),
('1', '3', '2020-07-02', '1795', '0:29:55', '1593705599'),  ('1', '6', '2020-07-02', '586', '0:9:46', '1593705599'),
('2', '1', '2020-07-02', '2359', '0:39:19', '1593705599'),  ('2', '4', '2020-07-02', '2359', '0:39:19', '1593705599'),
('1', '1', '2020-07-03', '1432', '0:23:52', '1593791999'),  ('1', '2', '2020-07-03', '1874', '0:31:14', '1593791999'),
('1', '3', '2020-07-03', '1829', '0:30:29', '1593791999'),  ('1', '6', '2020-07-03', '594', '0:9:54', '1593791999'),
('2', '1', '2020-07-03', '2282', '0:38:2', '1593791999'),  ('2', '4', '2020-07-03', '2282', '0:38:2', '1593791999'),
('1', '1', '2020-07-04', '1364', '0:22:44', '1593878399'),  ('1', '2', '2020-07-04', '1920', '0:32:0', '1593878399'),
('1', '3', '2020-07-04', '1789', '0:29:49', '1593878399'),  ('1', '6', '2020-07-04', '383', '0:6:23', '1593878399'),
('2', '1', '2020-07-04', '2284', '0:38:4', '1593878399'),  ('2', '4', '2020-07-04', '2284', '0:38:4', '1593878399'),
('1', '1', '2020-07-05', '1343', '0:22:23', '1593964799'),  ('1', '2', '2020-07-05', '1950', '0:32:30', '1593964799'),
('1', '3', '2020-07-05', '1815', '0:30:15', '1593964799'),  ('1', '6', '2020-07-05', '266', '0:4:26', '1593964799'),
('2', '1', '2020-07-05', '2242', '0:37:22', '1593964799'),  ('2', '4', '2020-07-05', '2242', '0:37:22', '1593964799'),
('1', '1', '2020-07-06', '1498', '0:24:58', '1594051199'),  ('1', '2', '2020-07-06', '1944', '0:32:24', '1594051199'),
('1', '3', '2020-07-06', '1806', '0:30:6', '1594051199'),  ('1', '6', '2020-07-06', '744', '0:12:24', '1594051199'),
('2', '1', '2020-07-06', '2356', '0:39:16', '1594051199'),  ('2', '4', '2020-07-06', '2356', '0:39:16', '1594051199'),
('1', '1', '2020-07-07', '1513', '0:25:13', '1594137599'),  ('1', '2', '2020-07-07', '1895', '0:31:35', '1594137599'),
('1', '3', '2020-07-07', '1742', '0:29:2', '1594137599'),  ('1', '6', '2020-07-07', '903', '0:15:3', '1594137599'),
('2', '1', '2020-07-07', '2338', '0:38:58', '1594137599'),  ('2', '4', '2020-07-07', '2338', '0:38:58', '1594137599'),
('1', '1', '2020-07-08', '1454', '0:24:14', '1594223999'),  ('1', '2', '2020-07-08', '1831', '0:30:31', '1594223999'),
('1', '3', '2020-07-08', '1723', '0:28:43', '1594223999'),  ('1', '6', '2020-07-08', '809', '0:13:29', '1594223999'),
('2', '1', '2020-07-08', '2282', '0:38:2', '1594223999'),  ('2', '4', '2020-07-08', '2282', '0:38:2', '1594223999'),
('1', '1', '2020-07-09', '1506', '0:25:6', '1594310399'),  ('1', '2', '2020-07-09', '1887', '0:31:27', '1594310399'),
('1', '3', '2020-07-09', '1835', '0:30:35', '1594310399'),  ('1', '6', '2020-07-09', '797', '0:13:17', '1594310399'),
('2', '1', '2020-07-09', '2233', '0:37:13', '1594310399'),  ('2', '4', '2020-07-09', '2233', '0:37:13', '1594310399'),
('1', '1', '2020-07-10', '1484', '0:24:44', '1594396799'),  ('1', '2', '2020-07-10', '1839', '0:30:39', '1594396799'),
('1', '3', '2020-07-10', '1809', '0:30:9', '1594396799'),  ('1', '6', '2020-07-10', '804', '0:13:24', '1594396799'),
('2', '1', '2020-07-10', '2197', '0:36:37', '1594396799'),  ('2', '4', '2020-07-10', '2197', '0:36:37', '1594396799'),
('1', '1', '2020-07-11', '1491', '0:24:51', '1594483199'),  ('1', '2', '2020-07-11', '1865', '0:31:5', '1594483199'),
('1', '3', '2020-07-11', '1800', '0:30:0', '1594483199'),  ('1', '6', '2020-07-11', '809', '0:13:29', '1594483199'),
('2', '1', '2020-07-11', '2159', '0:35:59', '1594483199'),  ('2', '4', '2020-07-11', '2159', '0:35:59', '1594483199'),
('1', '1', '2020-07-12', '1484', '0:24:44', '1594569599'),  ('1', '2', '2020-07-12', '1866', '0:31:6', '1594569599'),
('1', '3', '2020-07-12', '1833', '0:30:33', '1594569599'),  ('1', '6', '2020-07-12', '755', '0:12:35', '1594569599'),
('2', '1', '2020-07-12', '2119', '0:35:19', '1594569599'),  ('2', '4', '2020-07-12', '2119', '0:35:19', '1594569599'),
('1', '1', '2020-07-13', '1453', '0:24:13', '1594655999'),  ('1', '2', '2020-07-13', '1816', '0:30:16', '1594655999'),
('1', '3', '2020-07-13', '1789', '0:29:49', '1594655999'),  ('1', '6', '2020-07-13', '756', '0:12:36', '1594655999'),
('2', '1', '2020-07-13', '2265', '0:37:45', '1594655999'),  ('2', '4', '2020-07-13', '2265', '0:37:45', '1594655999'),
('2', '1', '2020-07-14', '2216', '0:36:56', '1594742399'),  ('1', '1', '2020-07-14', '1420', '0:23:40', '1594742399'),
('2', '4', '2020-07-14', '2216', '0:36:56', '1594742399'),  ('1', '6', '2020-07-14', '723', '0:12:3', '1594742399'),
('1', '3', '2020-07-14', '1732', '0:28:52', '1594742399'),  ('1', '2', '2020-07-14', '1807', '0:30:7', '1594742399'),
('2', '1', '2020-07-15', '2254', '0:37:34', '1594828799'),  ('1', '1', '2020-07-15', '1413', '0:23:33', '1594828799'),
('2', '4', '2020-07-15', '2254', '0:37:34', '1594828799'),  ('1', '6', '2020-07-15', '696', '0:11:36', '1594828799'),
('1', '3', '2020-07-15', '1757', '0:29:17', '1594828799'),  ('1', '2', '2020-07-15', '1787', '0:29:47', '1594828799');