/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.ospfv2.fsmVecino;

import msti.fsm.FSMContexto;
import msti.fsm.FSMEstado;
import msti.fsm.FSMEvento;
import msti.ospfv2.fsmInterfaz.FSMEstadoOSPFv2Interfaz;
import msti.ospfv2.fsmInterfaz.FSMIdEventoOSPFv2Interfaz;
import msti.ospfv2.fsmInterfaz.FSMMaquinaEstadosOSPFv2Interfaz;
import msti.ospfv2.fsmInterfaz.FSMMaquinaEstadosOSPFv2Interfaz.FSMIdAccionOSPFv2Interfaz;
import msti.ospfv2.fsmVecino.FSMMaquinaEstadosOSPFv2Vecino.FSMIdAccionOSPFv2Vecino;
import msti.rip.mensaje.MensajeRIPRuta;

public class FSMEstadoOSPFv2VecinoFull extends FSMEstadoOSPFv2Vecino {
	static {
		_instancia = new FSMEstadoOSPFv2VecinoFull(FSMIdEstadoOSPFv2Vecino.FULL);
	}

	protected FSMEstadoOSPFv2VecinoFull(FSMIdEstado id) {		
		super(id);
	}

	public static FSMEstado getInstance() {
		return _instancia;
	}

	@Override
	public FSMEstado procesarEventoKillNbr(
			FSMContexto contexto, FSMEvento evento) {

		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contextoV.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contextoV);
		
