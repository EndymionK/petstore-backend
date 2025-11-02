Guía rápida: desplegar en Render (forma más fácil)

1) Preparar el repositorio
 - Asegúrate de tener el código en un repo remoto (GitHub/GitLab/Bitbucket) y que la rama que desplegarás es `main`.
 - Si el proyecto usa el wrapper de Maven (`mvnw`), asegúrate de que el bit ejecutable está marcado en el repo:
   - Desde un entorno Unix (o Git Bash):
     git update-index --chmod=+x mvnw
     git commit -m "Make mvnw executable"
     git push

2) Conectar el repo a Render
 - Entra en https://render.com -> New -> Web Service
 - Autoriza tu proveedor Git y selecciona el repo `petstore-backend` y la rama (ej. `main`).

3) Configuración en la pantalla de creación
 - Environment: Public o Private según prefieras
 - Region: la más cercana a tus usuarios
 - Build Command:
     ./mvnw -DskipTests package
   (o `mvn -DskipTests package` si no usas el wrapper)
 - Start Command:
     java -jar target/*.jar --server.port=$PORT
   (Render expone el puerto en la variable de entorno `PORT`)
 - Instance Type: selecciona uno pequeño para pruebas (Starter)

4) Variables de entorno (importante)
 - En Render, edita la sección Environment → Environment Variables y añade:
     SPRING_DATASOURCE_URL
     SPRING_DATASOURCE_USERNAME
     SPRING_DATASOURCE_PASSWORD
   También añade cualquier secreto (JWT secret, etc.) que tu app necesite.

Nota: `application.properties` ya fue modificado para leer `SPRING_DATASOURCE_*` si existen.

5) Crear y desplegar
 - Haz clic en Create Web Service. Render hará build y arrancará automáticamente.

6) Verificación y logs
 - En Render, en el servicio, pestaña "Deploys" verás el build. En "Logs" verás la salida de la app.
 - Abre la URL pública que Render te da: https://<tu-servicio>.onrender.com
 - Prueba un endpoint con curl o PowerShell: `Invoke-RestMethod "https://<tu-servicio>.onrender.com/tu-endpoint"`

Problemas comunes
 - Error de compilación por versión de Java: asegúrate que Render usa JDK 21 (tu `pom.xml` lo requiere).
 - mvnw Permission denied: marca el wrapper como ejecutable en el repo (ver paso 1).
 - Conexión a DB fallida: revisa que la base de datos acepta conexiones desde Render (IP/allowlist) y las credenciales en env vars.

Si quieres, puedo:
 - Crear un `.render.yaml` para desplegar vía IaC automatizado.
 - Añadir health-check endpoint y configurar el "Health Check Path" en Render.
 - Preparar un Dockerfile para desplegar con Docker.
