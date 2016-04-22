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
package com.macrossx.application;

import java.util.EventListener;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.macrossx.embedded.BootServer;
import com.macrossx.embedded.ServletConfig;
import com.macrossx.embedded.jetty.JettyServer;
import com.macrossx.rest.HelloWorldResource;
import com.macrossx.servlet.HelloWorldServletModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class Application {
	public static void main(String...s){
		BootServer server = Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(BootServer.class).to(JettyServer.class);
				binder.bind(EventListener.class).toInstance(new ServletConfig(){
					@Override
					public List<AbstractModule> provider() {
						// TODO Auto-generated method stub
						List<AbstractModule> list = Lists.newArrayList();
						list.add(new JerseyServletModule(){
							protected void configureServlets() {
								bind(HelloWorldResource.class);
								serve("/rest/*").with(GuiceContainer.class);
							
							}
						});
						list.add(new HelloWorldServletModule());
						return list;
					}});
			}
		}).getInstance(BootServer.class);
		server.run();	
	}
	}
