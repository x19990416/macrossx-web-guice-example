/**
 * Copyright (C) 2016 X-Forever.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macrossx.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.groovy.control.CompilationFailedException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Path("rest")
@Singleton
public class HelloWorldResource {
    @GET
    @Path("hello")
    @Produces( MediaType.APPLICATION_JSON)
    public HelloBean createSimpleBean() {
        return new HelloBean("hello", System.currentTimeMillis());
    }
    
    @GET
    @Path("groovy")
    @Produces("text/plain")
    public String  groovy() {

    	TemplateEngine engine = new SimpleTemplateEngine();
    	Template template = null;
		try {
			template = engine.createTemplate(new InputStreamReader(this.getClass().getResourceAsStream("/template/groovy/userinfo.groovy")));
		} catch (CompilationFailedException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	//声明要绑定(或者叫合并)到模板的一个 Map 对象
    	List<User> users =  Lists.newArrayList(new User("a","ag"),new User("b","bg"));
    	//把模型数据归并到模板中，通过 Map 来传递参数
    			Map map = Maps.newHashMap();
    	map.put("title", "显示用户信息");
    	map.put("users",users);
    	map.put("footer","foot");
    	Writable result = template.make(map);
    	
    	result.toString();
    	//如果是作为一个 Groovylet writeTo() 语句就可以写成
     return result.toString();
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User{
    	private String name;	
    	private String gender;
    }
    
    
    @XmlRootElement
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HelloBean{
    	private String hello;
    	private long time;
    }
}
