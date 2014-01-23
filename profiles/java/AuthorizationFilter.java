package fr.eurecom.nerd.api.authentication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.Splitter;

public class AuthorizationFilter implements ContainerRequestFilter{

    private static NerdPrincipals principals = new NerdPrincipals();

    public void filter(ContainerRequestContext cx) throws IOException 
    {       
        NerdPrincipal user = null;
        byte[] content = null;
        String key= null;
        
        if(cx.getMethod().equals(HttpMethod.GET)) {
            UriInfo ui = cx.getUriInfo();
            MultivaluedMap<String, String> map = ui.getQueryParameters();
            key = map.getFirst("key");
        }
        else {  //let's assume GET,POST,PUT encapsulate key in the entities
            if (cx.hasEntity()) {           
                InputStream is = cx.getEntityStream() ;
                
                // deep copy of the input stream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while ((n = is.read(buf)) >= 0)
                    baos.write(buf, 0, n);
                content = baos.toByteArray();
                          
                String writer = new String(content, "UTF-8");
                Map<String,String> map = Splitter
                                          .on("&")
                                          .withKeyValueSeparator("=")
                                          .split(writer);
                key = map.get("key");
                
                cx.setEntityStream(new ByteArrayInputStream(content));
            }
        }
        
        if (key != null) user = principals.findUserByKey(key);
        cx.setSecurityContext(new Authorizer(user));
    }
}
