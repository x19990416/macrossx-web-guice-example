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
package com.macrossx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


public class JarResourceLoader {

	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		ManifestMF mi = getManifestMFInfo();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL.setURLStreamHandlerFactory(new ResourceURLStreamHandlerFactory(cl));
		URL[] rsrcUrls = new URL[mi.resourceClassPath.length];
		for (int i = 0; i < mi.resourceClassPath.length; ++i) {
			String rsrcPath = mi.resourceClassPath[i];
			if (rsrcPath.endsWith("/"))
				rsrcUrls[i] = new URL("rsrc:" + rsrcPath);
			else
				rsrcUrls[i] = new URL("jar:rsrc:" + rsrcPath + "!/");
		}
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		Class<?> c = Class.forName(mi.resourceMainClass, true, jceClassLoader);
		Method main = c.getMethod("main", new Class[] { args.getClass() });
		main.invoke(null, new Object[] { args });
	}

	private static ManifestMF getManifestMFInfo() throws IOException {
		try {
			Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader()
					.getResources("META-INF/MANIFEST.MF");
			URL url = (URL) resEnum.nextElement();
			InputStream is = url.openStream();
			if (is != null) {
				ManifestMF result = new ManifestMF();
				Manifest manifest = new Manifest(is);
				Attributes mainAttribs = manifest.getMainAttributes();
				result.resourceMainClass = mainAttribs.getValue("resource-Main-Class");
				String rsrcCP = mainAttribs.getValue("resource-Class-Path");
				if (rsrcCP == null)
					rsrcCP = "";
				result.resourceClassPath = splitSpaces(rsrcCP);
				if ((result.resourceMainClass != null) && (!(result.resourceMainClass.trim().equals(""))))
					return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Rsrc-Class-Path)");
			return null;
		}
		return null;
	}

	private static String[] splitSpaces(String line) {
		if (line == null)
			return null;
		List<String> result = new ArrayList<String>();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(32, firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos)
				result.add(line.substring(firstPos, lastPos));

			firstPos = lastPos + 1;
		}
		return ((String[]) result.toArray(new String[result.size()]));
	}

	public static class ManifestMF {
		public String resourceMainClass;
		public String[] resourceClassPath;
	}

	public static class ResourceURLStreamHandlerFactory implements URLStreamHandlerFactory {
		private ClassLoader classLoader;
		private URLStreamHandlerFactory chainFac;

		public ResourceURLStreamHandlerFactory(ClassLoader cl) {
			this.classLoader = cl;
		}

		public URLStreamHandler createURLStreamHandler(String protocol) {
			if ("rsrc".equals(protocol))
				return new ResourceURLStreamHandler(this.classLoader);
			if (this.chainFac != null)
				return this.chainFac.createURLStreamHandler(protocol);
			return null;
		}

		public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
			this.chainFac = fac;
		}
	}

	public static class ResourceURLStreamHandler extends URLStreamHandler {
		private ClassLoader classLoader;

		public ResourceURLStreamHandler(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		protected URLConnection openConnection(URL u) throws IOException {
			return new RsourceURLConnection(u, this.classLoader);
		}

		protected void parseURL(URL url, String spec, int start, int limit) {
			String file;
			if (spec.startsWith("rsrc:"))
				file = spec.substring(5);
			else if (url.getFile().equals("./"))
				file = spec;
			else if (url.getFile().endsWith("/"))
				file = url.getFile() + spec;
			else
				file = spec;
			super.setURL(url, "rsrc", "", -1, null, null, file, null, null);
		}
	}

	public static class RsourceURLConnection extends URLConnection {
		private ClassLoader classLoader;

		public RsourceURLConnection(URL url, ClassLoader classLoader) {
			super(url);
			this.classLoader = classLoader;
		}

		public void connect() throws IOException {
		}

		public InputStream getInputStream() throws IOException {
			String file = URLDecoder.decode(this.url.getFile(), "UTF-8");
			InputStream result = this.classLoader.getResourceAsStream(file);
			if (result == null)
				throw new MalformedURLException("Could not open InputStream for URL '" + this.url + "'");

			return result;
		}
	}
}
