 ```
 @Configuration
 public class SomeOne{
 @Bean
    public MapperScannerConfigurer mapperScannanerConfigurer() {
        final String basePackage = environment.getProperty("mybatis.dao.locations");
        final MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        configurer.setBasePackage(basePackage);
        return configurer;
    }

    @Bean
    public Interceptor interceptor4DB() {
        Interceptor4DB multiRepositoryPlugin = new Interceptor4DB();
        multiRepositoryPlugin.setTableNameVsHandler(Properties);
        multiRepositoryPlugin.setTableNameVsDataSource(Properties);
        return multiRepositoryPlugin;
    }
    }
 ```
properties
```
============================================================================
# MyBatis Configurations
#============================================================================
mybatis.mapper-locations=
mybatis.dao.locations=dao
mybatis.configuration.vfsImpl=org.mybatis.spring.boot.autoconfigure.SpringBootVFS
mybatis.configuration.cacheEnabled=true
mybatis.configuration.useGeneratedKeys=true
mybatis.configuration.defaultExecutorType=SIMPLE
mybatis.configuration.lazyLoadingEnabled=true
mybatis.configuration.defaultStatementTimeout=5000
mybatis.configuration.mapUnderscoreToCamelCase=true
mybatis.typeAliasesPackage=
#============================================================================
# Database Configurations
#============================================================================
jakiro.name=
jakiro.ds.default.url=jdbc:mysql://mysql.oneapm.me:3306/a
jakiro.ds.default.username=root
jakiro.ds.default.password=root

jakiro.ds.c.url=jdbc:mysql://mysql.oneapm.me:3306/b
jakiro.ds.c.username=root
jakiro.ds.c.password=blueware

jakiro.ds.c.url=jdbc:mysql://mysql.oneapm.me:3306/c
jakiro.ds.c.username=root
jakiro.ds.c.password=root


```
