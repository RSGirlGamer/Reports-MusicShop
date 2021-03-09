package com.project.reportsmusicshop.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

class DropboxTest {

	@Test
	void test() {
		String TOKEN = "sl.AsvP_vjlS6jLvczi05eUEbDKZs1hveTtVRRGa8oXVZvrWt-RcaAxfv5QaayKxcb94mduWm2GBFYQUGPlhHP0JEIBe8ZWISvF8428aqeRc5sLm8Onuehc06eGJ4CXWDmj08wWj4Q";
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("RSGirlGamer/test-dropbox").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig, TOKEN);
		try {
			assertNotNull(dbxClientV2);
			FullAccount fullAccount = dbxClientV2.users().getCurrentAccount();
			System.out.println(fullAccount.getEmail());
		} catch (DbxApiException e) {
			System.out.println(e.getCause());
		} catch (DbxException e) {
			System.out.println(e.getCause());
		}
	}

}
