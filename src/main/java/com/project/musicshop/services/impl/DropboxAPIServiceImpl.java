package com.project.musicshop.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.project.musicshop.services.DropboxAPIService;
import com.project.musicshop.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@Service
public class DropboxAPIServiceImpl implements DropboxAPIService {

	@Value("${spring.dropbox.directory.reports}")
	String DIRECTORY_REPORTS;
	@Value("${spring.dropbox.file.jrxml}")
	String FILE_JASPER_JRXML;
	@Autowired
	private JasperReportsService jasperReportsServiceImpl;
	@Override
	public Response downloadReport(DbxClientV2 dbxClientV2, String orderID, String client) {
		ByteArrayOutputStream fileByte = new ByteArrayOutputStream();
		String message = null;
		try {
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(DIRECTORY_REPORTS + FILE_JASPER_JRXML);
			downloader.download(fileByte);
			message = "Se ha generado exitósamente";
			JasperPrint jasperPrint = this.jasperReportsServiceImpl.compileReportJasper(fileByte, orderID);
			this.loadReportDropbox(dbxClientV2, orderID, client, jasperPrint);
		} catch (DownloadErrorException e) {
			System.out.println(e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			System.out.println(e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			System.out.println(e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (SQLException e) {
			System.out.println(e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (JRException e) {
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return Response.status(Response.Status.OK).entity(message).build();
	}

	@Override
	public void loadReportDropbox(DbxClientV2 dbxClientV2, String orderID, String client, JasperPrint jasperPrint) throws IOException, JRException, UploadErrorException, DbxException {
		String nameFilePDF = orderID + ".pdf";
		File filePDF = File.createTempFile("temp", nameFilePDF);
		InputStream inputStream = new FileInputStream(filePDF);
		JRPdfExporter jrPdfExporter = new JRPdfExporter();
		jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePDF));
		SimplePdfReportConfiguration simplePdfReportConfiguration = new SimplePdfReportConfiguration();
		jrPdfExporter.setConfiguration(simplePdfReportConfiguration);
		jrPdfExporter.exportReport();
		UploadBuilder uploadBuilder = dbxClientV2.files().uploadBuilder(DIRECTORY_REPORTS + "/" + client + "/" + nameFilePDF);
		uploadBuilder.withClientModified(new Date(filePDF.lastModified()));
		uploadBuilder.withMode(WriteMode.ADD);
		uploadBuilder.withAutorename(true);
		uploadBuilder.uploadAndFinish(inputStream);
		inputStream.close();
	}

}
