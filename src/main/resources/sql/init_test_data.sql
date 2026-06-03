USE zntk;

-- =========================
-- 1. 题目数据：20 道
-- question_type：1 单选，2 多选，3 判断，4 简答
-- difficulty：1 简单，2 中等，3 困难
-- status：1 启用
-- deleted：0 未删除
-- =========================

INSERT INTO question
(id, title, question_type, difficulty, knowledge_point, answer, analysis, status, create_time, update_time, deleted)
VALUES
    (1001, 'Redis 缓存穿透是什么？', 1, 2, 'Redis', 'A', '缓存穿透指查询缓存和数据库中都不存在的数据，导致请求直接打到数据库。', 1, NOW(), NOW(), 0),
    (1002, 'MyBatis-Plus 中 selectById 方法的作用是什么？', 1, 1, 'MyBatis-Plus', 'B', 'selectById 用于根据主键 ID 查询一条数据库记录。', 1, NOW(), NOW(), 0),
    (1003, 'Spring Boot 中 @RestController 的作用是什么？', 1, 1, 'Spring Boot', 'C', '@RestController 等于 @Controller + @ResponseBody，常用于返回 JSON 数据。', 1, NOW(), NOW(), 0),
    (1004, 'Redis ZSet 最适合用来实现下面哪个功能？', 1, 2, 'Redis', 'D', 'ZSet 带有 score 分数并且天然支持排序，所以适合排行榜。', 1, NOW(), NOW(), 0),
    (1005, '数据库逻辑删除一般使用什么字段表示？', 1, 1, 'MyBatis-Plus', 'A', '逻辑删除一般使用 deleted 字段，0 表示未删除，1 表示已删除。', 1, NOW(), NOW(), 0),
    (1006, '下面哪个注解通常用于参数校验？', 1, 2, 'Validation', 'B', '@Valid 用于触发参数校验，配合 @NotNull、@NotBlank 等注解使用。', 1, NOW(), NOW(), 0),
    (1007, 'RabbitMQ 主要用于解决什么问题？', 1, 2, 'RabbitMQ', 'C', '消息队列常用于异步处理、削峰填谷、系统解耦。', 1, NOW(), NOW(), 0),
    (1008, 'Spring 中 @Service 注解通常标记在哪一层？', 1, 1, 'Spring', 'B', '@Service 通常用于标记业务逻辑层。', 1, NOW(), NOW(), 0),
    (1009, 'MyBatis-Plus 中 LambdaQueryWrapper 的主要作用是什么？', 1, 2, 'MyBatis-Plus', 'A', 'LambdaQueryWrapper 用来拼接类型安全的查询条件。', 1, NOW(), NOW(), 0),
    (1010, 'Redis 中 SETNX 命令常用于实现什么？', 1, 2, 'Redis', 'D', 'SETNX 表示 key 不存在时才设置成功，常用于实现分布式锁。', 1, NOW(), NOW(), 0),

    (1011, '下面哪些属于 Redis 常见数据结构？', 2, 2, 'Redis', 'A,B,C', 'Redis 常见数据结构包括 String、Hash、ZSet 等。', 1, NOW(), NOW(), 0),
    (1012, '下面哪些属于 Spring Boot 常见配置文件？', 2, 1, 'Spring Boot', 'A,B', 'Spring Boot 常见配置文件包括 application.yml 和 application.properties。', 1, NOW(), NOW(), 0),
    (1013, '下面哪些属于后端接口常见分层？', 2, 1, '后端分层', 'A,B,C', 'Controller、Service、Mapper 是常见后端分层。', 1, NOW(), NOW(), 0),
    (1014, '下面哪些方式可以提升接口稳定性？', 2, 2, '工程规范', 'A,C,D', '参数校验、全局异常处理、统一返回结构都可以提升接口稳定性。', 1, NOW(), NOW(), 0),

    (1015, 'MyBatis-Plus 的 @TableLogic 可以实现物理删除。', 3, 1, 'MyBatis-Plus', 'B', '@TableLogic 实现的是逻辑删除，不是物理删除。', 1, NOW(), NOW(), 0),
    (1016, 'Redis ZSet 中的 score 可以用于排序。', 3, 1, 'Redis', 'A', 'ZSet 的每个 member 都有 score，Redis 可以根据 score 排序。', 1, NOW(), NOW(), 0),
    (1017, 'Controller 层一般直接编写复杂业务逻辑。', 3, 1, '后端分层', 'B', '复杂业务逻辑一般放在 Service 层，Controller 主要负责接收请求和返回响应。', 1, NOW(), NOW(), 0),

    (1018, '请简述什么是缓存穿透，以及一种常见解决方案。', 4, 3, 'Redis', '缓存穿透是查询缓存和数据库都不存在的数据，常见解决方案包括缓存空值或使用布隆过滤器。', '简答题当前先保存参考答案，后面可以接入 AI 判分。', 1, NOW(), NOW(), 0),
    (1019, '请简述 Controller、Service、Mapper 三层分别负责什么。', 4, 2, '后端分层', 'Controller 接收请求，Service 处理业务逻辑，Mapper 访问数据库。', '这是后端项目最基础的分层思想。', 1, NOW(), NOW(), 0),
    (1020, '请简述 Redis 分布式锁为什么要设置过期时间。', 4, 3, 'Redis', '为了防止程序异常导致锁一直不释放，从而造成死锁。', '过期时间可以作为兜底保护，但真实项目还要考虑锁续期和误删问题。', 1, NOW(), NOW(), 0)
    ON DUPLICATE KEY UPDATE
                         title = VALUES(title),
                         question_type = VALUES(question_type),
                         difficulty = VALUES(difficulty),
                         knowledge_point = VALUES(knowledge_point),
                         answer = VALUES(answer),
                         analysis = VALUES(analysis),
                         status = VALUES(status),
                         update_time = NOW(),
                         deleted = 0;

