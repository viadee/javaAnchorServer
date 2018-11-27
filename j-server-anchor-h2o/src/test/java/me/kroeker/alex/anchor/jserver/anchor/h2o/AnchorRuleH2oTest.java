package me.kroeker.alex.anchor.jserver.anchor.h2o;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({H2oUtil.class})
@Ignore
public class AnchorRuleH2oTest {

    @Mock
    private H2oApi api;

    @Mock
    private Response<ResponseBody> response;

    @Mock
    private ModelBO model;

    @Mock
    private FrameBO frame;

    @InjectMocks
    private AnchorRuleH2o anchor;

    @Before
    public void setUp() {
        ResponseBody body = mock(ResponseBody.class);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        Mockito.when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream("/" + Resources.AIRLINE_CSV)
        );
        Mockito.when(response.body()).thenReturn(body);

        when(anchor.createH2o(any())).thenReturn(api);
    }

    @Test
    public void testComputeRule() throws DataAccessException {
        FrameInstance instance = new FrameInstance(Resources.AIRLINE_FEATURE_MAPPING, Resources.SIMPLE_AIRLINE_INSTANCE);
        anchor.computeRule("connName", "model-Id", "frame-Id", instance, null);
    }
}
