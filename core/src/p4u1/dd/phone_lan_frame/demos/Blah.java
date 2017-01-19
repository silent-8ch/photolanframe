package p4u1.dd.phone_lan_frame.demos;

import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.Screen;
        import com.badlogic.gdx.graphics.GL20;
        import com.badlogic.gdx.graphics.Pixmap;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.BitmapFont;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.math.MathUtils;
        import com.badlogic.gdx.utils.StreamUtils;

public class Blah implements Screen{
    TextureRegion image;
    BitmapFont font;
    SpriteBatch batch;


    public Blah () {
        new Thread(new Runnable() {
            /** Downloads the content of the specified url to the array. The array has to be big enough. */
            private int download (byte[] out, String url) {
                InputStream in = null;
                try {
                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection)new URL(url).openConnection();
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
                    }
                    return readBytes;
                } catch (Exception ex) {
                    return 0;
                } finally {
                    StreamUtils.closeQuietly(in);
                }
            }

            @Override
            public void run () {
                byte[] bytes = new byte[200 * 1024]; // assuming the content is not bigger than 200kb.
                int numBytes = download(bytes, "http://krystal.hlusko.com/photo/wp-content/uploads/2017/01/20170109_104030-e1484189764996-768x1365.jpg");
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
                        public void run () {
                            image = new TextureRegion(new Texture(potPixmap), 0, 0, originalWidth, originalHeight);
                        }
                    });
                }
            }
        }).start();

        font = new BitmapFont();
        batch = new SpriteBatch();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (image != null) {
            batch.begin();
            batch.draw(image, 0, 0);
            batch.end();
        } else {
            batch.begin();
            font.draw(batch, "Downloading...", 100, 100);
            batch.end();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}