/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.io;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public abstract class Escritor implements Runnable, IEscritura {

	protected Sesion sesion;
	
	protected Aceptador aceptador;
	
	protected final BlockingQueue<Escritura> colaEscrituras;

	protected static final int MAXCOLA = 20;

	public Escritor() {
		/**
		 * Crea un comparador espec�fico, para poder ordenar la cola con prioridad primero por prioridad y despu�s
		 * por orden de introducci�n en la cola (idEscritura), pues la cola PriorityBlockingQueue gestiona s�lo el
		 * primer nivel (prioridad) por defecto, pero no garantiza que con la misma prioridad se entreguen en orden. 
		 */
		Comparator<Escritura> comparador = new Comparator<Escritura>() {

			@Override
			public int compare(Escritura o1, Escritura o2) {
				int prio1 = o1.getPrioridad().getCodigo();
				int prio2 = o2.getPrioridad().getCodigo();
				long id1 = o1.getId();
				long id2 = o2.getId();

				// Primero compara por prioridad. Despu�s por id (orden de introducci�n en la cola)
				return prio1 > prio2 ? 1 : (prio1 < prio2 ? -1 : (id1 > id2 ? 1 : (id1 < id2 ? -1 : 0)));
			}
			
		};
		this.colaEscrituras = new PriorityBlockingQueue<Escritura>(MAXCOLA, comparador);
	}
	
	@Override
	public abstract void run();
	
	public abstract OutputStream getOutputStream(Sesion sesion, int numBytesEscritura);

	/** 
	 * Escritura as�ncrona. Es decir, encola la petici�n para su procesado y env�o en segundo plano. 
	 * No se avisa de la finalizaci�n a ning�n objeto.
	 * 
	 * @param sesionDatagrama
	 * @param escritura
	 */
	public abstract void escribirAsincrono(Sesion sesion, Escritura escritura); 

	/** 
	 * Escritura as�ncrona. Es decir, encola la petici�n para su procesado y env�o en segundo plano
	 * Avisa de la finalizaci�n al objeto IEscrituraListener indicado.
	 * 
	 * @param sesionDatagrama
	 * @param escritura
	 * @param escrituraListener. IEscrituraListener que se invocar� cuando esta escritura finalice (se entregue al canal subyacente)
	 */
	public abstract void escribirAsincrono(Sesion sesion, Escritura escritura, IEscrituraListener escrituraListener); 

	/**
	 * Escritura s�ncrona. Es decir, s�lo retorna cuando la petici�n de env�o ha sido procesada por todos los
	 * filtros, y entregada al canal correspondiente.
	 * Cuando se solicita (canalAsincrono a true), tambi�n se configura, cuando ello es posible, el canal para env�o s�ncrono, es
	 * decir, que no retorne la llamada de env�o hasta no haber transmitido los datos.
	 * 
	 * @param sesionDatagrama
	 * @param escritura
	 * @param canalAsincrono
	 */
	public abstract void escribirSincrono(SesionDatagrama sesionDatagrama, Escritura escritura, boolean canalAsincrono, IEscrituraListener escrituraListener); 

	public Sesion getSesion() {
		return sesion;
	}

	public void setSesion(Sesion sesion) {
		this.sesion = sesion;
	}

	public Aceptador getAceptador() {
		return aceptador;
	}

	public void setAceptador(Aceptador aceptador) {
		this.aceptador = aceptador;
	}

}
