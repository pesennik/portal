package org.zametki.util;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.time.Time;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Same as wicket one but simpler: without strike line
 */
public class SimpleCaptchaImageResource extends DynamicImageResource {

    /**
     * This class is used to encapsulate all the filters that a character will get when rendered.
     * The changes are kept so that the size of the shapes can be properly recorded and reproduced
     * later, since it dynamically generates the size of the captcha image. The reason I did it this
     * way is because none of the JFC graphics classes are serializable, so they cannot be instance
     * variables here.
     */
    private static final class CharAttributes implements IClusterable {
        private static final long serialVersionUID = 1L;
        private final char c;
        private final String name;
        private final int rise;
        private final double rotation;
        private final double shearX;
        private final double shearY;

        CharAttributes(final char c, final String name, final double rotation, final int rise,
                       final double shearX, final double shearY) {
            this.c = c;
            this.name = name;
            this.rotation = rotation;
            this.rise = rise;
            this.shearX = shearX;
            this.shearY = shearY;
        }

        char getChar() {
            return c;
        }

        String getName() {
            return name;
        }

        int getRise() {
            return rise;
        }

        double getRotation() {
            return rotation;
        }

        double getShearX() {
            return shearX;
        }

        double getShearY() {
            return shearY;
        }
    }

    private static final long serialVersionUID = 1L;

    private static int randomInt(final Random rng, final int min, final int max) {
        return (int) (rng.nextDouble() * (max - min) + min);
    }

    private final IModel<String> challengeId;

    private final List<String> fontNames = Arrays.asList("Helvetica", "Arial", "Courier");
    private final int fontSize;
    private final int fontStyle;

    /**
     * Transient image data so that image only needs to be re-generated after de-serialization
     */
    private transient SoftReference<byte[]> imageData;

    private final int margin;
    private final Random rng;

    /**
     * Construct.
     *
     * @param challengeId The id of the challenge
     * @param fontSize    The font size
     * @param margin      The image's margin
     */
    public SimpleCaptchaImageResource(final IModel<String> challengeId, final int fontSize, final int margin, Random rng) {
        this.challengeId = challengeId;
        this.fontStyle = 1;
        this.fontSize = fontSize;
        this.margin = margin;
        this.rng = rng;
    }


    /**
     * Gets the id for the challenge
     *
     * @return The id for the challenge
     */
    public final IModel<String> getChallengeIdModel() {
        return challengeId;
    }

    /**
     * Causes the image to be redrawn the next time its requested.
     */
    public final void invalidate() {
        imageData = null;
    }

    @Override
    protected final byte[] getImageData(final Attributes attributes) {
        // get image data is always called in sync block
        byte[] data = null;
        if (imageData != null) {
            data = imageData.get();
        }
        if (data == null) {
            data = render();
            imageData = new SoftReference<>(data);
            setLastModifiedTime(Time.now());
        }
        return data;
    }

    private Font getFont(final String fontName) {
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * Renders this image
     *
     * @return The image data
     */
    protected byte[] render() {
        int width = margin * 2;
        int height = margin * 2;
        char[] chars = challengeId.getObject().toCharArray();
        List<CharAttributes> charAttributes = new ArrayList<>();
        TextLayout text;
        AffineTransform textAt;
        Shape shape;

        for (char ch : chars) {
            String fontName = fontNames.get(randomInt(rng, 0, fontNames.size()));
            double rotation = Math.toRadians(randomInt(rng, -35, 35));
            int rise = randomInt(rng, margin / 2, margin);

            double shearX = rng.nextDouble() * 0.2;
            double shearY = rng.nextDouble() * 0.2;
            CharAttributes cf = new CharAttributes(ch, fontName, rotation, rise, shearX, shearY);
            charAttributes.add(cf);
            text = new TextLayout(ch + "", getFont(fontName), new FontRenderContext(null, false,
                    false));
            textAt = new AffineTransform();
            textAt.rotate(rotation);
            textAt.shear(shearX, shearY);
            shape = text.getOutline(textAt);
            width += (int) shape.getBounds2D().getWidth();
            if (height < (int) shape.getBounds2D().getHeight() + rise) {
                height = (int) shape.getBounds2D().getHeight() + rise;
            }
        }

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = (Graphics2D) image.getGraphics();
        gfx.setBackground(Color.WHITE);
        int curWidth = margin;
        for (CharAttributes cf : charAttributes) {
            text = new TextLayout(cf.getChar() + "", getFont(cf.getName()),
                    gfx.getFontRenderContext());
            textAt = new AffineTransform();
            textAt.translate(curWidth, height - cf.getRise());
            textAt.rotate(cf.getRotation());
            textAt.shear(cf.getShearX(), cf.getShearY());
            shape = text.getOutline(textAt);
            curWidth += shape.getBounds().getWidth();
            gfx.setXORMode(Color.BLACK);
            gfx.fill(shape);
        }

        WritableRaster raster = image.getRaster();
        int[] vColor = new int[3];
        int[] oldColor = new int[3];

        // noise
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                raster.getPixel(x, y, oldColor);

                // hard noise
                vColor[0] = (int) (Math.floor(rng.nextFloat() * 1.03) * 255);
                // soft noise
                vColor[0] = vColor[0] ^ (170 + (int) (rng.nextFloat() * 80));
                // xor to image
                vColor[0] = vColor[0] ^ oldColor[0];
                vColor[1] = vColor[0];
                vColor[2] = vColor[0];

                raster.setPixel(x, y, vColor);
            }
        }
        return toImageData(image);
    }

}
