package es.rbp.metodogetter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Cuenta los segundos que psasn desde que se inció el servicio y los envía a {@link MainActivity}
 *
 * @author Ricardo Bordería Pi
 */
public class ServicioContador extends Service {

    /**
     * Extra para el valor por defecto para el intent del contador
     */
    public static final int SEGUNDO_POR_DEFECTO = 0;

    /**
     * Handler para manejar los hilos
     */
    private Handler handler;

    /**
     * Hilo que realiza el conteo y lo envía a las activities que lo escuchen
     */
    private Runnable hiloContador;

    private final IBinder binder = new LocalBinder();

    /**
     * Instancia de {@link Llamada} para acceder a sus métodos y enviar la información
     */
    private Llamada llamada;

    /**
     * Segundo actual desde el inicio del servicio
     */
    private int segundoActual;

    /**
     * Esta clase devuelve la instancia del servicio
     *
     * @author Ricardo Bordería Pi
     */
    public class LocalBinder extends Binder {

        /**
         * Devuelve una instancia de {@link ServicioContador}
         *
         * @return {@link ServicioContador}
         */
        public ServicioContador getServiceInstance() {
            return ServicioContador.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        segundoActual = 0;

        handler = new Handler();

        hiloContador = new Runnable() {
            @Override
            public void run() {
                segundoActual++;
                llamada.actualizarContador(segundoActual);
                handler.postDelayed(hiloContador, 1000);
            }
        };
        handler.postDelayed(hiloContador, 1000);
        Log.i("SERVICIO", "EMPEZADO");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(hiloContador);
        Log.i("SERVICIO", "DESTRUIDO");
        Toast.makeText(this, "Servicio destruido", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        handler.removeCallbacks(hiloContador);
        Log.i("ESTADO DEL SERVICIO", "PARADO");
        return super.stopService(name);
    }

    /**
     * Detiene el conteo
     */
    public void pararConteo() {
        handler.removeCallbacks(hiloContador);
        segundoActual = 0;
    }

    /**
     * Registra la activity desde la que es llamado
     *
     * @param activity activity que implemente {@link Llamada}
     */
    public void registrarActivity(Llamada activity) {
        this.llamada = activity;
    }

    /**
     * Interfaz para enviar información desde el servicio hasta el activity que la implemente
     */
    public interface Llamada {
        /**
         * Envía el segundo actual
         *
         * @param segundoActual {@link ServicioContador#segundoActual}
         */
        void actualizarContador(int segundoActual);
    }
}
