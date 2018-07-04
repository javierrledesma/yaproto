package msti.ospfv2;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.savarese.rocksaw.net.RawSocket;

import msti.io.AceptadorDatagrama;
import msti.io.AceptadorDatagramaMulticast;
import msti.io.AceptadorRawSocket;
import msti.io.Filtro;
import msti.io.FiltroCodec;
import msti.io.FiltroLog;
import msti.io.FiltroLog.NivelLog;
import msti.io.mensaje.IMensajeCodecFactoria;
import msti.io.mensaje.IMensajeCodificador;
import msti.io.mensaje.IMensajeDecodificador;
import msti.io.FiltroNotificador;
import msti.ospfv2.ProtocoloOSPFv2.AceptadorShutdown;
import msti.ospfv2.mensaje.MensajeOSPFv2Codec;
import msti.rip.mensaje.MensajeRIPCodec;

public class SimuladorEncaminadorOSPFv2 implements Runnable {

	//	private MaquinaEstados maquinaEstados;
	AceptadorRawSocket aceptador;
	
	/**
	 * @param args
	 */
	public SimuladorEncaminadorOSPFv2() {
		// Instancia una m�quina de Estados 
		// TODO: Aceptador debe instanciar un productor y una m�quina diferente para cada sesi�n nueva --aceptada--(y pasar a la m�quina la sesi�n para poder realizar la acci�n de salida)
		// La instancia el notificador. Lo que habr�a que hacer es construir una clase que construya las m�quina de estados
		// y suscribir esta m�quina al notificador para cada sesi�n (ilecturalistener).
		// esta clase suscribir� la maquina de estados creada a los eventos de la sesion asociada
//		maquinaEstados = new MaquinaEstados();
			
		// TODO
		
		// Construye un canal datagrama, con formato de pdu RIP.
		// Aceptador modo datagrama

/////	aceptador = new AceptadorDatagrama(); // Genera un lector y un escritor
		aceptador = new AceptadorRawSocket(); // Genera un lector y un escritor
		try {
			aceptador.crear(RawSocket.PF_INET, 89);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		//TODO: generar una cadena de filtros que los a�ade y enlace.
		// Codec de pdu RIP. TODO: Estos filtros se instancia uno, o varios por cada sesi�n?????
		FiltroCodec filtro1 = new FiltroCodec("Codec OSPFv2", 
									new IMensajeCodecFactoria() {
												MensajeOSPFv2Codec ospfv2Codec = null;

										@Override
										public IMensajeDecodificador getDecodificador() {
											if (ospfv2Codec == null) 
												ospfv2Codec = new MensajeOSPFv2Codec();
											return ospfv2Codec;
										}

										@Override
										public IMensajeCodificador getCodificador() {
											if (ospfv2Codec == null) 
												ospfv2Codec = new MensajeOSPFv2Codec();
											return ospfv2Codec;
										}										
		});
		aceptador.getCadenaFiltros().addFirst(filtro1.getNombre(), filtro1);
		// Log de mensajes recibidos (ya decodificados)
		FiltroLog filtro2 = new FiltroLog("Log");
		filtro2.setNivelLogMinimo(NivelLog.TRACE);
		aceptador.getCadenaFiltros().addLast(filtro2.getNombre(), filtro2);
		// Patr�n observable para el canal (permite observadores ISesionCreadaListener, ILecturaListener, IEscrituraListener)
		FiltroNotificador filtro3 = new FiltroNotificador("Notificador");
		aceptador.getCadenaFiltros().addLast(filtro3.getNombre(), filtro3);

		// Suscribe la m�quina de estados a los eventos del canal udp de mensajes
		// TODO: No es as�, en cada sesionCreada, deber�a instanciarse una m�quina de estados y suscribirla a los eventos del
		// notificador
		SimuladorEncaminadorOSPFv2Generador generador = new SimuladorEncaminadorOSPFv2Generador();
		filtro3.addSesionCreadaListener(generador); //suscribe			
		
		Thread hilo = new Thread(generador);
		hilo.start();
	}

	public void run() {
		// Establece un manejador para apagados abruptos
		AceptadorShutdown aceptadorShutdown = AceptadorShutdown.crear();
		aceptadorShutdown.setAceptador(this.aceptador);
		Runtime.getRuntime().addShutdownHook(aceptadorShutdown);	

		
		// bind a un puerto
		try {
			InetAddress inetA = InetAddress.getByName("192.168.1.1");
			System.out.println("Bind del canal OSPF iniciado, interfaz "+ inetA.getHostAddress());
			//Para a�adir m�s interfaces, hacer otro aceptadorOSPFv2 y otro bind, todo igual que este menos el bind (con la nueva ip)
			this.aceptador.bind(InetAddress.getByName("192.168.1.1"));
			///AllSPFRouters
			((AceptadorRawSocket)this.aceptador).unirGrupo(InetAddress.getByName("224.0.0.5"));
			///AlldRouters
			((AceptadorRawSocket)this.aceptador).unirGrupo(InetAddress.getByName("224.0.0.6"));
			// Pone en marcha ospf (usa para ello el hilo actual)
			System.out.println("Bind del canal OSPF terminado unido a  " + this.aceptador.getGrupos().size() + " grupos");		


		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("En bind() del aceptador(): m�todo .bind del socket");
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			System.out.println("En bind() del aceptador: getByName() nombre host desconocido");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("En bind() del aceptador(): unirGrupo() suelta IOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// En marcha (usa el hilo actual)
		aceptador.run();
	}

	public static void main(String[] args) {
		
		SimuladorEncaminadorOSPFv2 ospfv2 = new SimuladorEncaminadorOSPFv2();

		ospfv2.run();
		
	}

	// TODO: Llevar c�digo a aceptador
	
	public static class AceptadorShutdown extends Thread 
	{
		private AceptadorRawSocket aceptador;
		
		public static AceptadorShutdown crear() {
			return new AceptadorShutdown();
		}

		public void setAceptador(AceptadorRawSocket aceptador) {
			this.aceptador = aceptador;
		}

		public void run() {   
			System.out.println("En aceptador shutdown...");
			
			// Destruye los filtros
			for (Filtro filtro: aceptador.getCadenaFiltros())
				filtro.destroy();
			
			// Abandona los grupos multicast a los que se ha unido
			if (aceptador.isMulticast()) {
				AceptadorRawSocket _aceptador = (AceptadorRawSocket)aceptador;

				for (InetAddress grupo: _aceptador.getGrupos()){
					/*try {
						//llamar a abandonar grupo con todos los grupos
						//((MulticastSocket)_aceptador.getSocket()).leaveGroup(grupo);
						//TODO: leaveGroup sin implementar
					} catch (IOException e) {
						e.printStackTrace();
					}*/
				}
			}
	    }
		
	}
}