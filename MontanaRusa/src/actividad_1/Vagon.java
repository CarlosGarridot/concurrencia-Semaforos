
package actividad_1;

/**
 *
 * @author carlosgarridodeltoro
 */
public class Vagon implements Runnable {

    private int totalViajesNecesarios;
    private int pasajerosSobrantes;

    @Override
    public void run() {
        try {
            System.out.println("\nEl vagón está en funcionamiento ");

            // Calcular número de viajes necesarios
            MontanaRusa.mutex.acquire();
            totalViajesNecesarios = MontanaRusa.totalPasajeros / MontanaRusa.CAPACIDAD_VAGON;
            pasajerosSobrantes = MontanaRusa.totalPasajeros % MontanaRusa.CAPACIDAD_VAGON;
            System.out.println("Se necesitan " + totalViajesNecesarios + " viajes para "
                    + MontanaRusa.totalPasajeros + " pasajeros, " + pasajerosSobrantes + " pasajeros se quedan fuera");
            MontanaRusa.mutex.release();

            // Realizar viajes
            for (int viaje = 1; viaje <= totalViajesNecesarios; viaje++) {
                realizarViaje(viaje);
            }

            System.out.println("El vagón acaba, " + MontanaRusa.viajesRealizados + " viajes realizados. \n");

            // Acomodar pasajeros sobrantes si los hay
            if (pasajerosSobrantes > 0) {

                // Liberar a los pasajeros sobrantes para que se despidan
                for (int i = 0; i < pasajerosSobrantes; i++) {
                    MontanaRusa.pasajerosSobrantesListos.release();
                }

                // Esperar a que todos los pasajeros sobrantes se hayan despedido
                for (int i = 0; i < pasajerosSobrantes; i++) {
                    MontanaRusa.vagonListo.acquire();
                }
            }

            MontanaRusa.simulacionTerminada.release(); // Señalizar fin de simulación

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void realizarViaje(int numeroViaje) throws InterruptedException {
        System.out.println("---INICIANDO VIAJE " + numeroViaje + " ---");

        // Fase de carga
        System.out.println("El vagón preparado para embarcar...");
        MontanaRusa.vagonListo.release(MontanaRusa.CAPACIDAD_VAGON); // Permitir que 4 pasajeros suban

        // Esperar a que 4 pasajeros suban
        for (int i = 0; i < MontanaRusa.CAPACIDAD_VAGON; i++) {
            MontanaRusa.pasajeroSubido.acquire();
        }

        // Verificar si hay suficientes pasajeros (para el último viaje)
        MontanaRusa.mutex.acquire();
        int pasajerosDisponibles = MontanaRusa.totalPasajeros - MontanaRusa.pasajerosAtendidos;
        int pasajerosEsteViaje = Math.min(MontanaRusa.CAPACIDAD_VAGON, pasajerosDisponibles);
        MontanaRusa.mutex.release();

        // Iniciar marcha
        System.out.println("¡Vagón lleno! Se pone en marcha...");
        Thread.sleep(1000); 
        MontanaRusa.vagonEnMarcha.release(pasajerosEsteViaje); // Notificar a pasajeros que vamos en marcha

        Thread.sleep(1500);

        // Llegada al destino
        System.out.println("El vagón ha acabado la vuelta, descargando...");
        MontanaRusa.vagonLlegado.release(pasajerosEsteViaje); // Notificar llegada

        // Esperar a que todos bajen
        for (int i = 0; i < pasajerosEsteViaje; i++) {
            MontanaRusa.pasajeroBajado.acquire();
        }

        // Actualizar contadores
        MontanaRusa.mutex.acquire();
        MontanaRusa.pasajerosAtendidos += pasajerosEsteViaje;
        MontanaRusa.viajesRealizados++;
        MontanaRusa.mutex.release();

        System.out.println("--- FIN VIAJE " + numeroViaje + " ---\n");
        Thread.sleep(500); // Pausa entre viajes
    }
}
