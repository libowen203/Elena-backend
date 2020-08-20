package com.elena.elena;

import com.elena.elena.util.ElenaUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@SpringBootApplication
public class ElenaApplication {

	@Value("${graphml.source.name}")  private String graphmlFileName;


	public static void main(String[] args) {
		SpringApplication.run(ElenaApplication.class, args);
	}

	@Bean
	public DataSource sqliteDataSource(@Value("${sqlite.db}") String sqliteConnectionString){
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
		comboPooledDataSource.setJdbcUrl(sqliteConnectionString);
		comboPooledDataSource.setMinPoolSize(5);
		comboPooledDataSource.setAcquireIncrement(5);
		comboPooledDataSource.setMaxPoolSize(20);

		return comboPooledDataSource;
	}

	@Bean
	public String graphmlFileName(){
		return graphmlFileName;
	}

}
