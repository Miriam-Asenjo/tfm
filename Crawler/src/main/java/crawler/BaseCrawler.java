package crawler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

public abstract class BaseCrawler {

	protected CloseableHttpAsyncClient httpClient;
	
	public BaseCrawler(){

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        
        this.httpClient = HttpAsyncClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();

        this.httpClient.start();
	}
	
	
	public Response sendRequest(String url)
	{
		HttpGet request = new HttpGet();
		final Response crawlResponse = new Response();
		try{
			request.setURI(new URI(url));
			
			//UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            final CountDownLatch latch = new CountDownLatch(1);
			//request.setEntity(formEntity);
			
			httpClient.execute(request, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response) {
                	
                	HttpEntity responseEntity = response.getEntity();
                	if (responseEntity != null){
                	  try {
            		  	String content = EntityUtils.toString(responseEntity);
            		  	crawlResponse.setContent(content);
            		  	crawlResponse.setStatusCode(response.getStatusLine().getStatusCode());
            		  	latch.countDown();
            		  	
                	  } catch (Exception ex){
                		  latch.countDown();
                		System.out.println("Completed Exception e: " + ex.getMessage());
                	  }
                	}
                }

                public void failed(final Exception ex) {
                	latch.countDown();
                	System.out.println("Failed Exception e: " + ex.getMessage());
                	ex.printStackTrace();
                	crawlResponse.setException(ex);
                	
                }

                public void cancelled() {
                	latch.countDown();
                	System.out.println("Cancelled");
                }

            });
			
			latch.await();
		}catch (Exception e){
			System.out.println ("Exception: " + e);
			crawlResponse.setException(e);
		}finally {
			//this.httpClient.close();
		}
		
		return crawlResponse;		
		
	}
	
	public Response submitForm (String partialUrl, List<NameValuePair> formParams) throws Exception{
		HttpPost request = new HttpPost();
		final Response crawlResponse = new Response();
		try{
			request.setURI(new URI(partialUrl));
			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            final CountDownLatch latch = new CountDownLatch(1);
			request.setEntity(formEntity);
			
			httpClient.execute(request, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response) {
                	
                	HttpEntity responseEntity = response.getEntity();
                	if (responseEntity != null){
                	  try {
            		  	String content = EntityUtils.toString(responseEntity);
            		  	crawlResponse.setContent(content);
            		  	crawlResponse.setStatusCode(response.getStatusLine().getStatusCode());
            		  	latch.countDown();
            		  	
                	  } catch (Exception ex){
                		  latch.countDown();
                		System.out.println("Completed Exception e: " + ex.getMessage());
                	  }
                	}
                }

                public void failed(final Exception ex) {
                	latch.countDown();
                	System.out.println("Failed Exception e: " + ex.getMessage());
                	ex.printStackTrace();
                	crawlResponse.setException(ex);
                	
                }

                public void cancelled() {
                	latch.countDown();
                	System.out.println("Cancelled");
                }

            });
			
			latch.await();
		}catch (Exception e){
			System.out.println ("Exception: " + e);
			crawlResponse.setException(e);
		}finally {
			//this.httpClient.close();
		}
		
		return crawlResponse;
	}
	
	public void finalize() throws IOException {
		this.httpClient.close();
	}
	
	
}
