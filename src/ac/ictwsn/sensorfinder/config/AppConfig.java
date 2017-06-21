package ac.ictwsn.sensorfinder.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mysql.jdbc.Driver;

//FIXME all these hand-coded are dirty
@Configuration
@ComponentScan(
		basePackages = {"ac.ictwsn.sensorfinder"},
		excludeFilters = {
				@ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION),
				@ComponentScan.Filter(value = EnableWebMvc.class, type = FilterType.ANNOTATION)
		})
@EnableScheduling
@EnableAspectJAutoProxy
@EnableCaching
@EnableAsync
@EnableJpaRepositories(
		repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class,
		basePackages = {"ac.ictwsn.sensorfinder.repositories"})
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class AppConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(){
		EntityManagerFactory factory = entityManagerFactory().getObject();
		return new JpaTransactionManager(factory);
	}
	
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource ds = null;
		try {
			ds = new SimpleDriverDataSource((Driver) Class.forName(
					env.getProperty("jdbc.driverClassName")).newInstance(), 
					env.getProperty("jdbc.url"),
					env.getProperty("jdbc.username"),
					env.getProperty("jdbc.password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(false);
		vendorAdapter.setDatabase(Database.MYSQL);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setDataSource(dataSource());
		factory.setPackagesToScan("ac.ictwsn.sensorfinder.entities");
		
		Properties jpaProperties = new Properties();
		String pstr = "hibernate.hbm2ddl.auto";
		jpaProperties.put(pstr, env.getProperty(pstr));
		factory.setJpaProperties(jpaProperties);
		pstr = "hibernate.enable_lazy_load_no_trans";
		jpaProperties.put(pstr, env.getProperty(pstr));
		factory.setJpaProperties(jpaProperties);
		
		factory.afterPropertiesSet();
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		return factory;
    }
	
	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}
	
}
