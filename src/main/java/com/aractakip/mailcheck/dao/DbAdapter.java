package com.aractakip.mailcheck.dao;

import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.aractakip.mailcheck.beans.TempRM;

public class DbAdapter {

	private static final Logger logger = Logger.getLogger(DbAdapter.class);

	private SimpleJdbcInsert insertTemplate;
	private JdbcTemplate selectTemplate;

	public void setDataSource(DataSource dataSource) {
		this.insertTemplate = new SimpleJdbcInsert(dataSource);

		if (insertTemplate.getTableName() != "RP_TASK") {
			insertTemplate.setTableName("RP_TASK");
			insertTemplate.setGeneratedKeyName("ID");
		}
		insertTemplate.compile();
		selectTemplate = new JdbcTemplate(dataSource);
	}

	public String updateTask;
	public String selectTask;

	public String getUpdateTask() {
		return updateTask;
	}

	public void setUpdateTask(String updateTask) {
		this.updateTask = updateTask;
	}

	public String getSelectTask() {
		return selectTask;
	}

	public void setSelectTask(String selectTask) {
		this.selectTask = selectTask;
	}

	public int updateTask(String type,int kulId, int id) {

		logger.info(updateTask + type + " " + kulId + " " + id);
		return selectTemplate.update(updateTask, new Object[] { type, kulId, id });
	}
	
	public int updateTask1(String type,int kulId, int id,int gorevStatus) {
		
		String sql=updateTask;
		 if(gorevStatus==2)
			 sql=updateTask.replaceAll("gorev_status", "gorev_status2");
		 if(gorevStatus==3)
			 sql=updateTask.replaceAll("gorev_status", "gorev_status3");
		 
		logger.info(sql + type + " " + kulId + " " + id);
		return selectTemplate.update(sql, new Object[] { type, kulId, id });
	}

	public java.util.List selectTask(int id) {

		logger.info(selectTask + " " + id);
		return (java.util.List) selectTemplate.query(selectTask, new Object[] { id }, new TempRM());
	}

}