package com.project.musicshop.services;

import javax.ws.rs.core.Response;

import com.dropbox.core.v2.DbxClientV2;

public interface MailService {
	public Response sendEmail(DbxClientV2 dbxClientV2, String recipient, String client, String orderID);
}
