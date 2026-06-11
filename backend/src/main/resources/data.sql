INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '新生报到', '包含报到须知、材料清单与流程说明') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '新生报到') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '课程安排', '展示学期课程表与时间地点') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '课程安排') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '活动通知', '校园活动与社团招新信息') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '活动通知') LIMIT 1;

INSERT INTO instrument (name, type, rate_per_hour, consumable_fee, urgent_surcharge_rate, status, description)
SELECT * FROM (SELECT 'Bruker 600MHz 核磁共振仪', 'NMR', 500.00, 80.00, 0.50, 'AVAILABLE', '600MHz超导核磁共振波谱仪，支持液体和固体样品') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM instrument WHERE name = 'Bruker 600MHz 核磁共振仪') LIMIT 1;

INSERT INTO instrument (name, type, rate_per_hour, consumable_fee, urgent_surcharge_rate, status, description)
SELECT * FROM (SELECT 'Illumina NovaSeq 测序仪', 'SEQUENCER', 800.00, 150.00, 0.50, 'AVAILABLE', '高通量二代测序平台，支持WGS/WES/RNA-Seq') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM instrument WHERE name = 'Illumina NovaSeq 测序仪') LIMIT 1;

INSERT INTO instrument (name, type, rate_per_hour, consumable_fee, urgent_surcharge_rate, status, description)
SELECT * FROM (SELECT 'FEI Talos 透射电镜', 'EM', 600.00, 120.00, 0.50, 'AVAILABLE', '场发射透射电子显微镜，分辨率0.12nm') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM instrument WHERE name = 'FEI Talos 透射电镜') LIMIT 1;

INSERT INTO instrument (name, type, rate_per_hour, consumable_fee, urgent_surcharge_rate, status, description)
SELECT * FROM (SELECT 'Varian 400MHz 核磁共振仪', 'NMR', 300.00, 50.00, 0.50, 'AVAILABLE', '400MHz核磁共振波谱仪，常规有机物结构鉴定') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM instrument WHERE name = 'Varian 400MHz 核磁共振仪') LIMIT 1;

INSERT INTO instrument (name, type, rate_per_hour, consumable_fee, urgent_surcharge_rate, status, description)
SELECT * FROM (SELECT 'ZEISS 扫描电镜', 'EM', 400.00, 90.00, 0.50, 'MAINTENANCE', '场发射扫描电子显微镜，样品表面形貌观察') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM instrument WHERE name = 'ZEISS 扫描电镜') LIMIT 1;

INSERT INTO pi_account (pi_name, group_name, balance, created_at, updated_at)
SELECT * FROM (SELECT '张明远', '有机合成课题组', 50000.00, NOW() AS created_at, NOW() AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM pi_account WHERE group_name = '有机合成课题组') LIMIT 1;

INSERT INTO pi_account (pi_name, group_name, balance, created_at, updated_at)
SELECT * FROM (SELECT '李慧琳', '基因组学课题组', 80000.00, NOW() AS created_at, NOW() AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM pi_account WHERE group_name = '基因组学课题组') LIMIT 1;

INSERT INTO pi_account (pi_name, group_name, balance, created_at, updated_at)
SELECT * FROM (SELECT '王建国', '纳米材料课题组', 3000.00, NOW() AS created_at, NOW() AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM pi_account WHERE group_name = '纳米材料课题组') LIMIT 1;

INSERT INTO pi_account (pi_name, group_name, balance, created_at, updated_at)
SELECT * FROM (SELECT '陈雪梅', '药物化学课题组', 25000.00, NOW() AS created_at, NOW() AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM pi_account WHERE group_name = '药物化学课题组') LIMIT 1;
