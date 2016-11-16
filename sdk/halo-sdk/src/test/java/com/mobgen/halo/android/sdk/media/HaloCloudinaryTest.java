package com.mobgen.halo.android.sdk.media;

import android.util.Pair;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;

public class HaloCloudinaryTest extends HaloRobolectricTest {

    private static final String CLOUD_URL = "https://res.cloudinary.com/mobgen-adrianlopez/image/upload/v1456995248/eolwrkhwkxnve5r2uknt.jpg";

    private HaloCloudinary.Builder mBuilder;

    @Before
    public void initialize() {
        reloadBuilder();
    }

    private void reloadBuilder() {
        mBuilder = HaloCloudinary.builder();
    }

    @Test
    public void thatNotCloudinaryUrl() {
        String url = "http://google.com";
        //Url from outside cloudinary
        assertThat(mBuilder.build(url).url()).isEqualTo(url);
        //Cloud transformed
        assertThat(mBuilder.aspectRatio(2).build(url).url()).isEqualTo(url);
        assertNotEquals(CLOUD_URL, mBuilder.build(CLOUD_URL).url());
        assertThat(mBuilder.build(CLOUD_URL).toString()).isEqualTo(mBuilder.build(CLOUD_URL).url());
    }

    @Test
    public void thatCloudinaryUrlIsInvalid() {
        String invalidUrl = CLOUD_URL.replace("upload", "uplad");
        assertThat(mBuilder.aspectRatio(1).build(invalidUrl).url()).isEqualTo(invalidUrl);
    }

    @Test
    public void thatCloudinaryUrlWithEmptyParams() {
        String invalidUrl = CLOUD_URL.replace("upload", "uplad");
        assertThat(mBuilder.build(invalidUrl).url()).isEqualTo(invalidUrl);
    }

    @Test
    public void thatParcelBuilder() {
        String resultingUrl = mBuilder.aspectRatio(1).build(CLOUD_URL).url();
        String afterParse = TestUtils.testParcel(mBuilder, HaloCloudinary.Builder.CREATOR).build(CLOUD_URL).url();
        assertThat(afterParse).isEqualTo(resultingUrl);
        assertThat(mBuilder.describeContents()).isEqualTo(0);
    }

    @Test
    public void thatNamedTransformation() {
        assertThat(mBuilder.namedTransformation("myTransform").build(CLOUD_URL).url().contains("t_myTransform")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.namedTransformation(null).build(CLOUD_URL).url().contains("t_")).isFalse();
    }

    @Test
    public void thatAddAFlag() {
        assertThat(mBuilder.addFlags(HaloCloudinary.IMAGE_FLAG_ATTACHMENT).build(CLOUD_URL).url().contains("fl_" + HaloCloudinary.IMAGE_FLAG_ATTACHMENT)).isTrue();
        reloadBuilder();
        assertThat(mBuilder.addFlags().build(CLOUD_URL).url().contains("fl_")).isFalse();
    }

    @Test
    public void thatDpiIsOk() {
        assertThat(mBuilder.dpi(27).build(CLOUD_URL).url().contains("dn_" + 27)).isTrue();
    }

    @Test
    public void thatPage() {
        assertThat(mBuilder.page(7).build(CLOUD_URL).url().contains("pg_" + 7)).isTrue();
    }

    @Test
    public void thatDevicePixelRatioIsOk() {
        assertThat(mBuilder.devicePixelRatio(2).build(CLOUD_URL).url().contains("dpr_" + 2)).isTrue();
    }

    @Test
    public void thatColorRgbIsValid() {
        assertThat(mBuilder.colorRGB("#000000").build(CLOUD_URL).url().contains("co_rgb:#000000")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.colorRGB(null).build(CLOUD_URL).url().contains("co_rgb")).isFalse();
    }

    @Test
    public void thatColorNameIsValid() {
        assertThat(mBuilder.color("white").build(CLOUD_URL).url().contains("co_white")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.color(null).build(CLOUD_URL).url().contains("co_")).isFalse();
    }

    @Test
    public void thatDelayAnimation() {
        assertThat(mBuilder.delay(27).build(CLOUD_URL).url().contains("dl_27")).isTrue();
    }

    @Test
    public void thatHaveDefaultImage() {
        assertThat(mBuilder.defaultImage("default").build(CLOUD_URL).url().contains("d_default")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.defaultImage(null).build(CLOUD_URL).url().contains("d_")).isFalse();
    }

