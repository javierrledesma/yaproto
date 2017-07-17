/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.io;

import java.io.InputStream;
import java.io.OutputStream;

public interface IFiltro extends ILecturaListener, ISesionCreadaListener, IEscritura  {
	/**
	 * Devuelve el nombre (que debe ser �nico en la cadena) del filtro
	 */
	public String getNombre();
	/** 
	 * Invocado antes de comenzar el proceso de operaciones
	 */
	public void init();		
	/** 
	 * Invocado al destruir la cadena
	 */
	public void destroy();


	/**
	 * Proporciona un outputStream al filtro siguiente.
	 * Habitualmente pasar� la llamada al filtro anterior sin interceptarla, pero puede hacerlo para, p.ej., 
	 * ofrecer un ByteArrayOutputStream byte[] al filtro siguiente para alg�n tipo de proceso del byte[] antes de
	 * continuar su env�o por la cadena.
	 * 
	 * @param sesion
	 * @param numBytesEscritura -> n�mero de octetos que tiene previsto escribir
	 * @return OutputStream
	 */
	public OutputStream getOutputStream(Sesion sesion, int numBytesEscritura);

	/**
	 * Proporciona un inputStream al filtro siguiente.
	 * Habitualmente pasar� la llamada al filtro anterior sin interceptarla, pero puede hacerlo para, p.ej., 
	 * ofrecer un ByteArrayInputStream byte[] para alg�n tipo de proceso del byte[] antes de
	 * continuar su env�o por la cadena.
	 * 
	 * @param sesion
	 * @param numBytesEscritura -> n�mero de octetos que tiene previsto escribir
	 * @return OutputStream
	 */
	public InputStream getInputStream(Sesion sesion, int numBytesMaxLectura);
	
	/**
	 * Indica m�ximo de datos que espera consumir de la etapa anterior
	 */
	public int getMaxInputBytes();

}
