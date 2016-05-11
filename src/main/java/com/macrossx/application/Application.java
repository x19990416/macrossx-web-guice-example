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
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.macrossx.embedded.BootServer;
import com.macrossx.embedded.ServletConfig;
import com.macrossx.embedded.jetty.JettyServer;
import com.macrossx.rest.HelloWorldResource;
import com.macrossx.template.groovy.TemplateGroovyModule;
import com.macrossx.wechat.IWechatServer;
import com.macrossx.wechat.WechatServletModule;
import com.sun.jersey.guice.JerseyServletModule;

public class Application {
	public static Injector injector;

	public static void main(String... s) {
		Guice.createInjector(new Module() {
			public void configure(Binder binder) {
				binder.bind(BootServer.class).to(JettyServer.class);
				binder.bind(EventListener.class).toInstance(new ServletConfig() {
					@Override
					public List<Module> provider() {
						return Lists.newArrayList(new Module() {

							@Override
							public void configure(Binder binder) {
								binder.bindConstant().annotatedWith(Names.named("macrossx.wechat.token"))
										.to("wechattest");
								binder.bind(IWechatServer.class).to(WechatServer.class);
							}
						}, new TemplateGroovyModule(), new JerseyServletModule() {
							protected void configureServlets() {
								bind(HelloWorldResource.class);
								// serve("/*").with(GuiceContainer.class);
							}
						}, new WechatServletModule());
					}
				});
			}
		}).getInstance(BootServer.class).run();
	}
	
	public static class WechatServer implements IWechatServer{}

}
