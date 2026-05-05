package actividad_1;

/**
 *
 * @author carlosgarridodeltoro
 */

import java.util.*;
import java.util.concurrent.Semaphore;

public class MontanaRusa {

    // Constantes
    static final int CAPACIDAD_VAGON = 4;
    static volatile int totalPasajeros;
    static volatile int pasajerosEnVagon = 0;
    static volatile int viajesRealizados = 0;
    static volatile int pasajerosAtendidos = 0;

    static final String[] nombres = {
        "Ana", "Luis", "Marta", "Javier", "Elena",
        "Carlos", "Sofía", "David", "Laura", "Pedro",
        "Isabel", "Miguel", "Andrea", "Pablo", "Nuria",
        "Jorge", "Carmen", "Raúl", "Paula", "Ricardo"
    };

    // Semáforos (públicos para que todas las clases accedan)
    static public Semaphore mutex = new Semaphore(1); // Para secciones críticas
    static public Semaphore vagonListo = new Semaphore(0); // Vagon esperando pasajeros
    static public Semaphore pasajeroSubido = new Semaphore(0); // Señal de pasajero subido
    static public Semaphore vagonEnMarcha = new Semaphore(0); // Vagon en movimiento
    static public Semaphore vagonLlegado = new Semaphore(0); // Vagon llegó al destino
    static public Semaphore pasajeroBajado = new Semaphore(0); // Señal de pasajero bajado
    static public Semaphore simulacionTerminada = new Semaphore(0); // Fin de simulación
    static public Semaphore pasajerosSobrantesListos = new Semaphore(0); // Para pasajeros sobrantes
    
    public static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Generar número aleatorio de pasajeros
        totalPasajeros = random.nextInt(20) + 1;
        System.out.println("==== SIMULACIÓN DE VAGÓN ====");
        System.out.println("Número total de pasajeros: " + totalPasajeros);
        System.out.println("Capacidad del vagón: " + CAPACIDAD_VAGON);

        // Calcular pasajeros sobrantes
        int pasajerosSobrantes = totalPasajeros % CAPACIDAD_VAGON;
        int pasajerosConViaje = totalPasajeros - pasajerosSobrantes;
        System.out.println("Pasajeros con viaje: " + pasajerosConViaje);
        System.out.println("Pasajeros sobrantes: " + pasajerosSobrantes);
        System.out.println("=============================\n");

        // Crear e iniciar los pasajeros PRIMERO
        List<String> listaNames = new ArrayList<>(Arrays.asList(nombres));
        Collections.shuffle(listaNames, random);
        Thread[] pasajeros = new Thread[totalPasajeros];
        for (int i = 0; i < totalPasajeros; i++) {
            pasajeros[i] = new Thread(new Pasajero(i + 1, listaNames.get(i % listaNames.size())));
            pasajeros[i].start();
        }

        // Pausa para asegurar que todos los pasajeros se inicien
        Thread.sleep(1000);

        // Iniciar el vagón
        Thread vagonThread = new Thread(new Vagon());
        vagonThread.start();

        // Esperar a que termine la simulación
        simulacionTerminada.acquire();

        // Esperar a que todos los hilos terminen
        for (int i = 0; i < totalPasajeros; i++) {
            pasajeros[i].join();
        }
        vagonThread.join();

        System.out.println("\n=== SIMULACIÓN COMPLETADA ===");
        System.out.println("Total viajes realizados: " + viajesRealizados);
        System.out.println("Total pasajeros atendidos: " + (totalPasajeros - pasajerosSobrantes) + " de " + totalPasajeros);
        System.out.println("Pasajeros que volverán otro día: " + pasajerosSobrantes);
    }
}
