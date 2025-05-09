package com.money.tiger.dao.config;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.mongo.types.DatabaseDir;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import de.flapdoodle.reverse.transitions.Start;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                    Version version = Version.V8_0_0;
                    Path path = Paths.get("mongo-data-" + version.toString().replace(".", "")
                            .replace("Version{", "").replace("}", ""));
                    File file = new File(path.toUri());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    System.out.println(file.getAbsolutePath());

                    Transitions transitions = Mongod.instance()
                            .withNet(Start.to(Net.class).initializedWith(Net.defaults().withPort(port)))
                            .withDatabaseDir(Start.to(DatabaseDir.class).initializedWith(DatabaseDir.of(path)))
                            .transitions(version);
                    TransitionWalker.ReachedState<RunningMongodProcess> executable = transitions.walker()
                            .initState(StateID.of(RunningMongodProcess.class));


                    //old
//                    IMongodConfig config = new MongodConfigBuilder()
//                            .version(Version.Main.V4_0)
//                            .net(new Net("localhost", port == null ? 27018 : port, Network.localhostIsIPv6()))
//                            .replication(new Storage("mongo-data", null, 0))
//                            .build();
//                    MongodExecutable executable = MongodStarter.getDefaultInstance().prepare(config);
//                    executable.start();
                    beanFactory.registerSingleton("embeddedMongoServer", executable);
                } catch (Exception e) {
                    throw new BeanCreationException("Failed to start embedded MongoDB", e);
                }
            }
        };
    }
}