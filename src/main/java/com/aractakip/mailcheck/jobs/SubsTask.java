package com.aractakip.mailcheck.jobs;


import org.apache.log4j.Logger;

import com.aractakip.mailcheck.dao.DbAdapter;

public class SubsTask {
	private static final Logger logger = Logger.getLogger("rootLogger");

	DbAdapter dbo;


	public DbAdapter getDbo() {
		return dbo;
	}

	public void setDbo(DbAdapter dbo) {
		this.dbo = dbo;
	}



	public static String GetAttrKey(String searchText) {
		String retVal = "";
		try {

			if (searchText.contains("GUNLUKBURC")) {
				retVal = "BURC";
			} else if (searchText.contains("HAVADURUMU")) {
				retVal = "Şehirler";
			} else  {
				retVal = searchText;
			}
		} catch (Exception e) {
			logger.error("GetAttrValue:" + e);

		}
		logger.info("GetAttrValue|Result="+retVal);

		return retVal;
	}
 	

	

}
