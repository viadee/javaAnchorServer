package me.kroeker.alex.anchor.jsever.h2o.util;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oFrameDownload;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oMojoDownload;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 */
@ExtendWith(MockitoExtension.class)
public class H2oDownloadTest {

    @Mock
    H2oApi api;

    @Mock
    Response<ResponseBody> response;

    @Mock
    ResponseBody body;

    @BeforeEach
    public void setUp() throws IOException {
        when(response.isSuccessful()).thenReturn(true);
        when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream("/" + Resources.SIMPLE_CSV_FILE_STRING)
        );
        when(response.body()).thenReturn(body);
        when(api._downloadDataset_fetch(any())).thenReturn(response);
    }

    @Test
    public void testLoadCsvAndRemove() throws IOException {
        File csvFile;
        try (H2oFrameDownload h2oFrame = new H2oFrameDownload()) {
            csvFile = h2oFrame.getFile(api, "frame-key");
            assertEquals(460L, csvFile.length());
        }
        assertFalse(csvFile.exists());
    }

    @Test
    public void testLoadMojoAndRemove() throws IOException {
        File csvFile;
        // TODO test with dummy MOJO file
        try (H2oMojoDownload h2oFrame = new H2oMojoDownload()) {
            csvFile = h2oFrame.getFile(api, "frame-key");
            assertEquals(460L, csvFile.length());
        }
        assertFalse(csvFile.exists());
    }

}
