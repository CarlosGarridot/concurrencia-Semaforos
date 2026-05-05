
package actividad_1;

/**
 *
 * @author carlosgarridodeltoro
 */

public class Pasajero implements Runnable {
    private int id;
    private String nombre;
    private boolean esPasajeroSobrante = false;

    public Pasajero(int id, String nombre) {
        this.nombre = nombre;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            // Primero todos saludan
            System.out.println("    Hola mi nombre es: " + nombre);

            // Esperar a que el vagón esté listo o ser pasajero sobrante
            if (id > (MontanaRusa.totalPasajeros - (MontanaRusa.totalPasajeros % MontanaRusa.CAPACIDAD_VAGON))) {
                // Este es un pasajero sobrante
                esPasajeroSobrante = true;
                MontanaRusa.pasajerosSobrantesListos.acquire(); // Esperar señal del vagón
                System.out.println("    " + nombre + ": Volveré otro día");
                MontanaRusa.vagonListo.release(); // Notificar al vagón que ya se despidió
                return; // Terminar el hilo del pasajero sobrante
            }

            Thread.sleep(MontanaRusa.random.nextInt(100)); // Variacion de tiempo de espera del pasajero (No embarca por orden de creacion)
            
            // Esperar al vagon (Pasajeros normales (que caben en los viajes))
            MontanaRusa.vagonListo.acquire();

            // Subir al vagón
            MontanaRusa.mutex.acquire();
            MontanaRusa.pasajerosEnVagon++;
            System.out.println("    " + nombre + " embarcando.");
            MontanaRusa.mutex.release();

            // Notificar que subió
            MontanaRusa.pasajeroSubido.release();

            // Esperar a que el vagón se ponga en marcha
            MontanaRusa.vagonEnMarcha.acquire();

            // Esperar a que el vagón llegue al destino
            MontanaRusa.vagonLlegado.acquire();
            
            // Desembarcar y despedirse
            MontanaRusa.mutex.acquire();
            System.out.println("    " + nombre + " desdembarcando");
            System.out.println("    " + nombre + ": Hasta otra");
            MontanaRusa.pasajerosEnVagon--;
            MontanaRusa.mutex.release();

            // Notificar que bajó
            MontanaRusa.pasajeroBajado.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}