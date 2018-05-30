package intelmas.app.kpibe.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
// @EnableElasticsearchRepositories(basePackages = { "intelmas.app.importer.repository.elastic" })
public class ElasticSearchConfig {
	
	@Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;

    public String getCurrentDate(){
    	return LocalDate.now().toString();
    }
    
    @Bean(destroyMethod="close")
    public RestHighLevelClient client() throws Exception {
    	
    	String[] hosts = StringUtils.split(EsHost, ",");
    	HttpHost[] httpHosts = Arrays.stream(hosts).map( host -> {
    		return new HttpHost(host, EsPort, "http");
    	})
    	.toArray(HttpHost[]::new);

    	RestHighLevelClient client = new RestHighLevelClient(
    	        RestClient.builder(httpHosts)
    	        .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
	    	            @Override
	    	            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
	    	                return requestConfigBuilder.setConnectTimeout(500000)
	    	                        .setSocketTimeout(500000);
	    	            }
    	        	}).setMaxRetryTimeoutMillis(500000)
    	        );
    	
    	return client;
    }

    /*
    @Bean
    public Client client() throws Exception {

        Settings esSettings = Settings.settingsBuilder()
                .put("cluster.name", EsClusterName)
                .build();
        
        String[] hosts = StringUtils.split(EsHost, ",");
        TransportClient transportClient = TransportClient.builder()
        									.settings(esSettings)
        									.build();
        
        for(String host: hosts){
        	transportClient.addTransportAddress(
        			new InetSocketTransportAddress(InetAddress.getByName(host), EsPort));
        }
        
        return transportClient;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }
    */

}
