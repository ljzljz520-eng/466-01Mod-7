INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '新生报到', '包含报到须知、材料清单与流程说明') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '新生报到') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '课程安排', '展示学期课程表与时间地点') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '课程安排') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '活动通知', '校园活动与社团招新信息') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '活动通知') LIMIT 1;
