package intelmas.app.kpibe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@EnableCassandraRepositories(basePackages = { "intelmas.app.importer.repository.cassandra" })
public class CassandraConfig {

	@Autowired
	private Environment env;

	@Bean
	public CassandraClusterFactoryBean cluster() {
		PoolingOptions poolingOptions = new PoolingOptions();
		poolingOptions
	    .setCoreConnectionsPerHost(HostDistance.LOCAL,  4)
	    .setMaxConnectionsPerHost( HostDistance.LOCAL, 10)
	    .setCoreConnectionsPerHost(HostDistance.REMOTE, 2)
	    .setMaxConnectionsPerHost( HostDistance.REMOTE, 4)
		.setMaxRequestsPerConnection(HostDistance.LOCAL, 8768)
	    .setMaxRequestsPerConnection(HostDistance.REMOTE, 500);
		
		CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
		cluster.setContactPoints(env.getProperty("cassandra.contactpoints"));
		cluster.setPort(Integer.parseInt(env.getProperty("cassandra.port")));
		cluster.setPoolingOptions(poolingOptions);
		return cluster;
	}
	
	@Bean
    public CassandraMappingContext cassandraMapping() 
      throws ClassNotFoundException {
		BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext(); 
		mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), env.getProperty("cassandra.keyspace"))); 
        return mappingContext;
    }

	@Bean
    public CassandraConverter converter() throws ClassNotFoundException {
        return new MappingCassandraConverter(cassandraMapping());
    }

	@Bean
	public CassandraSessionFactoryBean session() throws Exception {

		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(cluster().getObject());
		session.setKeyspaceName(env.getProperty("cassandra.keyspace"));
		session.setConverter(converter());
		session.setSchemaAction(SchemaAction.NONE);

		return session;
	}

	@Bean
	public CassandraOperations cassandraTemplate() throws Exception {
		return new CassandraTemplate(session().getObject());
	}
}
