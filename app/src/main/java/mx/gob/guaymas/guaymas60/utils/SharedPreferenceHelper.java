package mx.gob.guaymas.guaymas60.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que sirve como asistente para el manejo de las preferencias de la aplicaci�n y manejar la sesi�n.
 * @author Julio Nava - Edgar Quintana
 * @version 0.1
 */
public class SharedPreferenceHelper {
	
	/**
	 * Configuraci�n para el acceso de las preferencias del sistema.
	 */
	SharedPreferences settings;

	/**
	 * Constructor del asistente del Shared Preferences.
	 * @param sharedPrefName Nombre del Shared Preference.
	 * @param contexts Contexto de la aplicaci�n.
	 */
	public SharedPreferenceHelper(String sharedPrefName, Context contexts){
		//Creamos la referencia al shared preferences
		settings = contexts.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
	}

	//Retorna T o F si existe el valor
	/**
	 * Retorna true or false si existe el valor.
	 * @param val La clave para referenciar a la variable.
	 * @return Si existe o no.
	 */
	public boolean isExist(String val){
		String value = settings.getString(val, null);
		if(value == null){
			return false;
		}else{
			return true;
		}
	}

	//Retorna el string del contenido de parametro en la preferencias
	/**
	 * Retorn el String del contenido del parametro en la preferencias.
	 * @param val La clave para referenciar a la variable.
	 * @return El valor de la variable.
	 */
	public String getStringFromShprf(String val){
		return settings.getString(val, null);
	}

	//Escribimos el valor string en las preferencias
	/**
	 * Escribir un valor en las preferencias.
	 * @param clave La clave para referenciar a la variable.
	 * @param valor El valor de la variable.
	 */
	public void writeString(String clave, String valor){
		SharedPreferences.Editor editor = settings.edit();
	
		editor.putString(clave, valor);
		editor.commit();
	}

	//Borrar un valor
	/**
	 * Eliminar un valor en las preferencias.
	 * @param clave La clave para referenciar a la variable.
	 */
	public void removeValue(String clave){
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(clave);
		editor.commit();
	}

}
