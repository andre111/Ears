package com.unascribed.ears.common.render;

import com.unascribed.ears.common.image.WritableEarsImage;

public record EarsSkinImages<T extends WritableEarsImage>(T skin, T wing) {
}