-- =========================
-- 2. 选项数据
-- 单选、多选、判断题都有选项
-- 简答题 1018-1020 暂时不需要选项
-- =========================

INSERT INTO question_option
(id, question_id, option_label, option_content, sort_order, create_time, update_time, deleted)
VALUES
    (2001, 1001, 'A', '查询缓存和数据库都不存在的数据', 1, NOW(), NOW(), 0),
    (2002, 1001, 'B', '大量 key 同时过期', 2, NOW(), NOW(), 0),
    (2003, 1001, 'C', '热点 key 失效导致请求打到数据库', 3, NOW(), NOW(), 0),
    (2004, 1001, 'D', 'Redis 内存不足', 4, NOW(), NOW(), 0),

    (2005, 1002, 'A', '查询所有数据', 1, NOW(), NOW(), 0),
    (2006, 1002, 'B', '根据主键 ID 查询一条数据', 2, NOW(), NOW(), 0),
    (2007, 1002, 'C', '删除一条数据', 3, NOW(), NOW(), 0),
    (2008, 1002, 'D', '分页查询数据', 4, NOW(), NOW(), 0),

    (2009, 1003, 'A', '声明数据库实体类', 1, NOW(), NOW(), 0),
    (2010, 1003, 'B', '声明 Mapper 接口', 2, NOW(), NOW(), 0),
    (2011, 1003, 'C', '声明返回 JSON 的控制器类', 3, NOW(), NOW(), 0),
    (2012, 1003, 'D', '声明配置文件', 4, NOW(), NOW(), 0),

    (2013, 1004, 'A', '用户登录', 1, NOW(), NOW(), 0),
    (2014, 1004, 'B', '文件上传', 2, NOW(), NOW(), 0),
    (2015, 1004, 'C', '数据库连接池', 3, NOW(), NOW(), 0),
    (2016, 1004, 'D', '成绩排行榜', 4, NOW(), NOW(), 0),

    (2017, 1005, 'A', 'deleted', 1, NOW(), NOW(), 0),
    (2018, 1005, 'B', 'name', 2, NOW(), NOW(), 0),
    (2019, 1005, 'C', 'password', 3, NOW(), NOW(), 0),
    (2020, 1005, 'D', 'token', 4, NOW(), NOW(), 0),

    (2021, 1006, 'A', '@TableId', 1, NOW(), NOW(), 0),
    (2022, 1006, 'B', '@Valid', 2, NOW(), NOW(), 0),
    (2023, 1006, 'C', '@TableLogic', 3, NOW(), NOW(), 0),
    (2024, 1006, 'D', '@Mapper', 4, NOW(), NOW(), 0),

    (2025, 1007, 'A', '页面美化', 1, NOW(), NOW(), 0),
    (2026, 1007, 'B', '图片压缩', 2, NOW(), NOW(), 0),
    (2027, 1007, 'C', '异步处理、削峰填谷、系统解耦', 3, NOW(), NOW(), 0),
    (2028, 1007, 'D', '生成验证码', 4, NOW(), NOW(), 0),

    (2029, 1008, 'A', 'Controller 层', 1, NOW(), NOW(), 0),
    (2030, 1008, 'B', 'Service 层', 2, NOW(), NOW(), 0),
    (2031, 1008, 'C', 'Mapper 层', 3, NOW(), NOW(), 0),
    (2032, 1008, 'D', 'Entity 层', 4, NOW(), NOW(), 0),

    (2033, 1009, 'A', '拼接查询条件', 1, NOW(), NOW(), 0),
    (2034, 1009, 'B', '启动 Spring Boot 项目', 2, NOW(), NOW(), 0),
    (2035, 1009, 'C', '生成 JWT Token', 3, NOW(), NOW(), 0),
    (2036, 1009, 'D', '连接 Redis', 4, NOW(), NOW(), 0),

    (2037, 1010, 'A', '分页查询', 1, NOW(), NOW(), 0),
    (2038, 1010, 'B', '发送邮件', 2, NOW(), NOW(), 0),
    (2039, 1010, 'C', '删除数据库记录', 3, NOW(), NOW(), 0),
    (2040, 1010, 'D', '实现分布式锁', 4, NOW(), NOW(), 0),

    (2041, 1011, 'A', 'String', 1, NOW(), NOW(), 0),
    (2042, 1011, 'B', 'Hash', 2, NOW(), NOW(), 0),
    (2043, 1011, 'C', 'ZSet', 3, NOW(), NOW(), 0),
    (2044, 1011, 'D', 'Controller', 4, NOW(), NOW(), 0),

    (2045, 1012, 'A', 'application.yml', 1, NOW(), NOW(), 0),
    (2046, 1012, 'B', 'application.properties', 2, NOW(), NOW(), 0),
    (2047, 1012, 'C', 'pom.xml', 3, NOW(), NOW(), 0),
    (2048, 1012, 'D', 'README.md', 4, NOW(), NOW(), 0),

    (2049, 1013, 'A', 'Controller', 1, NOW(), NOW(), 0),
    (2050, 1013, 'B', 'Service', 2, NOW(), NOW(), 0),
    (2051, 1013, 'C', 'Mapper', 3, NOW(), NOW(), 0),
    (2052, 1013, 'D', 'Wallpaper', 4, NOW(), NOW(), 0),

    (2053, 1014, 'A', '参数校验', 1, NOW(), NOW(), 0),
    (2054, 1014, 'B', '删除所有异常处理', 2, NOW(), NOW(), 0),
    (2055, 1014, 'C', '统一返回结构', 3, NOW(), NOW(), 0),
    (2056, 1014, 'D', '全局异常处理', 4, NOW(), NOW(), 0),

    (2057, 1015, 'A', '正确', 1, NOW(), NOW(), 0),
    (2058, 1015, 'B', '错误', 2, NOW(), NOW(), 0),

    (2059, 1016, 'A', '正确', 1, NOW(), NOW(), 0),
    (2060, 1016, 'B', '错误', 2, NOW(), NOW(), 0),

    (2061, 1017, 'A', '正确', 1, NOW(), NOW(), 0),
    (2062, 1017, 'B', '错误', 2, NOW(), NOW(), 0)
    ON DUPLICATE KEY UPDATE
                         option_content = VALUES(option_content),
                         sort_order = VALUES(sort_order),
                         update_time = NOW(),
                         deleted = 0;

