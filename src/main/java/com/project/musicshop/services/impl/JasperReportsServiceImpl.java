package com.project.musicshop.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.musicshop.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class JasperReportsServiceImpl implements JasperReportsService {
	
	@Value("${spring.datasource.driverClassName}")
	String driver;
	@Value("${spring.datasource.url}")
	String url;
	@Value("${spring.datasource.username}")
	String username;
	@Value("${spring.datasource.password}")
	String password;

	@Override
	public JasperPrint compileReportJasper(ByteArrayOutputStream fileByte, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException {
		InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/leaf_banner_violet.png");
		Map<String, Object> map = new HashMap<>();
		map.put("orderID", orderID);
		map.put("image", imageInputStream);
		byte[] bytes = fileByte.toByteArray();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		Class.forName(this.driver);
		Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		inputStream.close();
		return JasperFillManager.fillReport(jasperReport, map, connection);
	}

}