    @Test
    public void thatAddAUnderlayImage() {
        assertThat(mBuilder.underlay("default").build(CLOUD_URL).url().contains("u_default")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.underlay(null).build(CLOUD_URL).url().contains("u_")).isFalse();
    }

    @Test
    public void thatAddAOverlay() {
        assertThat(mBuilder.overlay("default").build(CLOUD_URL).url().contains("l_default")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.overlay(null).build(CLOUD_URL).url().contains("l_")).isFalse();
    }

    @Test
    public void thatAddOverlayText() {
        assertThat(mBuilder.overlayText("default").build(CLOUD_URL).url().contains("l_text:default")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.overlayText(null).build(CLOUD_URL).url().contains("l_text")).isFalse();
    }

    @Test
    public void thatBackgroundColorRGBIsValid() {
        assertThat(mBuilder.backgroundColorRGB("#000000").build(CLOUD_URL).url().contains("b_rgb:#000000")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.backgroundColorRGB(null).build(CLOUD_URL).url().contains("b_rgb")).isFalse();
    }

    @Test
    public void thatBackgroundColorIsValid() {
        assertThat(mBuilder.backgroundColor("white").build(CLOUD_URL).url().contains("b_white")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.backgroundColor(null).build(CLOUD_URL).url().contains("b_")).isFalse();
    }

    @Test
    public void thatAddABorder() {
        assertThat(mBuilder.border("dotted").build(CLOUD_URL).url().contains("bo_dotted")).isTrue();
        reloadBuilder();
        assertThat(mBuilder.border(null).build(CLOUD_URL).url().contains("b_")).isFalse();
    }

    @Test
    public void thatApplyEffectFilters() {
        assertThat(mBuilder.addEffects(HaloCloudinary.Effects.advancedRedEye()).build(CLOUD_URL).url().contains(HaloCloudinary.Effects.advancedRedEye().toString())).isTrue();
        reloadBuilder();
        assertThat(mBuilder.addEffects(HaloCloudinary.Effects.advancedRedEye(), null).build(CLOUD_URL).url().contains(HaloCloudinary.Effects.advancedRedEye().toString())).isTrue();
        assertThat(mBuilder.addEffects(HaloCloudinary.Effects.advancedRedEye(), null).build(CLOUD_URL).url().contains("e_null")).isFalse();
        reloadBuilder();
        assertThat(mBuilder.addEffects().build(CLOUD_URL).url().contains("e_")).isFalse();
    }

    @Test
    public void thatCanChangeOpacity() {
        assertThat(mBuilder.opacity(2).build(CLOUD_URL).url().contains("o_2")).isTrue();
    }

    @Test
    public void thatCanRotate() {
        assertThat(mBuilder.rotate(HaloCloudinary.Rotations.autoLeft()).build(CLOUD_URL).url().contains(HaloCloudinary.Rotations.autoLeft().toString())).isTrue();
        reloadBuilder();
        assertThat(mBuilder.rotate(null).build(CLOUD_URL).url().contains("a_")).isFalse();
    }

    @Test
    public void thatCanSetMaxRadius() {
        assertThat(mBuilder.maxRadius().build(CLOUD_URL).url().contains("r_max")).isTrue();
    }

    @Test
    public void thatCanSetRadius() {
        assertThat(mBuilder.radius(2).build(CLOUD_URL).url().contains("r_2")).isTrue();
    }

    @Test
    public void thatControlQualityCompression() {
        assertThat(mBuilder.quality(0.3f).build(CLOUD_URL).url().contains("q_0.3")).isTrue();
    }

    @Test
    public void thatCanSetYPosByPercentage() {
        assertThat(mBuilder.yPos(0.3f).build(CLOUD_URL).url().contains("y_0.3")).isTrue();
    }

    @Test
    public void thatCanSetYPosInPixels() {
        assertThat(mBuilder.yPos(17).build(CLOUD_URL).url().contains("y_17")).isTrue();
    }

    @Test
    public void thatXCanSetPosByPercentage() {
        assertThat(mBuilder.xPos(0.3f).build(CLOUD_URL).url().contains("x_0.3")).isTrue();
    }

    @Test
    public void thatCansetYPosInPixels() {
        assertThat(mBuilder.xPos(17).build(CLOUD_URL).url().contains("x_17")).isTrue();
    }

    @Test
    public void thatCanSetZoomPos() {
        assertThat(mBuilder.zoom(20).build(CLOUD_URL).url().contains("z_20")).isTrue();
    }

