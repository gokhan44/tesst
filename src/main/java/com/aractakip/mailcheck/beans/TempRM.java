package com.aractakip.mailcheck.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TempRM  implements  RowMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Temp skl=new Temp();
		skl.setTmp(rs.getString(1));
		return skl;
	
	}

}
