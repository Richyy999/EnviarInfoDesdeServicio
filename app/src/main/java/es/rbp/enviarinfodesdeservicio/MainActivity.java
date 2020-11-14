package es.rbp.enviarinfodesdeservicio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Esta activity empieza o termina el {@link ServicioContador} y muestra el segundo actual del servicio
 *
 * @author Ricardo Bordería Pi
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Objeto para recibir el broadcast
     */
    private BroadcastReceiver recibidor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int segundoActual = intent.getIntExtra(ServicioContador.CONTADOR_EXTRA, ServicioContador.SEGUNDO_POR_DEFECTO);
            actualizarSegundo(segundoActual);
        }
    };

    /**
     * TextView que muestra el segundo actual del servicio
     */
    private TextView lblSegundoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEmpezar = findViewById(R.id.btnEmpezar);
        Button btnParar = findViewById(R.id.btnParar);

        btnEmpezar.setOnClickListener(this);
        btnParar.setOnClickListener(this);

        lblSegundoActual = findViewById(R.id.lblSegundoActual);

        LocalBroadcastManager.getInstance(this).registerReceiver(recibidor, new IntentFilter(ServicioContador.MENSAJE_BROADCAST));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnEmpezar)
            empezarServicio();
        else if (id == R.id.btnParar)
            pararServicio();
    }

    /**
     * Empieza un {@link ServicioContador} y reinicia el TextView {@link MainActivity#lblSegundoActual}
     */
    private void empezarServicio() {
        lblSegundoActual.setText(String.valueOf(ServicioContador.SEGUNDO_POR_DEFECTO));
        Intent intent = new Intent(this, ServicioContador.class);
        startService(intent);
    }

    /**
     * Detiene un {@link ServicioContador}
     */
    private void pararServicio() {
        Intent intent = new Intent(this, ServicioContador.class);
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