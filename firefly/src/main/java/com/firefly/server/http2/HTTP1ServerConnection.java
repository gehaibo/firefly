package com.firefly.server.http2;

import com.firefly.codec.http2.decode.HttpParser;
import com.firefly.codec.http2.decode.HttpParser.RequestHandler;
import com.firefly.codec.http2.decode.HttpParser.ResponseHandler;
import com.firefly.codec.http2.encode.HttpGenerator;
import com.firefly.codec.http2.stream.AbstractHTTP1Connection;
import com.firefly.codec.http2.stream.HTTP2Configuration;
import com.firefly.net.Session;
import com.firefly.net.tcp.ssl.SSLSession;

public class HTTP1ServerConnection extends AbstractHTTP1Connection {

	public HTTP1ServerConnection(HTTP2Configuration config, Session tcpSession, SSLSession sslSession,
			HTTP1ServerRequestHandler requestHandler) {
		super(config, sslSession, tcpSession, requestHandler, null);
		requestHandler.connection = this;
	}

	@Override
	protected HttpParser initHttpParser(HTTP2Configuration config, RequestHandler requestHandler,
			ResponseHandler responseHandler) {
		return new HttpParser(requestHandler, config.getMaxRequestHeadLength());
	}

	@Override
	protected HttpGenerator initHttpGenerator() {
		return new HttpGenerator(true, true);
	}

	HttpParser getParser() {
		return parser;
	}

	HttpGenerator getGenerator() {
		return generator;
	}

	SSLSession getSSLSession() {
		return sslSession;
	}

	Session getTcpSession() {
		return tcpSession;
	}

	HTTP2Configuration getHTTP2Configuration() {
		return config;
	}

}