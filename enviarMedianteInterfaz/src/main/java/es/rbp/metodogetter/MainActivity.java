package es.rbp.metodogetter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Esta activity empieza o termina el {@link ServicioContador} y muestra el segundo actual del servicio
 * <p>
 * Para conectarse al servicio, hay que implementar la interfaz {@link ServiceConnection} y llamar al método {@link Context#bindService(Intent, ServiceConnection, int)}.
 * Solo puede haber un cliente por servicio, por lo que hay que llamar al método {@link Context#unbindService(ServiceConnection)} cuando se detenga
 * el activity para que cuando se cree otra vez poder conectarse al servicio de nuevo.
 *
 * @author Ricardo Bordería Pi
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection, ServicioContador.Llamada {

    /**
     * TextView que muestra el segundo actual del servicio
     */
    private TextView lblSegundoActual;

    /**
     * Instancia del servicio para acceder a sus métodos
     */
    private ServicioContador servicio;

    /**
     * Intent para identificar el servicio
     */
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEmpezar = findViewById(R.id.btnEmpezar);
        Button btnParar = findViewById(R.id.btnParar);

        btnEmpezar.setOnClickListener(this);
        btnParar.setOnClickListener(this);

        lblSegundoActual = findViewById(R.id.lblSegundoActual);

        intent = new Intent(this, ServicioContador.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    protected void onDestroy() {
        Log.i("ACTIVITY", "DESTRUIDO");
        Toast.makeText(this, "ACTIVITY DESTRUIDO", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnEmpezar)
            empezarServicio();
        else if (id == R.id.btnParar)
            pararServicio();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServicioContador.LocalBinder binder = (ServicioContador.LocalBinder) service;
        servicio = binder.getServiceInstance();
        servicio.registrarActivity(MainActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void actualizarContador(int segundoActual) {
        actualizarSegundo(segundoActual);
    }

    /**
     * Empieza un {@link ServicioContador} y reinicia el TextView {@link MainActivity#lblSegundoActual}
     */
    private void empezarServicio() {
        lblSegundoActual.setText(String.valueOf(ServicioContador.SEGUNDO_POR_DEFECTO));
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Detiene un {@link ServicioContador}
     */
    private void pararServicio() {
        servicio.pararConteo();
        unbindService(this);
        stopService(intent);
    }

    /**
     * Muestra el segundo enviado por el servicio y lo muestra en {@link MainActivity#lblSegundoActual}
     *
     * @param segundoActual segundo enviao por el servicio
     */
    private void actualizarSegundo(int segundoActual) {
        lblSegundoActual.setText(String.valueOf(segundoActual));
    }
}