-- =========================
-- 3. 试卷数据：4 张
-- =========================

INSERT INTO paper
(id, title, description, total_score, duration_minutes, status, create_time, update_time, deleted)
VALUES
    (3001, 'ZNTK Java 后端基础测试卷', '用于测试 Spring Boot、MyBatis-Plus、Redis 等基础知识。', 50, 60, 1, NOW(), NOW(), 0),
    (3002, 'ZNTK Redis 专项测试卷', '用于测试 Redis 缓存、ZSet、分布式锁等知识。', 40, 45, 1, NOW(), NOW(), 0),
    (3003, 'ZNTK Spring Boot 入门测试卷', '用于测试 Controller、Service、Validation、统一返回等基础知识。', 35, 40, 1, NOW(), NOW(), 0),
    (3004, 'ZNTK 综合能力测试卷', '覆盖 Redis、MyBatis-Plus、Spring Boot、消息队列和分层设计。', 60, 90, 1, NOW(), NOW(), 0)
    ON DUPLICATE KEY UPDATE
                         title = VALUES(title),
                         description = VALUES(description),
                         total_score = VALUES(total_score),
                         duration_minutes = VALUES(duration_minutes),
                         status = VALUES(status),
                         update_time = NOW(),
                         deleted = 0;

-- =========================
-- 4. 试卷题目关联
-- paper_question 表保存：哪张试卷包含哪道题、每题多少分、排序
-- =========================

