/*
Copyright (c) 2012 Javier Ramirez-Ledesma
Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
*/

package msti.netlink.mensaje;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import msti.io.mensaje.IMensaje;
import msti.io.mensaje.IMensajeBuilder;

public class MensajeNetlinkRouteAttributeDst extends MensajeNetlinkRouteAttribute implements IMensaje, IMensajeNetlinkRouteAttributeDst {
	/* si construy� y entreg� el mensaje, no permite nuevas construcciones */
	protected boolean estaConstruido = false;
	
	protected InetAddress dst;
	protected boolean hasDst = false;

	protected byte prefixLength;
	protected boolean hasPrefixLength = false;
	
	protected MensajeNetlinkRouteAttributeDst() {
		super();		
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream(super.getLongitudSerializado() + getLongitudSerializado());
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
	 * Este objeto es un miembro de una uni�n impl�cita en la propia clase
	 * Por ello, siempre incluir� en la salida los campos selector de la uni�n (tipo/longitud). 
	 * 
	 */
	/*
	 *  <------- NLA_HDRLEN ------> <-- NLA_ALIGN(payload)-->
	 * +---------------------+- - -+- - - - - - - - - -+- - -+
	 * |        Header       | Pad |     Payload       | Pad |
	 * |   (struct nlattr)   | ing |                   | ing |
	 * +---------------------+- - -+- - - - - - - - - -+- - -+
	 *  <-------------- nlattr->nla_len -------------->
	 */
	@Override
	public void writeToOutputStream(OutputStream output) throws IOException {
		DataOutputStream dos = new DataOutputStream(output);
		int escritos = 0;

		// Establece longitud correcta, para que super escriba bien la longitud (L=L+T+V)
		this.length = (short)this.getLongitudMensaje();
		this.hasLength = true;

		// Selectores de la uni�n (cabecera en Attribute)
		super.writeToOutputStream(output);
		escritos = super.getLongitudSerializado(); // se podr�a obtener tambi�n del output

		/* Alineamiento tras cabecera */
		for (int i = getRelleno(escritos); i > 0; i--, escritos++)
			dos.writeByte((byte)0);

		// Datos
		
		/* dst */
		byte[] bufer = this.getDst().getAddress(); 
		dos.write(bufer); // en orden de red siempre  TODO:Verificar que es as� en netlink
		escritos += bufer.length;
	}

	protected int getLongitudMensaje() {
		int total;

		// Cabecera + relleno
		total = super.getLongitudSerializado();
		total += getRelleno(total);
		// Datos
		total += this.getDst().getAddress().length;  
		return total;  //no relleno tras datos
	}

	@Override
	public int getLongitudSerializado() {
		return getLongitudMensaje(); // no a�ade relleno tras atributo, el que ponga varios juntos debe alinearlos
	}

	/*
	 * Clase MensajeRIP.Builder
	 * Para que no sea externa y poder poner los constructores del mensaje como privados, p.ej.
	 */
	public static class Builder extends MensajeNetlinkRouteAttribute.Builder implements IMensajeBuilder, IMensajeNetlinkRouteAttributeDst, IMensajeNetlinkRouteAttributeDst.Build {
		
		private Builder() {
			_builder = this;
			mensaje = new MensajeNetlinkRouteAttributeDst(); //inicializa el mensaje heredado con el tipo adecuado
		}

		/* Sobreescribe para cambiar el tipo devuelto y usarlo tipado donde se requiera */
		@Override
		protected MensajeNetlinkRouteAttributeDst getMensaje() {
			return (MensajeNetlinkRouteAttributeDst)this.mensaje;
		}

		
		public static Builder crear() {
			return new Builder();
		}

		@Override
		public MensajeNetlinkRouteAttributeDst build() {
			if (mensaje.estaConstruido)
				throw new IllegalStateException("Solicitado build() por segunda o sucesivas veces de un objeto ya construido.");
			if (! estaCompleto())
				throw new IllegalStateException("Solicitado build() sobre objeto no completo (uno o m�s campos obligatorios sin rellenar)");

			// Marca para que falle siguiente build()
			mensaje.estaConstruido = true;

			return getMensaje();
		}

		public static MensajeNetlinkRouteAttributeDst getDefaultInstanceforType() {
			// TODO: Petici�n de tabla de rutas puede ser el mensaje por defecto
			MensajeNetlinkRouteAttributeDst mensaje = new MensajeNetlinkRouteAttributeDst();
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
		public Builder mezclarDesde(InputStream inputStream) throws IOException {
			DataInputStream dis = new DataInputStream(inputStream);

			// Le�da cabecera sin relleno
			int leidos = 4; //TODO: no anclar este valor

			/* Alineamiento tras cabecera */
			for (int i = getRelleno(leidos); i > 0; i--, leidos++) 
				dis.readByte();

			/* Datos */
			
			/* dst */
			byte[] bufer = new byte[getMensaje().getLength() - leidos]; 
			dis.read(bufer);
			this.setDst(InetAddress.getByAddress(bufer)); // siempre en orden de red			
			leidos += bufer.length;
			
			return this;
		}

		/** 
		 * Copia los campos modificados en mensajeOrigen al mensaje actual
		 */
		@Override
		public Builder mezclarDesde(IMensaje mensajeOrigen) {
			// Copia desde la base los campos comunes
			if (mensaje instanceof IMensajeNetlinkRouteAttribute) {
				System.out.println("Mezclar desde base (es Imensajenetlink o subclase");
				super.mezclarDesde(mensajeOrigen);
			}
			else if (mensaje instanceof IMensajeNetlinkRouteAttributeDst) {
				// Campos comunes ya copiados en el instanceof anterior

				System.out.println("Mezclar resto (es Imensajenetlinkroute o subclase)");

				// Copia s�lo los campos rellenos desde el objeto origen
				MensajeNetlinkRouteAttributeDst _mensajeOrigen = (MensajeNetlinkRouteAttributeDst)mensajeOrigen;
				if (_mensajeOrigen.hasDst())
					this.setDst(_mensajeOrigen.getDst());
			}
			else 
				throw new IllegalArgumentException("mezclarDesde(IMensaje): objeto recibido no es de clase MensajeNetlinkRouteAttribute");

			return this;
		}


		@Override
		public boolean estaCompleto() {
			return (super.estaCompleto() &&
					getMensaje().hasDst());			
		}

		/* IMensajeNetlinkRouteAttributeDst */

		@Override
		public Builder setDst(InetAddress dst) {
			getMensaje().dst = dst;
			getMensaje().hasDst = true;			
			return this;
		}

		@Override
		public InetAddress getDst() {
			return getMensaje().dst;
		}

		@Override
		public boolean hasDst() {
			return getMensaje().hasDst;
		}

		@Override
		public Builder setPrefixLength (byte prefixLength) {
			getMensaje().prefixLength = prefixLength;
			getMensaje().hasPrefixLength = true;			
			return this;
		}

		@Override
		public byte getPrefixLength() {
			return getMensaje().prefixLength;
		}

		@Override
		public boolean hasPrefixLength() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" MensajeNetlinkRouteAttributeDst");

		sb.append("\n Cabecera:");
		sb.append("  length="); 		sb.append(this.getLength());
		sb.append("; type="); 			sb.append(this.getAttributeType());
		
		sb.append("\n Datos:");
		sb.append("  dst="); 			sb.append(this.getDst());
		
		return sb.toString();
	}

	@Override
	public InetAddress getDst() {
		return this.dst;
	}

	@Override
	public boolean hasDst() {
		return this.hasDst;
	}

	@Override
	public byte getPrefixLength() {
		return this.prefixLength;
	}

	@Override
	public boolean hasPrefixLength() {
		// TODO Auto-generated method stub
		return this.hasPrefixLength;
	}

}
