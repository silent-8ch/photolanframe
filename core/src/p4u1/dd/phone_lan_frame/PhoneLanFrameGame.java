package p4u1.dd.phone_lan_frame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import p4u1.dd.phone_lan_frame.network.BroadcastServer;
import p4u1.dd.phone_lan_frame.network.ClientWorkhorse;
import p4u1.dd.phone_lan_frame.network.PhotoLanServer;
import p4u1.dd.phone_lan_frame.ui.ConnectionScreen;

public class PhoneLanFrameGame extends Game {
	public PhotoLanServer server;
	public ClientWorkhorse client;
	public BitmapFont font;
	public BitmapFont smallfont;

	@Override
	public void create () {
		font = new BitmapFont(Gdx.files.internal("img/headline.fnt"),
				false);
		smallfont = new BitmapFont(Gdx.files.internal("img/ak_med.fnt"),
				false);
		this.setScreen(new ConnectionScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	}

	public void setGameServer(PhotoLanServer arg0){
		this.server = arg0;
	}

	public PhotoLanServer getServer() {
		return server;
	}
}
