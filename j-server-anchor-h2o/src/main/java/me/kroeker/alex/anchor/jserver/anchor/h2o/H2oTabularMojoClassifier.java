package me.kroeker.alex.anchor.jserver.anchor.h2o;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class H2oTabularMojoClassifier implements ClassificationFunction<TabularInstance> {

    private final EasyPredictModelWrapper modelWrapper;
    private final List<String> columnNames;
    private final Function<AbstractPrediction, Integer> predictionDiscretizer;

    public H2oTabularMojoClassifier(InputStream mojoInputStream, Function<AbstractPrediction, Integer> predictionDiscretizer) throws IOException {
        this(mojoInputStream, predictionDiscretizer, null);
    }

    public H2oTabularMojoClassifier(InputStream mojoInputStream, Function<AbstractPrediction, Integer> predictionDiscretizer, List<String> columnNames) throws IOException {
        this.predictionDiscretizer = predictionDiscretizer;

        final MojoReaderBackend reader = MojoReaderBackendFactory.createReaderBackend(mojoInputStream,
                MojoReaderBackendFactory.CachingStrategy.MEMORY);
        final MojoModel model = ModelMojoReader.readFrom(reader);
        this.modelWrapper = new EasyPredictModelWrapper(model);
        if (columnNames == null) {
            columnNames = Arrays.asList(model.getNames());
        }
        this.columnNames = Collections.unmodifiableList(columnNames);
    }

    @Override
    public int predict(TabularInstance instance) {
        RowData row = new RowData();
        int i = 0;
        for (String columnName : columnNames) {
            Object value = instance.getInstance()[i++];
            if (value instanceof Integer) {
                value = ((Integer) value).doubleValue();
            }
            row.put(columnName, value);
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

}