    @Test
    public void thatCanSetGravityPositionToCropMode() {
        assertThat(mBuilder.gravity(HaloCloudinary.IMAGE_GRAVITY_ALL_FACES).build(CLOUD_URL).url().contains("g_" + HaloCloudinary.IMAGE_GRAVITY_ALL_FACES)).isTrue();
        reloadBuilder();
        assertThat(mBuilder.gravity(null).build(CLOUD_URL).url().contains("g_")).isFalse();
    }

    @Test
    public void thatCanSetAspectRatio() {
        assertThat(mBuilder.aspectRatio("10:10").build(CLOUD_URL).url().contains("ar_10:10")).isTrue();
    }

    @Test
    public void thatCanSetAspectRatioByPercentage() {
        assertThat(mBuilder.aspectRatio(0.2f).build(CLOUD_URL).url().contains("ar_0.2")).isTrue();
    }

    @Test
    public void thatCanCropImage() {
        assertThat(mBuilder.crop(HaloCloudinary.CROP_MODE_AOI_CROP).build(CLOUD_URL).url().contains("c_" + HaloCloudinary.CROP_MODE_AOI_CROP)).isTrue();
        reloadBuilder();
        assertThat(mBuilder.crop(null).build(CLOUD_URL).url().contains("c_")).isFalse();
    }

    @Test
    public void thatCanSetHeightInPixels() {
        assertThat(mBuilder.height(20).build(CLOUD_URL).url().contains("h_20")).isTrue();
    }

    @Test
    public void thatCanSetHeightByPercentage() {
        assertThat(mBuilder.height(0.5f).build(CLOUD_URL).url().contains("h_0.5")).isTrue();
    }

    @Test
    public void thatCanSetWidthInPixels() {
        assertThat(mBuilder.width(20).build(CLOUD_URL).url().contains("w_20")).isTrue();
    }

    @Test
    public void thatCanSetWidthByPercentage() {
        assertThat(mBuilder.width(0.5f).build(CLOUD_URL).url().contains("w_0.5")).isTrue();
    }

    @Test
    public void thatCanApplyAdvaceRedEyeEffect() {
        assertThat(HaloCloudinary.Effects.advancedRedEye().toString().equals("e_adv_redeye")).isTrue();
    }

    @Test
    public void thatCanApplyAutoBrightnessEffect() {
        assertThat(HaloCloudinary.Effects.autoBrightness().toString().equals("e_auto_brightness")).isTrue();
    }

    @Test
    public void thatCanApplyAutoColorEffect() {
        assertThat(HaloCloudinary.Effects.autoColor().toString().equals("e_auto_color")).isTrue();
    }

    @Test
    public void thatCanApplyAutoContrastEffect() {
        assertThat(HaloCloudinary.Effects.autoContrast().toString().equals("e_auto_contrast")).isTrue();
    }

    @Test
    public void thatCanApplyBlackAndWhiteEffect() {
        assertThat(HaloCloudinary.Effects.blackAndWhite().toString().equals("e_blackwhite")).isTrue();
    }

    @Test
    public void thatCanApplyBlueEffect() {
        assertThat(HaloCloudinary.Effects.blue(10).toString().equals("e_blue:10")).isTrue();
        assertThat(HaloCloudinary.Effects.blue(null).toString().equals("e_blue")).isTrue();
        assertThat(HaloCloudinary.Effects.blue(-1000).toString().equals("e_blue")).isTrue();
    }

    @Test
    public void thatCanApplyBlurEffect() {
        assertThat(HaloCloudinary.Effects.blur(10).toString().equals("e_blur:10")).isTrue();
        assertThat(HaloCloudinary.Effects.blur(null).toString().equals("e_blur")).isTrue();
        assertThat(HaloCloudinary.Effects.blur(-1000).toString().equals("e_blur")).isTrue();
    }

    @Test
    public void thatCanApplyBlurFacesEffect() {
        assertThat(HaloCloudinary.Effects.blurFaces(10).toString().equals("e_blur_faces:10")).isTrue();
        assertThat(HaloCloudinary.Effects.blurFaces(null).toString().equals("e_blur_faces")).isTrue();
        assertThat(HaloCloudinary.Effects.blurFaces(-1000).toString().equals("e_blur_faces")).isTrue();
    }

    @Test
    public void thatCanApplyBrightnessEffect() {
        assertThat(HaloCloudinary.Effects.brightness(10).toString().equals("e_brightness:10")).isTrue();
        assertThat(HaloCloudinary.Effects.brightness(null).toString().equals("e_brightness")).isTrue();
        assertThat(HaloCloudinary.Effects.brightness(-1000).toString().equals("e_brightness")).isTrue();
    }

