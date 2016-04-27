package mx.gob.guaymas.guaymas60.utils.photo;

import android.os.Environment;

import java.io.File;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

	@Override
	public File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		  Environment.getExternalStoragePublicDirectory(
				  Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}
}
