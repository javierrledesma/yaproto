/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.ospfv2.fsmVecino;

import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;

import msti.fsm.FSMAccion;
import msti.fsm.FSMContexto;
import msti.ospfv2.TablaRutas;
import msti.ospfv2.TablaRutas.EstadoRuta;
import msti.ospfv2.TablaRutas.Ruta;
//import msti.rip.mensaje.MensajeRIPRespuesta;

public class FSMAccionDesactivarTemporizadorInactivity implements FSMAccion {
	/** La instancia singleton */
	protected static FSMAccionDesactivarTemporizadorInactivity _instancia = new FSMAccionDesactivarTemporizadorInactivity();

	/**
	 * Devuelve una instancia. Resuelve internamente posibles problemas de concurrencia en la instanciación
	 * @return Instancia
	 */
	public static FSMAccion getInstance() {
		return _instancia;
	}

	protected FSMAccionDesactivarTemporizadorInactivity() {
	}

	
	@Override
	public void execute(FSMContexto contexto, Object o) {	
		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		ScheduledFuture tareaTemporizada = (ScheduledFuture) contextoV.getTemporizadorInactivity();
		
		if (tareaTemporizada != null) {
			tareaTemporizada.cancel(false);
			tareaTemporizada = null;
		}
		
		
		
		
	}

}