package es.rbp.enviarinfodesdeservicio;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Cuenta los segundos que psasn desde que se inció el servicio y los envía a {@link MainActivity}
 *
 * @author Ricardo Bordería Pi
 */
public class ServicioContador extends Service {

    /**
     * Clave del mensaje de broadcast para que las demás activities reciban la información
     */
    public static final String MENSAJE_BROADCAST = "actualizarConteo";
    /**
     * Extra del intent que contiene el segundo actual
     */
    public static final String CONTADOR_EXTRA = "extraContador";

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

    /**
     * Segundo actual desde el inicio del servicio
     */
    private int segundoActual;

    public ServicioContador() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        segundoActual = 0;

        handler = new Handler();
        hiloContador = new Runnable() {
            @Override
            public void run() {
                segundoActual++;
                enviarSegundoActual();
                handler.postDelayed(hiloContador, 1000);
            }
        };
        handler.postDelayed(hiloContador, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(hiloContador);
        Log.i("ESTADO DEL SERVICIO", "DESTRUIDO");
        Toast.makeText(this, "SERVICIO DESTRUIDO", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        handler.removeCallbacks(hiloContador);
        Log.i("ESTADO DEL SERVICIO", "PARADO");
        return super.stopService(name);
    }

    /**
     * Envía el {@link ServicioContador#segundoActual} por broadcast
     */
    private void enviarSegundoActual() {
        Intent intent = new Intent(MENSAJE_BROADCAST);
        intent.putExtra(CONTADOR_EXTRA, segundoActual);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