		/** Obtiene y ejecuta acciones */
		//Notificar UpdateVecino
		FSMEvento eventoNeighborChange = new FSMEvento(FSMIdEventoOSPFv2Interfaz.NEIGHBORCHANGE, contextoV.getNeighborID());
		((FSMMaquinaEstadosOSPFv2Interfaz)contextoV.getContextoInterfaz().getMaquinaEstados()).encolarEvento(eventoNeighborChange);
		
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contextoV, evento.getArgumento());
		//Desactivar InactivityTimer
		FSMIdAccionOSPFv2Vecino.DESACTIVAR_TEMPORIZADOR_INACTIVITY.getInstance().execute(contextoV, evento.getArgumento());
				
		
		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contextoV);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEventoInactivityTimer(
			FSMContexto contexto, FSMEvento evento) {

		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contextoV.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contextoV);
		
		/** Obtiene y ejecuta acciones */
		//Notificar UpdateVecino
		FSMEvento eventoNeighborChange = new FSMEvento(FSMIdEventoOSPFv2Interfaz.NEIGHBORCHANGE, contextoV.getNeighborID());
		((FSMMaquinaEstadosOSPFv2Interfaz)contextoV.getContextoInterfaz().getMaquinaEstados()).encolarEvento(eventoNeighborChange);
		
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contextoV, evento.getArgumento());

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contextoV);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEventoLLDown(FSMContexto contexto,
			FSMEvento evento) {

		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contextoV.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contextoV);
		
		/** Obtiene y ejecuta acciones */
		//Notificar UpdateVecino
		FSMEvento eventoNeighborChange = new FSMEvento(FSMIdEventoOSPFv2Interfaz.NEIGHBORCHANGE, contextoV.getNeighborID());
		((FSMMaquinaEstadosOSPFv2Interfaz)contextoV.getContextoInterfaz().getMaquinaEstados()).encolarEvento(eventoNeighborChange);
		
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contextoV, evento.getArgumento());
		//Desactivar InactivityTimer
		FSMIdAccionOSPFv2Vecino.DESACTIVAR_TEMPORIZADOR_INACTIVITY.getInstance().execute(contextoV, evento.getArgumento());		
				
		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contextoV);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEventoStart(
			FSMContexto contexto, FSMEvento evento) {
		
	
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);
		
		/** Acciones */		
		
		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEventoHelloReceived(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);
		
		/** Acciones */
		//Iniciar InactivityTimer
		FSMIdAccionOSPFv2Vecino.INICIAR_TEMPORIZADOR_INACTIVITY.getInstance().execute(contexto, evento.getArgumento());
		
		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEvento2WayReceived(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);
		
		/** No acción */
		
		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}

	@Override
	public FSMEstado procesarEvento1WayReceived(
			FSMContexto contexto, FSMEvento evento) {

		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contextoV.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contextoV);

		/** Obtiene y ejecuta acciones */
		//Notificar UpdateVecino
		FSMEvento eventoNeighborChange = new FSMEvento(FSMIdEventoOSPFv2Interfaz.NEIGHBORCHANGE, contextoV.getNeighborID());
		((FSMMaquinaEstadosOSPFv2Interfaz)contextoV.getContextoInterfaz().getMaquinaEstados()).encolarEvento(eventoNeighborChange);
		
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contextoV, evento.getArgumento());


		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contextoV);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoSeqNumberMismatch(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** Obtiene y ejecuta acciones */
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contexto, evento.getArgumento());
		
		//Incrementar DDSequenceNumber
		FSMIdAccionOSPFv2Vecino.INCREMENTAR_DDSEQUENCENUMBER_DEL_VECINO.getInstance().execute(contexto, evento.getArgumento());
		//Declararse Master y enviar DatabaseDescriptionPackets con I M y MS a 1
		FSMIdAccionOSPFv2Vecino.COMO_MASTER_ENVIAR_DATABASEDESCRIPTIONPACKETS_VACIOS.getInstance().execute(contexto, evento.getArgumento());
		//esto hacerlo hasta que se produzca la siguiente transicion cada RxmtInterval (no lo pone pero ser� como el Init-2WayReceived
		
		//iniciar TemporizadorEnvioDDPconIMMS
		FSMIdAccionOSPFv2Vecino.INICIAR_TEMPORIZADOR_ENVIO_DDP_CON_IMMS.getInstance().execute(contexto, evento.getArgumento());

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoBadLSReq(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** Obtiene y ejecuta acciones */
		//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
		FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contexto, evento.getArgumento());
		
		//Incrementar DDSequenceNumber
		FSMIdAccionOSPFv2Vecino.INCREMENTAR_DDSEQUENCENUMBER_DEL_VECINO.getInstance().execute(contexto, evento.getArgumento());
		//Declararse Master y enviar DatabaseDescriptionPackets con I M y MS a 1
		FSMIdAccionOSPFv2Vecino.COMO_MASTER_ENVIAR_DATABASEDESCRIPTIONPACKETS_VACIOS.getInstance().execute(contexto, evento.getArgumento());
		//esto hacerlo hasta que se produzca la siguiente transicion cada RxmtInterval (no lo pone pero ser� como el Init-2WayReceived
		
		//iniciar TemporizadorEnvioDDPconIMMS
		FSMIdAccionOSPFv2Vecino.INICIAR_TEMPORIZADOR_ENVIO_DDP_CON_IMMS.getInstance().execute(contexto, evento.getArgumento());

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoNegotiationDone(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** No acción */

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoExchangeDone(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** No acción */

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoLoadingDone(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** No acción */

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoAdjOK(
			FSMContexto contexto, FSMEvento evento) {

		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		
		/** Acciones y evaluar guarda*/
		//Determiar si debe conserva la adjacencia con el vecino
		FSMIdAccionOSPFv2Vecino.DETERMINAR_SI_SE_CONSERVA_ADJACENCIA.getInstance().execute(contextoV, evento.getArgumento());
		//esta acci�n modifica el atributo EstadoAdjacencia del contexto
		
		String guarda;		
		guarda=contextoV.getEstadoAdjacencia();
		
		System.out.println("FSMEstadoOSPFv2VecinoFull:eventoAdjOK(): guarda= " + guarda);

		if (guarda.equals("Destruir adjacencia")) {
			//Limpia de LSA las listas LinkStateRetransmissionList, DatabaseSummaryList y LinkStateRequestList
			FSMIdAccionOSPFv2Vecino.LIMPIAR_LISTAS.getInstance().execute(contextoV, evento.getArgumento());
			
		}
		// else if (guarda.equals("Conservar adjacencia"):  no hay acciones que ejecutar 		
		
		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contextoV.getMaquinaEstados().getEstadoSiguiente(this, evento, guarda);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contextoV); 

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contextoV);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	@Override
	public FSMEstado procesarEventoDDPconIMMSTimer(
			FSMContexto contexto, FSMEvento evento) {

		/** Obtiene el estado siguiente */
		FSMEstado estadoSiguiente = contexto.getMaquinaEstados().getEstadoSiguiente(this, evento);

		/** Si esta transición cambia de estado, ejecuta método de salida del estado anterior */
		if (estadoSiguiente != this)
			this.onSalida(contexto);

		/** No acción */

		/** Ejecuta método de entrada de estado anterior */
		if (estadoSiguiente != this)
			estadoSiguiente.onEntrada(contexto);

		/** Devuelve el siguiente estado */
		return estadoSiguiente;
	}
	
	
	public void onSalida(FSMContexto contexto){
		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		//LLama a la accion de generacion de routerLinks
		FSMIdAccionOSPFv2Interfaz.GENERAR_LSA_ROUTER_LINKS.getInstance().execute(contextoV.getContextoInterfaz(), this.getId());
		
		//Adem�s si somos DR de esta red, llama a la accion de generacion de networkLinks
		FSMEstadoOSPFv2Interfaz estadoInterfaz = (FSMEstadoOSPFv2Interfaz) contextoV.getContextoInterfaz().getMaquinaEstados().getEstadoActivo();
		if(estadoInterfaz.getId().equals(FSMEstadoOSPFv2Interfaz.FSMIdEstadoOSPFv2Interfaz.DR)){
			FSMIdAccionOSPFv2Interfaz.GENERAR_LSA_NETWORK_LINKS.getInstance().execute(contextoV.getContextoInterfaz(), this.getId());
		}
		
	}	
	public void onEntrada(FSMContexto contexto){
		FSMContextoOSPFv2Vecino contextoV=(FSMContextoOSPFv2Vecino) contexto;
		//LLama a la accion de generacion de routerLinks
		FSMIdAccionOSPFv2Interfaz.GENERAR_LSA_ROUTER_LINKS.getInstance().execute(contextoV.getContextoInterfaz(), this.getId());
		
		//Adem�s si somos DR de esta red, llama a la accion de generacion de networkLinks
		FSMEstadoOSPFv2Interfaz estadoInterfaz = (FSMEstadoOSPFv2Interfaz) contextoV.getContextoInterfaz().getMaquinaEstados().getEstadoActivo();
		if(estadoInterfaz.getId().equals(FSMEstadoOSPFv2Interfaz.FSMIdEstadoOSPFv2Interfaz.DR)){
			FSMIdAccionOSPFv2Interfaz.GENERAR_LSA_NETWORK_LINKS.getInstance().execute(contextoV.getContextoInterfaz(), this.getId());
		}
		
	}

}