package no.daffern.vehicle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import no.daffern.vehicle.Main;

public class DesktopLauncher {



	public static void main (String[] arg) {



		if (false){
			TexturePacker.Settings settings = new TexturePacker.Settings();

			TexturePacker.process(settings, "./interface", "./texturepacker/", "game");

		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;

		new LwjglApplication(new Main(), config);
	}
}