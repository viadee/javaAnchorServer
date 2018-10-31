package me.kroeker.alex.anchor.jserver.anchor.h2o;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import hex.ModelCategory;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class H2OTabularClassificationMojoClassifier<T extends TabularInstance> extends H2OTabularMojoClassifier<T> {

    public H2OTabularClassificationMojoClassifier(InputStream mojoInputStream) throws IOException {
        super(mojoInputStream);
    }

    public H2OTabularClassificationMojoClassifier(InputStream mojoInputStream, List<String> columnNames) throws IOException {
        super(mojoInputStream, columnNames);
    }

}
