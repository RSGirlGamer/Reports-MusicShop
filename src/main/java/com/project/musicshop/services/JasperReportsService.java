package com.project.musicshop.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface JasperReportsService {
	JasperPrint compileReportJasper(ByteArrayOutputStream fileByte, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException;
}
