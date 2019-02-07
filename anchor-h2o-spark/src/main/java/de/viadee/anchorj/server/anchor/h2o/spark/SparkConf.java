package de.viadee.anchorj.server.anchor.h2o.spark;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.configuration.AppConfiguration;

/**
 *
 */
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class SparkConf {

    private static final Logger LOG = LoggerFactory.getLogger(SparkConf.class);

    private org.apache.spark.SparkConf sparkConf;

    private JavaSparkContext sparkContext;

    private AppConfiguration configuration;

    public SparkConf(@Autowired AppConfiguration appConfiguration) {
        this.configuration = appConfiguration;
    }

    /**
     * Call only from synchronized methods.
     *
     * @return the Spark configuration
     * @throws DataAccessException if connection with spark fails
     */
    private org.apache.spark.SparkConf getSparkConf() throws DataAccessException {
        LOG.info("Creating Spark Configuration");
        if (this.sparkConf == null) {
            try (Stream<Path> libs = Files.list(Paths.get(this.configuration.getSparkLibFolder()))) {
                this.sparkConf = new org.apache.spark.SparkConf().setAppName("javaAnchorServer")
                        .setMaster(this.configuration.getSparkMasterUrl())
                        .set("spark.shuffle.service.enabled", "false")
                        .set("spark.dynamicAllocation.enabled", "false")
                        .setJars(libs.map(Path::toFile)
                                .map(File::getAbsolutePath).toArray(String[]::new))
                        .set("spark.rdd.compress", "true");
            } catch (IOException e) {
                throw new DataAccessException("Failed to connect to the Spark Master: "
                        + this.configuration.getSparkMasterUrl(), e);
            }
        }
        return this.sparkConf;
    }

    public JavaSparkContext getSparkContext() throws DataAccessException {
        if (this.sparkContext == null) {
            this.sparkContext = new JavaSparkContext(this.getSparkConf());
        }
        return this.sparkContext;
    }

    @PreDestroy
    public void preDestroy() {
        if (this.sparkContext != null) {
            LOG.info("Closing Spark Context");
            this.sparkContext.close();
        }
    }

}
