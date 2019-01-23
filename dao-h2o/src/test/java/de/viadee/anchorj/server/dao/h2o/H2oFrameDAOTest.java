package de.viadee.anchorj.server.dao.h2o;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.configuration.AppConfiguration;
import de.viadee.anchorj.server.model.CategoricalColumnSummary;
import de.viadee.anchorj.server.model.FrameSummary;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;
import water.bindings.pojos.ColV3;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.FrameV3;
import water.bindings.pojos.FramesV3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class H2oFrameDAOTest {

    @Mock
    private H2oApi api;

    @Mock
    private Response<ResponseBody> response;

    @BeforeEach
    void setUp() {
        ResponseBody body = mock(ResponseBody.class);
        when(response.isSuccessful()).thenReturn(true);
        when(body.byteStream()).thenReturn(new ByteArrayInputStream("A,B,C\n1,1,1\n2,2,2".getBytes())
        );
        when(response.body()).thenReturn(body);
    }

    @Test
    void testGetFrameSummary() throws IOException {
        H2oFrameDAO frameDAO = new MockFrameDAO();
        when(api._downloadDataset_fetch(any())).thenReturn(response);

        FramesV3 framesV3 = new FramesV3();
        FrameV3 h2oFrame = new FrameV3();
        framesV3.frames = new FrameV3[] {h2oFrame};
        when(api.frameSummary(any(FrameKeyV3.class))).thenReturn(framesV3);
        FrameKeyV3 h2oFrameKey = new FrameKeyV3();
        h2oFrameKey.name = "frameId";
        h2oFrame.frameId = h2oFrameKey;
        h2oFrame.rows = 2;

        ColV3[] h2oCols = new ColV3[3];
        ColV3 col1 = new ColV3();
        col1.label = "A";
        col1.type = "enum";
        col1.data = new double[] { 1, 2 };
        col1.histogramBins = new long[] { 1, 1 };
        col1.domain = new String[] { "1", "2" };

        ColV3 col2 = new ColV3();
        col2.label = "B";
        col2.type = "string";
        col2.stringData = new String[] { "1", "2" };
        col2.histogramBins = new long[] { 1, 1 };
        col2.domain = new String[] { "1", "2" };

        ColV3 col3 = new ColV3();
        col3.label = "C";
        col3.type = "metric";
        col3.data = new double[] { 1.0, 2.0 };
        col3.mean = 1.5;
        col3.mins = new double[] {1.0};
        col3.maxs = new double[] {2.0};

        h2oCols[0] = col1;
        h2oCols[1] = col2;
        h2oCols[2] = col3;
        h2oFrame.columns = h2oCols;

        FrameSummary summary = frameDAO.getFrameSummary("", "frameId");
        assertNotNull(summary);
        assertEquals("frameId", summary.getFrame_id());
        assertEquals(2, summary.getRow_count());
        CategoricalColumnSummary<Serializable> catSum = (CategoricalColumnSummary) summary.getColumn_summary_list().iterator().next();
        assertEquals("1", catSum.getCategories().get(0).getName());
        assertEquals(0.5, catSum.getCategories().get(0).getFreq());
        assertEquals("2", catSum.getCategories().get(1).getName());
        assertEquals(0.5, catSum.getCategories().get(1).getFreq());
        assertEquals(2, catSum.getUnique());
        Iterator it = catSum.getData().iterator();
        assertEquals(1.0, it.next());
        assertEquals(2.0, it.next());
    }

    private class MockFrameDAO extends H2oFrameDAO {
        private MockFrameDAO() {
            super(null);
        }

        @Override
        public H2oApi createH2o(AppConfiguration configuration, String connectionName) {
            return api;
        }
    }

}
