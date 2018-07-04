/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.ospfv2.mensaje;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import msti.io.mensaje.IMensaje;
import msti.io.mensaje.IMensajeBuilder;
import msti.ospfv2.ChecksumOSPFv2;
import msti.ospfv2.mensaje.MensajeOSPFv2Hello.Builder;


public class MensajeOSPFv2LinkStateUpdate extends MensajeOSPFv2 implements IMensaje, IMensajeOSPFv2LinkStateUpdate {
	/* si construy� y entreg� el mensaje, no permite nuevas construcciones */
	protected boolean estaConstruido = false;

	/*public static final int MAXRIPRUTAS = 25;
	//protected List<IMensajeRIPRuta> ripRutas;
	protected boolean hasRutas = false;

	protected Boolean peticionTablaCompleta = null; 
	*/
	protected boolean hasLSAs=false;
	protected boolean hasAdvertisements=false;
	
	protected int advertisements;
	protected List<IMensajeOSPFv2LSA> lSAs;
	
	protected MensajeOSPFv2LinkStateUpdate() {
		super();
		/* Impl�cito en esta clase, est�n estos dos campos de la cabecera. Los fuerza TODO: Quitar */
		tipo = Tipo.OSPFLinkStateUpdate;
		hasTipo = true;
		version = 2;
		hasVersion = true;
	}

	/* IMensaje (serializaci�n, construcci�n) */
	
	@Override
	public Builder newBuilder() {
		return Builder.crear();
	}

	public IMensajeBuilder toBuilder() {
		/* Crea un nuevo builder */
		Builder builder = newBuilder();
		/* Inicializa el mensaje en el nuevo builder con los campos modificados del mensaje actual */
		builder.mezclarDesde(this);
		return builder;
	}
	
