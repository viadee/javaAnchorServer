package me.kroeker.alex.anchor.jserver.anchor.h2o;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
public class AnchorPredicateH2OTest {

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

    @BeforeEach
    public void setUp() {
        ResponseBody body = mock(ResponseBody.class);
        when(response.isSuccessful()).thenReturn(true);
        when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream("/" + Resources.AIRLINE_CSV)
        );
        when(response.body()).thenReturn(body);

        when(anchor.createH2o(any())).thenReturn(api);
    }

    @Test
    public void testComputeRule() throws DataAccessException {
        FrameInstance instance = new FrameInstance(Resources.AIRLINE_FEATURE_MAPPING, Resources.SIMPLE_AIRLINE_INSTANCE);
        anchor.computeRule("connName", "model-Id", "frame-Id", instance, null);
    }
}
