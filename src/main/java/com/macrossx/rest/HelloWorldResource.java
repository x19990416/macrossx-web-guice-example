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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Singleton;

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
    @XmlRootElement
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HelloBean{
    	private String hello;
    	private long time;
    }
}