	@Override
	public byte[] writeToByteArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(this.getLongitudSerializado());
		try {
			super.writeToOutputStream(baos);
			writeToOutputStream(baos);
		} 
		catch (IOException e) {
			// No I/O involucrada sobre byteArray
		}
		return baos.toByteArray();
	}
	
	/**
	 * Serializa este objeto sobre un outputStream proporcionado.
	 * 
	 * Este objeto es un miembro de una uni�n (herencia de MensajeRIP, selector: tipo/version) impl�cita en la propia clase
	 * Por ello, siempre incluir� en la salida los campos selector de la uni�n (tipo/version). 
	 * 
	 */
	@Override
	public void writeToOutputStream(OutputStream output) throws IOException {
		//escribe la cabecera
		super.writeToOutputStream(output);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		//escribe advertisements
		if (hasAdvertisements)
			dos.writeInt(advertisements);
		//escribe lSAHeaders
		if (hasLSAs){
			for (IMensajeOSPFv2LSA lSA: lSAs)
			((IMensaje) lSA).writeToOutputStream(dos);
		}
		
		//colocar lo del baos detr�s del mensajeSerializado
		ByteArrayOutputStream baosMensajeCompleto = new ByteArrayOutputStream( );
		baosMensajeCompleto.write(mensajeSerializado);
		baosMensajeCompleto.write(baos.toByteArray());
		
		mensajeSerializado= baosMensajeCompleto.toByteArray();
		
		//antes de calcular checksum, calcular y rellenar PacketLengh
		short packetLengthUpdate = (short) getLongitudSerializado();
		mensajeSerializado[2] =  (byte)((packetLengthUpdate >> 8) & 0xff);
		mensajeSerializado[3] = (byte)(packetLengthUpdate & 0xff);
		packetLength = packetLengthUpdate;
		
		//calcular checksum
		//checksum est� a 0, calcularlo, rellenarlo (en el array mensaje Serializado)
		short checksumUpdate = (short) ChecksumOSPFv2.calcularChecksumOSPF(mensajeSerializado);
		mensajeSerializado[12] =  (byte)((checksumUpdate >> 8) & 0xff);
		mensajeSerializado[13] = (byte)(checksumUpdate & 0xff);
		checksum=checksumUpdate;
		
		output.write(mensajeSerializado);
		
		

	}

	@Override
	public int getLongitudSerializado() {
		//cabecera (super) +4 del int advertisement + recorremos todos los LSA para ver su tama�o (ya que no sabemos cual ser� en cada uno)
		int longitud=super.getLongitudSerializado();
		longitud+=4;
		
		ListIterator<IMensajeOSPFv2LSA> litr=lSAs.listIterator();
		while(litr.hasNext()){
	            longitud+=((IMensaje) litr.next()).getLongitudSerializado();
	    }	
		
		return longitud;
			
	}
	
	/*
	 * Clase MensajeRIP.Builder
	 * Para que no sea externa y poder poner los constructores del mensaje como privados, p.ej.
	 */
	public static class Builder extends MensajeOSPFv2.Builder implements IMensajeBuilder, IMensajeOSPFv2LinkStateUpdate, IMensajeOSPFv2LinkStateUpdate.Build {
		
		private Builder() {
			// No invoca al super()
			mensaje = new MensajeOSPFv2LinkStateUpdate();
		}

		protected MensajeOSPFv2LinkStateUpdate getMensaje() {
			return (MensajeOSPFv2LinkStateUpdate)this.mensaje;
		}

		public static Builder crear() {
			return new Builder();
		}

		@Override
		public MensajeOSPFv2LinkStateUpdate build() {
			if (getMensaje().estaConstruido)
				throw new IllegalStateException("Solicitado build() por segunda o sucesivas veces de un objeto ya construido.");
			if (! estaCompleto())
				throw new IllegalStateException("Solicitado build() sobre objeto no completo (uno o m�s campos obligatorios sin rellenar)");

			// Marca para que falle siguiente build()
			getMensaje().estaConstruido = true;

			return getMensaje();
		}

		public static MensajeOSPFv2LinkStateUpdate getDefaultInstanceforType() {
			MensajeOSPFv2LinkStateUpdate mensaje = new MensajeOSPFv2LinkStateUpdate();
			mensaje.estaConstruido = true;
			return mensaje;
		}
		
		/**
		 * Internamente es un wrapper a mezclarDesde InputStream
		 */
		@Override
		public Builder mezclarDesde(byte[] datos) {
			/* No lee los campos comunes, pues los lee la clase super() y ella invoca a este mezclarDesde */
			ByteArrayInputStream bis = new ByteArrayInputStream(datos);
			try { 
				mezclarDesde(bis);
			}
			catch (IOException e) {
				// No debe dar excepci�n I/O sobre un array de octetos
			}			
			return this;
		}

		/**
		 * MezclaDesde los campos del elemento. 
		 * 
		 * Este elemento es un miembro de una uni�n (herencia de MensajeRIP, selector: tipo/version) impl�cita en la clase
		 * Por ello, si se desea incluir en la mezcla los campos selector de la uni�n (tipo/version) deber�a usar el
		 * m�todo mezclarDesde de la uni�n (en la clase base). 
		 */
		@Override
		public Builder mezclarDesde(InputStream inputStream, byte[] mensajeSerializado) throws IOException {
			
			DataInputStream dis = new DataInputStream(inputStream);
			dis.read(mensajeSerializado, 24, inputStream.available()); 
						
			//Comprobar checksum del mensaje con la funcion verificar
			//si est� bien, lees, sino checksumOK false
			if(ChecksumOSPFv2.verificarChecksumOSPF(mensajeSerializado)){
				ByteArrayInputStream bais = new ByteArrayInputStream(mensajeSerializado);
				dis = new DataInputStream(bais);
				//cabecera
				byte[] cabecera = new byte[24];
				dis.read(cabecera, 0, 24);
				
				//Advertisements
				this.setAdvertisements(dis.readInt());
				//lSAs	
				while (dis.available() > 0) { 
					this.addLSA(MensajeOSPFv2LSA.Builder.crear()
											.mezclarDesde(dis,null)
											.build());
				}
				setIsChecksumOK(true);
				
			}else{
				setIsChecksumOK(false);
			}
					
			return this;
		}

		/** 
		 * Copia los campos modificados en mensajeOrigen al mensaje actual
		 */
		@Override
		public IMensajeBuilder mezclarDesde(IMensaje mensajeOrigen) {
			if (mensaje instanceof IMensajeOSPFv2)   
				super.mezclarDesde(mensajeOrigen);
			else if (mensaje instanceof MensajeOSPFv2LinkStateUpdate){
				MensajeOSPFv2LinkStateUpdate _mensajeOrigen = (MensajeOSPFv2LinkStateUpdate) mensajeOrigen;

				if (_mensajeOrigen.hasAdvertisements())
					this.setAdvertisements(_mensajeOrigen.getAdvertisements());	
				
				if (_mensajeOrigen.hasLSAs())
					this.setLSAs(_mensajeOrigen.getLSAs());				
			}
			else
				throw new IllegalArgumentException("IMensajeOSPFv2LinkStateUpdate::mezclarDesde(IMensaje): objeto recibido no es de clase IMensajeOSPFv2LinkStateUpdate");
			
				return this;
		}


		@Override
		public boolean estaCompleto() {
			return ((getMensaje().hasAdvertisements)&&(getMensaje().hasLSAs)); 
		}

		/* IMensajeRIPPeticion */

		@Override
		public  int getAdvertisements() {
			// TODO Auto-generated method stub
			return getMensaje().advertisements;
		}
		
		public  List<IMensajeOSPFv2LSA> getLSAs() {
			// TODO Auto-generated method stub
			return getMensaje().lSAs;
		}

		/**
		 * No realiza copia de los objetos IMensajeRipRuta.
		 * Si el mensaje no ten�a lista, establece como lista la indicada. Si ten�a lista, a�ade los elementos de 
		 * la lista indicada a la existente.
		 */
		@Override
		/*public Builder setRIPRutas(List<IMensajeRIPRuta> ripRutas) {
			if (getMensaje().ripRutas == null)
				getMensaje().ripRutas = ripRutas;
			else
				for (IMensajeRIPRuta mensajeRIPRuta: ripRutas)
					getMensaje().ripRutas.add(mensajeRIPRuta);
			getMensaje().hasRutas = true;
			return this;
		}*/

		public Builder setAdvertisements(int advertisements) {
			getMensaje().advertisements =advertisements;
			getMensaje().hasAdvertisements = true;
			return this;
		}
		
		public Builder setLSAs(List<IMensajeOSPFv2LSA> lSAs) {
			if (getMensaje().lSAs == null){
				getMensaje().lSAs =lSAs;
			}
			else{
				for (IMensajeOSPFv2LSA lSA: lSAs)
					getMensaje().lSAs.add(lSA);
			}
			getMensaje().hasLSAs = true;		
			return this;
		}
		
		/*@Override
		public Builder removeRIPRutas() {
			getMensaje().ripRutas = null;
			getMensaje().hasRutas = false;
			return this;
		}*/
		
		public Builder removeLSAs() {
			getMensaje().lSAs = null;
			getMensaje().hasLSAs = false;
			return this;
		}

		/*@Override
		public Builder addRIPRuta(IMensajeRIPRuta mensajeRIPRuta) {
			if (getMensaje().ripRutas == null)
				getMensaje().ripRutas = new ArrayList<IMensajeRIPRuta>(MAXRIPRUTAS);
			getMensaje().ripRutas.add(mensajeRIPRuta);
			getMensaje().hasRutas = true;
			return this;
		}*/
		
		@Override
		public Builder addLSA(IMensajeOSPFv2LSA lSA) {
			if (getMensaje().lSAs == null)
				getMensaje().lSAs = new ArrayList<IMensajeOSPFv2LSA>();
			getMensaje().lSAs.add(lSA);
			getMensaje().hasLSAs = true;
			return this;
		}

		/*@Override
		public boolean hasRIPRutas() {
			return getMensaje().hasRutas;
		}*/
		public boolean hasAdvertisements() {
			return getMensaje().hasAdvertisements;
		}
		
		public boolean hasLSAs() {
			return getMensaje().hasLSAs;
		}
		
		//

		/*@Override
		public boolean esPeticionTablaCompleta() {
			return getMensaje().esPeticionTablaCompleta();
		}

		public Builder setPeticionTablaCompleta(boolean b) {
			getMensaje().peticionTablaCompleta = new Boolean(true);
			return this;
		}*/

	}

	/**
	 * TODO: De momento, hasta separar la interfaz IMensajeRIPRuta en dos IMensajeRIPRuta.Getters, 
	 * IMensajeRIPRuta.Setters (usando IMensajeRIPRuta.Builder como tipos pasados, 
	 * y devolviendo en todos los set el mismo objeto Builder para poder encadenar la 
	 * construcci�n en una l�nea de c�digo.
	 * 
	 *  Por ello de momento se repite el c�digo al estar el setter en ambas clases (mensaje y builder)
	 *  Repito el c�digo porque el bueno debe estar en el Builder, pero para no repetir deber�a invocar desde el 
	 *  Builder al set del Mensaje y poner el bueno en el getMensaje(). As� en un futuro s�lo hay que borrar el setter
	 *  del mensaje.
	 *
	 */
	
	public int getAdvertisements() {
		// TODO Auto-generated method stub
		return this.advertisements;
	}

	public List<IMensajeOSPFv2LSA> getLSAs() {
		// TODO Auto-generated method stub
		return this.lSAs;
	}
	
	public boolean hasAdvertisements() {
		return this.hasAdvertisements;
	}
	
	public boolean hasLSAs() {
		return this.hasLSAs;
	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----> Mensaje Link State Update OSPFv2");
		
		if(hasTipo)
			sb.append("Tipo " +Byte.toString(tipo.getCodigo()));
		if(hasVersion)
			sb.append(",Versi�n " + Byte.toString(version));
		if(hasPacketLength)
			sb.append(",PacketLength " + Short.toString(packetLength));
		if(hasRouterID)
			sb.append(",RouterID " + Integer.toString(routerID));
		if(hasAreaID)
			sb.append(",AreaID " + Integer.toString(areaID));
		if(hasChecksum)
			sb.append(",Checksum " + Short.toString(checksum));
		if(hasAutype)
			sb.append(",Autype " + Short.toString(autype));
		if(hasAuthentication)
			sb.append(",Authentication " + Long.toString(authentication));
		
		
		if (hasAdvertisements)
			sb.append(",Advertisments " + Integer.toString(advertisements));
		
		if (this.lSAs != null)
			sb.append(",LSAs ");
			for (IMensajeOSPFv2LSA lSA: lSAs)
				sb.append(lSA.toString() + " ");

		return sb.toString();
	}

}