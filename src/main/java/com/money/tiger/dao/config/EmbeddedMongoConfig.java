package com.money.tiger.dao.config;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EmbeddedMongoConfig {

    @Bean
    public BeanDefinitionRegistryPostProcessor forceMongoStartup() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                try {
                    Environment env = beanFactory.getBean(Environment.class);
                    Integer port = env.getProperty("spring.data.mongodb.port", Integer.class);
                    IMongodConfig config = new MongodConfigBuilder()
                            .version(Version.Main.V4_0)
                            .net(new Net("localhost", port == null ? 27018 : port, Network.localhostIsIPv6()))
                            .replication(new Storage("mongo-data", null, 0))
                            .build();
                    MongodExecutable executable = MongodStarter.getDefaultInstance().prepare(config);
                    executable.start();
                    beanFactory.registerSingleton("embeddedMongoServer", executable);
                } catch (Exception e) {
                    throw new BeanCreationException("Failed to start embedded MongoDB", e);
                }
            }
        };
    }
}