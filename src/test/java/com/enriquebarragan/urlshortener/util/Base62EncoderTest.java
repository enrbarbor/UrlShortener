package com.enriquebarragan.urlshortener.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

    @Test
    void encode_shouldConvertNumberToShortCode() {
        assertThat(Base62Encoder.encode(0)).isEqualTo("0");
        assertThat(Base62Encoder.encode(61)).isEqualTo("z");
        assertThat(Base62Encoder.encode(62)).isEqualTo("10");
    }

    @Test
    void decode_shouldConvertShortCodeToNumber() {
        assertThat(Base62Encoder.decode("0")).isEqualTo(0);
        assertThat(Base62Encoder.decode("z")).isEqualTo(61);
        assertThat(Base62Encoder.decode("10")).isEqualTo(62);
    }

    @Test
    void encodeAndDecode_shouldBeInverses() {
        long original = 123456789;
        String encoded = Base62Encoder.encode(original);
        long decoded = Base62Encoder.decode(encoded);

        assertThat(decoded).isEqualTo(original);
    }
}