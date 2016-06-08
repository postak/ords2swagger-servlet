package postak.swagger;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.jdbc.pool.OracleDataSource;

/**
 * Servlet implementation class getSwagger
 */

@WebServlet(
        description = "Swagger Servlet", 
        urlPatterns = { "/getSwagger" })
public class getSwagger extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private PrintWriter out = null;
    String c_jdbcUrl;
    String c_username;
    String c_password;
    String c_title;
	String c_description;
	String c_port;
	String c_hostname;
	String c_basepath;
	String c_workspace;
	String c_filetype;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getSwagger() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		out = response.getWriter();
                
        if ((c_jdbcUrl = request.getParameter("jdbcUrl")) == null) {
        	out.println("c_jdbcUrl parameter is missing");
        	return;
        }
        if ((c_username = request.getParameter("username")) == null) {
        	out.println("c_username parameter is missing");
        	return;
        }
        if ((c_password = request.getParameter("password")) == null) {
        	out.println("c_password parameter is missing");
        	return;
        }
        
        if ((c_title = request.getParameter("rest_title")) == null) {
        	out.println("c_title parameter is missing");
        	return;
        }
		
		if ((c_description = request.getParameter("rest_description")) == null) {
			out.println("c_description parameter is missing");
	       	return;
	    }
		
		if ((c_port = request.getParameter("rest_port")) == null) {
			out.println("c_port parameter is missing");
	       	return;
	    }		
		
		if ((c_hostname = request.getParameter("rest_hostname")) == null) {
			out.println("c_hostname parameter is missing");
	       	return;
	    }		
		
		if ((c_basepath = request.getParameter("rest_basepath")) == null) {
			out.println("c_basepath parameter is missing");
	       	return;
	    }		
		
		if ((c_workspace = request.getParameter("workspace")) == null) {
			out.println("c_workspace parameter is missing");
	       	return;
	    }		
		
		if ((c_filetype = request.getParameter("filetype")) == null) {
			out.println("filetype parameter is missing");
	       	return;
	    }		
		
		
    	switch( c_filetype.toLowerCase() ) {
    		case "ords":     
    			ords2Swagger();
    			break;
      		case "apex":     
    			apex2Swagger();
    			break;
      		default:     
      			out.println(c_filetype + " - Not supported ");
    			break;
    	}
	}
	 
	private void ords2Swagger()
	{
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
        try {
    		
        	OracleDataSource ods = new OracleDataSource();
	        
    		ods.setURL(c_jdbcUrl);
	        ods.setUser(c_username);
	        ods.setPassword(c_password);
	        
//            out.println("jdbcUrl "+ jdbcUrl);
//            out.println("username "+ username);
//            out.println("password "+ password);

	        conn = ods.getConnection();
			stmt = conn.createStatement();
			
			String query = "SELECT method, base_path, pattern,source_type,source FROM user_ords_services where status = 'PUBLISHED'";
			
			//Execute the SQL statement 
			rs = stmt.executeQuery(query);
						
						
            yaml_output(0,"swagger: '2.0'");
            yaml_output(0,"info:");
            yaml_output(2,"title: " + c_title);
            yaml_output(2,"description: " + c_description);
            yaml_output(2,"version: \"1.0.0\"");
            yaml_output(0,"# the domain of the service");
            yaml_output(0,"host: " + c_hostname + ":" + c_port);
            yaml_output(0,"basePath: " + c_basepath);
            yaml_output(0,"schemes:");
            yaml_output(2,"- http");
            yaml_output(2,"- https");
            yaml_output(0,"consumes:");
            yaml_output(2,"- application/json");
            yaml_output(0,"produces:");
            yaml_output(2,"- application/json");
            yaml_output(0,"paths:");

		   	String lastpath = "@";

			while(rs.next()) {
			
				String method = rs.getString("method"); 
				String base_path = rs.getString("base_path"); 
				String pattern = rs.getString("pattern");
				String source_type = rs.getString("source_type");
				String source = rs.getString("source");
				
//	            yaml_output(0,"---------------------------------- ");
//	            yaml_output(0,"method "+ method);
//	            yaml_output(0,"base_path "+ base_path);
//	            yaml_output(0,"pattern "+ pattern);
//	            yaml_output(0,"source_type "+ source_type);
//	            yaml_output(0,"source "+ source);	            
//	            yaml_output(0,"---------------------------------- ");


	            if (pattern.indexOf('.') >= 0) {
	            	
//		            yaml_output(0,"CASO 1");

	            	// no params

	            	if (base_path.equals(lastpath) == false) {
	            		lastpath = base_path;
	            		yaml_output(2, base_path + ":");
	            	}
          
	            	yaml_output(4, method.toLowerCase() + ":");


	            	switch( method.toLowerCase() ) {
	            		case "get":     
	    	            	yaml_output(6,"responses:");
	    	            	yaml_output(8,"200:");
	            			yaml_output(10,"description: return data from table " + source);
	            			break;
	            		case "put":     
	    	            	yaml_output(6,"responses:");
	    	            	yaml_output(8,"200:");
	    	            	yaml_output(10,"description: insert data to table " + source);  
	            			break;
	            		case "post":    
				            yaml_output(6,"produces:");
				            yaml_output(8,"- application/json");
				            yaml_output(6,"parameters:");
				            yaml_output(8,"- in: body");
				            yaml_output(10,"name: body");
				            yaml_output(10,"description: user data in JSON");
				            yaml_output(10,"required: true");
				            yaml_output(12,"schema:");
				            yaml_output(12,"type: object");
	    	            	yaml_output(6,"responses:");
	    	            	yaml_output(8,"200:");
	    	            	yaml_output(10,"description: insert data to  table " + source); 
	            			break;
	            		case "delete":  
	    	            	yaml_output(6,"responses:");
	    	            	yaml_output(8,"200:");
	    	            	yaml_output(10,"description: delete record in table " + source);  
	            			break;
	            		default:        
	    	            	yaml_output(6,"responses:");
	    	            	yaml_output(8,"200:");
	    	            	yaml_output(10,"description: UNSUPPORTED operation");  
	            			break;
	            	}
	            }
			    else if ( pattern.indexOf( ":" )  >= 0 ) {

//		            yaml_output(0,"CASO 2");
//		            yaml_output(0,"lastpath = " + lastpath);
//		            yaml_output(0,"base_path + pattern = " + base_path + pattern);
		            
			        // param found (es.  aaa/:empno/:ename or pattern a/:dep )
		            
			    	String  prefix = "";
			    	String allparams = pattern;
			    	
			    	if (pattern.charAt(0) != ':') {
			    		// a prefix to parameters exist
			    		prefix = pattern.split("/")[0] + "/";
			    		allparams = pattern.split("/")[1];
			    	}
			
			        if (lastpath.equals( base_path + pattern ) == false) {
			            lastpath = base_path + pattern;
			            yaml_output(2, base_path + prefix + allparams.replace(':', '{').replaceAll("/", "}/") + "}:");
			    	}			
			        
	            	yaml_output(4, method.toLowerCase()  + ":");
			        yaml_output(6,"parameters: ");
			
			        String param[] = allparams.split("/");
			        for (int i = 0;i < param.length;i++)
			        {
			            yaml_output(0,"        - name: " + param[i].replace(':', ' '));
			            yaml_output(0,"          in: path");
			            yaml_output(0,"          type: string");
			            yaml_output(0,"          description: parameter " + param[i] + " for statement | ");
			            yaml_output(0,"            " + source.replaceAll("\n", "\n            "));
			            yaml_output(0,"          required: true");
			        }
			           
			        switch( method.toLowerCase() ) {
			        	case "get":     
					        yaml_output(6,"responses:");
					        yaml_output(8,"200:");
					        yaml_output(10,"description: return data from |"); 
			        		break;
			        	case "put":     
					        yaml_output(6,"responses:");
					        yaml_output(8,"200:");
					        yaml_output(10,"description: insert data to | "); 
			        		break;
			        	case "post":    
				            yaml_output(6,"produces:");
				            yaml_output(8,"- application/json");
				            yaml_output(6,"parameters:");
				            yaml_output(8,"- in: body");
				            yaml_output(10,"name: body");
				            yaml_output(10,"description: user data in JSON");
				            yaml_output(10,"required: true");
				            yaml_output(10,"schema:");
				            yaml_output(12,"type: object");
					        yaml_output(6,"responses:");
					        yaml_output(8,"200:");
					        yaml_output(10,"description: insert data to | "); 
			        		break;
			        	case "delete":  
					        yaml_output(6,"responses:");
					        yaml_output(8,"200:");
					        yaml_output(10,"description: delete record in | "); 
			        		break;
			        	default:        
					        yaml_output(6,"responses:");
					        yaml_output(8,"200:");
					        yaml_output(10,"description: UNSUPPORTED operation"); 
			        		break;
			        }
		            yaml_output(12, source.replaceAll("\n", "\n            "));
			    }

			    else if ( pattern.equals("batchload")) {
	            	
	            	// skip this record
	            }
			    else {
			    	
		            out.println("UNSUPPORTED SCENARIO");
		            out.println("---------------------------------- ");
		            out.println("method "+ method);
		            out.println("base_path "+ base_path);
		            out.println("pattern "+ pattern);
		            out.println("source_type "+ source_type);
		            out.println("source "+ source);	            
		            out.println("---------------------------------- ");


			    }       
			} 		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			out.println(e);
		}
		catch (Exception e) {
			//Handle other errors
			out.println(e);
		}
		finally {			
			//Perform clean up try {
			try {
				if (rs != null)
					rs.close();
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				out.println(e);
			}
		} 

	}

	private void apex2Swagger()
	{
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
        try {
    		
        	OracleDataSource ods = new OracleDataSource();
	        
    		ods.setURL(c_jdbcUrl);
	        ods.setUser(c_username);
	        ods.setPassword(c_password);
	        
//            yaml_output(0,"jdbcUrl "+ jdbcUrl);
//            yaml_output(0,"username "+ username);
//            yaml_output(0,"password "+ password);

	        conn = ods.getConnection();
			stmt = conn.createStatement();
			
			String query =  "SELECT  DISTINCT APEX_REST_RESOURCE_HANDLERS.workspace workspace," +
							"APEX_REST_RESOURCE_TEMPLATES.module_name module_name, method," +
							"APEX_REST_RESOURCE_MODULES.uri_prefix uri_prefix, "  +
							"APEX_REST_RESOURCE_TEMPLATES.uri_template uri_template," +
							"source_type source_type , to_char(source ) source, format "  +
							"FROM  APEX_REST_RESOURCE_HANDLERS, APEX_REST_RESOURCE_TEMPLATES, APEX_REST_RESOURCE_MODULES WHERE  "  +
							"APEX_REST_RESOURCE_HANDLERS.template_id = APEX_REST_RESOURCE_TEMPLATES.template_id AND "  +
							"APEX_REST_RESOURCE_MODULES.module_name = APEX_REST_RESOURCE_TEMPLATES.module_name AND " +
				            "APEX_REST_RESOURCE_HANDLERS.workspace = UPPER('" + c_workspace + "') " +
							"ORDER BY module_name,uri_prefix, uri_template";
			
			//Execute the SQL statement 

			rs = stmt.executeQuery(query);
						
			out.flush();
						          
            yaml_output(0,"swagger: '2.0'");
            yaml_output(0,"info:");
            yaml_output(2,"title: " + c_title);
            yaml_output(2,"description: " + c_description);
            yaml_output(2,"version: \"1.0.0\"");
            yaml_output(0,"# the domain of the service");
            yaml_output(0,"host: " + c_hostname + ":" + c_port);
            yaml_output(0,"basePath: " + c_basepath);
            yaml_output(0,"schemes:");
            yaml_output(2,"- http");
            yaml_output(2,"- https");
            yaml_output(0,"consumes:");
            yaml_output(2,"- application/json");
            yaml_output(0,"produces:");
            yaml_output(2,"- application/json");
            yaml_output(0,"paths:");


		   	String lastpath = "@";

			while(rs.next()) {
			
				String t_workspace = rs.getString("workspace"); 
				String t_module_name = rs.getString("module_name"); 
				String t_method = rs.getString("method"); 
				String t_uri_prefix = rs.getString("uri_prefix"); 
				String t_uri_template = rs.getString("uri_template"); 
				String t_source_type = rs.getString("source_type");
				String t_source = rs.getString("source");
				String t_format = rs.getString("format");
				
//	            out.println("---------------------------------- ");
//	            out.println("method "+ t_method);
//	            out.println("uri_prefix "+ t_uri_prefix);
//	            out.println("uri_template "+ t_uri_template);
//	            out.println("source_type "+ t_source_type);
//	            out.println("source "+ t_source);	            
//	            out.println("---------------------------------- ");
//				out.flush();

            	// c'e' un parametro (contract/{p1}{p2}/{p3}/
            		        
				if (t_uri_prefix.charAt(0) != '/')
					yaml_output(2,"/" + t_uri_prefix + t_uri_template + ":");
				else
					yaml_output(2,t_uri_prefix + t_uri_template + ":");
				
	        	Pattern p = Pattern.compile("\\{([^\\}]*)\\}");
	        	Matcher m = p.matcher(t_uri_template);

	        	yaml_output(4,t_method.toLowerCase() + ":");
	        	

	        	Boolean firstelement = true;
	        	
	        	while (m.find()) {
	                String param = m.group(1);
	                
		        	if (firstelement) {
				        yaml_output(6,"parameters:");
				        firstelement = false;
		        	}
	        
		            yaml_output(8,"- name: " + param);
		            yaml_output(10,"in: path");
		            yaml_output(10,"type: string");
		            yaml_output(10,"description: parameter " + param + " for statement | ");
		            yaml_output(12,t_source.replaceAll("\n", "\n            "));
		            yaml_output(10,"required: true");
		        }
		           
	        	switch( t_method.toLowerCase() ) {
		        	case "get":     
				        yaml_output(6,"responses:");
				        yaml_output(8,"200:");
				        yaml_output(10,"description: return data from |"); 
		        		break;
		        	case "put":     
				        yaml_output(6,"responses:");
				        yaml_output(8,"200:");
				        yaml_output(10,"description: insert data to | "); 
		        		break;
		        	case "post":    
			            yaml_output(6,"produces:");
			            yaml_output(8,"- application/json");
			            yaml_output(6,"parameters:");
			            yaml_output(8,"- in: body");
			            yaml_output(10,"name: body");
			            yaml_output(10,"description: user data in JSON");
			            yaml_output(10,"required: true");
			            yaml_output(10,"schema:");
			            yaml_output(12,"type: object");
				        yaml_output(6,"responses:");
				        yaml_output(8,"200:");
				        yaml_output(10,"description: insert data to | "); 
		        		break;
		        	case "delete":  
				        yaml_output(6,"responses:");
				        yaml_output(8,"200:");
				        yaml_output(10,"description: delete record in | "); 
		        		break;
		        	default:        
				        yaml_output(6,"responses:");
				        yaml_output(8,"200:");
				        yaml_output(10,"description: UNSUPPORTED operation"); 
		        		break;
		        }
	        	yaml_output(12, t_source.replaceAll("\n", "\n            "));
		    }

  		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			out.println(e);
		}
		catch (Exception e) {
			//Handle other errors
			out.println(e);
		}
		finally {			
			//Perform clean up try {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				out.println(e);
			}
		} 

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         doGet(request,response);
    }
	
	private void yaml_output (int level, String text)
	{
		while (level-- > 0)
			out.append(' ');
		out.println(text);
	}

}
