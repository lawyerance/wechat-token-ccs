package com.honing.css.spring.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

class TokenServiceImplTest {



	@Test
	void doObtainStableToken() throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/stable_token";
		RestTemplate restTemplate =
			new RestTemplateBuilder().additionalMessageConverters(new SourceHttpMessageConverter<>(), new MappingJackson2HttpMessageConverter()).build();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		StableTokenDTO param = new StableTokenDTO();
		param.setAppid("wxe4cb8a0f17a8f284");
		param.setSecret("38715372e58f3bde0c92eea3a4760ea6");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseEntity<Map<String, Object>> exchange =
			restTemplate.exchange(url,HttpMethod.POST,new HttpEntity<>(param, headers),
				new ParameterizedTypeReference<Map<String,
				Object>>() {
				@Override
				public Type getType() {
					return super.getType();
				}
			});
		Map<String, Object> body = exchange.getBody();
		System.out.println(body);

	}

	void test() throws Exception {
		String url = "https://dev.pinyiche.club/app-api/member/leader/info/page";
		RestTemplate restTemplate =
			new RestTemplateBuilder().additionalMessageConverters(new SourceHttpMessageConverter<>(),
				new MappingJackson2HttpMessageConverter()).additionalInterceptors(new ClientHttpRequestLoggingInterceptor()).build();
		Map<String, Object> param = Collections.singletonMap("current", 1);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseEntity<Map<String, Object>> exchange =
			restTemplate.exchange(url,HttpMethod.POST,new HttpEntity<>(param, headers),
				new ParameterizedTypeReference<Map<String,
					Object>>() {
					@Override
					public Type getType() {
						return super.getType();
					}
				});
		Map<String, Object> body = exchange.getBody();
		System.out.println(body);

	}


//	void doObtainStableTokenUseHttpComponts() throws Exception {
//		StableTokenDTO param = new StableTokenDTO();
//		param.setAppid("wxe4cb8a0f17a8f284");
//		param.setSecret("38715372e58f3bde0c92eea3a4760ea6");
//
//		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//		HttpPost post = new HttpPost("https://api.weixin.qq" + ".com/cgi-bin" + "/stable_token");
//		post.setEntity(new StringEntity(mapper.writeValueAsString(param)));
//		CloseableHttpResponse execute = httpClient.execute(post);
//
//		InputStream body = execute.getEntity().getContent();
//		System.out.println(StreamUtils.copyToString(body, StandardCharsets.UTF_8));
//
//	}


//	@Test
	void testHttpMessageConverter() throws Exception {
		StableTokenDTO param = new StableTokenDTO();
		param.setAppid("wxe4cb8a0f17a8f284");
		param.setSecret("38715372e58f3bde0c92eea3a4760ea6");

		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		mappingJackson2HttpMessageConverter.write(param, MediaType.APPLICATION_JSON, new S(outputStream));
		System.out.println(outputStream.toString(StandardCharsets.UTF_8));
	}

	static class S implements HttpOutputMessage {
		private final OutputStream os;

		S(OutputStream os) {
			this.os = os;
		}

		@Override
		public OutputStream getBody() throws IOException {

			return os;
		}

		@Override
		public HttpHeaders getHeaders() {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return headers;
		}
	}

}
