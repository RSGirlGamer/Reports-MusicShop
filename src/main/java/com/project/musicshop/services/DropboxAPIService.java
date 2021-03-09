package com.project.musicshop.services;

import java.io.IOException;

import javax.ws.rs.core.Response;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadErrorException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface DropboxAPIService {
	Response downloadReport(DbxClientV2 dbxClientV2, String orderID, String client);
	void loadReportDropbox(DbxClientV2 dbxClientV2, String orderID, String client, JasperPrint jasperPrint) throws IOException, JRException, UploadErrorException, DbxException;
	
}
