package p4u1.dd.phone_lan_frame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Paul on 1/18/2017.
 */

public class URLSprite {
    TextureRegion image;
    volatile public boolean imageReady = false;
    PhotoGrabber grabber;
    public Sprite sprite;

    public URLSprite (String arg0) {
        grabber = new PhotoGrabber(arg0);
    }

    private class PhotoGrabber {

        public PhotoGrabber(final String arg0) {
            new Thread(new Runnable() {
                /**
                 * Downloads the content of the specified url to the array. The array has to be big enough.
                 */
                private int download(byte[] out, String url) {
                    InputStream in = null;
                    try {
                        HttpURLConnection conn = null;
                        conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(false);
                        conn.setUseCaches(true);
                        conn.connect();
                        in = conn.getInputStream();
                        int readBytes = 0;
                        while (true) {
                            int length = in.read(out, readBytes, out.length - readBytes);
                            if (length == -1) break;
                            readBytes += length;
                            //Gdx.app.log("phlusko", "downloading...");
                        }
                        return readBytes;
                    } catch (Exception ex) {
                        return 0;
                    } finally {
                        StreamUtils.closeQuietly(in);
                    }
                }

                @Override
                public void run() {
                    byte[] bytes = new byte[400 * 1024]; // assuming the content is not bigger than 200kb.
                    int numBytes = download(bytes, arg0);
                    if (numBytes != 0) {
                        // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
                        Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
                        final int originalWidth = pixmap.getWidth();
                        final int originalHeight = pixmap.getHeight();
                        //int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
                        int width = pixmap.getWidth();
                        int height = pixmap.getHeight();
                        //int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
                        final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
                        potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                        pixmap.dispose();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                image = new TextureRegion(new Texture(potPixmap), 0, 0, originalWidth, originalHeight);
                                sprite = new Sprite(image);
                                imageReady = true;
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
