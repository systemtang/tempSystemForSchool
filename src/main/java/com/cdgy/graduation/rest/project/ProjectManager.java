package com.cdgy.graduation.rest.project;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/project")
@RequestScoped
public class ProjectManager {
	@POST
	@Path("/creatNewProject")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * 教师创建新的课题项目
	 */
	public String creatNewProject(String reqstr){
		
		return "";
	}
}