INSERT INTO paper_question
(id, paper_id, question_id, score, sort_order, create_time, deleted)
VALUES
-- 3001 Java 后端基础测试卷：10 道题，每题 5 分
(4001, 3001, 1001, 5, 1, NOW(), 0),
(4002, 3001, 1002, 5, 2, NOW(), 0),
(4003, 3001, 1003, 5, 3, NOW(), 0),
(4004, 3001, 1004, 5, 4, NOW(), 0),
(4005, 3001, 1005, 5, 5, NOW(), 0),
(4006, 3001, 1006, 5, 6, NOW(), 0),
(4007, 3001, 1007, 5, 7, NOW(), 0),
(4008, 3001, 1008, 5, 8, NOW(), 0),
(4009, 3001, 1009, 5, 9, NOW(), 0),
(4010, 3001, 1010, 5, 10, NOW(), 0),

-- 3002 Redis 专项测试卷：8 道题，每题 5 分
(4011, 3002, 1001, 5, 1, NOW(), 0),
(4012, 3002, 1004, 5, 2, NOW(), 0),
(4013, 3002, 1010, 5, 3, NOW(), 0),
(4014, 3002, 1011, 5, 4, NOW(), 0),
(4015, 3002, 1016, 5, 5, NOW(), 0),
(4016, 3002, 1018, 5, 6, NOW(), 0),
(4017, 3002, 1020, 5, 7, NOW(), 0),
(4018, 3002, 1014, 5, 8, NOW(), 0),

-- 3003 Spring Boot 入门测试卷：7 道题，每题 5 分
(4019, 3003, 1003, 5, 1, NOW(), 0),
(4020, 3003, 1006, 5, 2, NOW(), 0),
(4021, 3003, 1008, 5, 3, NOW(), 0),
(4022, 3003, 1012, 5, 4, NOW(), 0),
(4023, 3003, 1013, 5, 5, NOW(), 0),
(4024, 3003, 1017, 5, 6, NOW(), 0),
(4025, 3003, 1019, 5, 7, NOW(), 0),

-- 3004 综合能力测试卷：12 道题，每题 5 分
(4026, 3004, 1001, 5, 1, NOW(), 0),
(4027, 3004, 1002, 5, 2, NOW(), 0),
(4028, 3004, 1003, 5, 3, NOW(), 0),
(4029, 3004, 1004, 5, 4, NOW(), 0),
(4030, 3004, 1007, 5, 5, NOW(), 0),
(4031, 3004, 1009, 5, 6, NOW(), 0),
(4032, 3004, 1010, 5, 7, NOW(), 0),
(4033, 3004, 1011, 5, 8, NOW(), 0),
(4034, 3004, 1014, 5, 9, NOW(), 0),
(4035, 3004, 1016, 5, 10, NOW(), 0),
(4036, 3004, 1018, 5, 11, NOW(), 0),
(4037, 3004, 1020, 5, 12, NOW(), 0)
    ON DUPLICATE KEY UPDATE
                         score = VALUES(score),
                         sort_order = VALUES(sort_order),
                         deleted = 0;