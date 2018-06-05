package com.refactoring.rxo.datasource.mysql.member;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by fei.yao on 16/7/26.
 */
@Configuration
@MapperScan(basePackages = MemberDataSourceConfig.PACKAGE, markerInterface = MemberMybatisMapper.class, sqlSessionFactoryRef = "memberSqlSessionFactory")
public class MemberDataSourceConfig implements EnvironmentAware {
    private final Logger LOGGER = LoggerFactory.getLogger(MemberDataSourceConfig.class);

    /**
     * 精确到 master 目录，以便跟其他数据源隔离
     */
    static final String PACKAGE = "com.refactoring.rxo.member";

    private RelaxedPropertyResolver mybatisPropertyResolver;


    /**
     * application.properties中对应属性的前缀
     *
     * @return
     */
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "memberDataSource")
    @Primary
    public DataSource memberDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }


    /**
     * 获取配置信息
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        mybatisPropertyResolver = new RelaxedPropertyResolver(environment, "mybatis.");
    }

    @Bean(name = "memberSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("memberDataSource") DataSource datasource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        //添加mybatis配置文件
        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        bean.setConfigLocation(resourceResolver.getResource(mybatisPropertyResolver.getProperty("config-location")));
        try {
            bean.setMapperLocations(resourceResolver.getResources(mybatisPropertyResolver.getProperty("member-mapper-locations")));
            return bean.getObject();
        } catch (IOException e) {
            LOGGER.error("获取mapper资源出现异常", e);
            throw new RuntimeException("获取mapper资源出现异常", e);
        } catch (Exception e) {
            LOGGER.error("初始化sqlSessionFactory时出现异常", e);
            throw new RuntimeException("初始化sqlSessionFactory时出现异常", e);
        }
    }

    @Bean(name = "memberTransactionManager")
    @Primary
    public DataSourceTransactionManager memberTransactionManager() {
        return new DataSourceTransactionManager(memberDataSource());
    }

}
