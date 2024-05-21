package com.aractakip.mailcheck.ws;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.aractakip.mailcheck.dao.DbAdapter;
import com.aractakip.mailcheck.jobs.SubsTask;
import com.aractakip.mailcheck.utils.Utils;

public class Service {
	// log4j configuration
	private static final Logger logger = Logger.getLogger(Service.class);

	// spring configuration
	ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
	DbAdapter dbAdapter = context.getBean(DbAdapter.class);
	Utils utl = context.getBean(Utils.class);  
	SubsTask subsTask = context.getBean(SubsTask.class);

	
    @GET
    @Path("{id}/{updtId}/{type}")
    @Produces(MediaType.TEXT_HTML)
    public String update1(@PathParam("id") int id, @PathParam("updtId") int updtId,
            @PathParam("type") String type) {
        
          int count=dbAdapter.updateTask(type,updtId,id);
          String result="<body><h1 style=\"color:green;\">" + utl.getSucesMess() + "</body></h1>";
          if (count<1) 
              result="<body><h1 style=\"color:red;\">" + utl.getWrongMess() + "</body></h1>";;

        
         return "<html> " + "<title>" + "Talep Degerlendirme" + "</title>"
          + "<body><h1>" + result + "</body></h1>" + "</html> ";
    }
    
    @GET
    @Path("{id}/{updtId}/{gorevStatus}/{type}")
    @Produces(MediaType.TEXT_HTML)
    public String update2(@PathParam("id") int id, @PathParam("updtId") int updtId,@PathParam("gorevStatus") int gorevStatus,
            @PathParam("type") String type) {
        
          int count=dbAdapter.updateTask1(type,updtId,id,gorevStatus);
          String result="<body><h1 style=\"color:green;\">" + utl.getSucesMess() + "</body></h1>";
          if (count<1) 
              result="<body><h1 style=\"color:red;\">" + utl.getWrongMess() + "</body></h1>";;

        
         return "<html> " + "<title>" + "Talep Degerlendirme" + "</title>"
          + "<body><h1>" + result + "</body></h1>" + "</html> ";
    }

//	@GET
//	@Path("update/{id}/{updtId}/{type}")
//	@Produces("application/json")
//	public Response updateTask(@PathParam("id") int id,
//			                           @PathParam("updtId") int updtId,
//			                           @PathParam("type") String type) {
//		Response response = new Response();
//        String result=utl.getSucesMess();
//		int count=dbAdapter.updateTask(type,updtId,id);
//		
//		if (count<1) 
//			result=utl.getWrongMess();
//
//		response.setResult(result);
//		logger.info("updateTask|||ID:" + id +"|KulId="+updtId+"|Type:"+ type+"|Result:"+ result);
//
//		return response;
//
//	}
//	

//	@POST
//	@Path("countRequest")
//	@Consumes("application/json")
//	@Produces("application/json")
//	public ResponseCount countRequest(RequestCount request) {
//
//		ResponseCount resp = new ResponseCount();
//
//		String result = "";
//		String errCode = "";
//		String errDescription = "";
//		try {
//    	    logger.info("countRequest||Msisdn="+ request.getMsisdn() +"|Service="+request.getService()+"|Ip="+request.getIp()+"|Page="+request.getPage());
//				if (request.getMsisdn() != null && request.getService() != null) {
//					result = "OK";
//					errCode = "00";
//					errDescription = "NoError";
//				} else {
//					result = "err";
//					errCode = "97";
//					errDescription = "MissingParameters";
//				}
//		} catch (Exception e) {
//			logger.info(e);
//			result = "err";
//			errCode = "95";
//			errDescription = "SystemError";
//		}
//
//		resp.setResult(result);
//		resp.setErr_code(errCode);
//		resp.setErr_desc(errDescription);
//		logger.info("countRequestResp||Result=" + result + "|ErrorCode="+ errCode + "|ErrDesc=" + errDescription);
//
//		return resp;
//
//	}


	public Utils getUtl() {
		return utl;
	}

	public void setUtl(Utils utl) {
		this.utl = utl;
	}

	
}