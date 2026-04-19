package jp.i432kg.footprint.infrastructure.storage.repository;

import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class StorageRepositoryTestSupport {

    private StorageRepositoryTestSupport() {
    }

    static byte[] jpegBytes(final int width, final int height) throws IOException {
        return imageBytes("jpg", width, height);
    }

    static byte[] pngBytes(final int width, final int height) throws IOException {
        return imageBytes("png", width, height);
    }

    static Metadata metadataWithExifAndGps(
            final BigDecimal latitude,
            final BigDecimal longitude,
            final LocalDateTime takenAt
    ) {
        final Metadata metadata = mock(Metadata.class);
        final GpsDirectory gpsDirectory = mock(GpsDirectory.class);
        final ExifSubIFDDirectory exifSubIFDDirectory = mock(ExifSubIFDDirectory.class);

        when(metadata.getFirstDirectoryOfType(GpsDirectory.class)).thenReturn(gpsDirectory);
        when(gpsDirectory.getGeoLocation()).thenReturn(
                new GeoLocation(latitude.doubleValue(), longitude.doubleValue())
        );
        when(metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)).thenReturn(exifSubIFDDirectory);
        when(exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
                .thenReturn(Date.from(takenAt.atZone(ZoneId.systemDefault()).toInstant()));
        when(metadata.containsDirectoryOfType(ExifIFD0Directory.class)).thenReturn(true);

        return metadata;
    }

    static Metadata metadataWithoutExif() {
        final Metadata metadata = mock(Metadata.class);
        when(metadata.getFirstDirectoryOfType(GpsDirectory.class)).thenReturn(null);
        when(metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)).thenReturn(null);
        when(metadata.containsDirectoryOfType(ExifIFD0Directory.class)).thenReturn(false);
        return metadata;
    }

    static ResponseInputStream<GetObjectResponse> responseInputStream(final byte[] bytes) {
        return new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(bytes));
    }

    private static byte[] imageBytes(final String format, final int width, final int height) throws IOException {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, new Color((x * 40) % 255, (y * 40) % 255, 120).getRGB());
            }
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, format, outputStream);
            return outputStream.toByteArray();
        }
    }
}
