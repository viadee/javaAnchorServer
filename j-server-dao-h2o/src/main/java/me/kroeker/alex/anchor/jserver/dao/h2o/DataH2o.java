package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.kroeker.alex.anchor.jserver.model.CategoricalColumnSummary;
import me.kroeker.alex.anchor.jserver.model.CategoryFreq;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import water.bindings.pojos.*;
import water.bindings.proxies.retrofit.Frames;

/**
 * @author ak902764
 */
@Component
public class DataH2o extends BaseH2oAccess implements DataDAO {

    @Override
    public Map<String, Collection<String>> caseSelectConditions(String h2oServer, String modelId, String frameId)
            throws DataAccessException {
        return null;
    }

    @Override
    public FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException {
        FrameKeyV3 frameKey = new FrameKeyV3();
        frameKey.name = frameId;
        try {
            // TODO frame summary causes MalformedJsonException due to "NaN" value for value mean when column type
            // TODO is enum or string
            FrameV3 h2oFrame = this.loadFrameSummary(H2O_SERVER.get(connectionName), frameId);
            FrameSummary frame = new FrameSummary();

            frame.setFrame_id(h2oFrame.frameId.name);
            int rowCount = h2oFrame.rowCount;
            frame.setRow_count(rowCount);

            for (ColV3 h2oCol : h2oFrame.columns) {
                String columnType = h2oCol.type;
                double[] columnData = h2oCol.data;
                ColumnSummary column;

                if ("enum".equals(columnType)) {
                    column = new CategoricalColumnSummary();
                    long[] histogramBins = h2oCol.histogramBins;
                    String[] domain = h2oCol.domain;

                    int domainLength = domain.length;
                    int domainMaxItems = 20;
                    double freqCount = 0;
                    List<CategoryFreq> categories = new ArrayList<>(domainLength);
                    Map<String, Double> domainFreq = new HashMap<>();
                    for (int i = 0; i < domainLength; i++) {
                        categories.add(new CategoryFreq(domain[i], histogramBins[i] / (double) rowCount));
                    }
                    categories.sort((a, b) -> (int) (a.getFreq() - b.getFreq()));

                    ((CategoricalColumnSummary) column).setCategories(categories);
                    ((CategoricalColumnSummary) column).setUnique(categories.size());
                } else if ("string".equals(columnType) || "uuid".equals(columnType)) {
                    column = new CategoricalColumnSummary();
                } else {

                }

                column.setColumn_type(columnType);
            }

            return frame;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }
    }

    private FrameV3 loadFrameSummary(String url, String frameId) throws IOException {
        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .enableComplexMapKeySerialization()
                .setLenient()
                .create();

        int timeout_s = 60;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout_s, TimeUnit.SECONDS)
                .writeTimeout(timeout_s, TimeUnit.SECONDS)
                .readTimeout(timeout_s, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FramesV3 frame = retrofit.create(Frames.class).columns(frameId).execute().body();
        return frame.frames[0];
    }

}
