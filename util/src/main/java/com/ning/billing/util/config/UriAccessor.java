/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class UriAccessor {
	private static final String URI_SCHEME_FOR_CLASSPATH = "jar";
	private static final String URI_SCHEME_FOR_FILE = "file";

	public static InputStream accessUri(String uri)  throws IOException, URISyntaxException {
		return accessUri(new URI(uri));
	}
	
	public static InputStream accessUri(URI uri) throws IOException {
		String scheme = uri.getScheme();
        URL url = null;
        if (scheme.equals(URI_SCHEME_FOR_CLASSPATH)) {
        	return UriAccessor.class.getResourceAsStream(uri.getPath());
        } else if (scheme.equals(URI_SCHEME_FOR_FILE) &&
        	!uri.getSchemeSpecificPart().startsWith("/")) { // interpret URIs of this form as relative path uris
        	url = new File(uri.getSchemeSpecificPart()).toURI().toURL();
        }
        url = uri.toURL();
    	return url.openConnection().getInputStream();
	}
	
	public static String accessUriAsString(String uri)  throws IOException, URISyntaxException {
		return accessUriAsString(new URI(uri));
	}
	
	public static String accessUriAsString(URI uri) throws IOException {
		InputStream stream = accessUri(uri);
		return new Scanner(stream).useDelimiter("\\A").next();
	}
}
