package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.viadee.anchorj.ClassificationFunction;
import de.viadee.anchorj.tabular.TabularInstance;
import hex.genmodel.ModelMojoReader;
import hex.genmodel.MojoModel;
import hex.genmodel.MojoReaderBackend;
import hex.genmodel.MojoReaderBackendFactory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.AbstractPrediction;
import me.kroeker.alex.anchor.jserver.anchor.PredictException;

public class H2oTabularMojoClassifier implements ClassificationFunction<TabularInstance> {
    private static final long serialVersionUID = -7682877358184283439L;

    private static final Logger LOG = LoggerFactory.getLogger(H2oTabularMojoClassifier.class);

    private final EasyPredictModelWrapper modelWrapper;
    private final List<String> columnNames;
    private final SerializableFunction predictionDiscretizer;

    public H2oTabularMojoClassifier(
            InputStream mojoInputStream,
            SerializableFunction predictionDiscretizer,
            List<String> columnNames) throws IOException {

        this.predictionDiscretizer = predictionDiscretizer;

        final MojoReaderBackend reader = MojoReaderBackendFactory.createReaderBackend(mojoInputStream,
                MojoReaderBackendFactory.CachingStrategy.MEMORY);
        final MojoModel model = ModelMojoReader.readFrom(reader);

        this.modelWrapper = new EasyPredictModelWrapper(model);
        this.columnNames = Collections.unmodifiableList(columnNames);
    }

    @Override
    public int predict(final TabularInstance instance) {
        Object[] instanceValues;
        if (instance.getOriginalInstance() != null) {
            instanceValues = instance.getOriginalInstance();
        } else {
            LOG.warn("Trying to predict with h2o model and the discretized " +
                    "instance values since the original instance is null");
            instanceValues = instance.getInstance();
        }

        RowData row = new RowData();
        int i = 0;
        for (String columnName : columnNames) {
            Object value = instanceValues[i];
            row.put(columnName, value);
            i++;
        }

        try {
            AbstractPrediction prediction = this.getModelWrapper().predict(row);
            return predictionDiscretizer.apply(prediction);
        } catch (hex.genmodel.easy.exception.PredictException e) {
            throw new PredictException(e);
        }
    }

    public EasyPredictModelWrapper getModelWrapper() {
        return modelWrapper;
    }

    public interface SerializableFunction extends Function<AbstractPrediction, Integer>, Serializable {
    }

}
