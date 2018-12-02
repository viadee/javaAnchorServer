package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.test.resources.Resources;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class AnchorRuleH2oTest {

    @Mock
    private H2oApi api;

    @Mock
    private FrameBO frameBO;

    @Mock
    private ModelBO modelBO;

    @Mock
    private Response<ResponseBody> frameResponse;

    @InjectMocks
    private AnchorRuleH2o anchor;

    @Test
    @Disabled
    void testComputeRule() throws IOException {
        anchor = spy(anchor);
        when(anchor.createH2o(any())).thenReturn(api);

        ResponseBody body = mock(ResponseBody.class);
        when(frameResponse.isSuccessful()).thenReturn(true);
        when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream(Resources.AIRLINE_CSV)
        );
        when(frameResponse.body()).thenReturn(body);
        when(api._downloadDataset_fetch(any())).thenReturn(frameResponse);

        FrameSummary frameSummary = new FrameSummary();
        frameSummary.setFrame_id("frame_id");
        frameSummary.setRow_count(6);
        Collection<ColumnSummary<?>> columnSummaries = new ArrayList<>();

        when(frameBO.getFrameSummary(any(), any())).thenReturn(null);

        FrameInstance instance = new FrameInstance(Resources.AIRLINE_FEATURE_MAPPING, Resources.SIMPLE_AIRLINE_INSTANCE);
        anchor.computeRule("", "", "", instance, null);
    }
}
