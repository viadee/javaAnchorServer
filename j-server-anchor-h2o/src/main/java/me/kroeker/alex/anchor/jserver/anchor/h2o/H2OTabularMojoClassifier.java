package me.kroeker.alex.anchor.jserver.anchor.h2o;

import de.goerke.tobias.anchorj.base.ClassificationFunction;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import hex.genmodel.ModelMojoReader;
import hex.genmodel.MojoModel;
import hex.genmodel.MojoReaderBackend;
import hex.genmodel.MojoReaderBackendFactory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class H2OTabularMojoClassifier<T extends TabularInstance> implements ClassificationFunction<T> {


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
    public int predict(T instance) {
//        if (columnNames.size() != instance.getFeatureCount())
//            throw new IllegalArgumentException("ColumnNames size does not match instance's feature count");

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
            int predictionValue = -1;
            AbstractPrediction prediction = this.getModelWrapper().predict(row);
            if (prediction instanceof RegressionModelPrediction) {
                predictionValue = (int) ((RegressionModelPrediction) prediction).value;
            } else if (prediction instanceof BinomialModelPrediction) {
                predictionValue = ((BinomialModelPrediction) prediction).labelIndex;
            } else if (prediction instanceof MultinomialModelPrediction) {
                predictionValue = ((MultinomialModelPrediction) prediction).labelIndex;
            }

            return predictionValue;
        } catch (PredictException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int[] predict(T[] instances) {
        return ClassificationFunction.super.predict(instances);
    }

    public EasyPredictModelWrapper getModelWrapper() {
        return modelWrapper;
    }

}
