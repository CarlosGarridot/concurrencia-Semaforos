# Simulación de la Montaña Rusa

Simulación de concurrencia con semáforos en Java.

---

## Descripción

Simulación de un vagón de montaña rusa con capacidad para 4 pasajeros. El vagón solo parte cuando está completamente lleno y da servicio a un número aleatorio de pasajeros de forma concurrente.

Cada pasajero y el vagón son hilos independientes que se sincronizan exclusivamente mediante semáforos.

---

## Funcionamiento

- Se genera un número aleatorio de pasajeros, cada uno con un nombre único
- Los pasajeros saludan al llegar y esperan a que el vagón esté listo
- El vagón espera a que suban exactamente 4 pasajeros antes de partir
- Al llegar al destino, los pasajeros desembarcan y se despiden
- Si el número de pasajeros no es múltiple de 4, los sobrantes se despiden sin viajar

---

## Sincronización

La sincronización se resuelve únicamente con semáforos (`java.util.concurrent.Semaphore`):

| Semáforo | Función |
|----------|---------|
| `mutex` | Exclusión mutua en secciones críticas |
| `vagonListo` | Avisa a los pasajeros que pueden embarcar |
| `pasajeroSubido` | El pasajero notifica al vagón que ha subido |
| `vagonEnMarcha` | El vagón notifica que ha partido |
| `vagonLlegado` | El vagón notifica que ha llegado al destino |
| `pasajeroBajado` | El pasajero notifica que ha desembarcado |
| `pasajerosSobrantesListos` | Gestiona los pasajeros que no pueden viajar |

---

## Estructura

```
MontanaRusa/
├── src/
│   └── actividad_1/
│       ├── Actividad_1.java   # Clase principal y semáforos globales
│       ├── Vagon.java         # Hilo del vagón
│       └── Pasajero.java      # Hilo de cada pasajero
```

---

## Cómo ejecutarlo

```bash
# Compilar
javac src/actividad_1/*.java

# Ejecutar
java -cp src actividad_1.MontanaRusa
```

---

## Tecnologías

![Java](https://img.shields.io/badge/Java-17+-orange?logo=java)
