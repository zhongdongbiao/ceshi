spring:
  application:
    name: 100060
  profiles:
    active: dev  #production
  servlet:
    multipart:
      enabled: true
      max-file-size: 3MB
      max-request-size: 3MB
  cloud:
    config:
      discovery:
        enabled: true
        serviceId: 100009
#    inetutils:
#      preferred-networks: 10
  mvc:
    pathmatch:
      use-suffix-pattern: true
  # 配置动态数据源中的主数据源
  datasource:
    dynamic:
      datasource:
        master:
          type: com.alibaba.druid.pool.DruidDataSource
          driver-class-name: com.mysql.jdbc.Driver
#          url: jdbc:mysql://10.197.186.244:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#          url: jdbc:mysql://10.0.44.30:3307/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
          url: jdbc:mysql://pc-bp1981n96h2y6bnev.rwlb.rds.aliyuncs.com:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#          username: root
#          password: mysql123
          username: cs_bi
          password: CSBI@pdb2022
          initial-size: 40 # 初始化时建立物理连接的个数
          min-idle: 1   # 最小连接池数量
          max-active: 400 #  最大连接数量
          query-timeout: 600000 #查询超时
        git_adb:
          type: com.alibaba.druid.pool.DruidDataSource
          driver-class-name: com.mysql.jdbc.Driver
#          url: jdbc:mysql://10.197.186.244:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#          url: jdbc:mysql://10.0.44.30:3307/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
          url: jdbc:mysql://am-bp1m1va78575i9b7q167320.ads.aliyuncs.com:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#          username: root
#          password: mysql123
          username: cs_bi_adb
          password: CSBI@adb2022
          initial-size: 40 # 初始化时建立物理连接的个数
          min-idle: 1   # 最小连接池数量
          max-active: 400 #  最大连接数量
          query-timeout: 600000 #查询超时
        shuce_db:
          type: com.alibaba.druid.pool.DruidDataSource
          driver-class-name: com.mysql.jdbc.Driver
          url: jdbc:mysql://192.168.200.122:3306/dospaas?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
          username: panasonic_cc
          password: 569180524245165_fsd_c517cb829a3bece7D
          initial-size: 40 # 初始化时建立物理连接的个数
          min-idle: 1   # 最小连接池数量
          max-active: 400 #  最大连接数量
          query-timeout: 600000 #查询超时
      primary: master
      druid:
        wall:
          multi-statement-allow: true

  #sharding-jdbc配置
  #数据源
  shardingsphere:
    datasource:
      names: db1
      db1:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
#        url: jdbc:mysql://10.197.186.244:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#        url: jdbc:mysql://10.0.44.30:3307/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
        url: jdbc:mysql://pc-bp1981n96h2y6bnev.rwlb.rds.aliyuncs.com:3306/data_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowMultiQueries=true
#        username: root
#        password: mysql123
        username: cs_bi
        password: CSBI@pdb2022
        initial-size: 100 # 初始化时建立物理连接的个数
        min-idle: 50   # 最小连接池数量
        max-active: 3000 #  最大连接数量
        query-timeout: 600000 #查询超时
        #filters: stat,config # stat表示sql合并
        #testOnBorrow: false  # 申请连接时执行validationQuery检测连接是否有效
    sharding:
      tables:
        t_part_drawing_stock:
          # 202206-202612按月分
          actualDataNodes: db1.t_part_drawing_stock_${0..54}
          tableStrategy:
            standard:
              shardingColumn: create_time
              #精确分片算法类名称，用于=和IN。。该类需实现 PreciseShardingAlgorithm 接口并提供无参数的构造器
              preciseAlgorithmClassName: utry.data.common.DateShardingAlgorithm
              rangeAlgorithmClassName: utry.data.common.DateShardingAlgorithm
          key-generator-column-name: id
    #打印sql(打印逻辑sql、以及实际sql)
    props:
      sql:
        show: true

eureka:
  instance:
    prefer-ip-address: true
    instance-id: ${spring.cloud.client.ip-address}:data:${server.port}
#    metadata-map:
#      zone: zdb # 与前端约定访问服务标识
  client:
    serviceUrl:
#      defaultZone: http://10.0.44.31:8888/eureka/
#      defaultZone: http://10.197.186.243:8881/eureka/ #生产环境
      defaultZone: http://10.197.180.185:8881/eureka/

    
#jdbc
utry:
  jdbc:
    dialect: mysql
  auth_ignore_uri_pattern: "\\.(gif|js|css|png|html|je?pg)$|swagger|/v2/api-docs|/dataSource/monitor/|/gateway/|/authServer/|/oam/|/login|/updatePassword|/subApi/|/actuator/"
  license:
    turn-on: false

mybatis:
  config-location: classpath:myBatis-config.xml
  mapper-locations: classpath:mapper/**/*.xml

server:
  port: 8052
  context-parameters:
    authentication_ignore_uri_pattern: "\\.(gif|js|css|png|html|je?pg)$|swagger|/v2/api-docs|/dataSource/monitor/|/gateway/|/authServer/|/oam/|/login"


logging:
  level:
    org:
      springframework:
        web:
          socket:
            server:
              support: off


pagehelper:
  helperDialect: mysql
  reasonable: true
  supportMethodsArguments: true
  params: count=countSql


info:
  app:
    name: 100060
  company:
    name: utry.cn
  build:
    version: 1.0

management:
  health:
    db:
      enabled: false
    redis:
      enabled: false
    elasticsearch:
      enabled: false
