/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.io;

public interface ILecturaListener {

	// TODO: al leer, entregar s�lo object mensaje y no estructura Lectura ?
	/** 
	 * Invocado tras recibirse un nuevo mensaje
	 */
	public void sesionInactiva(Sesion sesion);

	/** 
	 * Invocado tras cerrarse desde el extremo de red una sesi�n
	 */
	public void sesionCerrada(Sesion sesion);

	/* Lectura */

	/** 
	 * Invocado tras recibirse un nuevo mensaje
	 */
	public boolean mensajeRecibido(Sesion sesion, Lectura lectura);

	/* Errores */
	/** 
	 * Invocado tras producirse una excepci�n en alguna de las etapas anteriores
	 */
	public void excepcionCapturada(Sesion sesion, Lectura lectura, Throwable e);
}
