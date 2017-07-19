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
