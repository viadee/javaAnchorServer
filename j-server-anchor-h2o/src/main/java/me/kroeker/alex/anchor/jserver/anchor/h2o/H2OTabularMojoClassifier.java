package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.goerke.tobias.anchorj.ClassificationFunction;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import hex.genmodel.ModelMojoReader;
import hex.genmodel.MojoModel;
import hex.genmodel.MojoReaderBackend;
import hex.genmodel.MojoReaderBackendFactory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;
import me.kroeker.alex.anchor.jserver.anchor.PredictException;

public class H2OTabularMojoClassifier implements ClassificationFunction<TabularInstance> {

    private final EasyPredictModelWrapper modelWrapper;
    private final List<String> columnNames;

    public H2OTabularMojoClassifier(InputStream mojoInputStream) throws IOException {
        this(mojoInputStream, null);
    }

    public H2OTabularMojoClassifier(InputStream mojoInputStream, List<String> columnNames) throws IOException {
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
            int predictionValue;
            AbstractPrediction prediction = this.getModelWrapper().predict(row);
            if (prediction instanceof RegressionModelPrediction) {
                predictionValue = (int) ((RegressionModelPrediction) prediction).value;
            } else if (prediction instanceof BinomialModelPrediction) {
                predictionValue = ((BinomialModelPrediction) prediction).labelIndex;
            } else if (prediction instanceof MultinomialModelPrediction) {
                predictionValue = ((MultinomialModelPrediction) prediction).labelIndex;
            } else {
                throw new UnsupportedOperationException("Prediction of type: " + prediction.getClass().getSimpleName()
                        + "; not supported");
            }

            return predictionValue;
        } catch (hex.genmodel.easy.exception.PredictException e) {
            throw new PredictException(e);
        }
    }

    public EasyPredictModelWrapper getModelWrapper() {
        return modelWrapper;
    }

}
