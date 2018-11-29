package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private static final Logger LOG = LoggerFactory.getLogger(H2oTabularMojoClassifier.class);

    private final EasyPredictModelWrapper modelWrapper;
    private final List<String> columnNames;
    private final Function<AbstractPrediction, Integer> predictionDiscretizer;
    private final Collection<String> indexOfCategoricalColumns;
    private final Map<TabularInstance, Integer> predictionCache;

    public H2oTabularMojoClassifier(InputStream mojoInputStream, Function<AbstractPrediction, Integer> predictionDiscretizer, List<String> columnNames, Collection<String> indexOfCategoricalValues, int dataListSize) throws IOException {
        this.predictionDiscretizer = predictionDiscretizer;
        this.indexOfCategoricalColumns = indexOfCategoricalValues;

        final MojoReaderBackend reader = MojoReaderBackendFactory.createReaderBackend(mojoInputStream,
                MojoReaderBackendFactory.CachingStrategy.MEMORY);
        final MojoModel model = ModelMojoReader.readFrom(reader);

        this.modelWrapper = new EasyPredictModelWrapper(model);
        this.columnNames = Collections.unmodifiableList(columnNames);
        this.predictionCache = Collections.synchronizedMap(new FixedSizeCache<>(10_000, dataListSize));
    }

    @Override
    public int predict(final TabularInstance instance) {
        if (this.predictionCache != null && this.predictionCache.containsKey(instance)) {
            return this.predictionCache.get(instance);
        } else {
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
                Object value = instanceValues[i++];
                if (indexOfCategoricalColumns != null && indexOfCategoricalColumns.contains(columnName)) {
                    value = String.valueOf(value);
                } else if (value instanceof Integer) {
                    value = ((Integer) value).doubleValue();
                }
                row.put(columnName, value);
            }

            try {
                AbstractPrediction prediction = this.getModelWrapper().predict(row);
                Integer predictionValue = predictionDiscretizer.apply(prediction);
                this.predictionCache.put(instance, predictionValue);

                return predictionValue;
            } catch (hex.genmodel.easy.exception.PredictException e) {
                throw new PredictException(e);
            }
        }
    }

    public EasyPredictModelWrapper getModelWrapper() {
        return modelWrapper;
    }

    private class FixedSizeCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        public FixedSizeCache(final int maxSize, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            this.maxSize = maxSize;
        }

        public FixedSizeCache(final int maxSize, int initialCapacity) {
            super(initialCapacity);
            this.maxSize = maxSize;
        }

        public FixedSizeCache(final int maxSize) {
            super();
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > this.maxSize;
        }
    }

}
