package no.daffern.vehicle.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.kotcrab.vis.ui.widget.file.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daffern on 16.06.2017.
 */
public class TexturePackerHelper {

	private static String IMAGE_LIST = "image_list.txt";

	public static boolean ensureAtlasUpdated(String input, String outputFile){

		FileHandle fileHandle = Gdx.files.local(input);

		if (checkDirectoryChanged(fileHandle)) {
			updateAtlas(input,outputFile);
			return true;
		}
		return false;
	}
	public static void updateAtlas(String inputFolder, String outputFile){

		String[] strings = outputFile.split("/");
		String output = strings[0];
		String packName = strings[1];

		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.combineSubdirectories = true;
		TexturePacker.process(settings, inputFolder, output, packName);
	}

	public static boolean checkDirectoryChanged(FileHandle root) {

		FileHandle imageList = root.child(IMAGE_LIST);

		List<FileHandle> files = findAllDirectories(root);


		String lastString = "";
		String currentString = "Automatically generated by TexturePackerHelper, do not edit \n\n";

		if (imageList.exists())
			lastString = imageList.readString();

		for (FileHandle fileHandle : files) {

			if (!fileHandle.isDirectory())
				currentString += fileHandle.toString() + "\n";

		}

		if (lastString.equals(currentString)){
			Tools.log(TexturePackerHelper.class, "images are up to date");

			return false;
		}
		else {
			Tools.log(TexturePackerHelper.class, "images changed since last, writing new file...");
			imageList.writeString(currentString,false);
			return true;
		}
	}

	private static  List<FileHandle> findAllDirectories(FileHandle root) {

		ArrayList<FileHandle> files = new ArrayList<>();
		files.add(root);
		int index = 0;


		while (index < files.size()) {

			FileHandle current = files.get(index);


			FileHandle[] array = current.list();
			files.addAll(Arrays.asList(array));


			index++;

		}
		return files;
	}


}