    @Test
    public void thatCanApplyColorizeEffect() {
        assertThat(HaloCloudinary.Effects.colorize(10).toString().equals("e_colorize:10")).isTrue();
        assertThat(HaloCloudinary.Effects.colorize(null).toString().equals("e_colorize")).isTrue();
        assertThat(HaloCloudinary.Effects.colorize(-1000).toString().equals("e_colorize")).isTrue();
    }

    @Test
    public void thatCanApplyContrastEffect() {
        assertThat(HaloCloudinary.Effects.contrast(10).toString().equals("e_contrast:10")).isTrue();
        assertThat(HaloCloudinary.Effects.contrast(null).toString().equals("e_contrast")).isTrue();
        assertThat(HaloCloudinary.Effects.contrast(-1000).toString().equals("e_contrast")).isTrue();
    }

    @Test
    public void thatCanApplyDisplaceEffect() {
        assertThat(HaloCloudinary.Effects.displace().toString().equals("e_displace")).isTrue();

    }

    @Test
    public void thatCanApplyDistortEffect() {
        assertThat(HaloCloudinary.Effects.distort(new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1)).toString().equals("e_distort:1:1:1:1:1:1:1:1")).isTrue();
    }

    @Test
    public void thatCanApplyFillLightEffect() {
        assertThat(HaloCloudinary.Effects.fillLight(10).toString().equals("e_fill_light:10")).isTrue();
        assertThat(HaloCloudinary.Effects.fillLight(null).toString().equals("e_fill_light")).isTrue();
        assertThat(HaloCloudinary.Effects.fillLight(-1000).toString().equals("e_fill_light")).isTrue();
    }

    @Test
    public void thatCanApplyGammaEffect() {
        assertThat(HaloCloudinary.Effects.gamma(10).toString().equals("e_gamma:10")).isTrue();
        assertThat(HaloCloudinary.Effects.gamma(null).toString().equals("e_gamma")).isTrue();
        assertThat(HaloCloudinary.Effects.gamma(-1000).toString().equals("e_gamma")).isTrue();
    }

    @Test
    public void thatCanApplyGradientFadeEffect() {
        assertThat(HaloCloudinary.Effects.gradientFade(10).toString().equals("e_gradient_fade:10")).isTrue();
        assertThat(HaloCloudinary.Effects.gradientFade(null).toString().equals("e_gradient_fade")).isTrue();
        assertThat(HaloCloudinary.Effects.gradientFade(-1000).toString().equals("e_gradient_fade")).isTrue();
    }

    @Test
    public void thatCanApplyGrayscaleEffect() {
        assertThat(HaloCloudinary.Effects.grayscale().toString().equals("e_grayscale")).isTrue();
    }

    @Test
    public void thatCanApplyGreenEffect() {
        assertThat(HaloCloudinary.Effects.green(10).toString().equals("e_green:10")).isTrue();
        assertThat(HaloCloudinary.Effects.green(null).toString().equals("e_green")).isTrue();
        assertThat(HaloCloudinary.Effects.green(-1000).toString().equals("e_green")).isTrue();
    }

    @Test
    public void thatCanApplyHueEffect() {
        assertThat(HaloCloudinary.Effects.hue(10).toString().equals("e_hue:10")).isTrue();
        assertThat(HaloCloudinary.Effects.hue(null).toString().equals("e_hue")).isTrue();
        assertThat(HaloCloudinary.Effects.hue(-1000).toString().equals("e_hue")).isTrue();
    }

    @Test
    public void thatCanApplyImproveEffect() {
        assertThat(HaloCloudinary.Effects.improve().toString().equals("e_improve")).isTrue();
    }

    @Test
    public void thatCanApplyMakeTransparentEffect() {
        assertThat(HaloCloudinary.Effects.makeTransparent().toString().equals("e_make_transparent")).isTrue();
    }

    @Test
    public void thatCanApplyMultiplyEffect() {
        assertThat(HaloCloudinary.Effects.multiply().toString().equals("e_multiply")).isTrue();
    }

    @Test
    public void thatCanApplyNegateEffect() {
        assertThat(HaloCloudinary.Effects.negate().toString().equals("e_negate")).isTrue();
    }

    @Test
    public void thatCanApplyOilPaintEffect() {
        assertThat(HaloCloudinary.Effects.oilPaint(10).toString().equals("e_oil_paint:10")).isTrue();
        assertThat(HaloCloudinary.Effects.oilPaint(null).toString().equals("e_oil_paint")).isTrue();
        assertThat(HaloCloudinary.Effects.oilPaint(-1000).toString().equals("e_oil_paint")).isTrue();
    }

    @Test
    public void thatCanApplyOverlayEffect() {
        assertThat(HaloCloudinary.Effects.overlay().toString().equals("e_overlay")).isTrue();
    }

    @Test
    public void thatCanApplyPixelateEffect() {
        assertThat(HaloCloudinary.Effects.pixelate(10).toString().equals("e_pixelate:10")).isTrue();
        assertThat(HaloCloudinary.Effects.pixelate(null).toString().equals("e_pixelate")).isTrue();
        assertThat(HaloCloudinary.Effects.pixelate(-1000).toString().equals("e_pixelate")).isTrue();
    }

    @Test
    public void thatCanApplyPixelateFacesEffect() {
        assertThat(HaloCloudinary.Effects.pixelateFaces(10).toString().equals("e_pixelate_faces:10")).isTrue();
        assertThat(HaloCloudinary.Effects.pixelateFaces(null).toString().equals("e_pixelate_faces")).isTrue();
        assertThat(HaloCloudinary.Effects.pixelateFaces(-1000).toString().equals("e_pixelate_faces")).isTrue();
    }

    @Test
    public void thatCanApplyRedEffect() {
        assertThat(HaloCloudinary.Effects.red(10).toString().equals("e_red:10")).isTrue();
        assertThat(HaloCloudinary.Effects.red(null).toString().equals("e_red")).isTrue();
        assertThat(HaloCloudinary.Effects.red(-1000).toString().equals("e_red")).isTrue();
    }

    @Test
    public void thatCanApplyRedEyeEffect() {
        assertThat(HaloCloudinary.Effects.redEye().toString().equals("e_redeye")).isTrue();
    }

    @Test
    public void thatCanApplySaturationEffect() {
        assertThat(HaloCloudinary.Effects.saturation(10).toString().equals("e_saturation:10")).isTrue();
        assertThat(HaloCloudinary.Effects.saturation(null).toString().equals("e_saturation")).isTrue();
        assertThat(HaloCloudinary.Effects.saturation(-1000).toString().equals("e_saturation")).isTrue();
    }

    @Test
    public void thatCanApplyScreenEffect() {
        assertThat(HaloCloudinary.Effects.screen().toString().equals("e_screen")).isTrue();
    }

    @Test
    public void thatCanApplySepiaEffect() {
        assertThat(HaloCloudinary.Effects.sepia(10).toString().equals("e_sepia:10")).isTrue();
        assertThat(HaloCloudinary.Effects.sepia(null).toString().equals("e_sepia")).isTrue();
        assertThat(HaloCloudinary.Effects.sepia(-1000).toString().equals("e_sepia")).isTrue();
    }

    @Test
    public void thatCanApplyShearEffect() {
        assertThat(HaloCloudinary.Effects.shear(10, 10).toString().equals("e_shear:10.0:10.0")).isTrue();
    }

    @Test
    public void thatCanApplyShadowEffect() {
        assertThat(HaloCloudinary.Effects.shadow(10).toString().equals("e_shadow:10")).isTrue();
        assertThat(HaloCloudinary.Effects.shadow(null).toString().equals("e_shadow")).isTrue();
        assertThat(HaloCloudinary.Effects.shadow(-1000).toString().equals("e_shadow")).isTrue();
    }

    @Test
    public void thatCanApplySharpenEffect() {
        assertThat(HaloCloudinary.Effects.sharpen(10).toString().equals("e_sharpen:10")).isTrue();
        assertThat(HaloCloudinary.Effects.sharpen(null).toString().equals("e_sharpen")).isTrue();
        assertThat(HaloCloudinary.Effects.sharpen(-1000).toString().equals("e_sharpen")).isTrue();
    }

    @Test
    public void thatCanApplyTrimEffect() {
        assertThat(HaloCloudinary.Effects.trim(10).toString().equals("e_trim:10")).isTrue();
        assertThat(HaloCloudinary.Effects.trim(null).toString().equals("e_trim")).isTrue();
        assertThat(HaloCloudinary.Effects.trim(101).toString().equals("e_trim")).isTrue();
    }

    @Test
    public void thatCanApplyUnsharpMaskEffect() {
        assertThat(HaloCloudinary.Effects.unsharpMask(10).toString().equals("e_unsharp_mask:10")).isTrue();
        assertThat(HaloCloudinary.Effects.unsharpMask(null).toString().equals("e_unsharp_mask")).isTrue();
        assertThat(HaloCloudinary.Effects.unsharpMask(-1000).toString().equals("e_unsharp_mask")).isTrue();
    }

    @Test
    public void thatCanApplyVibranceEffect() {
        assertThat(HaloCloudinary.Effects.vibrance(10).toString().equals("e_vibrance:10")).isTrue();
        assertThat(HaloCloudinary.Effects.vibrance(null).toString().equals("e_vibrance")).isTrue();
        assertThat(HaloCloudinary.Effects.vibrance(-1000).toString().equals("e_vibrance")).isTrue();
        assertThat(HaloCloudinary.Effects.vibrance(1000).toString().equals("e_vibrance")).isTrue();
    }

    @Test
    public void thatViesusCorrectEffect() {
        assertThat(HaloCloudinary.Effects.viesusCorrect().toString().equals("e_viesus_correct")).isTrue();
    }

    @Test
    public void thatCanChangeEffect() {
        assertThat(HaloCloudinary.Effects.vignette(10).toString().equals("e_vignette:10")).isTrue();
        assertThat(HaloCloudinary.Effects.vignette(null).toString().equals("e_vignette")).isTrue();
        assertThat(HaloCloudinary.Effects.vignette(-1).toString().equals("e_vignette")).isTrue();
        assertThat(HaloCloudinary.Effects.vignette(101).toString().equals("e_vignette")).isTrue();
    }

    @Test
    public void thatParcelableEffect() {
        HaloCloudinary.Effect effect = HaloCloudinary.Effects.negate();
        assertThat(TestUtils.testParcel(effect, HaloCloudinary.Effect.CREATOR).toString().equals(effect.toString())).isTrue();
        assertThat(effect.describeContents()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("all")
    public void thatEqualsEffect() {
        assertThat(HaloCloudinary.Effects.negate()).isEqualTo(HaloCloudinary.Effects.negate());
        assertThat(HaloCloudinary.Effects.negate().hashCode()).isEqualTo(HaloCloudinary.Effects.negate().hashCode());
        assertThat(HaloCloudinary.Effects.negate().equals(null)).isFalse();
        assertThat(HaloCloudinary.Effects.negate().equals(new Object())).isFalse();
        HaloCloudinary.Effect effect = HaloCloudinary.Effects.negate();
        assertNotEquals(new Object(), HaloCloudinary.Effects.negate());
    }

    @Test
    public void thatCanChangeRotations() {
        assertThat(HaloCloudinary.Rotations.autoLeft().toString().contains("a_auto_left")).isTrue();
        assertThat(HaloCloudinary.Rotations.autoRight().toString().contains("a_auto_right")).isTrue();
        assertThat(HaloCloudinary.Rotations.custom(10).toString().contains("a_10.0")).isTrue();
        assertThat(HaloCloudinary.Rotations.horizontalFlip().toString().contains("a_hflip")).isTrue();
        assertThat(HaloCloudinary.Rotations.verticalFlip().toString().contains("a_vflip")).isTrue();
        assertThat(HaloCloudinary.Rotations.ignore().toString().contains("a_ignore")).isTrue();
    }

    @Test
    public void thatParcelableRotation() {
        HaloCloudinary.Rotation rotation = HaloCloudinary.Rotations.verticalFlip();
        assertThat(TestUtils.testParcel(rotation, HaloCloudinary.Rotation.CREATOR).toString().equals(rotation.toString())).isTrue();
        assertThat(rotation.describeContents()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("all")
    public void thatEqualsRotation() {
        assertThat(HaloCloudinary.Rotations.horizontalFlip()).isEqualTo(HaloCloudinary.Rotations.horizontalFlip());
        assertThat(HaloCloudinary.Rotations.horizontalFlip().hashCode()).isEqualTo(HaloCloudinary.Rotations.horizontalFlip().hashCode());
        assertThat(HaloCloudinary.Rotations.horizontalFlip().equals(null)).isFalse();
        assertThat(HaloCloudinary.Rotations.horizontalFlip().equals(new Object())).isFalse();
        HaloCloudinary.Rotation rotation = HaloCloudinary.Rotations.horizontalFlip();
        assertNotEquals(new Object(), HaloCloudinary.Rotations.horizontalFlip());
    }
}
