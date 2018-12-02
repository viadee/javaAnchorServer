package de.viadee.anchorj.server.h2o.util;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.test.resources.Resources;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelKeyV3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class H2oDownloadTest {

    @Mock
    private H2oApi api;

    @Mock
    private Response<ResponseBody> response;

    @BeforeEach
    void setUp() {
        ResponseBody body = mock(ResponseBody.class);
        when(response.isSuccessful()).thenReturn(true);
        when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream(Resources.SIMPLE_CSV_FILE_STRING)
        );
        when(response.body()).thenReturn(body);
    }

    @Test
    void testLoadCsvAndRemove() throws IOException {
        when(api._downloadDataset_fetch(any())).thenReturn(response);

        File csvFile;
        try (H2oFrameDownload h2oFrame = new H2oFrameDownload()) {
            csvFile = h2oFrame.getFile(api, "frame-key");
            assertEquals(460L, csvFile.length());
        }
        assertFalse(csvFile.exists());
    }

    @Test
    void testLoadMojoAndRemove() throws IOException {
        when(api.modelMojo(any(ModelKeyV3.class))).thenReturn(response);

        File csvFile;
        // TODO test with dummy MOJO file
        try (H2oMojoDownload h2oMojo = new H2oMojoDownload()) {
            csvFile = h2oMojo.getFile(api, "frame-key");
            assertEquals(460L, csvFile.length());
        }
        assertFalse(csvFile.exists());
    }

}
