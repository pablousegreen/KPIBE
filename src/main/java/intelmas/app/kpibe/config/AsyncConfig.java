package intelmas.app.kpibe.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@EnableAsync
public class AsyncConfig {
	
	@Value("${executor.corePoolSize}")
	private int executorCorePoolSize;
	
	@Value("${executor.maxPoolSize}")
	private int executorMaxPoolSize;
	
	@Value("${executor.queueCapacity}")
	private int executorQueueCapacity;
	
	@Value("${executor.threadNamePrefix}")
	private String threadNamePrefix;
	
	@Bean(name = "taskExecutor")
    public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorCorePoolSize);
        executor.setMaxPoolSize(executorMaxPoolSize);
        executor.setQueueCapacity(executorQueueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
